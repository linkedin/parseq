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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


/**
 * This class provides a set of factory methods for create common
 * {@link Task}s.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class Tasks {
  private Tasks() {
  }

  /**
   * Creates a new {@link Task} that have a value of type Void. Because the
   * returned task has no value, it is typically used to produce side-effects.
   *
   * @deprecated  As of 2.0.0, replaced by {@link Task#action(String, com.linkedin.parseq.function.Action) Task.action}
   * @param name a name that describes the action
   * @param runnable the action that will be executed when the task is run
   * @return the new task
   * @see Task#action(String, com.linkedin.parseq.function.Action) Task.action
   */
  @Deprecated
  public static Task<Void> action(final String name, final Runnable runnable) {
    return Task.action(name, runnable::run);
  }

  /**
   * Creates a new {@link Task} that's value will be set to the value returned
   * from the supplied callable. This task is useful when doing basic
   * computation that does not require asynchrony. It is not appropriate for
   * long running or blocking tasks.
   *
   * @deprecated  As of 2.0.0, replaced by {@link Task#callable(String, Callable) Task.callable}
   * @param name a name that describes the action
   * @param callable the callable to execute when this task is run
   * @param <T> the type of the return value for this task
   * @return the new task
   * @see Task#callable(String, Callable) Task.callable
   */
  @Deprecated
  public static <T> Task<T> callable(final String name, final Callable<? extends T> callable) {
    return Task.callable(name, () -> callable.call());
  }

  /**
   * Creates a new {@link Task} that's value will be set to the value returned
   * from the supplied callable. This task is useful when doing basic
   * computation that does not require asynchrony. It is not appropriate for
   * long running or blocking tasks.
   *
   * @deprecated  As of 2.0.0, replaced by {@link Task#callable(String, Callable) Task.callable}
   * @param name a name that describes the action
   * @param callable the callable to execute when this task is run
   * @param <T> the type of the return value for this task
   * @return the new task
   * @see Task#callable(String, Callable) Task.callable
   */
  @Deprecated
  public static <T> Task<T> callable(final String name, final ThrowableCallable<? extends T> callable) {
    return new CallableTask<T>(name, callable);
  }

  /**
   * Creates a new task that will run the given tasks sequentially (e.g.
   * task1 will be finished before task2 starts). The value of the seq task will
   * be the result of the last task in the sequence.
   *
   * @deprecated  As of 2.0.0, replaced by {@link Task#map(String, com.linkedin.parseq.function.Function1) Task.map},
   * {@link Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap},
   * {@link Task#andThen(String, Task) Task.andThen} and other methods in {@link Task}.
   * @see Task#map(String, com.linkedin.parseq.function.Function1) Task.map
   * @see Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap
   * @see Task#andThen(String, Task) Task.andThen
   * @see Task
   */
  @Deprecated
  public static <T> Task<T> seq(Task<?> task1, Task<T> task2) {
    return vaseq(task1, task2);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#map(String, com.linkedin.parseq.function.Function1) Task.map},
   * {@link Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap},
   * {@link Task#andThen(String, Task) Task.andThen} and other methods in {@link Task}.
   * @see Task#map(String, com.linkedin.parseq.function.Function1) Task.map
   * @see Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap
   * @see Task#andThen(String, Task) Task.andThen
   * @see Task
   */
  @Deprecated
  public static <T> Task<T> seq(Task<?> task1, Task<?> task2, Task<T> task3) {
    return vaseq(task1, task2, task3);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#map(String, com.linkedin.parseq.function.Function1) Task.map},
   * {@link Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap},
   * {@link Task#andThen(String, Task) Task.andThen} and other methods in {@link Task}.
   * @see Task#map(String, com.linkedin.parseq.function.Function1) Task.map
   * @see Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap
   * @see Task#andThen(String, Task) Task.andThen
   * @see Task
   */
  @Deprecated
  public static <T> Task<T> seq(Task<?> task1, Task<?> task2, Task<?> task3, Task<T> task4) {
    return vaseq(task1, task2, task3, task4);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#map(String, com.linkedin.parseq.function.Function1) Task.map},
   * {@link Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap},
   * {@link Task#andThen(String, Task) Task.andThen} and other methods in {@link Task}.
   * @see Task#map(String, com.linkedin.parseq.function.Function1) Task.map
   * @see Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap
   * @see Task#andThen(String, Task) Task.andThen
   * @see Task
   */
  @Deprecated
  public static <T> Task<T> seq(Task<?> task1, Task<?> task2, Task<?> task3, Task<?> task4, Task<T> task5) {
    return vaseq(task1, task2, task3, task4, task5);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#map(String, com.linkedin.parseq.function.Function1) Task.map},
   * {@link Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap},
   * {@link Task#andThen(String, Task) Task.andThen} and other methods in {@link Task}.
   * @see Task#map(String, com.linkedin.parseq.function.Function1) Task.map
   * @see Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap
   * @see Task#andThen(String, Task) Task.andThen
   * @see Task
   */
  @Deprecated
  public static <T> Task<T> seq(Task<?> task1, Task<?> task2, Task<?> task3, Task<?> task4, Task<?> task5,
      Task<T> task6) {
    return vaseq(task1, task2, task3, task4, task5, task6);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#map(String, com.linkedin.parseq.function.Function1) Task.map},
   * {@link Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap},
   * {@link Task#andThen(String, Task) Task.andThen} and other methods in {@link Task}.
   * @see Task#map(String, com.linkedin.parseq.function.Function1) Task.map
   * @see Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap
   * @see Task#andThen(String, Task) Task.andThen
   * @see Task
   */
  @Deprecated
  public static <T> Task<T> seq(Task<?> task1, Task<?> task2, Task<?> task3, Task<?> task4, Task<?> task5,
      Task<?> task6, Task<T> task7) {
    return vaseq(task1, task2, task3, task4, task5, task6, task7);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#map(String, com.linkedin.parseq.function.Function1) Task.map},
   * {@link Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap},
   * {@link Task#andThen(String, Task) Task.andThen} and other methods in {@link Task}.
   * @see Task#map(String, com.linkedin.parseq.function.Function1) Task.map
   * @see Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap
   * @see Task#andThen(String, Task) Task.andThen
   * @see Task
   */
  @Deprecated
  public static <T> Task<T> seq(Task<?> task1, Task<?> task2, Task<?> task3, Task<?> task4, Task<?> task5,
      Task<?> task6, Task<?> task7, Task<T> task8) {
    return vaseq(task1, task2, task3, task4, task5, task6, task7, task8);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#map(String, com.linkedin.parseq.function.Function1) Task.map},
   * {@link Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap},
   * {@link Task#andThen(String, Task) Task.andThen} and other methods in {@link Task}.
   * @see Task#map(String, com.linkedin.parseq.function.Function1) Task.map
   * @see Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap
   * @see Task#andThen(String, Task) Task.andThen
   * @see Task
   */
  @Deprecated
  public static <T> Task<T> seq(Task<?> task1, Task<?> task2, Task<?> task3, Task<?> task4, Task<?> task5,
      Task<?> task6, Task<?> task7, Task<?> task8, Task<T> task9) {
    return vaseq(task1, task2, task3, task4, task5, task6, task7, task8, task9);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#map(String, com.linkedin.parseq.function.Function1) Task.map},
   * {@link Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap},
   * {@link Task#andThen(String, Task) Task.andThen} and other methods in {@link Task}.
   * @see Task#map(String, com.linkedin.parseq.function.Function1) Task.map
   * @see Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap
   * @see Task#andThen(String, Task) Task.andThen
   * @see Task
   */
  @Deprecated
  public static <T> Task<T> seq(Task<?> task1, Task<?> task2, Task<?> task3, Task<?> task4, Task<?> task5,
      Task<?> task6, Task<?> task7, Task<?> task8, Task<?> task9, Task<T> task10) {
    return vaseq(task1, task2, task3, task4, task5, task6, task7, task8, task9, task10);
  }

  /**
   * Creates a new task that, when run, will run each of the supplied tasks in
   * order. The value of this new task is the value of the last task in the
   * sequence. <strong>This method is not type-safe</strong>.
   *
   * @param tasks the tasks to run sequentially
   * @param <T> the result value for the sequence of tasks
   * @return a new task that will run the given tasks sequentially
   * @deprecated  As of 2.0.0, replaced by {@link Task#map(String, com.linkedin.parseq.function.Function1) Task.map},
   * {@link Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap},
   * {@link Task#andThen(String, Task) Task.andThen} and other methods in {@link Task}.
   * @see Task#map(String, com.linkedin.parseq.function.Function1) Task.map
   * @see Task#flatMap(String, com.linkedin.parseq.function.Function1) Task.flatMap
   * @see Task#andThen(String, Task) Task.andThen
   * @see Task
   */
  @Deprecated
  public static <T> Task<T> seq(final Iterable<? extends Task<?>> tasks) {
    return new SeqTask<T>("seq", tasks);
  }

  /**
   * Creates a task that will run another task as a side effect once the primary task
   * completes successfully. The side effect will not be run if the primary task fails or
   * is canceled. The entire task is marked done once the base task completes, even if
   * the side effect has not been run.
   *
   * @param parent the primary task.
   * @param sideEffect the side effect of the primary task.
   * @param <T> the result value of the parent task, and the resulting task.
   * @return a new task that will be done once parent completes, but has the given side effect.
   * @deprecated  As of 2.0.0, replaced by {@link Task#withSideEffect(String, com.linkedin.parseq.function.Function1) Task.withSideEffect}
   * @see Task#withSideEffect(String, com.linkedin.parseq.function.Function1) Task.withSideEffect
   */
  @Deprecated
  public static <T> Task<T> withSideEffect(final Task<T> parent, final Task<?> sideEffect) {
    return parent.withSideEffect(t -> sideEffect);
  }

  /**
   * Creates a new task that will run the given tasks in parallel (e.g. task1
   * can be executed at the same time as task2). When all tasks complete
   * successfully, you can use {@link com.linkedin.parseq.ParTask#get()} to
   * get a list of the results. If at least one task failed, then this task will
   * also be marked as failed. Use {@link com.linkedin.parseq.ParTask#getTasks()}
   * or {@link com.linkedin.parseq.ParTask#getSuccessful()} to get results in
   * this case.
   * @deprecated  As of 2.0.0, replaced by {@link Task#par(Task, Task) Task.par}
   * @see Task#par(Task, Task) Task.par
   */
  @Deprecated
  public static <T> ParTask<T> par(Task<? extends T> task1, Task<? extends T> task2) {
    return vapar(task1, task2);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#par(Task, Task) Task.par}
   * @see Task#par(Task, Task) Task.par
   */
  @Deprecated
  public static <T> ParTask<T> par(Task<? extends T> task1, Task<? extends T> task2, Task<? extends T> task3) {
    return vapar(task1, task2, task3);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#par(Task, Task) Task.par}
   * @see Task#par(Task, Task) Task.par
   */
  @Deprecated
  public static <T> ParTask<T> par(Task<? extends T> task1, Task<? extends T> task2, Task<? extends T> task3,
      Task<? extends T> task4) {
    return vapar(task1, task2, task3, task4);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#par(Task, Task) Task.par}
   * @see Task#par(Task, Task) Task.par
   */
  @Deprecated
  public static <T> ParTask<T> par(Task<? extends T> task1, Task<? extends T> task2, Task<? extends T> task3,
      Task<? extends T> task4, Task<? extends T> task5) {
    return vapar(task1, task2, task3, task4, task5);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#par(Task, Task) Task.par}
   * @see Task#par(Task, Task) Task.par
   */
  @Deprecated
  public static <T> ParTask<T> par(Task<? extends T> task1, Task<? extends T> task2, Task<? extends T> task3,
      Task<? extends T> task4, Task<? extends T> task5, Task<? extends T> task6) {
    return vapar(task1, task2, task3, task4, task5, task6);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#par(Task, Task) Task.par}
   * @see Task#par(Task, Task) Task.par
   */
  @Deprecated
  public static <T> ParTask<T> par(Task<? extends T> task1, Task<? extends T> task2, Task<? extends T> task3,
      Task<? extends T> task4, Task<? extends T> task5, Task<? extends T> task6, Task<? extends T> task7) {
    return vapar(task1, task2, task3, task4, task5, task6, task7);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#par(Task, Task) Task.par}
   * @see Task#par(Task, Task) Task.par
   */
  @Deprecated
  public static <T> ParTask<T> par(Task<? extends T> task1, Task<? extends T> task2, Task<? extends T> task3,
      Task<? extends T> task4, Task<? extends T> task5, Task<? extends T> task6, Task<? extends T> task7,
      Task<? extends T> task8) {
    return vapar(task1, task2, task3, task4, task5, task6, task7, task8);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#par(Task, Task) Task.par}
   * @see Task#par(Task, Task) Task.par
   */
  @Deprecated
  public static <T> ParTask<T> par(Task<? extends T> task1, Task<? extends T> task2, Task<? extends T> task3,
      Task<? extends T> task4, Task<? extends T> task5, Task<? extends T> task6, Task<? extends T> task7,
      Task<? extends T> task8, Task<? extends T> task9) {
    return vapar(task1, task2, task3, task4, task5, task6, task7, task8, task9);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#par(Task, Task) Task.par}
   * @see Task#par(Task, Task) Task.par
   */
  @Deprecated
  public static <T> ParTask<T> par(Task<? extends T> task1, Task<? extends T> task2, Task<? extends T> task3,
      Task<? extends T> task4, Task<? extends T> task5, Task<? extends T> task6, Task<? extends T> task7,
      Task<? extends T> task8, Task<? extends T> task9, Task<? extends T> task10) {
    return vapar(task1, task2, task3, task4, task5, task6, task7, task8, task9, task10);
  }

  /**
   * Creates a new task that will run each of the supplied tasks in parallel (e.g.
   * tasks[0] can be run at the same time as task2). This is a type-safe,
   * collection-based alternative to {@link #vapar(Task[])}.
   * <p>
   * When all tasks complete successfully, you can use
   * {@link com.linkedin.parseq.ParTask#get()} to get a list of the results. If
   * at least one task failed, then this task will also be marked as failed. Use
   * {@link com.linkedin.parseq.ParTask#getTasks()} or
   * {@link com.linkedin.parseq.ParTask#getSuccessful()} to get results in this
   * case.
   *
   * @param tasks the tasks to run in parallel
   * @return The results of the tasks
   */
  public static <T> ParTask<T> par(final Iterable<? extends Task<? extends T>> tasks) {
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
   * @deprecated  As of 2.0.0, replaced by {@link Task#withTimeout(long, TimeUnit) Task.withTimeout}.
   * @see Task#withTimeout(long, TimeUnit) Task.withTimeout
   */
  @Deprecated
  public static <T> Task<T> timeoutWithError(final long time, final TimeUnit unit, final Task<T> task) {
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
   * @deprecated  As of 2.0.0, replaced by {@link Task#withTimeout(long, TimeUnit) Task.withTimeout}.
   * @see Task#withTimeout(long, TimeUnit) Task.withTimeout
   */
  @Deprecated
  public static <T> Task<T> timeoutWithError(final String name, final long time, final TimeUnit unit,
      final Task<T> task) {
    return new TimeoutWithErrorTask<T>(name, time, unit, task);
  }

  /**
   * Creates a new task that will run the given array of tasks sequentially
   * (e.g. tasks[0] will be finished before tasks[1] is run). <strong>This
   * method is not type-safe</strong> - prefer one of the {@code seq} options
   * when possible. The value of this new task is the value of the last task in
   * the sequence.
   *
   * @param tasks the tasks to run sequentially
   * @param <T> the result value for the sequence of tasks
   * @return a new task that will run the given tasks sequentially
   */
  private static <T> Task<T> vaseq(final Task<?>... tasks) {
    return new SeqTask<T>("seq", Arrays.asList(tasks));
  }

  /**
   * Creates a new task that will run each of the supplied tasks in parallel (e.g.
   * tasks[0] can be run at the same time as task2). <strong>This method is not
   * type-safe</strong> - prefer one of the {@code par} options when possible.
   * <p>
   * When all tasks complete successfully, you can use
   * {@link com.linkedin.parseq.ParTask#get()} to get a list of the results. If
   * at least one task failed, then this task will also be marked as failed. Use
   * {@link com.linkedin.parseq.ParTask#getTasks()} or
   * {@link com.linkedin.parseq.ParTask#getSuccessful()} to get results in this
   * case.
   *
   * @param tasks the tasks to run in parallel
   * @return a new task that will run the given tasks in parallel
   */
  private static <T> ParTask<T> vapar(final Task<?>... tasks) {
    final List<Task<T>> taskList = new ArrayList<Task<T>>(tasks.length);
    for (Task<?> task : tasks) {
      @SuppressWarnings("unchecked")
      final Task<T> typedTask = (Task<T>) task;
      taskList.add(typedTask);
    }
    return par(taskList);
  }
}
