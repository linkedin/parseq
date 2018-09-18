package com.linkedin.parseq;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

final class DotHandler extends AbstractHandler {

  private final GraphvizEngine _graphvizEngine;
  private final Engine _engine;

  DotHandler(GraphvizEngine graphvizEngine, Engine engine) {
    _graphvizEngine = graphvizEngine;
    _engine = engine;
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (target.startsWith("/dot")) {
      baseRequest.setHandled(true);
      // Process request in async mode
      final AsyncContext ctx = request.startAsync();
      // Generate response
      final Task<HttpResponse> responseTask = _graphvizEngine.build(request.getParameter("hash"), request.getInputStream())
          .andThen("response", graphvizResponse -> {
            // Set status
            response.setStatus(graphvizResponse.getStatus());
            // Write body
            PrintWriter writer = response.getWriter();
            writer.write(graphvizResponse.getBody());
            // Complete async mode
            ctx.complete();
          });
      // Execute
      _engine.run(responseTask);
    }
  }
}