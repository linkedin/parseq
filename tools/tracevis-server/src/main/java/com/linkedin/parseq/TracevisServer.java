package com.linkedin.parseq;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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

  private static final int CACHE_SIZE = 1024;
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

    final HashManager hashManager = new HashManager(TracevisServer::removeCached, CACHE_SIZE);
    final Runtime runtime = Runtime.getRuntime();

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
              LOG.info("building: " + hash);
              PrintWriter writer = response.getWriter();
              try {
                Files.copy(request.getInputStream(), file(hash, "dot").toPath(), StandardCopyOption.REPLACE_EXISTING);
              } catch (FileAlreadyExistsException e) {
                LOG.warn("failed writing dot file: ", file(hash, "dot").toPath());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
              }
              try {
                int result =
                    runtime.exec(
                        new String[] { DOT, "-T" + TYPE, "-Grankdir=LR", "-Gnewrank=true", file(hash, "dot")
                            .getAbsolutePath(), "-o", file(hash, TYPE).getAbsolutePath() }).waitFor();
                if (result != 0) {
                  response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                  writer.write("graphviz process returned: " + result);
                }
                hashManager.add(hash);
                response.setStatus(HttpServletResponse.SC_OK);
              } catch (Exception e) {
                writer.write(e.toString());
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

    server.start();

    server.join();
  }

}
