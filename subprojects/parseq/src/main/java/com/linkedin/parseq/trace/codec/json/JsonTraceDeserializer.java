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

import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.TraceRelationship;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
class JsonTraceDeserializer {
  private JsonTraceDeserializer() {
  }

  public static Trace deserialize(final JsonNode rootNode) throws IOException {
    try {
      final Long planId = getLongField(rootNode, JsonTraceCodec.PLAN_ID);
      final String planClass = getTextField(rootNode, JsonTraceCodec.PLAN_CLASS);
      final Map<Long, ShallowTrace> traceMap = parseTraces(rootNode);
      return new Trace(traceMap, parseRelationships(rootNode, traceMap), planClass, planId);
    } catch (RuntimeException e) {
      throw new IOException(e);
    }
  }

  private static Map<Long, ShallowTrace> parseTraces(final JsonNode rootNode) throws IOException {
    Map<Long, ShallowTrace> traceMap = new HashMap<>();
    for (JsonNode traceNode : getField(rootNode, JsonTraceCodec.TRACES)) {
      final long traceId = getLongField(traceNode, JsonTraceCodec.TRACE_ID);
      final ShallowTraceBuilder shallowBuilder = new ShallowTraceBuilder(traceId);

      final String name = getTextField(traceNode, JsonTraceCodec.TRACE_NAME);
      shallowBuilder.setName(name);

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

      if (traceNode.get(JsonTraceCodec.TRACE_ATTRIBUTES) != null) {
        for (JsonNode node : getField(traceNode, JsonTraceCodec.TRACE_ATTRIBUTES)) {
          String key = getTextField(node, JsonTraceCodec.TRACE_ATTRIBUTE_KEY);
          String value = getTextField(node, JsonTraceCodec.TRACE_ATTRIBUTE_VALUE);
          shallowBuilder.addAttribute(key, value);
        }
      }

      final ResultType resultType = ResultType.valueOf(getTextField(traceNode, JsonTraceCodec.TRACE_RESULT_TYPE));
      shallowBuilder.setResultType(resultType);

      if (traceNode.get(JsonTraceCodec.TRACE_TASK_TYPE) != null)
        shallowBuilder.setTaskType(getTextField(traceNode, JsonTraceCodec.TRACE_TASK_TYPE));

      traceMap.put(traceId, shallowBuilder.build());
    }
    return traceMap;
  }

  private static Set<TraceRelationship> parseRelationships(final JsonNode rootNode,
      final Map<Long, ShallowTrace> traceMap) throws IOException {
    Set<TraceRelationship> relationships = new HashSet<>();

    for (JsonNode node : getField(rootNode, JsonTraceCodec.RELATIONSHIPS)) {
      final Relationship relationship =
          Relationship.valueOf(getTextField(node, JsonTraceCodec.RELATIONSHIP_RELATIONSHIP));
      final long from = getIntField(node, JsonTraceCodec.RELATIONSHIP_FROM);
      final long to = getIntField(node, JsonTraceCodec.RELATIONSHIP_TO);
      if (!traceMap.containsKey(from)) {
        throw new IOException("Missing trace with id: " + from + " referenced by relationship: " + relationship);
      }
      if (!traceMap.containsKey(to)) {
        throw new IOException("Missing trace with id: " + to + " referenced by relationship: " + relationship);
      }
      relationships.add(new TraceRelationship(new ShallowTraceBuilder(traceMap.get(from)),
          new ShallowTraceBuilder(traceMap.get(to)), relationship));
    }
    return relationships;
  }

  private static boolean getBooleanField(final JsonNode node, final String fieldName) throws IOException {
    return getField(node, fieldName).getBooleanValue();
  }

  private static int getIntField(final JsonNode node, final String fieldName) throws IOException {
    return getField(node, fieldName).getIntValue();
  }

  private static long getLongField(final JsonNode node, final String fieldName) throws IOException {
    return getField(node, fieldName).getLongValue();
  }

  private static String getTextField(final JsonNode node, final String fieldName) throws IOException {
    return getField(node, fieldName).getTextValue();
  }

  private static JsonNode getField(final JsonNode node, final String fieldName) throws IOException {
    final JsonNode field = node.get(fieldName);
    if (field == null) {
      throw new IOException("Missing field: '" + fieldName + "' in " + node.getValueAsText());
    }
    return field;
  }
}
