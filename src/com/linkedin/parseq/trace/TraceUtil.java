package com.linkedin.parseq.trace;

import java.io.IOException;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.trace.codec.json.JsonTraceCodec;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TraceUtil {

  private static final JsonTraceCodec JSON_CODEC = new JsonTraceCodec();

  /**
   * Returns trace of a task serialized to a JSON string.
   * @param task task to get trace from
   * @return JSON representation of the trace
   * @throws IOException
   */
  public static String getJsonTrace(final Task<?> task) throws IOException {
    return JSON_CODEC.encode(task.getTrace());
  }

  /**
   * Returns trace serialized to a JSON string.
   * @param trace trace to serialize to JSON
   * @return JSON representation of the trace
   * @throws IOException
   */
  public static String getJsonTrace(final Trace trace) throws IOException {
    return JSON_CODEC.encode(trace);
  }

}
