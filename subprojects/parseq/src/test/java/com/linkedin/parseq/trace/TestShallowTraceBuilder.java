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

import static org.testng.AssertJUnit.*;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestShallowTraceBuilder {
  @Test
  public void testEarlyFinishWithValue() {
    final ShallowTraceBuilder builder = new ShallowTraceBuilder(IdGenerator.getNextId());
    builder.setName("test");
    builder.setResultType(ResultType.EARLY_FINISH);
    builder.setValue("non-null-value");

    try {
      builder.build();
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Expected case
    }
  }

  @Test
  public void testUninishedWithValue() {
    final ShallowTraceBuilder builder = new ShallowTraceBuilder(IdGenerator.getNextId());
    builder.setName("test");
    builder.setResultType(ResultType.UNFINISHED);
    builder.setValue("non-null-value");

    try {
      builder.build();
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Expected case
    }
  }

  @Test
  public void testCopyconstructor() {
    final ShallowTraceBuilder builder = new ShallowTraceBuilder(IdGenerator.getNextId());
    builder.setName("test");
    builder.setResultType(ResultType.SUCCESS);
    builder.setValue("value");
    builder.setNativeStartNanos(123L);
    builder.setNativePendingNanos(234L);
    builder.setNativeEndNanos(345L);
    builder.addAttribute("test", "value");
    final ShallowTraceBuilder copied = new ShallowTraceBuilder(builder.build());
    assertEquals(builder.build(), copied.build());
  }

  @Test
  public void testEquals() {
    final ShallowTraceBuilder builder = new ShallowTraceBuilder(100L);
    builder.setName("test");
    builder.setResultType(ResultType.SUCCESS);
    builder.setValue("value");
    builder.setNativeStartNanos(123L);
    builder.setNativePendingNanos(234L);
    builder.setNativeEndNanos(345L);

    ShallowTraceBuilder builderCopy = new ShallowTraceBuilder(100L);
    builderCopy.setName("test");
    builderCopy.setResultType(ResultType.SUCCESS);
    builderCopy.setValue("value");
    builderCopy.setNativeStartNanos(123L);
    builderCopy.setNativePendingNanos(234L);
    builderCopy.setNativeEndNanos(345L);
    assertEquals(builder.build(), builderCopy.build());

    builderCopy = new ShallowTraceBuilder(100L);
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
    builderCopy.setNativeStartNanos(123L);
    builderCopy.setNativePendingNanos(234L);
    builderCopy.setNativeEndNanos(345L);
    assertFalse(builder.build().equals(builderCopy.build()));

    builderCopy = new ShallowTraceBuilder(100L);
    builderCopy.setName("test");
    builderCopy.setResultType(ResultType.SUCCESS);
    builderCopy.setNativeStartNanos(123L);
    builderCopy.setNativePendingNanos(234L);
    builderCopy.setNativeEndNanos(345L);
    assertFalse(builder.build().equals(builderCopy.build()));

    builderCopy = new ShallowTraceBuilder(100L);
    builderCopy.setName("no-test");
    builderCopy.setResultType(ResultType.SUCCESS);
    builderCopy.setValue("value");
    builderCopy.setNativeStartNanos(123L);
    builderCopy.setNativePendingNanos(234L);
    builderCopy.setNativeEndNanos(345L);
    builderCopy.setResultType(ResultType.ERROR);
    assertFalse(builder.build().equals(builderCopy.build()));
  }

  @Test
  public void testDeprecatedTimeMethods() {
    ShallowTraceBuilder builder = new ShallowTraceBuilder((Long) null);
    assertNull(builder.getId());
    assertNull(builder.getStartNanos());
    assertNull(builder.getEndNanos());
    assertNull(builder.getPendingNanos());

    builder = new ShallowTraceBuilder(new Long(1L));
    assertEquals(builder.getId(), new Long(1L));
    builder.setStartNanos(null);
    assertEquals(builder.getNativeStartNanos(), -1L);
    builder.setEndNanos(null);
    assertEquals(builder.getNativeEndNanos(), -1L);
    builder.setPendingNanos(null);
    assertEquals(builder.getNativePendingNanos(), -1L);

    builder.setNativeStartNanos(1L);
    assertEquals(builder.getStartNanos(), new Long(1L));
    builder.setNativeEndNanos(1L);
    assertEquals(builder.getEndNanos(), new Long(1L));
    builder.setNativePendingNanos(1L);
    assertEquals(builder.getPendingNanos(), new Long(1L));

    builder.setStartNanos(new Long(1L));
    assertEquals(builder.getStartNanos(), new Long(1L));
    assertEquals(builder.getNativeStartNanos(), 1L);
    builder.setEndNanos(new Long(1L));
    assertEquals(builder.getEndNanos(), new Long(1L));
    assertEquals(builder.getNativeEndNanos(), 1L);
    builder.setPendingNanos(new Long(1L));
    assertEquals(builder.getPendingNanos(), new Long(1L));
    assertEquals(builder.getNativePendingNanos(), 1L);
  }
}
