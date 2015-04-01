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

import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.TraceBuilder;

import java.util.concurrent.TimeUnit;

/**
 * A context provides an API to {@link Task}s for the purpose of scheduling
 * other tasks. Each task gets its own context, but contexts are hierarchical
 * such that any state changes made from this context are visible to other
 * contexts in the hierarchy.
 * <p>
 * If a task finished while it still has pending timers or tasks, those
 * timers and tasks will be cancelled - they are guaranteed not to execute.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public interface Context
{
  /**
   * Creates a timer that will invoke the given task if the calling task has
   * not yet finished.
   *
   * @param time the time for the timeout
   * @param unit the unit of the time
   * @param task the task to run if the timeout expires
   * @return a handle that can be used to explicitly cancel the timer
   */
  Cancellable createTimer(long time, TimeUnit unit, Task<?> task);

  /**
   * Runs one or more tasks in parallel.
   *
   * @param tasks the tasks to run
   */
  void run(Task<?>... tasks);

  /**
   * TODO
   * @param task
   * @param rootTask
   */
  void runSubTask(Task<?> task, Task<?> rootTask);

  /**
   * Provides a mechanism of ordering the execution of some child tasks after
   * the resolution of the given promises (which may also be tasks).
   *
   * @param promises the promises that must be resolved before executing
   *                 tasks from {@link After}
   * @return a handle that can be used to schedule tasks that will be executed
   *         after the given promises are resolved.
   */
  After after(Promise<?>... promises);

  /**
   * Provides a mechanism for "plugins" to generate data which should be passed in via the context.
   * The intent is that the "plugin" registers engine specific data it needed via the engine builder.
   * Then the run method can access this data via the getEngineProperty() method.
   *
   * @param key The key to the engine related property which the Task will access.
   * @return The engine related property which has been stored with this key.
   */
  Object getEngineProperty(String key);

  ShallowTraceBuilder getShallowTraceBuilder();

  TraceBuilder getTraceBuilder();

  Long getPlanId();

  TaskLogger getTaskLogger();
}
