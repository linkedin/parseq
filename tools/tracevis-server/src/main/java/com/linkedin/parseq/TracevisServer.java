package com.linkedin.parseq;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
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

  private static final int CACHE_SIZE = 1024;
  private static final long TIMEOUT_MS = 5000;
  private static final String CACHE_LOCATION = "./tracevis/cache/";
  private static final String TYPE = "svg";

  private static File file(String hash, String ext) {
    return new File(CACHE_LOCATION + hash + "." + ext);
  }

  private static void removeCached(String hash) {
    file(hash, TYPE).delete();
    file(hash, "dot").delete();
  }

 public static void main(String[] args) throws Exception {
    final int PORT = Integer.parseInt(args[0]);
    final String DOT = args[1];

    LOG.info("Starting server on port: " + PORT + ", dot location: " + DOT);

    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    final Engine engine = new EngineBuilder()
        .setTaskExecutor(scheduler)
        .setTimerScheduler(scheduler)
        .build();

    final HashManager hashManager = new HashManager(TracevisServer::removeCached, CACHE_SIZE);

    final File cacheLocation = new File(CACHE_LOCATION);
    Files.createDirectories(cacheLocation.toPath());
    for (File f: cacheLocation.listFiles()) {
      f.delete();
    }

    Server server = new Server(PORT);

    ResourceHandler resource_handler = new ResourceHandler();
    resource_handler.setDirectoriesListed(true);
    resource_handler.setWelcomeFiles(new String[] { "trace.html" });
    resource_handler.setResourceBase("./tracevis/");

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
            if (hashManager.contains(hash)) {
              LOG.info("hash found in cache: " + hash);
              response.setStatus(HttpServletResponse.SC_OK);
            } else {
              try {
                Files.copy(request.getInputStream(), file(hash, "dot").toPath(), StandardCopyOption.REPLACE_EXISTING);
                handleGraphBuilding(DOT, hashManager, request, response, hash, engine);
              } catch (FileAlreadyExistsException e) {
                LOG.warn("failed writing dot file: ", file(hash, "dot").toPath());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
              }
            }
          }
        }
      }

      /**
       * Writes
       */
      private void writeFailureInfo(final HttpServletResponse response, final Result result) throws IOException {
        final PrintWriter writer = response.getWriter();
          writer.write("graphviz process returned: " + result.getStatus() + "\n");
          writer.write("stdout:\n");
          Files.lines(result.getStdout())
            .forEach(line -> writer.println(line));
          writer.write("\nstderr:\n");
          Files.lines(result.getStderr())
            .forEach(line -> writer.println(line));
      }

      private void handleGraphBuilding(final String DOT, final HashManager hashManager,
          final HttpServletRequest request, final HttpServletResponse response, final String hash,
          final Engine engine) throws IOException {

        LOG.info("building: " + hash);

        //process request in async mode
        final AsyncContext ctx = request.startAsync();

        // Task that runs a graphviz command.
        // We give process TIMEOUT_MS time to finish, after that
        // it will be forcefully killed.
        final Task<Result> graphviz =
            Exec.command("graphviz", TIMEOUT_MS, TimeUnit.MILLISECONDS,
                DOT,
                "-T" + TYPE,
                "-Grankdir=LR", "-Gnewrank=true",
                file(hash, "dot").getAbsolutePath(),
                "-o", file(hash, TYPE).getAbsolutePath());

        //Since Exec utility allows only certain number of processes
        //to run in parallel and rests are enqueued, we also specify
        //timeout for this equal to 2 * TIMEOUT_MS
        final Task<Result> graphvizWithTimeout =
            graphviz.withTimeout(TIMEOUT_MS * 2, TimeUnit.MILLISECONDS);

        //task that handles result
        final Task<Result> handleRsult = graphvizWithTimeout
            .andThen("response", result -> {
              if (result.getStatus() != 0) {
                writeFailureInfo(response, result);
              } else {
                hashManager.add(hash);
              }
            });

        //task that completes response by setting status and completing async context
        final Task<Result> completeResponse = handleRsult
            .lastly("complete", result -> {
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
            });

        //run plan
        engine.run(completeResponse);
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
      engine.shutdown();
      scheduler.shutdownNow();
      Exec.close();
    }
  }

}
