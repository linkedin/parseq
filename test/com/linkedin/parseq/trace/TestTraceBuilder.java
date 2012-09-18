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

import org.testng.annotations.Test;

import java.util.Collections;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.fail;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TestTraceBuilder
{
  @Test
  public void testBuildEmptyTrace()
  {
    try
    {
      new TraceRelationshipBuilder<Integer>().buildRoot();
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException e)
    {
      // Expected case
    }
  }

  @Test
  public void testAddTrace()
  {
    final ShallowTrace shallowTrace = new ShallowTraceBuilder("root1", ResultType.UNFINISHED).build();
    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();
    builder.addTrace(0, shallowTrace);
    assertEquals(shallowTrace, builder.getTrace(0));
  }

  @Test
  public void testAddTraceTwice()
  {
    final ShallowTrace shallowTrace = new ShallowTraceBuilder("root1", ResultType.UNFINISHED).build();
    final ShallowTrace shallowTrace2 = new ShallowTraceBuilder("root1", ResultType.UNFINISHED).build();
    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();
    builder.addTrace(0, shallowTrace);

    try
    {
      builder.addTrace(0, shallowTrace);
      fail("Should have thrown IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }

  @Test
  public void testBuildTraceWithTwoRoots()
  {
    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();
    builder.addTrace(0, new ShallowTraceBuilder("root1", ResultType.UNFINISHED).build());
    builder.addTrace(1, new ShallowTraceBuilder("root2", ResultType.UNFINISHED).build());

    try
    {
      builder.buildRoot();
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException e)
    {
      // Expected case
    }
  }

  @Test
  public void testBuildTraceByKeyWithTwoRoots()
  {
    final ShallowTrace root1 = new ShallowTraceBuilder("root1", ResultType.UNFINISHED).build();
    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();
    builder.addTrace(0, root1);
    builder.addTrace(1, new ShallowTraceBuilder("root2", ResultType.UNFINISHED).build());

    assertEquals(root1.getName(), builder.buildTrace(0).getName());
  }

  @Test
  public void testBuildTraceWithCycle()
  {
    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();
    builder.addTrace(0, new ShallowTraceBuilder("root1", ResultType.UNFINISHED).build());
    builder.addTrace(1, new ShallowTraceBuilder("child1", ResultType.UNFINISHED).build());
    builder.addTrace(2, new ShallowTraceBuilder("child2", ResultType.UNFINISHED).build());
    builder.addRelationship(Relationship.PARENT_OF, 0, 1);
    builder.addRelationship(Relationship.PARENT_OF, 0, 2);
    builder.addRelationship(Relationship.SUCCESSOR_OF, 1, 2);
    builder.addRelationship(Relationship.SUCCESSOR_OF, 2, 1);

    try
    {
      builder.buildRoot();
      fail("Expected IllegalStateException");
    }
    catch (IllegalStateException e)
    {
      // Expected case
    }
  }

  @Test
  public void testReplaceTrace()
  {
    final ShallowTrace trace = new ShallowTraceBuilder("trace", ResultType.UNFINISHED).build();
    final ShallowTrace replacement = new ShallowTraceBuilder("replacement", ResultType.UNFINISHED).build();

    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();
    builder.addTrace(0, trace);
    builder.replaceTrace(0, replacement);

    assertEquals(replacement.getName(), builder.buildRoot().getName());
  }

  @Test
  public void testReplaceNonExistentTrace()
  {
    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();

    try
    {
      builder.replaceTrace(0, new ShallowTraceBuilder("replacement", ResultType.UNFINISHED).build());
      fail("Should have thrown IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }

  @Test
  public void testAddRelationship()
  {
    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();
    builder.addTrace(0, new ShallowTraceBuilder("root1", ResultType.UNFINISHED).build());
    builder.addTrace(1, new ShallowTraceBuilder("root2", ResultType.UNFINISHED).build());


    builder.addRelationship(Relationship.PARENT_OF, 0, 1);

    assertEquals(Collections.singleton(new Related<Integer>(Relationship.PARENT_OF, 1)), builder.getRelationships(0));
    assertEquals(Collections.emptySet(), builder.getRelationships(1));
  }

  @Test
  public void testContainsRelationship()
  {
    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();
    builder.addTrace(0, new ShallowTraceBuilder("root1", ResultType.UNFINISHED).build());
    builder.addTrace(1, new ShallowTraceBuilder("root2", ResultType.UNFINISHED).build());
    builder.addTrace(2, new ShallowTraceBuilder("root3", ResultType.UNFINISHED).build());

    builder.addRelationship(Relationship.PARENT_OF, 0, 1);
    builder.addRelationship(Relationship.POSSIBLE_SUCCESSOR_OF, 1, 2);

    assertTrue(builder.containsRelationship(Relationship.PARENT_OF.name(), 0, 1));
    assertTrue(builder.containsRelationship(Relationship.POSSIBLE_SUCCESSOR_OF.name(), 1, 2));
    assertFalse(builder.containsRelationship(Relationship.POTENTIAL_PARENT_OF.name(), 0, 1));
    assertFalse(builder.containsRelationship(Relationship.SUCCESSOR_OF.name(), 0, 1));
    assertFalse(builder.containsRelationship(Relationship.PARENT_OF.name(), 2, 1));
    assertFalse(builder.containsRelationship(Relationship.SUCCESSOR_OF.name(), 1, 2));
  }

  @Test
  public void testRemoveRelationship()
  {
    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();
    builder.addTrace(0, new ShallowTraceBuilder("root1", ResultType.UNFINISHED).build());
    builder.addTrace(1, new ShallowTraceBuilder("root2", ResultType.UNFINISHED).build());
    builder.addRelationship(Relationship.PARENT_OF, 0, 1);
    builder.removeRelationship(Relationship.PARENT_OF, 0, 1);

    assertEquals(Collections.emptySet(), builder.buildTrace(0).getRelated());
  }

  @Test
  public void testRemoveNonExistentRelationship()
  {
    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();
    builder.addTrace(0, new ShallowTraceBuilder("root1", ResultType.UNFINISHED).build());
    builder.addTrace(1, new ShallowTraceBuilder("root2", ResultType.UNFINISHED).build());

    try
    {
      builder.removeRelationship(Relationship.PARENT_OF, 0, 1);
      fail("Should have thrown IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }

  @Test
  public void testRemovePreviouslyRemovedRelationship()
  {
    final TraceRelationshipBuilder<Integer> builder = new TraceRelationshipBuilder<Integer>();
    builder.addTrace(0, new ShallowTraceBuilder("root1", ResultType.UNFINISHED).build());
    builder.addTrace(1, new ShallowTraceBuilder("root2", ResultType.UNFINISHED).build());
    builder.addRelationship(Relationship.PARENT_OF, 0, 1);
    builder.removeRelationship(Relationship.PARENT_OF, 0, 1);

    try
    {
      builder.removeRelationship(Relationship.PARENT_OF, 0, 1);
      fail("Should have thrown IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }
}
