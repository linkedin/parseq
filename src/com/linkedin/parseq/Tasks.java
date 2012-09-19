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

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * This class provides a set of factory methods for create common
 * {@link Task}s.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class Tasks
{
  private Tasks() {}

  /**
   * Creates a new {@link Task} that have a value of type Void. Because the
   * returned task has no value, it is typically used to produce side-effects.
   *
   * @param name a name that describes the action
   * @param runnable the action that will be executed when the task is run
   * @return the new task
   */
  public static Task<Void> action(final String name, final Runnable runnable)
  {
    return new ActionTask(name, runnable);
  }

  /**
   * Creates a new {@link Task} that's value will be set to the value returned
   * from the supplied callable. This task is useful when doing basic
   * computation that does not require asynchrony. It is not appropriate for
   * long running or blocking tasks.
   *
   * @param name a name that describes the action
   * @param callable the callable to execute when this task is run
   * @param <T> the type of the return value for this task
   * @return the new task
   */
  public static <T> Task<T> callable(final String name,
                                     final Callable<? extends T> callable)
  {
    return new CallableTask<T>(name, callable);
  }

  /**
   * Creates a new task that, when run, will run each of the supplied tasks in
   * order. The value of this new task is the value of the last task in the
   * sequence.
   *
   * @param tasks the tasks to run sequentially
   * @param <T> the result value for the sequence of tasks
   * @return a new task that will run the given tasks sequentially
   */
  public static <T> Task<T> seq(final Task<?>... tasks)
  {
    return seq(Arrays.asList(tasks));
  }

  /**
   * Creates a new task that, when run, will run each of the supplied tasks in
   * order. The value of this new task is the value of the last task in the
   * sequence.
   *
   * @param tasks the tasks to run sequentially
   * @param <T> the result value for the sequence of tasks
   * @return a new task that will run the given tasks sequentially
   */
  public static <T> Task<T> seq(final Iterable<? extends Task<?>> tasks)
  {
    return new SeqTask<T>("seq", tasks);
  }

  /**
   * Creates a new task that, when run, will run each of the supplied tasks in
   * parallel. The new task does not specify the type for its values. To get a
   * type safe list of values use {@link #par}.
   *
   * @param tasks the tasks to run in parallel
   * @return a new task that will run the given tasks in parallel
   */
  public static ParTask<?> par(final Task<?>... tasks)
  {
    //Strip the type info. Read method comment.
    @SuppressWarnings("unchecked")
    final ParTask<?> par = new ParTaskImpl<Object>("par", Arrays.asList(tasks));
    return par;
  }

  /**
   * Creates a new task that, when run, will run each of the supplied tasks in
   * parallel. The new task will return the set of result from the tasks.
   * The results ordering will not match the ordering of the tasks if any
   * failure has occurred.
   *
   * @param tasks the tasks to run in parallel
   * @return The results of the tasks
   */
  public static <T> ParTask<T> par(final Iterable<? extends Task<? extends T>> tasks)
  {
    return new ParTaskImpl<T>("par", tasks);
  }

  /**
   * Creates a new task that wraps the given task. If the given task finishes
   * before the timeout occurs then this task takes on the value of the task.
   * If the task does not complete in the given time then this task will
   * have a TimeoutException. The wrapped task may be cancelled when a timeout
   * occurs.
   *
   * @param time the time to wait before timing out
   * @param unit the units for the time
   * @param task the task to wrap
   * @param <T> the value type for the task
   * @return the new timeout task
   */
  public static <T> Task<T> timeoutWithError(final long time, final TimeUnit unit,
                                             final Task<T> task)
  {
    return timeoutWithError("timeoutWithError", time, unit, task);
  }

  /**
   * This method is similar to {@link #timeoutWithError(long, java.util.concurrent.TimeUnit, Task)},
   * but it also takes a name that will be set on the returned task.
   *
   * @param name the name of this task
   * @param time the time to wait before timing out
   * @param unit the units for the time
   * @param task the task to wrap
   * @param <T> the value type for the task
   * @return the new timeout task
   * @see #timeoutWithError(long, java.util.concurrent.TimeUnit, Task)
   */
  public static <T> Task<T> timeoutWithError(final String name,
                                             final long time, final TimeUnit unit,
                                             final Task<T> task)
  {
    return new TimeoutWithErrorTask<T>(name, time, unit, task);
  }
}
