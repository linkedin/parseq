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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.fail;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TestShallowTraceBuilder
{
  @Test
  public void testEarlyFinishWithValue()
  {
    final ShallowTraceBuilder builder = new ShallowTraceBuilder("test", ResultType.EARLY_FINISH);
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
    final ShallowTraceBuilder builder = new ShallowTraceBuilder("test", ResultType.UNFINISHED);
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
    final ShallowTraceBuilder builder = new ShallowTraceBuilder("test", ResultType.SUCCESS);
    builder.setValue("value");
    builder.setStartNanos(123L);
    builder.setPendingNanos(234L);
    builder.setEndNanos(345L);

    ShallowTraceBuilder builderCopy = new ShallowTraceBuilder(builder);
    assertEquals(builder.build(), builderCopy.build());

    builderCopy = new ShallowTraceBuilder(builder)
        .setName("not-test");
    assertFalse(builder.build().equals(builderCopy.build()));

    builderCopy = new ShallowTraceBuilder(builder)
        .setValue(null);
    assertFalse(builder.build().equals(builderCopy.build()));


    builderCopy = new ShallowTraceBuilder(builder)
        .setResultType(ResultType.ERROR);
    assertFalse(builder.build().equals(builderCopy.build()));
  }
}
