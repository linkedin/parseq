package com.linkedin.parseq;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.httpclient.HttpClient;


public class TracevisServer {

  private static final Logger LOG = LoggerFactory.getLogger(TracevisServer.class);

  private final Path _staticContentLocation;
  private final Path _heapsterContentLocation;
  private final Path _cacheLocation;
  private final int _cacheSize;
  private final long _timeoutMs;
  private final int _port;
  private final String _dotLocation;
  final GraphvizEngine _graphvizEngine;

  protected Server _server;

  public TracevisServer(final String dotLocation, final int port, final Path baseLocation, final Path heapsterLocation,
      final int cacheSize, final long timeoutMs) {
    _dotLocation = dotLocation;
    _port = port;
    _staticContentLocation = baseLocation.resolve(Constants.TRACEVIS_SUBDIRECTORY);
    _heapsterContentLocation = heapsterLocation.resolve(Constants.HEAPSTER_SUBDIRECTORY);
    _cacheLocation = _staticContentLocation.resolve(Constants.CACHE_SUBDIRECTORY);
    _cacheSize = cacheSize;
    _timeoutMs = timeoutMs;
    _graphvizEngine = new GraphvizEngine(_dotLocation, _cacheLocation, _cacheSize, _timeoutMs,
        Runtime.getRuntime().availableProcessors(), Constants.DEFAULT_REAPER_DELAY_MS,
        Constants.DEFAULT_PROCESS_QUEUE_SIZE);
  }


  public void start()
      throws Exception {
    LOG.info("TracevisServer base location: " + _staticContentLocation + ", heapster location: " + _heapsterContentLocation);
    LOG.info("Starting TracevisServer on port: " + _port + ", graphviz location: " + _dotLocation + ", cache size: "
        + _cacheSize + ", graphviz timeout: " + _timeoutMs + "ms");

    final ScheduledExecutorService scheduler =
        Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    final Engine engine = new EngineBuilder().setTaskExecutor(scheduler).setTimerScheduler(scheduler).build();

    Files.createDirectories(_cacheLocation);
    for (File f : _cacheLocation.toFile().listFiles()) {
      f.delete();
    }

    _graphvizEngine.start();

    _server = new Server();
    _server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", -1);
    _server.setConnectors(getConnectors(_server));

    TracePostHandler tracePostHandler = new TracePostHandler(_staticContentLocation.toString());

    ResourceHandler traceHandler = new ResourceHandler();
    traceHandler.setDirectoriesListed(true);
    traceHandler.setWelcomeFiles(new String[]{"trace.html"});
    traceHandler.setResourceBase(_staticContentLocation.toString());

    ResourceHandler heapsterHandler = new ResourceHandler();
    heapsterHandler.setDirectoriesListed(true);
    heapsterHandler.setResourceBase(_heapsterContentLocation.toString());

    // Add the ResourceHandler to the server.
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{
        new DotHandler(_graphvizEngine, engine),
        new JhatHandler(engine),
        tracePostHandler,
        traceHandler,
        new HealthCheckHandler(),
        heapsterHandler,
        new DefaultHandler()
        });
    _server.setHandler(handlers);

    try {
      _server.start();
      _server.join();
    } finally {
      _server.stop();
      _graphvizEngine.stop();
      engine.shutdown();
      scheduler.shutdownNow();
      HttpClient.close();
    }
  }

  protected Connector[] getConnectors(Server server)
  {
    ServerConnector connector = new ServerConnector(server);
    connector.setPort(_port);
    return new Connector[] { connector };
  }
}
