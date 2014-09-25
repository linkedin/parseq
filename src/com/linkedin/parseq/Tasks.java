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
                                     final ThrowableCallable<? extends T> callable)
  {
    return new CallableTask<T>(name, callable);
  }

  /**
   * Creates a new task that will run the given tasks sequentially (e.g.
   * task1 will be finished before task2 starts). The value of the seq task will
   * be the result of the last task in the sequence.
   */
  public static <T> Task<T> seq(Task<?> task1,
                                Task<T> task2)
  {
    return vaseq(task1, task2);
  }

  /**
   * @see #seq(Task, Task)
   */
  public static <T> Task<T> seq(Task<?> task1,
                                Task<?> task2,
                                Task<T> task3)
  {
    return vaseq(task1, task2, task3);
  }

  /**
   * @see #seq(Task, Task)
   */
  public static <T> Task<T> seq(Task<?> task1,
                                Task<?> task2,
                                Task<?> task3,
                                Task<T> task4)
  {
    return vaseq(task1, task2, task3, task4);
  }

  /**
   * @see #seq(Task, Task)
   */
  public static <T> Task<T> seq(Task<?> task1,
                                Task<?> task2,
                                Task<?> task3,
                                Task<?> task4,
                                Task<T> task5)
  {
    return vaseq(task1, task2, task3, task4, task5);
  }

  /**
   * @see #seq(Task, Task)
   */
  public static <T> Task<T> seq(Task<?> task1,
                                Task<?> task2,
                                Task<?> task3,
                                Task<?> task4,
                                Task<?> task5,
                                Task<T> task6)
  {
    return vaseq(task1, task2, task3, task4, task5, task6);
  }

  /**
   * @see #seq(Task, Task)
   */
  public static <T> Task<T> seq(Task<?> task1,
                                Task<?> task2,
                                Task<?> task3,
                                Task<?> task4,
                                Task<?> task5,
                                Task<?> task6,
                                Task<T> task7)
  {
    return vaseq(task1, task2, task3, task4, task5, task6, task7);
  }

  /**
   * @see #seq(Task, Task)
   */
  public static <T> Task<T> seq(Task<?> task1,
                                Task<?> task2,
                                Task<?> task3,
                                Task<?> task4,
                                Task<?> task5,
                                Task<?> task6,
                                Task<?> task7,
                                Task<T> task8)
  {
    return vaseq(task1, task2, task3, task4, task5, task6, task7, task8);
  }

  /**
   * @see #seq(Task, Task)
   */
  public static <T> Task<T> seq(Task<?> task1,
                                Task<?> task2,
                                Task<?> task3,
                                Task<?> task4,
                                Task<?> task5,
                                Task<?> task6,
                                Task<?> task7,
                                Task<?> task8,
                                Task<T> task9)
  {
    return vaseq(task1, task2, task3, task4, task5, task6, task7, task8, task9);
  }

  /**
   * @see #seq(Task, Task)
   */
  public static <T> Task<T> seq(Task<?> task1,
                                Task<?> task2,
                                Task<?> task3,
                                Task<?> task4,
                                Task<?> task5,
                                Task<?> task6,
                                Task<?> task7,
                                Task<?> task8,
                                Task<?> task9,
                                Task<T> task10)
  {
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
   */
  public static <T> Task<T> seq(final Iterable<? extends Task<?>> tasks)
  {
    return new SeqTask<T>("seq", tasks);
  }

  /**
   * Creates a new task that, when run, will run each of the supplied tasks in
   * order, plus an additional task. The value of this new task is value of the last task in the
   * sequence. <strong>This method is not type-safe</strong>.
   *
   * @param tasks the tasks to run sequentially
   * @param task1 the last task to run
   * @param <T> the result value for the sequence of tasks
   * @return a new task that will run the given tasks sequentially
   */
  public static <T> Task<T> seq(final Iterable<? extends Task<?>> tasks, Task<?> task1)
  {
    final List<Task<T>> taskList = getTypedTaskList(tasks);
    @SuppressWarnings("unchecked")
    final Task<T> typedTask = (Task<T>) task1;
    taskList.add(typedTask);

    return new SeqTask<T>("seq", taskList);
  }

  private static <T> List<Task<T>> getTypedTaskList(Iterable<? extends Task<?>> tasks)
  {
    final List<Task<T>> taskList = new ArrayList<Task<T>>();
    for (Task<?> task : tasks)
    {
      @SuppressWarnings("unchecked")
      final Task<T> typedTask = (Task<T>) task;
      taskList.add(typedTask);
    }
    return taskList;
  }

  /**
   * Creates a new task that will run the given tasks in parallel (e.g. task1
   * can be executed at the same time as task2). When all tasks complete
   * successfully, you can use {@link com.linkedin.parseq.ParTask#get()} to
   * get a list of the results. If at least one task failed, then this task will
   * also be marked as failed. Use {@link com.linkedin.parseq.ParTask#getTasks()}
   * or {@link com.linkedin.parseq.ParTask#getSuccessful()} to get results in
   * this case.
   */
  public static <T> ParTask<T> par(Task<? extends T> task1,
                                   Task<? extends T> task2)
  {
    return vapar(task1, task2);
  }

  /**
   * @see #par(Task, Task)
   */
  public static <T> ParTask<T> par(Task<? extends T> task1,
                                   Task<? extends T> task2,
                                   Task<? extends T> task3)
  {
    return vapar(task1, task2, task3);
  }

  /**
   * @see #par(Task, Task)
   */
  public static <T> ParTask<T> par(Task<? extends T> task1,
                                   Task<? extends T> task2,
                                   Task<? extends T> task3,
                                   Task<? extends T> task4)
  {
    return vapar(task1, task2, task3, task4);
  }

  /**
   * @see #par(Task, Task)
   */
  public static <T> ParTask<T> par(Task<? extends T> task1,
                                   Task<? extends T> task2,
                                   Task<? extends T> task3,
                                   Task<? extends T> task4,
                                   Task<? extends T> task5)
  {
    return vapar(task1, task2, task3, task4, task5);
  }

  /**
   * @see #par(Task, Task)
   */
  public static <T> ParTask<T> par(Task<? extends T> task1,
                                   Task<? extends T> task2,
                                   Task<? extends T> task3,
                                   Task<? extends T> task4,
                                   Task<? extends T> task5,
                                   Task<? extends T> task6)
  {
    return vapar(task1, task2, task3, task4, task5, task6);
  }

  /**
   * @see #par(Task, Task)
   */
  public static <T> ParTask<T> par(Task<? extends T> task1,
                                   Task<? extends T> task2,
                                   Task<? extends T> task3,
                                   Task<? extends T> task4,
                                   Task<? extends T> task5,
                                   Task<? extends T> task6,
                                   Task<? extends T> task7)
  {
    return vapar(task1, task2, task3, task4, task5, task6, task7);
  }

  /**
   * @see #par(Task, Task)
   */
  public static <T> ParTask<T> par(Task<? extends T> task1,
                                   Task<? extends T> task2,
                                   Task<? extends T> task3,
                                   Task<? extends T> task4,
                                   Task<? extends T> task5,
                                   Task<? extends T> task6,
                                   Task<? extends T> task7,
                                   Task<? extends T> task8)
  {
    return vapar(task1, task2, task3, task4, task5, task6, task7, task8);
  }

  /**
   * @see #par(Task, Task)
   */
  public static <T> ParTask<T> par(Task<? extends T> task1,
                                   Task<? extends T> task2,
                                   Task<? extends T> task3,
                                   Task<? extends T> task4,
                                   Task<? extends T> task5,
                                   Task<? extends T> task6,
                                   Task<? extends T> task7,
                                   Task<? extends T> task8,
                                   Task<? extends T> task9)
  {
    return vapar(task1, task2, task3, task4, task5, task6, task7, task8, task9);
  }

  /**
   * @see #par(Task, Task)
   */
  public static <T> ParTask<T> par(Task<? extends T> task1,
                                   Task<? extends T> task2,
                                   Task<? extends T> task3,
                                   Task<? extends T> task4,
                                   Task<? extends T> task5,
                                   Task<? extends T> task6,
                                   Task<? extends T> task7,
                                   Task<? extends T> task8,
                                   Task<? extends T> task9,
                                   Task<? extends T> task10)
  {
    return vapar(task1, task2, task3, task4, task5, task6, task7, task8, task9, task10);
  }

  /**
   * Creates a new task that will run each of the supplied tasks in parallel (e.g.
   * tasks[0] can be run at the same time as task2). This is a type-safe,
   * collection-based alternative to {@link #vapar(Task[])}.
   * <p/>
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
  private static <T> Task<T> vaseq(final Task<?>... tasks)
  {
    return new SeqTask<T>("seq", Arrays.asList(tasks));
  }

  /**
   * Creates a new task that will run each of the supplied tasks in parallel (e.g.
   * tasks[0] can be run at the same time as task2). <strong>This method is not
   * type-safe</strong> - prefer one of the {@code par} options when possible.
   * <p/>
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
  private static <T> ParTask<T> vapar(final Task<?>... tasks)
  {
    final List<Task<T>> taskList = new ArrayList<Task<T>>(tasks.length);
    for (Task<?> task : tasks)
    {
      @SuppressWarnings("unchecked")
      final Task<T> typedTask = (Task<T>)task;
      taskList.add(typedTask);
    }
    return par(taskList);
  }
}