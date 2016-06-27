package com.linkedin.parseq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.resource.Resource;

final class TracePostHandler extends AbstractHandler {

  private final String _traceHtml;

  TracePostHandler(String tracevisBase) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Resource traceResource = Resource.newResource(tracevisBase).getResource("trace.html");
    traceResource.writeTo(baos, 0, traceResource.length());
    _traceHtml = baos.toString();
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (target.equals("/trace.html") && (HttpMethod.POST.is(request.getMethod()))) {
      baseRequest.setHandled(true);
      String trace = request.getParameter("trace");
      response.getWriter().write(traceHtml(trace));
    }
  }

  private String traceHtml(String trace) {
    return _traceHtml +  "\n" +
        "<script>\n"
        + "window.onload = function() {\n"
        + "inputJSON.property('value', JSON.stringify(" + trace + "));\n"
        + "refreshView();\n"
        + "}\n"
        + "</script>\n";
  }

}