package com.linkedin.parseq;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

import com.linkedin.parseq.exec.Exec;
import com.linkedin.parseq.exec.Exec.Result;


public class TracevisServer {

  private static final Logger LOG = LoggerFactory.getLogger(TracevisServer.class);

  private final Path _staticContentLocation;
  private final Path _cacheLocation;
  private final int _cacheSize;
  private final long _timeoutMs;
  private final int _port;
  private final String _dotLocation;
  private final HashManager _hashManager;

  public TracevisServer(final String dotLocation, final int port, final Path baseLocation, final int cacheSize, final long timeoutMs) {
    _dotLocation = dotLocation;
    _port = port;
    _staticContentLocation = baseLocation.resolve(Constants.TRACEVIS_SUBDIRECTORY);
    _cacheLocation = _staticContentLocation.resolve(Constants.CACHE_SUBDIRECTORY);
    _cacheSize = cacheSize;
    _timeoutMs = timeoutMs;
    _hashManager = new HashManager(this::removeCached, _cacheSize);
  }

  private Path pathToCacheFile(String hash, String ext) {
    return _cacheLocation.resolve(hash + "." + ext);
  }

  private File cacheFile(String hash, String ext) {
    return pathToCacheFile(hash, ext).toFile();
  }

  private void removeCached(String hash) {
    cacheFile(hash, Constants.OUTPUT_TYPE).delete();
    cacheFile(hash, "dot").delete();
  }

 public void start() throws Exception {

   LOG.info("TracevisServer base location: " + _staticContentLocation);
    LOG.info("Starting TracevisServer on port: " + _port + ", graphviz location: " + _dotLocation + ", cache size: " + _cacheSize +
        ", graphviz timeout: " + _timeoutMs + "ms");

    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    final Engine engine = new EngineBuilder()
        .setTaskExecutor(scheduler)
        .setTimerScheduler(scheduler)
        .build();

    Files.createDirectories(_cacheLocation);
    for (File f: _cacheLocation.toFile().listFiles()) {
      f.delete();
    }

    Server server = new Server(_port);

    ResourceHandler resource_handler = new ResourceHandler();
    resource_handler.setDirectoriesListed(true);
    resource_handler.setWelcomeFiles(new String[] { "trace.html" });
    resource_handler.setResourceBase(_staticContentLocation.toString());

    final Handler dotHandler = new AbstractHandler() {

      @Override
      public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
          throws IOException, ServletException {
        if (target.startsWith("/dot")) {
          baseRequest.setHandled(true);
          String hash = request.getParameter("hash");
          if (hash == null) {
            LOG.info("missing hash");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          } else {
            if (_hashManager.contains(hash)) {
              LOG.info("hash found in cache: " + hash);
              response.setStatus(HttpServletResponse.SC_OK);
            } else {
              try {
                Files.copy(request.getInputStream(), pathToCacheFile(hash, "dot"), StandardCopyOption.REPLACE_EXISTING);
                handleGraphBuilding(request, response, hash, engine);
              } catch (FileAlreadyExistsException e) {
                LOG.warn("failed writing dot file: ", pathToCacheFile(hash, "dot"));
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
              }
            }
          }
        }
      }
    };

    // Add the ResourceHandler to the server.
    HandlerList handlers = new HandlerList();
    handlers.setHandlers(new Handler[] { dotHandler, resource_handler, new DefaultHandler() });
    server.setHandler(handlers);

    try {
      server.start();
      server.join();
    } finally {
      server.stop();
      engine.shutdown();
      scheduler.shutdownNow();
      Exec.close();
    }
  }


 /**
  * Writes error info back to the client.
  */
 private void writeGenericFailureInfo(final HttpServletResponse response, final Result result) throws IOException {
   final PrintWriter writer = response.getWriter();
     writer.write("graphviz process returned: " + result.getStatus() + "\n");
     writer.write("stdout:\n");
     Files.lines(result.getStdout())
       .forEach(line -> writer.println(line));
     writer.write("stderr:\n");
     Files.lines(result.getStderr())
       .forEach(line -> writer.println(line));
 }

 private void handleGraphBuilding(final HttpServletRequest request, final HttpServletResponse response,
     final String hash, final Engine engine) throws IOException {

   LOG.info("building: " + hash);

   //process request in async mode
   final AsyncContext ctx = request.startAsync();

   // Task that runs a graphviz command.
   // We give process TIMEOUT_MS time to finish, after that
   // it will be forcefully killed.
   final Task<Result> graphviz =
       Exec.command("graphviz", _timeoutMs, TimeUnit.MILLISECONDS,
           _dotLocation,
           "-T" + Constants.OUTPUT_TYPE,
           "-Grankdir=LR", "-Gnewrank=true",
           pathToCacheFile(hash, "dot").toString(),
           "-o", pathToCacheFile(hash, Constants.OUTPUT_TYPE).toString());

   //Since Exec utility allows only certain number of processes
   //to run in parallel and rest is enqueued, we also specify
   //timeout for this equal to 2 * graphviz timeout
   final Task<Result> graphvizWithTimeout =
       graphviz.withTimeout(_timeoutMs * 2, TimeUnit.MILLISECONDS);

   //task that handles result
   final Task<Result> handleRsult = graphvizWithTimeout
       .andThen("response", result -> {
         switch (result.getStatus()) {
           case 0:
             _hashManager.add(hash);
             break;
           case 137:
             final PrintWriter writer = response.getWriter();
             writer.write("graphviz process was killed becuase it did not finish within " + _timeoutMs + "ms");
             break;
           default:
             writeGenericFailureInfo(response, result);
             break;
         }
       });

   //task that completes response by setting status and completing async context
   final Task<Result> completeResponse = handleRsult
       .transform("complete", result -> {
         if (!result.isFailed()) {
           if (result.get().getStatus() == 0) {
             response.setStatus(HttpServletResponse.SC_OK);
           } else {
             response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
           }
         } else {
           final PrintWriter writer = response.getWriter();
           writer.write(result.getError().toString());
           response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         }
         ctx.complete();
         return result;
       });

   //run plan
   engine.run(completeResponse);
 }

}
