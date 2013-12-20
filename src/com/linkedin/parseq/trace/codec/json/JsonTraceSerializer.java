/*
 * Copyright 2012 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.parseq.trace.codec.json;

import com.linkedin.parseq.trace.Related;
import com.linkedin.parseq.trace.Trace;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
class JsonTraceSerializer
{
  private JsonTraceSerializer() {}

  public static void serialize(Trace root, JsonGenerator generator) throws IOException
  {
    // Build a map of traces to their unique ids for serialization
    final Map<Trace, Integer> traceIds = buildTraceIdMap(root);
    generator.writeStartObject();
    writeTraces(traceIds, generator);
    writeRelationships(traceIds, generator);
    generator.writeEndObject();
  }

  private static void writeTraces(final Map<Trace, Integer> traceIds,
                                  final JsonGenerator generator) throws IOException
  {
    generator.writeArrayFieldStart(JsonTraceCodec.TRACES);
    for (Map.Entry<Trace, Integer> entry : traceIds.entrySet())
    {
      final Trace trace = entry.getKey();
      final int traceId = entry.getValue();
      writeTrace(trace, traceId, generator);
    }
    generator.writeEndArray();
  }

  private static void writeTrace(final Trace trace, final int traceId,
                                 final JsonGenerator generator) throws IOException
  {
    generator.writeStartObject();
    generator.writeNumberField(JsonTraceCodec.TRACE_ID, traceId);
    generator.writeStringField(JsonTraceCodec.TRACE_NAME, trace.getName());
    generator.writeStringField(JsonTraceCodec.TRACE_RESULT_TYPE, trace.getResultType().toString());
    generator.writeBooleanField(JsonTraceCodec.TRACE_HIDDEN, trace.getHidden());
    generator.writeBooleanField(JsonTraceCodec.TRACE_SYSTEM_HIDDEN, trace.getSystemHidden());

    if (trace.getValue() != null)
    {
      generator.writeStringField(JsonTraceCodec.TRACE_VALUE, trace.getValue());
    }

    if (trace.getStartNanos() != null)
    {
      generator.writeNumberField(JsonTraceCodec.TRACE_START_NANOS, trace.getStartNanos());
    }

    if (trace.getPendingNanos() != null)
    {
      generator.writeNumberField(JsonTraceCodec.TRACE_PENDING_NANOS, trace.getPendingNanos());
    }

    if (trace.getEndNanos() != null)
    {
      generator.writeNumberField(JsonTraceCodec.TRACE_END_NANOS, trace.getEndNanos());
    }

    generator.writeEndObject();
  }

  private static void writeRelationships(final Map<Trace, Integer> traceIds,
                                         final JsonGenerator generator) throws IOException
  {
    generator.writeArrayFieldStart(JsonTraceCodec.RELATIONSHIPS);
    for (Map.Entry<Trace, Integer> entry : traceIds.entrySet())
    {
      final Trace trace = entry.getKey();
      final int fromId = entry.getValue();
      for (Related<Trace> related : trace.getRelated())
      {
        final int toId = traceIds.get(related.getRelated());
        final String relationship = related.getRelationship();
        generator.writeStartObject();
        generator.writeStringField(JsonTraceCodec.RELATIONSHIP_RELATIONSHIP, relationship);
        generator.writeNumberField(JsonTraceCodec.RELATIONSHIP_FROM, fromId);
        generator.writeNumberField(JsonTraceCodec.RELATIONSHIP_TO, toId);
        generator.writeEndObject();
      }
    }
    generator.writeEndArray();
  }

  private static Map<Trace, Integer> buildTraceIdMap(final Trace root)
  {
    final Map<Trace, Integer> traceIds = new HashMap<Trace, Integer>();
    final ArrayDeque<Trace> stack = new ArrayDeque<Trace>();
    stack.push(root);

    int counter = 0;
    while (!stack.isEmpty())
    {
      final Trace trace = stack.pop();
      traceIds.put(trace, counter++);
      for (Related<Trace> related : trace.getRelated())
      {
        final Trace relatedTrace = related.getRelated();
        if (!traceIds.containsKey(relatedTrace))
        {
          stack.push(related.getRelated());
        }
      }
    }

    return traceIds;
  }
}
