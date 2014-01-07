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
import com.linkedin.parseq.trace.RelationshipEntry;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.ShallowTraceEntry;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.codec.TraceCodec;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

/**
 * @author Chi Chan (ckchan@linkedin.com)
 * @author Chris Pettitt
 */
public class TestJsonTraceCodec
{
  private TraceCodec _codec = new JsonTraceCodec();

  @Test
  public void testReversibleUnstartedTrace() throws IOException
  {
    final MutableTrace trace = new MutableTrace();
    trace.addTrace(0, new ShallowTraceBuilder("test", ResultType.UNFINISHED).build());
    assertReversible(trace);
  }

  @Test
  public void testReversibleSuccessfulTrace() throws IOException
  {
    final MutableTrace trace = new MutableTrace();
    trace.addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
        .setValue("test value")
        .setStartNanos(0L)
        .setPendingNanos(50L)
        .setEndNanos(100L)
        .build());
    assertReversible(trace);
  }

  @Test
  public void testReversibleSuccessfulTraceWithNullValue() throws IOException
  {
    final MutableTrace trace = new MutableTrace();
    trace.addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
                                .setStartNanos(0L)
                                .setPendingNanos(50L)
                                .setEndNanos(100L)
                                .build());
    assertReversible(trace);
  }

  @Test
  public void testReversibleErrorTrace() throws IOException
  {
    final MutableTrace trace = new MutableTrace();
    trace.addTrace(0, new ShallowTraceBuilder("test", ResultType.ERROR)
        .setValue("error value")
        .setStartNanos(0L)
        .setPendingNanos(50L)
        .setEndNanos(100L)
        .build());
    assertReversible(trace);
  }

  @Test
  public void testReversibleUnfinishedTrace() throws IOException
  {
    // If we have started a task we also must set the end time
    final MutableTrace trace = new MutableTrace();
    trace.addTrace(0, new ShallowTraceBuilder("test", ResultType.UNFINISHED)
                                .setStartNanos(0L)
                                .setPendingNanos(50L)
                                .setEndNanos(100L)
                                .build());
    assertReversible(trace);
  }

  @Test
  public void testReversibleWithHiddenTrace() throws IOException
  {
    final MutableTrace trace = new MutableTrace();
    trace.addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
        .setStartNanos(0L)
        .setPendingNanos(50L)
        .setEndNanos(100L)
        .setHidden(true)
        .build());
    assertReversible(trace);
  }

  @Test
  public void testReversibleTraceWithChild() throws IOException
  {
    final MutableTrace trace = new MutableTrace();
    trace.addTrace(0, new ShallowTraceBuilder("parent", ResultType.SUCCESS)
        .setValue("parent value")
        .setStartNanos(0L)
        .setPendingNanos(100L)
        .setEndNanos(200L)
        .build());
    trace.addTrace(1, new ShallowTraceBuilder("child", ResultType.SUCCESS)
        .setValue("child value")
        .setStartNanos(50L)
        .setPendingNanos(75L)
        .setEndNanos(100L)
        .build());
    trace.addRelationship(0, 1, Relationship.PARENT_OF);
    assertReversible(trace);
  }

  @Test
  public void testReversibleTraceWithPredecessor() throws IOException
  {
    final MutableTrace trace = new MutableTrace();
    trace.addTrace(0, new ShallowTraceBuilder("successor", ResultType.SUCCESS)
        .setValue("successor value")
        .setStartNanos(100L)
        .setPendingNanos(150L)
        .setEndNanos(200L)
        .build());
     trace.addTrace(1, new ShallowTraceBuilder("predecessor", ResultType.SUCCESS)
         .setValue("predecessor value")
         .setStartNanos(0L)
         .setPendingNanos(50L)
         .setEndNanos(100L)
         .build());
    trace.addRelationship(0, 1, Relationship.SUCCESSOR_OF);
    assertReversible(trace);
  }

  @Test
  public void testReversibleTraceWithDiamond() throws IOException
  {
    final MutableTrace trace = new MutableTrace();
    trace.addTrace(0, new ShallowTraceBuilder("source", ResultType.SUCCESS)
                                .setValue("source value")
                                .setStartNanos(0L)
                                .setPendingNanos(25L)
                                .setEndNanos(50L)
                                .build());
    trace.addTrace(1, new ShallowTraceBuilder("left", ResultType.SUCCESS)
                                .setValue("left value")
                                .setStartNanos(50L)
                                .setPendingNanos(75L)
                                .setEndNanos(100L)
                                .build());
    trace.addTrace(2, new ShallowTraceBuilder("right", ResultType.SUCCESS)
                                .setValue("right value")
                                .setStartNanos(50L)
                                .setPendingNanos(75L)
                                .setEndNanos(100L)
                                .build());
    trace.addTrace(3, new ShallowTraceBuilder("sink", ResultType.SUCCESS)
                                .setValue("sink value")
                                .setStartNanos(100L)
                                .setPendingNanos(125L)
                                .setEndNanos(150L)
                                .build());
    trace.addRelationship(1, 0, Relationship.SUCCESSOR_OF);
    trace.addRelationship(2, 0, Relationship.SUCCESSOR_OF);
    trace.addRelationship(3, 1, Relationship.SUCCESSOR_OF);
    trace.addRelationship(3, 2, Relationship.SUCCESSOR_OF);
    assertReversible(trace);
  }

  // We use this to ensure that the building we do below is working correctly
  @Test
  public void testCustomBuilding() throws IOException
  {
    final long snapshotNanos = System.nanoTime();
    final String json = buildJson(snapshotNanos,
                                  new String[] {traceStr(1, "parent", ResultType.UNFINISHED, false),
                                                traceStr(2, "child", ResultType.UNFINISHED, false),
                                                traceStr(3, "predecessor", ResultType.UNFINISHED, false)},
                                  new String[] {hierStr(1, 2), orderStr(3, 1)});
    final Trace trace;

    try
    {
      trace = decodeString(json);
    }
    catch (IOException e)
    {
      fail("JSON parse failed. Document:\n" + json + "\nError: " + e.toString());
      return;
    }

    assertEquals(snapshotNanos, trace.getSnapshotNanos());
    assertEquals("parent", trace.getShallowTrace(1).getName());
    assertEquals(ResultType.UNFINISHED, trace.getShallowTrace(1).getResultType());
    assertEquals(Collections.singleton(Relationship.PARENT_OF), trace.getRelationships(1, 2));
    assertEquals(Collections.singleton(Relationship.SUCCESSOR_OF), trace.getRelationships(1, 3));
  }

  @Test
  public void testEmptyDocument() throws IOException
  {
    try
    {
      decodeString("{}");
      fail("Expected IOException");
    }
    catch (IOException e)
    {
      // expected case
    }
  }

  @Test
  public void testDocWitInvalidPredecessorReference() throws IOException
  {
    final String json = buildJson(System.nanoTime(),
                                  new String[]{traceStr(1, "name", ResultType.UNFINISHED, false)},
                                  new String[]{orderStr(2, 1)});
    try
    {
      decodeString(json);
      fail("Expected IOException");
    }
    catch (IOException e)
    {
      // expected case
    }
  }

  @Test
  public void testDocWitInvalidSuccessorReference()
  {
    final String json = buildJson(System.nanoTime(),
                                  new String[] {traceStr(1, "name", ResultType.UNFINISHED, false)},
                                  new String[] {orderStr(1, 2)});
    try
    {
      decodeString(json);
      fail("Expected IOException");
    }
    catch (IOException e)
    {
      // expected case
    }
  }

  @Test
  public void testDocWitInvalidChildReference() throws IOException
  {
    final String json = buildJson(System.nanoTime(),
                                  new String[]{traceStr(1, "name", ResultType.UNFINISHED, false)},
                                  new String[]{hierStr(1, 2)});
    try
    {
      decodeString(json);
      fail("Expected IOException");
    }
    catch (IOException e)
    {
      // expected case
    }
  }

  @Test
  public void testDocWitInvalidParentReference() throws IOException
  {
    final String json = buildJson(System.nanoTime(),
                                  new String[]{traceStr(1, "name", ResultType.UNFINISHED, false)},
                                  new String[]{hierStr(2, 1)});
    try
    {
      decodeString(json);
      fail("Expected IOException");
    }
    catch (IOException e)
    {
      // expected case
    }
  }

  private String buildJson(final long snapshotNanos,
                           final String[] traces,
                           final String[] related)
  {
    return "{" +
              quote(JsonTraceCodec.SNAPSHOT_NANOS) + ": " + snapshotNanos + "," +
              arrayFieldStr(JsonTraceCodec.TRACES, traces) + "," +
              arrayFieldStr(JsonTraceCodec.RELATIONSHIPS, related) +
           "}";
  }

  private String traceStr(final int id, final String name, final ResultType resultType, final boolean hidden)
  {
    return "{" +
              quote(JsonTraceCodec.TRACE_ID) + ": " + id + "," +
              quote(JsonTraceCodec.TRACE_NAME) + ": " + quote(name) + "," +
              quote(JsonTraceCodec.TRACE_HIDDEN) + ": " + hidden + "," +
              quote(JsonTraceCodec.TRACE_RESULT_TYPE) + ": " +
              quote(resultType.toString()) +
           "}";
  }

  private String hierStr(final int parentId, final int childId)
  {
    return "{" +
              quote(JsonTraceCodec.RELATIONSHIP_RELATIONSHIP) + ": " + quote(Relationship.PARENT_OF.name()) + "," +
              quote(JsonTraceCodec.RELATIONSHIP_FROM) + ": " + parentId + "," +
              quote(JsonTraceCodec.RELATIONSHIP_TO) + ": " + childId +
           "}";
  }

  private String orderStr(final int predecessorId, final int successorId)
  {
    return "{" +
              quote(JsonTraceCodec.RELATIONSHIP_RELATIONSHIP) + ": " + quote(Relationship.SUCCESSOR_OF.name()) + "," +
              quote(JsonTraceCodec.RELATIONSHIP_FROM) + ": " + successorId + "," +
              quote(JsonTraceCodec.RELATIONSHIP_TO) + ": " + predecessorId +
           "}";
  }

  private String arrayFieldStr(final String name, final String... elems)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("\"").append(name).append("\": [");
    for (int i = 0; i < elems.length; i++)
    {
      sb.append(" ").append(elems[i]);
      if (i + 1 < elems.length) {
        sb.append(",");
      }
    }
    sb.append(" ]");
    return sb.toString();
  }

  private String quote(String name)
  {
    return "\"" + name + "\"";
  }

  private Trace decodeString(final String str) throws IOException
  {
    return _codec.decode(new ByteArrayInputStream(str.getBytes()));
  }

  private void assertReversible(final Trace trace) throws IOException
  {
    assertReversibleStream(trace);
    assertReversibleString(trace);
  }

  private void assertReversibleStream(final Trace trace) throws IOException
  {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    _codec.encode(trace, baos);
    final byte[] json = baos.toByteArray();

    final Trace deserialized;
    try
    {
      deserialized = _codec.decode(new ByteArrayInputStream(json));
    }
    catch (IOException e)
    {
      fail("Exception during deserialization: " + e.getMessage() + "\n" + new String(json));
      // Make the compiler happy by claiming to return here
      return;
    }

    assertTraceGraphEquals(trace, deserialized);
  }

  private void assertReversibleString(final Trace trace) throws IOException
  {
    final String json =_codec.encode(trace);

    final Trace deserialized;
    try
    {
      deserialized = _codec.decode(json);
    }
    catch (IOException e)
    {
      fail("Exception during deserialization: " + e.getMessage() + "\n" + json);
      // Make the compiler happy by claiming to return here
      return;
    }

    assertTraceGraphEquals(trace, deserialized);
  }

  private void assertTraceGraphEquals(final Trace expected,
                                      final Trace actual)
  {

    assertEquals(new HashSet<ShallowTraceEntry>(expected.traces()), new HashSet<ShallowTraceEntry>(actual.traces()));
    assertEquals(new HashSet<RelationshipEntry>(expected.relationships()), new HashSet<RelationshipEntry>(actual.relationships()));
  }
}
