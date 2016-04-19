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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
import com.linkedin.restli.client.config.ParSeqRestClientConfig;
import com.linkedin.restli.common.BatchResponse;
import com.linkedin.restli.examples.RestLiIntTestServer;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.client.GreetingsBuilders;


public abstract class ParSeqRestClientIntegrationTest extends BaseEngineTest {

  protected static final String URI_PREFIX = "http://localhost:1338/";

  private static ScheduledExecutorService _serverScheduler;
  private static Engine _serverEngine;
  private static HttpServer _server;

  private static HttpClientFactory _clientFactory;
  private static List<Client> _transportClients;
  private static RestClient _restClient;

  private static final BatchingSupport _batchingSupport = new BatchingSupport();

  private ParSeqRestClient _parseqClient;

  protected abstract ParSeqRestClientConfig getParSeqRestClientGonfig();

  protected abstract boolean expectBatching();

  @BeforeClass
  public static void init() throws Exception {
    _serverScheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    _serverEngine = new EngineBuilder().setTaskExecutor(_serverScheduler).setTimerScheduler(_serverScheduler)
        .setPlanDeactivationListener(_batchingSupport).build();
    _server = RestLiIntTestServer.createServer(_serverEngine, RestLiIntTestServer.DEFAULT_PORT,
        RestLiIntTestServer.supportedCompression, true, 5000);
    _server.start();
    _clientFactory = new HttpClientFactory();
    _transportClients = new ArrayList<Client>();
    Client client = newTransportClient(Collections.<String, String> emptyMap());
    _restClient = new RestClient(client, URI_PREFIX);
  }

  @AfterClass
  public static void shutdown() throws Exception {
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
      FutureCallback<None> callback = new FutureCallback<None>();
      client.shutdown(callback);
      callback.get();
    }
    if (_clientFactory != null) {
      FutureCallback<None> callback = new FutureCallback<None>();
      _clientFactory.shutdown(callback);
      callback.get();
    }
  }

  private static Client newTransportClient(Map<String, ? extends Object> properties) {
    Client client = new TransportClientAdapter(_clientFactory.getClient(properties));
    _transportClients.add(client);
    return client;
  }

  @Override
  protected void customizeEngine(EngineBuilder engineBuilder) {
    engineBuilder.setPlanDeactivationListener(_batchingSupport);


    _parseqClient = new ParSeqRestClientBuilder()
        .setRestClient(_restClient)
        .setBatchingSupport(_batchingSupport)
        .setConfig(getParSeqRestClientGonfig())
        .build();
  }

  protected Task<String> toMessage(Task<Response<Greeting>> greeting) {
    return greeting.map("toMessage", g -> g.getEntity().getMessage());
  }

  protected Task<Response<Greeting>> greeting(Long id) {
    return _parseqClient.createTask(new GreetingsBuilders().get().id(id).build());
  }

  protected Task<Response<BatchResponse<Greeting>>> greetings(Long... ids) {
    return _parseqClient.createTask(new GreetingsBuilders().batchGet().ids(ids).build());
  }

  protected boolean hasTask(final String name, final Trace trace) {
    return trace.getTraceMap().values().stream().anyMatch(shallowTrace -> shallowTrace.getName().equals(name));
  }

  private String getTestClassName() {
    return this.getClass().getName();
  }


  // ---------- Tests ----------

  @Test
  public void testGetRequests() {
    Task<?> task = Task.par(greeting(1L), greeting(2L));
    runAndWait(getTestClassName() + ".testGetRequests", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
    }
  }

  @Test
  public void testGetRequestsWithSameCustomHeaders() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addHeader("H1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addHeader("H1", "V1").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithSameCustomHeaders", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
    }
  }

  @Test
  public void testGetRequestsWithSameQueryParams() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addParam("K1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addParam("K1", "V1").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithSameQueryParams", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
    }
  }

  @Test
  public void testGetRequestsWithDifferentCustomQueryParamValuesNoBatching() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addParam("K1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addParam("K1", "V2").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomQueryParamValuesNoBatching", task);
    assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomHeaderValuesNoBatching() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addHeader("H1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addHeader("H1", "V2").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomHeadersNoBatching", task);
    assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomHeadersNoBatching() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addHeader("H1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addHeader("H2", "V1").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomHeadersNoBatching", task);
    assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomQueryParamsNoBatching() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addParam("K1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addParam("K2", "V1").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomQueryParamsNoBatching", task);
    assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithError() {
    Task<String> task = Task.par(toMessage(greeting(1L)), toMessage(greeting(-1L)).recover(e -> "failed"))
        .map("combine", (x, y) -> x + y);
    runAndWait(getTestClassName() + ".testGetRequestsWithError", task);
    assertEquals(task.get(), "Good morning!failed");
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
    }
  }

  @Test
  public void testBatchGetRequests() {
    Task<?> task = Task.par(greetings(1L, 2L), greetings(3L, 4L));
    runAndWait(getTestClassName() + ".testBatchGetRequests", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
    }
  }

  @Test
  public void testGetAndBatchGetRequests() {
    Task<?> task = Task.par(greeting(1L), greetings(2L, 3L));
    runAndWait(getTestClassName() + ".testGetAndBatchGetRequests", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
    }
  }

  @Test
  public void testSingleGetRequestIsNotBatched() {
    Task<?> task = greeting(1L);
    runAndWait(getTestClassName() + ".testSingleGetRequestIsNotBatched", task);
    assertFalse(hasTask("greetings batch_get(1)", task.getTrace()));
  }

  @Test
  public void testDuplicateGetRequestIsNotBatched() {
    Task<?> task = Task.par(greeting(1L), greeting(1L));
    runAndWait(getTestClassName() + ".testDuplicateGetRequestIsNotBatched", task);
    assertFalse(hasTask("greetings batch_get(1)", task.getTrace()));
  }
}
