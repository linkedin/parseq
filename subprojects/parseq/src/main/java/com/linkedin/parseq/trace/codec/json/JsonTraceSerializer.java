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

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;

import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.TraceRelationship;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
class JsonTraceSerializer {
  private JsonTraceSerializer() {
  }

  public static void serialize(Trace trace, JsonGenerator generator) throws IOException {
    generator.writeStartObject();
    writeTraces(trace, generator);
    writeRelationships(trace, generator);
    generator.writeEndObject();
  }

  private static void writeTraces(final Trace trace, final JsonGenerator generator) throws IOException {
    generator.writeNumberField(JsonTraceCodec.PLAN_ID, trace.getPlanId());
    generator.writeStringField(JsonTraceCodec.PLAN_CLASS, trace.getPlanClass());
    generator.writeArrayFieldStart(JsonTraceCodec.TRACES);
    for (Map.Entry<Long, ShallowTrace> entry : trace.getTraceMap().entrySet()) {
      final ShallowTrace t = entry.getValue();
      writeTrace(t, generator);
    }
    generator.writeEndArray();
  }

  private static void writeTrace(final ShallowTrace trace, final JsonGenerator generator) throws IOException {
    generator.writeStartObject();
    generator.writeNumberField(JsonTraceCodec.TRACE_ID, trace.getNativeId());
    generator.writeStringField(JsonTraceCodec.TRACE_NAME, trace.getName());
    generator.writeStringField(JsonTraceCodec.TRACE_RESULT_TYPE, trace.getResultType().toString());
    generator.writeBooleanField(JsonTraceCodec.TRACE_HIDDEN, trace.getHidden());
    generator.writeBooleanField(JsonTraceCodec.TRACE_SYSTEM_HIDDEN, trace.getSystemHidden());

    if (trace.getValue() != null) {
      generator.writeStringField(JsonTraceCodec.TRACE_VALUE, trace.getValue());
    }

    if (trace.getNativeStartNanos() != -1) {
      generator.writeNumberField(JsonTraceCodec.TRACE_START_NANOS, trace.getNativeStartNanos());
    }

    if (trace.getNativePendingNanos() != -1) {
      generator.writeNumberField(JsonTraceCodec.TRACE_PENDING_NANOS, trace.getNativePendingNanos());
    }

    if (trace.getNativeEndNanos() != -1) {
      generator.writeNumberField(JsonTraceCodec.TRACE_END_NANOS, trace.getNativeEndNanos());
    }

    if (trace.getAttributes() != null && trace.getAttributes().size() > 0) {
      generator.writeArrayFieldStart(JsonTraceCodec.TRACE_ATTRIBUTES);
      for (Map.Entry<String, String> attribute : trace.getAttributes().entrySet()) {
        generator.writeStartObject();
        generator.writeStringField(JsonTraceCodec.TRACE_ATTRIBUTE_KEY, attribute.getKey());
        generator.writeStringField(JsonTraceCodec.TRACE_ATTRIBUTE_VALUE, attribute.getValue());
        generator.writeEndObject();
      }
      generator.writeEndArray();
    }

    if (trace.getTaskType() != null) {
      generator.writeStringField(JsonTraceCodec.TRACE_TASK_TYPE, trace.getTaskType());
    }

    generator.writeEndObject();
  }

  private static void writeRelationships(final Trace trace, final JsonGenerator generator) throws IOException {
    generator.writeArrayFieldStart(JsonTraceCodec.RELATIONSHIPS);
    for (TraceRelationship rel : trace.getRelationships()) {
      generator.writeStartObject();
      generator.writeStringField(JsonTraceCodec.RELATIONSHIP_RELATIONSHIP, rel.getRelationhsip().name());
      generator.writeNumberField(JsonTraceCodec.RELATIONSHIP_FROM, rel.getFrom());
      generator.writeNumberField(JsonTraceCodec.RELATIONSHIP_TO, rel.getTo());
      generator.writeEndObject();
    }
    generator.writeEndArray();
  }

}
