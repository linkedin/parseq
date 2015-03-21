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

package com.linkedin.parseq.trace;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import com.linkedin.parseq.internal.IdGenerator;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestTraceBuilder
{

  @Test
  public void testAddRelationship()
  {
    final ShallowTraceBuilder trace1 = new ShallowTraceBuilder(IdGenerator.getNextId())
      .setName("task1")
      .setResultType(ResultType.UNFINISHED);
    final ShallowTraceBuilder trace2 = new ShallowTraceBuilder(IdGenerator.getNextId())
    .setName("task2")
    .setResultType(ResultType.UNFINISHED);
    final TraceBuilder builder = new TraceBuilder(1024);
    builder.addRelationship(Relationship.SUCCESSOR_OF, trace1, trace2);
    Trace trace = builder.build();
    assertEquals(trace1.build(), trace.getTraceMap().get(trace1.getId()));
    assertEquals(trace2.build(), trace.getTraceMap().get(trace2.getId()));
    assertEquals(1, trace.getRelationships().size());
    assertTrue(trace.getRelationships().contains(new TraceRelationship(trace1.getId(), trace2.getId(), Relationship.SUCCESSOR_OF)));
  }

  @Test
  public void testAddRelationshipTwice()
  {
    final ShallowTraceBuilder trace1 = new ShallowTraceBuilder(IdGenerator.getNextId())
    .setName("task1")
    .setResultType(ResultType.UNFINISHED);
    final ShallowTraceBuilder trace2 = new ShallowTraceBuilder(IdGenerator.getNextId())
    .setName("task2")
    .setResultType(ResultType.UNFINISHED);
    final TraceBuilder builder = new TraceBuilder(1024);
    builder.addRelationship(Relationship.SUCCESSOR_OF, trace1, trace2);
    builder.addRelationship(Relationship.SUCCESSOR_OF, trace1, trace2);
    Trace trace = builder.build();
    assertEquals(trace1.build(), trace.getTraceMap().get(trace1.getId()));
    assertEquals(trace2.build(), trace.getTraceMap().get(trace2.getId()));
    assertEquals(1, trace.getRelationships().size());
    assertTrue(trace.getRelationships().contains(new TraceRelationship(trace1.getId(), trace2.getId(), Relationship.SUCCESSOR_OF)));
  }
}
