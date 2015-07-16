package com.linkedin.parseq;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TracevisServer {

  private static final Logger LOG = LoggerFactory.getLogger(TracevisServer.class);

  private final Path _staticContentLocation;
  private final Path _cacheLocation;
  private final int _cacheSize;
  private final long _timeoutMs;
  private final int _port;
  private final String _dotLocation;
  private final GraphvizEngine _graphvizEngine;

  public TracevisServer(final String dotLocation, final int port, final Path baseLocation, final int cacheSize,
      final long timeoutMs) {
    _dotLocation = dotLocation;
    _port = port;
    _staticContentLocation = baseLocation.resolve(Constants.TRACEVIS_SUBDIRECTORY);
    _cacheLocation = _staticContentLocation.resolve(Constants.CACHE_SUBDIRECTORY);
    _cacheSize = cacheSize;
    _timeoutMs = timeoutMs;
    _graphvizEngine = new GraphvizEngine(_dotLocation, _cacheLocation, _cacheSize, _timeoutMs,
        Runtime.getRuntime().availableProcessors(), Constants.DEFAULT_REAPER_DELAY_MS,
        Constants.DEFAULT_PROCESS_QUEUE_SIZE);
  }

  public void start()
      throws Exception {
    LOG.info("TracevisServer base location: " + _staticContentLocation);
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

    ResourceHandler resource_handler = new ResourceHandler();
    resource_handler.setDirectoriesListed(true);
    resource_handler.setWelcomeFiles(new String[]{"trace.html"});
    resource_handler.setResourceBase(_staticContentLocation.toString());

    final Handler dotHandler = new AbstractHandler() {

      @Override
      public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
        if (target.startsWith("/dot")) {
          baseRequest.setHandled(true);
          // Process request in async mode
          final AsyncContext ctx = request.startAsync();
          // Generate response
          final Task responseTask = _graphvizEngine.build(request.getParameter("hash"), request.getInputStream())
              .andThen("response", tuple -> {
                // Set status
                response.setStatus(tuple._1());
                // Write body
                PrintWriter writer = response.getWriter();
                writer.write(tuple._2());
                // Complete async mode
                ctx.complete();
              });
          // Execute
          engine.run(responseTask);
        }
      }
    };

    // Add the ResourceHandler to the server.
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[]{dotHandler, resource_handler, new DefaultHandler()});
    server.setHandler(handlers);

    try {
      server.start();
      server.join();
    } finally {
      server.stop();
      _graphvizEngine.stop();
      engine.shutdown();
      scheduler.shutdownNow();
    }
  }
}
