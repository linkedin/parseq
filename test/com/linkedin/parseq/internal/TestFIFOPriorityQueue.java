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

package com.linkedin.parseq.internal;

import com.linkedin.parseq.Priority;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TestFIFOPriorityQueue {
  @Test
  public void testPollOnEmpty() {
    final FIFOPriorityQueue<?> queue = new FIFOPriorityQueue<>();
    assertNull(queue.poll());
  }

  @Test
  public void testPollOnUnprioritizedSequence() {
    final FIFOPriorityQueue<PrioritizableInt> queue = new FIFOPriorityQueue<>();
    queue.add(new PrioritizableInt(1));
    queue.add(new PrioritizableInt(2));
    queue.add(new PrioritizableInt(3));

    assertEquals(1, queue.poll().getValue());
    assertEquals(2, queue.poll().getValue());
    assertEquals(3, queue.poll().getValue());
    assertNull(queue.poll());
  }

  @Test
  public void testPollWithPriority() {
    final FIFOPriorityQueue<PrioritizableInt> queue = new FIFOPriorityQueue<>();
    queue.add(new PrioritizableInt(-5, 1));
    queue.add(new PrioritizableInt(10, 2));
    queue.add(new PrioritizableInt(0, 3));

    assertEquals(2, queue.poll().getValue());
    assertEquals(3, queue.poll().getValue());
    assertEquals(1, queue.poll().getValue());
  }

  @Test
  public void testPollWithOverlappingPriorities() {
    final FIFOPriorityQueue<PrioritizableInt> queue = new FIFOPriorityQueue<>();
    queue.add(new PrioritizableInt(-5, 1));
    queue.add(new PrioritizableInt(10, 2));
    queue.add(new PrioritizableInt(0, 3));
    queue.add(new PrioritizableInt(10, 4));
    queue.add(new PrioritizableInt(0, 5));
    queue.add(new PrioritizableInt(-5, 6));

    assertEquals(2, queue.poll().getValue());
    assertEquals(4, queue.poll().getValue());
    assertEquals(3, queue.poll().getValue());
    assertEquals(5, queue.poll().getValue());
    assertEquals(1, queue.poll().getValue());
    assertEquals(6, queue.poll().getValue());
  }

  @Test
  public void testPollWithDefaultPriority() {
    final FIFOPriorityQueue<PrioritizableInt> queue = new FIFOPriorityQueue<>();
    queue.add(new PrioritizableInt(-5, 1));
    queue.add(new PrioritizableInt(10, 2));
    queue.add(new PrioritizableInt(3));

    assertEquals(2, queue.poll().getValue());
    assertEquals(3, queue.poll().getValue());
    assertEquals(1, queue.poll().getValue());
  }

  private static class Int {
    private final int _value;

    private Int(final int value) {
      _value = value;
    }

    public int getValue() {
      return _value;
    }
  }

  private static class PrioritizableInt extends Int implements Prioritizable {
    private final int _priority;

    private PrioritizableInt(final int value) {
      this(Priority.DEFAULT_PRIORITY, value);
    }

    private PrioritizableInt(final int priority, final int value) {
      super(value);
      _priority = priority;
    }

    public int getPriority() {
      return _priority;
    }
  }
}
