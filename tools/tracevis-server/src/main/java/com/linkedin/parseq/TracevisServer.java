package com.linkedin.parseq;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
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

  static String readFullyFromJar(String filename) throws Exception {
    final URI uri = TracevisServer.class.getResource(filename).toURI();
    Path path;
    try {
      path = Paths.get(uri);
      return readFromPath(path);
    } catch (FileSystemNotFoundException exp) {
      try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
        path = Paths.get(uri);
        return readFromPath(path);
      }
    }
  }

  private static String readFromPath(Path path) throws IOException {
    final byte[] bytes = Files.readAllBytes(path);
    return new String(bytes);
  }

 public static void main(String[] args) throws Exception {
    final int PORT = Integer.parseInt(args[0]);
    final String DOT = args[1];

    LOG.info("Starting server on port: " + PORT + ", dot location: " + DOT);

    final String SVGPan = readFullyFromJar("/SVGPan");

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
                handleGraphBuilding(DOT, hashManager, request, response, hash, engine, SVGPan);
              } catch (FileAlreadyExistsException e) {
                LOG.warn("failed writing dot file: ", file(hash, "dot").toPath());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
              }
            }
          }
        }
      }

      private void handleGraphBuilding(final String DOT, final HashManager hashManager,
          final HttpServletRequest request, final HttpServletResponse response, final String hash,
          final Engine engine, String SVGPan) throws IOException {
        LOG.info("building: " + hash);

        final AsyncContext ctx = request.startAsync();
        final Task<Result> graphviz = Exec.command("graphviz", TIMEOUT_MS, TimeUnit.MILLISECONDS, DOT, "-T" + TYPE, "-Grankdir=LR", "-Gnewrank=true",
            file(hash, "dot").getAbsolutePath())
            .withTimeout(TIMEOUT_MS * 2, TimeUnit.MILLISECONDS)
            .andThen("response", result -> {
              final PrintWriter writer = response.getWriter();
              if (result.getStatus() != 0) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writer.write("graphviz process returned: " + result.getStatus() + "\n");
                writer.write("stdout:\n");
                Files.lines(result.getStdout())
                  .forEach(line -> writer.println(line));
                writer.write("\nstderr:\n");
                Files.lines(result.getStderr())
                  .forEach(line -> writer.println(line));
                ctx.complete();
              } else {
                PrintWriter resultFileWriter =
                    new PrintWriter(Files.newOutputStream(file(hash, TYPE).toPath(), StandardOpenOption.CREATE_NEW));
                try {
                  Files.lines(result.getStdout())
                  .forEach(line -> {
                    if (line.startsWith("<g id=\"graph0\"")) {
                      resultFileWriter.println(SVGPan);
                    }
                    resultFileWriter.println(line);
                  });
                } finally {
                  resultFileWriter.close();
                }
                hashManager.add(hash);
                response.setStatus(HttpServletResponse.SC_OK);
                ctx.complete();
              }
            })
            .onFailure(e -> {
              final PrintWriter writer = response.getWriter();
              writer.write(e.toString());
              response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
              ctx.complete();
            });
        engine.run(graphviz);
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
