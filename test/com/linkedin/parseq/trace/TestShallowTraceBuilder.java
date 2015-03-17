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

import com.linkedin.parseq.internal.IdGenerator;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.fail;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestShallowTraceBuilder
{
  @Test
  public void testEarlyFinishWithValue()
  {
    final ShallowTraceBuilder builder = new ShallowTraceBuilder(IdGenerator.getNextId());
    builder.setName("test");
    builder.setResultType(ResultType.EARLY_FINISH);
    builder.setValue("non-null-value");

    try
    {
      builder.build();
      fail("Expected IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }

  @Test
  public void testUninishedWithValue()
  {
    final ShallowTraceBuilder builder = new ShallowTraceBuilder(IdGenerator.getNextId());
    builder.setName("test");
    builder.setResultType(ResultType.UNFINISHED);
    builder.setValue("non-null-value");

    try
    {
      builder.build();
      fail("Expected IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }

  @Test
  public void testEquals()
  {
    final ShallowTraceBuilder builder = new ShallowTraceBuilder(100L);
    builder.setName("test");
    builder.setResultType(ResultType.SUCCESS);
    builder.setValue("value");
    builder.setStartNanos(123L);
    builder.setPendingNanos(234L);
    builder.setEndNanos(345L);

    ShallowTraceBuilder builderCopy = new ShallowTraceBuilder(100L);
    builderCopy.setName("test");
    builderCopy.setResultType(ResultType.SUCCESS);
    builderCopy.setValue("value");
    builderCopy.setStartNanos(123L);
    builderCopy.setPendingNanos(234L);
    builderCopy.setEndNanos(345L);
    assertEquals(builder.build(), builderCopy.build());

    builderCopy = new ShallowTraceBuilder(100L);
    builderCopy.setName("no-test");
    builderCopy.setResultType(ResultType.SUCCESS);
    builderCopy.setValue("value");
    builderCopy.setStartNanos(123L);
    builderCopy.setPendingNanos(234L);
    builderCopy.setEndNanos(345L);
    assertFalse(builder.build().equals(builderCopy.build()));

    builderCopy = new ShallowTraceBuilder(100L);
    builderCopy.setName("test");
    builderCopy.setResultType(ResultType.SUCCESS);
    builderCopy.setStartNanos(123L);
    builderCopy.setPendingNanos(234L);
    builderCopy.setEndNanos(345L);
    assertFalse(builder.build().equals(builderCopy.build()));


    builderCopy = new ShallowTraceBuilder(100L);
    builderCopy.setName("no-test");
    builderCopy.setResultType(ResultType.SUCCESS);
    builderCopy.setValue("value");
    builderCopy.setStartNanos(123L);
    builderCopy.setPendingNanos(234L);
    builderCopy.setEndNanos(345L);
    builderCopy.setResultType(ResultType.ERROR);
    assertFalse(builder.build().equals(builderCopy.build()));
  }
}
