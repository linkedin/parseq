package com.linkedin.restli.client;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.linkedin.restli.client.config.BatchingConfig;
import com.linkedin.restli.client.config.ParSeqRestClientConfig;
import com.linkedin.restli.client.config.ResourceConfig;
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

  public abstract ParSeqRestClientConfig getParSeqRestClientGonfig();

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

  protected Task<Response<Greeting>> greeting(Long id) {
    return _parseqClient.createTask(new GreetingsBuilders().get().id(id).build());
  }

  protected Task<Response<BatchResponse<Greeting>>> greetings(Long... ids) {
    return _parseqClient.createTask(new GreetingsBuilders().batchGet().ids(ids).build());
  }

  protected boolean hasTask(final String name, final Trace trace) {
    return trace.getTraceMap().values().stream().anyMatch(shallowTrace -> shallowTrace.getName().equals(name));
  }


}
