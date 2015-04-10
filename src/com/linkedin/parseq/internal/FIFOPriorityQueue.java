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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class FIFOPriorityQueue<T> {
  private final BlockingQueue<Entry<T>> _queue = new PriorityBlockingQueue<Entry<T>>();
  private final AtomicLong _sequenceNumber = new AtomicLong();

  private final int _defaultPriority;

  public FIFOPriorityQueue() {
    this(Priority.DEFAULT_PRIORITY);
  }

  public FIFOPriorityQueue(int defaultPriority) {
    _defaultPriority = defaultPriority;
  }

  public void add(T value) {
    final int priority = value instanceof Prioritizable ? ((Prioritizable) value).getPriority() : _defaultPriority;
    _queue.add(new Entry<T>(_sequenceNumber.getAndIncrement(), priority, value));
  }

  public T poll() {
    final Entry<T> entry = _queue.poll();
    return entry == null ? null : entry._value;
  }

  private static class Entry<T> implements Comparable<Entry<T>> {
    private final long _sequenceNumber;
    private final int _priority;
    private final T _value;

    private Entry(final long sequenceNumber, final int priority, final T value) {
      _sequenceNumber = sequenceNumber;
      _priority = priority;
      _value = value;
    }

    @Override
    public int compareTo(final Entry<T> o) {
      final int comp = compare(o._priority, _priority);
      return comp == 0 ? compare(_sequenceNumber, o._sequenceNumber) : comp;
    }

    private int compare(final long lhs, final long rhs) {
      if (lhs < rhs) {
        return -1;
      } else if (lhs > rhs) {
        return 1;
      }
      return 0;
    }
  }
}
