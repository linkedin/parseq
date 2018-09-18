/*
 * Copyright 2017 LinkedIn, Inc
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

import java.util.concurrent.ConcurrentLinkedDeque;

import com.linkedin.parseq.Priority;
import com.linkedin.parseq.internal.SerialExecutor.TaskQueue;

/**
 * {@link TaskQueue} implementation that recognizes only two classes of priorities:
 * <ul>
 * <li>greater than {@link Priority#DEFAULT_PRIORITY}</li>
 * <li>less or equal to {@link Priority#DEFAULT_PRIORITY}</li>
 * </ul>
 * Elements from first class are polled in FIFO order while elements from second class are polled
 * in LIFO order. The rationale behind it is that elements with highest priorities will be mostly
 * timeouts and we want to minimize variance in difference between time at which they were supposed
 * to run and time they actually run. We poll default and lower priority elements using LIFO in order
 * to improve memory cache locality: tasks scheduled most recently for execution are most likely to still
 * be in cache.
 * <p>
 * It is a faster implementation of {@link TaskQueue} than {@link FIFOPriorityQueue}
 * that can be used when tasks do not use priorities in general.
 * <p>
 * This queue supports two classes of priorities because {@code Priority.MAX_PRIORITY}
 * is used by timeouts.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
public class LIFOBiPriorityQueue<T extends Prioritizable> implements SerialExecutor.TaskQueue<T> {

  private final ConcurrentLinkedDeque<T> _highPriority = new ConcurrentLinkedDeque<>();
  private final ConcurrentLinkedDeque<T> _lowPriority = new ConcurrentLinkedDeque<>();

  public void add(T value) {
    if (value.getPriority() > Priority.DEFAULT_PRIORITY) {
      _highPriority.addLast(value);
    } else {
      _lowPriority.addFirst(value);
    }
  }

  public T poll() {
    T highPriority = _highPriority.pollFirst();
    return (highPriority != null) ? highPriority : _lowPriority.pollFirst();
  }
}
