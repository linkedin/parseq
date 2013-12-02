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

import com.linkedin.parseq.internal.trace.MutableTrace;
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
class JsonTraceDeserializer
{
  private JsonTraceDeserializer() {}

  public static Trace deserialize(final JsonNode rootNode) throws IOException
  {
    final MutableTrace trace = new MutableTrace();
    try
    {
      parseTrace(rootNode, trace);
      return trace.freeze();
    }
    catch (RuntimeException e)
    {
      throw new IOException(e);
    }
  }

  private static void parseTrace(final JsonNode rootNode, final MutableTrace trace) throws IOException
  {
    trace.setSnapshotNanos(getLongField(rootNode, JsonTraceCodec.SNAPSHOT_NANOS));
    parseShallowTraces(rootNode, trace);
    parseRelationships(rootNode, trace);
  }

  private static void parseShallowTraces(final JsonNode rootNode,
                                         final MutableTrace trace) throws IOException
  {
    for (JsonNode traceNode : getField(rootNode, JsonTraceCodec.TRACES))
    {
      final int traceId = getIntField(traceNode, JsonTraceCodec.TRACE_ID);
      final String name = getTextField(traceNode, JsonTraceCodec.TRACE_NAME);
      final ResultType resultType = ResultType.valueOf(getTextField(traceNode, JsonTraceCodec.TRACE_RESULT_TYPE));
      final ShallowTraceBuilder shallowBuilder = new ShallowTraceBuilder(name, resultType);

      if (traceNode.get(JsonTraceCodec.TRACE_HIDDEN) != null)
        shallowBuilder.setHidden(getBooleanField(traceNode, JsonTraceCodec.TRACE_HIDDEN));

      if (traceNode.get(JsonTraceCodec.TRACE_SYSTEM_HIDDEN) != null)
        shallowBuilder.setSystemHidden(getBooleanField(traceNode, JsonTraceCodec.TRACE_SYSTEM_HIDDEN));

      if (traceNode.get(JsonTraceCodec.TRACE_VALUE) != null)
        shallowBuilder.setValue(getTextField(traceNode, JsonTraceCodec.TRACE_VALUE));

      if (traceNode.get(JsonTraceCodec.TRACE_START_NANOS) != null)
        shallowBuilder.setStartNanos(getLongField(traceNode, JsonTraceCodec.TRACE_START_NANOS));

      if (traceNode.get(JsonTraceCodec.TRACE_PENDING_NANOS) != null)
        shallowBuilder.setPendingNanos(getLongField(traceNode, JsonTraceCodec.TRACE_PENDING_NANOS));

      if (traceNode.get(JsonTraceCodec.TRACE_END_NANOS) != null)
        shallowBuilder.setEndNanos(getLongField(traceNode, JsonTraceCodec.TRACE_END_NANOS));

      trace.addTrace(traceId, shallowBuilder.build());
    }
  }

  private static void parseRelationships(final JsonNode rootNode,
                                         final MutableTrace trace) throws IOException
  {
    for (JsonNode node : getField(rootNode, JsonTraceCodec.RELATIONSHIPS))
    {
      final Relationship relationship = Relationship.valueOf(getTextField(node, JsonTraceCodec.RELATIONSHIP_RELATIONSHIP));
      final int from = getIntField(node, JsonTraceCodec.RELATIONSHIP_FROM);
      final int to = getIntField(node, JsonTraceCodec.RELATIONSHIP_TO);
      trace.addRelationship(from, to, relationship);
    }
  }

  private static boolean getBooleanField(final JsonNode node, final String fieldName) throws IOException
  {
    return getField(node, fieldName).getBooleanValue();
  }

  private static int getIntField(final JsonNode node, final String fieldName) throws IOException
  {
    return getField(node, fieldName).getIntValue();
  }

  private static long getLongField(final JsonNode node, final String fieldName) throws IOException
  {
    return getField(node, fieldName).getLongValue();
  }

  private static String getTextField(final JsonNode node,
                                     final String fieldName) throws IOException
  {
    return getField(node, fieldName).getTextValue();
  }

  private static JsonNode getField(final JsonNode node, final String fieldName) throws IOException
  {
    final JsonNode field = node.get(fieldName);
    if (field == null)
    {
      throw new IOException("Missing field: '" + fieldName + "' in " + node.getValueAsText());
    }
    return field;
  }
}
