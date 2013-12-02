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

package com.linkedin.parseq;

import com.linkedin.parseq.internal.TaskListener;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.Trace;

/**
 * A task represents a deferred execution that also contains its resulting
 * value. In addition, tasks include some tracing information that can be
 * used with various trace printers.
 * <p/>
 * Tasks should generally be run using either an {@link Engine} or a
 * {@link Context}. They should not be run directly.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public interface Task<T> extends Promise<T>, Cancellable
{
  /**
   * Returns the name of this task. If no name was set during construction
   * this method will return the value of {@link #toString()}. In most
   * cases it is preferable to explicitly set a name.
   */
  String getName();

  /**
   * Returns the priority for this task.
   *
   * @return the priority for this task.
   */
  int getPriority();

  /**
   * Overrides the priority for this task. Higher priority tasks will be
   * executed before lower priority tasks in the same context. In most cases,
   * the default priority is sufficient.
   * <p/>
   * The default priority is 0. Use {@code priority < 0} to make a task
   * lower priority and {@code priority > 0} to make a task higher
   * priority.
   * <p/>
   * If the task has already started execution the priority cannot be
   * changed.
   *
   * @param priority the new priority for the task.
   * @return {@code true} if the priority was be set; otherwise {@code false}.
   * @throws IllegalArgumentException if the priority is out of range
   * @see Priority
   */
  boolean setPriority(int priority);

  /**
   * Returns a trace for this task alone. The trace is a point-in-time
   * snapshot, so successive invocations of {@code getShallowTrace} may
   * return different results.
   */
  ShallowTrace getShallowTrace();

  /**
   * Returns the trace for this task. The trace will be a point-in-time snapshot
   * and may change over time until the task is completed.
   *
   * @return the trace related to this task
   */
  Trace getTrace();

  /**
   * Attempts to bind this task to the given context. A task may only be bound
   * once. This method is reserved for use by the ParSeq framework only.
   *
   * @param context the context to assign to this task.
   * @return {@code true} if the assignment was successful.
   */
  boolean assignContext(Context context);

  /**
   * Attempts to run the task with the given context. This method is for use by
   * the ParSeq framework only.
   */
  void contextRun();

  /**
   * Adds a listener to this task that is notified each time the task changes
   * state. This method is for use by the ParSeq framework only.
   */
  void addTaskListener(TaskListener tracingTaskListener);
}
