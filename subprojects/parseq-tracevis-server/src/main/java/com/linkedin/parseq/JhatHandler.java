package com.linkedin.parseq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.asynchttpclient.Response;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.linkedin.parseq.httpclient.HttpClient;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.TraceBuilder;
import com.linkedin.parseq.trace.TraceRelationship;
import com.linkedin.parseq.trace.codec.json.JsonTraceCodec;


final class JhatHandler extends AbstractHandler {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Pattern REGEX = Pattern.compile("^.*?<table border='1'>.*?<tr><td>\\s*(.*)\\s*</td></tr>", Pattern.DOTALL);
  private static final JsonTraceCodec CODEC = new JsonTraceCodec();


  private final Engine _engine;
  private final String _script;

  JhatHandler(Engine engine) throws IOException {
    _engine = engine;
    _script = read(getClass().getClassLoader().getResourceAsStream("RecoverParSeqTracesFromHeapDump.js"));
  }

  private static String read(InputStream input) throws IOException {
    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
      return buffer.lines().collect(Collectors.joining("\n"));
    }
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    if (target.startsWith("/jhat")) {

      baseRequest.setHandled(true);
      // Process request in async mode
      final AsyncContext ctx = request.startAsync();

      final Task<?> responseTask = fetchJSON(request)
          .recover("handleFailure", this::handleFailure)
          .andThen("writeResponseAndComplete", r -> writeResponseAndComplete(response, r, ctx));

      // Execute
      _engine.run(responseTask);
    }
  }

  private void writeResponseAndComplete(HttpServletResponse response, HttpResponse r, AsyncContext ctx) throws IOException {
    response.getWriter().write(r.getBody());
    response.setStatus(r.getStatus());
    ctx.complete();
  }

  private HttpResponse handleFailure(Throwable t) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    t.printStackTrace(pw);
    String stackTrace = sw.toString();
    return new HttpResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request:\n" + stackTrace);
  }

  private Task<HttpResponse> fetchJSON(HttpServletRequest request) {
    String location = request.getParameter("location");
    if (location == null) {
      return Task.value(new HttpResponse(HttpServletResponse.SC_BAD_REQUEST, "Missing location query parameter"));
    } else {
      return Task.flatten(Task.callable(() -> oqlGetTask(location)))
        .map("processOQLResponse", this::processOQLResponse);
    }
  }

  private Task<Response> oqlGetTask(String location) {
    try {
      return HttpClient.get(location + "/oql/")
        .setRequestTimeout(900000)
        .addQueryParam("query", _script).task("runOQL");
    } catch (Exception e) {
      throw new RuntimeException("Can't create GET request to jhat server using location: " + location, e);
    }
  }

  private HttpResponse processOQLResponse(Response response) throws IOException {
    String responseBody = response.getResponseBody();
    if (response.getStatusCode() != HttpServletResponse.SC_OK) {
      return new HttpResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to query Jhat:\n" + responseBody);
    } else {
      Matcher regexMatcher = REGEX.matcher(responseBody);
      if (regexMatcher.find()) {
        String cutResponse = regexMatcher.group(1);
        String fixedCutResponse = cutResponse.substring(0, cutResponse.length() - 4) + " ]";
        final JsonNode resultsArr =
            OBJECT_MAPPER.readTree(OBJECT_MAPPER.getJsonFactory().createJsonParser(fixedCutResponse));
        final StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");
        for (JsonNode node : resultsArr) {
          Trace trace = CODEC.decode(node.toString());
          TraceBuilder builder = new TraceBuilder(trace.getRelationships().size() + 1, trace.getPlanClass(), trace.getPlanId());
          Map<Long, ShallowTraceBuilder> traceMap = new HashMap<>();
          trace.getTraceMap().forEach((key, value) -> {
            ShallowTraceBuilder stb = new ShallowTraceBuilder(value);
            traceMap.put(key, stb);
            builder.addShallowTrace(stb);
          });
          for (TraceRelationship rel : trace.getRelationships()) {
            builder.addRelationship(rel.getRelationhsip(), traceMap.get(rel.getFrom()), traceMap.get(rel.getTo()));
          }
          joiner.add(CODEC.encode(builder.build()));
        }
        return new HttpResponse(HttpServletResponse.SC_OK, joiner.toString());
      } else {
        return new HttpResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            "Failed parsing Jhat response:\n" + responseBody);
      }
    }
  }
}