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

import com.linkedin.parseq.trace.RelationshipEntry;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceEntry;
import com.linkedin.parseq.trace.Trace;
import org.codehaus.jackson.JsonGenerator;

import java.io.IOException;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
class JsonTraceSerializer
{
  private JsonTraceSerializer() {}

  public static void serialize(final Trace trace,
                               final JsonGenerator generator) throws IOException
  {
    generator.writeStartObject();
    generator.writeNumberField(JsonTraceCodec.SNAPSHOT_NANOS, trace.getSnapshotNanos());
    writeShallowTraces(trace, generator);
    writeRelationships(trace, generator);
    generator.writeEndObject();
  }

  private static void writeShallowTraces(final Trace trace,
                                         final JsonGenerator generator) throws IOException
  {
    generator.writeArrayFieldStart(JsonTraceCodec.TRACES);
    for (final ShallowTraceEntry traceEntry : trace.traces())
    {
      writeTrace(traceEntry.getId(), traceEntry.getShallowTrace(), generator);
    }
    generator.writeEndArray();
  }

  private static void writeTrace(final int traceId,
                                 final ShallowTrace shallowTrace,
                                 final JsonGenerator generator) throws IOException
  {
    generator.writeStartObject();
    generator.writeNumberField(JsonTraceCodec.TRACE_ID, traceId);
    generator.writeStringField(JsonTraceCodec.TRACE_NAME, shallowTrace.getName());
    generator.writeStringField(JsonTraceCodec.TRACE_RESULT_TYPE, shallowTrace.getResultType().toString());
    generator.writeBooleanField(JsonTraceCodec.TRACE_HIDDEN, shallowTrace.getHidden());
    generator.writeBooleanField(JsonTraceCodec.TRACE_SYSTEM_HIDDEN, shallowTrace.getSystemHidden());

    if (shallowTrace.getValue() != null)
    {
      generator.writeStringField(JsonTraceCodec.TRACE_VALUE, shallowTrace.getValue());
    }

    if (shallowTrace.getStartNanos() != null)
    {
      generator.writeNumberField(JsonTraceCodec.TRACE_START_NANOS, shallowTrace.getStartNanos());
    }

    if (shallowTrace.getPendingNanos() != null)
    {
      generator.writeNumberField(JsonTraceCodec.TRACE_PENDING_NANOS, shallowTrace.getPendingNanos());
    }

    if (shallowTrace.getEndNanos() != null)
    {
      generator.writeNumberField(JsonTraceCodec.TRACE_END_NANOS, shallowTrace.getEndNanos());
    }

    generator.writeEndObject();
  }

  private static void writeRelationships(final Trace trace,
                                         final JsonGenerator generator) throws IOException
  {
    generator.writeArrayFieldStart(JsonTraceCodec.RELATIONSHIPS);
    for (RelationshipEntry entry : trace.relationships())
    {
      generator.writeStartObject();
      generator.writeStringField(JsonTraceCodec.RELATIONSHIP_RELATIONSHIP, entry.getRelationship().name());
      generator.writeNumberField(JsonTraceCodec.RELATIONSHIP_FROM, entry.getFromId());
      generator.writeNumberField(JsonTraceCodec.RELATIONSHIP_TO, entry.getToId());
      generator.writeEndObject();
    }
    generator.writeEndArray();
  }
}
