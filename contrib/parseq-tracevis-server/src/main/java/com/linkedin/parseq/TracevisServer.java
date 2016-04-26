package com.linkedin.parseq;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
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

    Server server = new Server(_port);
    server.setAttribute("org.eclipse.jetty.server.Request.maxFormContentSize", -1);

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
        heapsterHandler,
        new DefaultHandler()
        });
    server.setHandler(handlers);

    try {
      server.start();
      server.join();
    } finally {
      server.stop();
      _graphvizEngine.stop();
      engine.shutdown();
      scheduler.shutdownNow();
      HttpClient.close();
    }
  }
}
