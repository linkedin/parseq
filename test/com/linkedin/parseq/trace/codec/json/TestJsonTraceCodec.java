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
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.internal.trace.TraceRelationshipBuilder;
import com.linkedin.parseq.trace.codec.TraceCodec;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
    final Trace trace = new TraceRelationshipBuilder<Integer>()
        .addTrace(0, new ShallowTraceBuilder("test", ResultType.UNFINISHED).build())
        .buildRoot();
    assertReversible(trace);
  }

  @Test
  public void testReversibleSuccessfulTrace() throws IOException
  {
    final Trace trace = new TraceRelationshipBuilder<Integer>()
        .addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
            .setValue("test value")
            .setStartNanos(0L)
            .setPendingNanos(50L)
            .setEndNanos(100L)
            .build())
        .buildRoot();
    assertReversible(trace);
  }

  @Test
  public void testReversibleSuccessfulTraceWithNullValue() throws IOException
  {
    final Trace trace = new TraceRelationshipBuilder<Integer>()
        .addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
            .setStartNanos(0L)
            .setPendingNanos(50L)
            .setEndNanos(100L)
            .build())
        .buildRoot();
    assertReversible(trace);
  }

  @Test
  public void testReversibleWithNoAttributes() throws IOException
  {
    Set<String> attributes = new HashSet<String>();

    final Trace trace = new TraceRelationshipBuilder<Integer>()
            .addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
                    .setStartNanos(0L)
                    .setPendingNanos(50L)
                    .setEndNanos(100L)
                    .build())
            .buildRoot();
    assertReversible(trace);
  }

  @Test
  public void testReversibleWithSingleAttributes() throws IOException
  {
    Set<String> attributes = new HashSet<String>();
    attributes.add("value1");
    final Trace trace = new TraceRelationshipBuilder<Integer>()
            .addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
                    .setStartNanos(0L)
                    .setPendingNanos(50L)
                    .setEndNanos(100L)
                    .addAttribute("key1", "value1")
                    .build())
            .buildRoot();
    assertReversible(trace);
  }

  @Test
  public void testReversibleWithMultipleAttributes() throws IOException
  {
    final Trace trace = new TraceRelationshipBuilder<Integer>()
            .addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
                    .setStartNanos(0L)
                    .setPendingNanos(50L)
                    .setEndNanos(100L)
                    .addAttribute("key1", "value1")
                    .addAttribute("key2", "value2")
                    .addAttribute("key3", "value3")
                    .build())
            .buildRoot();
    assertReversible(trace);
  }

  @Test
  public void testReversibleWithRemoveAttributes() throws IOException
  {
    final Trace trace = new TraceRelationshipBuilder<Integer>()
            .addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
                    .setStartNanos(0L)
                    .setPendingNanos(50L)
                    .setEndNanos(100L)
                    .addAttribute("key1", "value1")
                    .addAttribute("key2", "value1")
                    .removeAttribute("key1")
                    .removeAttribute("key2")
                    .build())
            .buildRoot();
    assertReversible(trace);
  }

  @Test
  public void testWithDupAttributes() throws IOException
  {
    final Trace trace = new TraceRelationshipBuilder<Integer>()
            .addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
                    .setStartNanos(0L)
                    .setPendingNanos(50L)
                    .setEndNanos(100L)
                    .addAttribute("key1", "value1")
                    .addAttribute("key2", "value2")
                    .addAttribute("key2", "value3")
                    .build())
            .buildRoot();

    assertReversible(trace);

  }

  @Test
  public void testWithNonExistingRemoveAttributes() throws IOException
  {
    final Trace trace = new TraceRelationshipBuilder<Integer>()
            .addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
                    .setStartNanos(0L)
                    .setPendingNanos(50L)
                    .setEndNanos(100L)
                    .removeAttribute("key1")
                    .build())
            .buildRoot();

    assertReversible(trace);
  }

  @Test
  public void testReversibleErrorTrace() throws IOException
  {
    final Trace trace = new TraceRelationshipBuilder<Integer>()
        .addTrace(0, new ShallowTraceBuilder("test", ResultType.ERROR)
            .setValue("error value")
            .setStartNanos(0L)
            .setPendingNanos(50L)
            .setEndNanos(100L)
            .build())
        .buildRoot();

    assertReversible(trace);
  }

  @Test
  public void testReversibleUnfinishedTrace() throws IOException
  {
    // If we have started a task we also must set the end time
    final Trace trace = new TraceRelationshipBuilder<Integer>()
        .addTrace(0, new ShallowTraceBuilder("test", ResultType.UNFINISHED)
            .setStartNanos(0L)
            .setPendingNanos(50L)
            .setEndNanos(100L)
            .build())
        .buildRoot();

    assertReversible(trace);
  }

  @Test
  public void testReversibleWithHiddenTrace() throws IOException
  {
    final Trace trace = new TraceRelationshipBuilder<Integer>()
            .addTrace(0, new ShallowTraceBuilder("test", ResultType.SUCCESS)
                    .setStartNanos(0L)
                    .setPendingNanos(50L)
                    .setEndNanos(100L)
                    .setHidden(true)
                    .build())
            .buildRoot();
    assertReversible(trace);
  }

  @Test
  public void testReversibleTraceWithChild() throws IOException
  {
    final Trace trace = new TraceRelationshipBuilder<Integer>()
        .addTrace(0, new ShallowTraceBuilder("parent", ResultType.SUCCESS)
            .setValue("parent value")
            .setStartNanos(0L)
            .setPendingNanos(100L)
            .setEndNanos(200L)
            .build())
        .addTrace(1, new ShallowTraceBuilder("child", ResultType.SUCCESS)
            .setValue("child value")
            .setStartNanos(50L)
            .setPendingNanos(75L)
            .setEndNanos(100L)
            .build())
        .addRelationship(Relationship.PARENT_OF, 0, 1)
        .buildRoot();
    assertReversible(trace);
  }

  @Test
  public void testReversibleTraceWithPredecessor() throws IOException
  {
    final Trace trace = new TraceRelationshipBuilder<Integer>()
        .addTrace(0, new ShallowTraceBuilder("successor", ResultType.SUCCESS)
            .setValue("successor value")
            .setStartNanos(100L)
            .setPendingNanos(150L)
            .setEndNanos(200L)
            .build())
        .addTrace(1, new ShallowTraceBuilder("predecessor", ResultType.SUCCESS)
            .setValue("predecessor value")
            .setStartNanos(0L)
            .setPendingNanos(50L)
            .setEndNanos(100L)
            .build())
        .addRelationship(Relationship.SUCCESSOR_OF, 0, 1)
        .buildRoot();

    assertReversible(trace);
  }

  @Test
  public void testReversibleTraceWithDiamond() throws IOException
  {
    final Trace trace = new TraceRelationshipBuilder<Integer>()
        .addTrace(0, new ShallowTraceBuilder("source", ResultType.SUCCESS)
              .setValue("source value")
              .setStartNanos(0L)
              .setPendingNanos(25L)
              .setEndNanos(50L)
              .build())
        .addTrace(1, new ShallowTraceBuilder("left", ResultType.SUCCESS)
            .setValue("left value")
            .setStartNanos(50L)
            .setPendingNanos(75L)
            .setEndNanos(100L)
            .build())
        .addTrace(2, new ShallowTraceBuilder("right", ResultType.SUCCESS)
            .setValue("right value")
            .setStartNanos(50L)
            .setPendingNanos(75L)
            .setEndNanos(100L)
            .build())
        .addTrace(3, new ShallowTraceBuilder("sink", ResultType.SUCCESS)
            .setValue("sink value")
            .setStartNanos(100L)
            .setPendingNanos(125L)
            .setEndNanos(150L)
            .build())
        .addRelationship(Relationship.SUCCESSOR_OF, 1, 0)
        .addRelationship(Relationship.SUCCESSOR_OF, 2, 0)
        .addRelationship(Relationship.SUCCESSOR_OF, 3, 1)
        .addRelationship(Relationship.SUCCESSOR_OF, 3, 2)
        .buildRoot();

    assertReversible(trace);
  }

  // We use this to ensure that the building we do below is working correctly
  @Test
  public void testCustomBuilding() throws IOException
  {
    final String json = buildJson(new String[] {traceStr(1, "parent", ResultType.UNFINISHED, false),
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

    assertEquals("parent", trace.getName());
    assertEquals(ResultType.UNFINISHED, trace.getResultType());

    assertEquals(2, trace.getRelated().size());

    String childName = null;
    String predecessorName = null;
    for (Related<Trace> related : trace.getRelated())
    {
      if (related.getRelationship().equals(Relationship.PARENT_OF.name()))
      {
        childName = related.getRelated().getName();
      }
      else if (related.getRelationship().equals(Relationship.SUCCESSOR_OF.name()))
      {
        predecessorName = related.getRelated().getName();
      }
    }

    assertEquals("child", childName);
    assertEquals("predecessor", predecessorName);
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
  public void testDocWithNoTraces() throws IOException
  {
    final String json = buildJson(new String[0], new String[0]);
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
  public void testDocWitInvalidPredecessorReference() throws IOException
  {
    final String json = buildJson(new String[]{traceStr(1, "name", ResultType.UNFINISHED, false)},
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
    final String json = buildJson(new String[] {traceStr(1, "name", ResultType.UNFINISHED, false)},
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
    final String json = buildJson(new String[]{traceStr(1, "name", ResultType.UNFINISHED, false)},
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
    final String json = buildJson(new String[]{traceStr(1, "name", ResultType.UNFINISHED, false)},
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

  private String buildJson(final String[] traces,
                           final String[] related)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("{")
        .append(arrayFieldStr(JsonTraceCodec.TRACES, traces)).append(",")
        .append(arrayFieldStr(JsonTraceCodec.RELATIONSHIPS, related))
      .append("}");
    return sb.toString();
  }

  private String traceStr(final int id, final String name, final ResultType resultType, final boolean hidden)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("{")
        .append(quote(JsonTraceCodec.TRACE_ID)).append(": ").append(id).append(",")
        .append(quote(JsonTraceCodec.TRACE_NAME)).append(": ").append(quote(name)).append(",")
        .append(quote(JsonTraceCodec.TRACE_HIDDEN)).append(": ").append(hidden).append(",")
        .append(quote(JsonTraceCodec.TRACE_RESULT_TYPE)).append(": ").append(quote(resultType.toString()))
        .append("}");
    return sb.toString();
  }

  private String hierStr(final int parentId, final int childId)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("{")
        .append(quote(JsonTraceCodec.RELATIONSHIP_RELATIONSHIP)).append(": ").append(quote(Relationship.PARENT_OF.name())).append(",")
        .append(quote(JsonTraceCodec.RELATIONSHIP_FROM)).append(": ").append(parentId).append(",")
        .append(quote(JsonTraceCodec.RELATIONSHIP_TO)).append(": ").append(childId)
        .append("}");
    return sb.toString();
  }

  private String orderStr(final int predecessorId, final int successorId)
  {
    final StringBuilder sb = new StringBuilder();
    sb.append("{")
        .append(quote(JsonTraceCodec.RELATIONSHIP_RELATIONSHIP)).append(": ").append(quote(Relationship.SUCCESSOR_OF.name())).append(",")
        .append(quote(JsonTraceCodec.RELATIONSHIP_FROM)).append(": ").append(successorId).append(",")
        .append(quote(JsonTraceCodec.RELATIONSHIP_TO)).append(": ").append(predecessorId)
      .append("}");
    return sb.toString();
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

    assertEquals(trace, deserialized);
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

    assertEquals(trace, deserialized);
  }
}
