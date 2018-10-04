/*
 * Copyright 2016 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.restli.client;

import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.DynamicRecordMetadata;
import com.linkedin.r2.filter.FilterChains;
import com.linkedin.restli.client.base.GetRequestBuilderBase;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.ResourceSpecImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.linkedin.common.callback.FutureCallback;
import com.linkedin.common.util.None;
import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.r2.transport.http.server.HttpServer;
import com.linkedin.restli.client.config.RequestConfigOverrides;
import com.linkedin.restli.common.BatchResponse;
import com.linkedin.restli.common.EmptyRecord;
import com.linkedin.restli.examples.RestLiIntTestServer;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.greetings.client.AssociationsSubBuilders;
import com.linkedin.restli.examples.greetings.client.GreetingsBuilders;


public abstract class ParSeqRestClientIntegrationTest extends BaseEngineTest {

  private static final AtomicInteger PORTER = new AtomicInteger(14497);
  private final int _port = PORTER.getAndIncrement();

  protected final String URI_PREFIX = "http://localhost:" + _port + "/";

  private ScheduledExecutorService _serverScheduler;
  private Engine _serverEngine;
  private HttpServer _server;

  private HttpClientFactory _clientFactory;
  private List<Client> _transportClients;
  private RestClient _restClient;

  private final BatchingSupport _batchingSupport = new BatchingSupport();

  private final ThreadLocal<InboundRequestContext> _inboundRequestContext = new ThreadLocal<>();

  protected ParSeqRestliClient _parseqClient;

  protected abstract ParSeqRestliClientConfig getParSeqRestClientConfig();

  protected void setInboundRequestContext(InboundRequestContext irc) {
    _inboundRequestContext.set(irc);
  }

  protected void clearInboundRequestContext() {
    _inboundRequestContext.remove();
  }

  @BeforeClass
  public void init() throws Exception {
    _serverScheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    EngineBuilder serverEngineBuilder = new EngineBuilder();
    serverEngineBuilder.setTaskExecutor(_serverScheduler).setTimerScheduler(_serverScheduler)
    .setPlanDeactivationListener(_batchingSupport);
    _serverEngine = serverEngineBuilder.build();
    _server = RestLiIntTestServer.createServer(_serverEngine, _port,
        true, 5000, null, FilterChains.empty(), true, false, false);
    _server.start();
    _clientFactory = new HttpClientFactory();
    _transportClients = new ArrayList<>();
    _restClient = createRestClient();
  }

  protected RestClient createRestClient() {
    Client client = newTransportClient(Collections.<String, String> emptyMap());
    return new RestClient(client, URI_PREFIX);
  }

  @AfterClass
  public void shutdown() throws Exception {
    if (_server != null) {
      _server.stop();
    }
    if (_serverEngine != null) {
      _serverEngine.shutdown();
    }
    if (_serverScheduler != null) {
      _serverScheduler.shutdownNow();
    }
    for (Client client : _transportClients) {
      FutureCallback<None> callback = new FutureCallback<>();
      client.shutdown(callback);
      callback.get();
    }
    if (_clientFactory != null) {
      FutureCallback<None> callback = new FutureCallback<>();
      _clientFactory.shutdown(callback);
      callback.get();
    }
  }

  private Client newTransportClient(Map<String, ? extends Object> properties) {
    Client client = new TransportClientAdapter(_clientFactory.getClient(properties));
    _transportClients.add(client);
    return client;
  }

  protected void customizeParSeqRestliClient(ParSeqRestliClientBuilder parSeqRestliClientBuilder) {
  }

  @Override
  protected void customizeEngine(EngineBuilder engineBuilder) {
    engineBuilder.setPlanDeactivationListener(_batchingSupport);

    ParSeqRestliClientBuilder parSeqRestliClientBuilder = new ParSeqRestliClientBuilder()
        .setRestClient(_restClient)
        .setBatchingSupport(_batchingSupport)
        .setConfig(getParSeqRestClientConfig())
        .setInboundRequestContextFinder(() -> Optional.ofNullable(_inboundRequestContext.get()));

    customizeParSeqRestliClient(parSeqRestliClientBuilder);

    _parseqClient = parSeqRestliClientBuilder.build();
  }

  protected Task<String> toMessage(Task<Response<Greeting>> greeting) {
    return greeting.map("toMessage", g -> g.getEntity().getMessage());
  }

  protected Task<Response<Greeting>> greetingGet(Long id) {
    return _parseqClient.createTask(new GreetingsBuilders().get().id(id).build());
  }

  // This method is for the "testBatchingGetRequestsWithDiffKeyType" test to create a request with String Key type.
  protected Task<Response<Greeting>> greetingGetWithStringKey(String id) {
    String _baseUriTemplate = "greetings";
    ResourceSpec _resourceSpec = new ResourceSpecImpl(EnumSet.allOf(ResourceMethod.class), Collections.emptyMap(), Collections.emptyMap(), String.class, null, null, Greeting.class, Collections.emptyMap());
    return _parseqClient.createTask(new GetRequestBuilder(_baseUriTemplate, Greeting.class, _resourceSpec, RestliRequestOptions.DEFAULT_OPTIONS).id(id).build());
  }

  protected Task<Response<Greeting>> greetingGetWithProjection(Long id, PathSpec... fields) {
    return _parseqClient.createTask(new GreetingsBuilders().get().id(id).fields(fields).build());
  }

  protected Task<Response<Message>> associationsGet(String src, String dst, String id) {
    return _parseqClient.createTask(new AssociationsSubBuilders().get().srcKey(src).destKey(dst).id(id).build());
  }

  protected Task<Response<Message>> associationsGet(String src, String dst, String id, RequestConfigOverrides configOverrides) {
    return _parseqClient.createTask(new AssociationsSubBuilders().get().srcKey(src).destKey(dst).id(id).build(), configOverrides);
  }

  protected Task<Response<Greeting>> greetingGet(Long id, RequestConfigOverrides configOverrides) {
    return _parseqClient.createTask(new GreetingsBuilders().get().id(id).build(), configOverrides);
  }

  protected Task<Response<EmptyRecord>> greetingDel(Long id) {
    return _parseqClient.createTask(new GreetingsBuilders().delete().id(id).build());
  }

  protected Task<Response<EmptyRecord>> greetingDel(Long id, RequestConfigOverrides configOverrides) {
    return _parseqClient.createTask(new GreetingsBuilders().delete().id(id).build(), configOverrides);
  }

  protected Task<Response<BatchResponse<Greeting>>> greetings(Long... ids) {
    return _parseqClient.createTask(new GreetingsBuilders().batchGet().ids(ids).build());
  }

  protected Task<Response<BatchResponse<Greeting>>> greetingsWithProjection(Collection<PathSpec> fields, Long... ids) {
    return _parseqClient.createTask(new GreetingsBuilders().batchGet().ids(ids).fields(fields.toArray(new PathSpec[fields.size()])).build());
  }

  protected Task<Response<BatchResponse<Greeting>>> greetings(RequestConfigOverrides configOverrides, Long... ids) {
    return _parseqClient.createTask(new GreetingsBuilders().batchGet().ids(ids).build(), configOverrides);
  }

  protected boolean hasTask(final String name, final Trace trace) {
    return trace.getTraceMap().values().stream().anyMatch(shallowTrace -> shallowTrace.getName().equals(name));
  }

  protected String getTestClassName() {
    return this.getClass().getName();
  }

  protected static <T> void addProperty(Map<String, Map<String, Object>> config, String property, String key, T value) {
    Map<String, Object> map = config.computeIfAbsent(property, k -> new HashMap<>());
    map.put(key, value);
  }
}
