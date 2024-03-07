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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.linkedin.parseq.internal.InternalUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.ResultType;


/**
 * A {@link Task} that will run all of the constructor-supplied tasks in parallel.
 * <p>
 * Use {@link Tasks#par(Task[])} or {@link Tasks#par(Iterable)} to create an
 * instance of this class.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 * @see Task#par(Task, Task) Task.par
 */
/* package private */ class ParTaskImpl<T> extends BaseTask<List<T>>implements ParTask<T> {
  private final Task<? extends Task<? extends T>>[] _tasks;

  @SuppressWarnings("unchecked")
  private static final Task<? extends Task<?>>[] EMPTY = new Task[0];

  @SuppressWarnings("unchecked")
  public ParTaskImpl(final String name) {
    super(name);
    _tasks = (Task<? extends Task<? extends T>>[]) EMPTY;
  }

  public ParTaskImpl(final String name, final Iterable<? extends Task<? extends T>> tasks) {
    super(name);

    if (tasks instanceof Collection) {
      _tasks = tasksFromCollection((Collection<? extends Task<? extends T>>) tasks);
    } else {
      _tasks = tasksFromIterable(tasks);
    }

    if (_tasks.length == 0) {
      throw new IllegalArgumentException("No tasks to parallelize!");
    }
  }

  @SuppressWarnings("unchecked")
  private Task<? extends Task<? extends T>>[] tasksFromIterable(Iterable<? extends Task<? extends T>> tasks) {
    List<Task<T>> taskList = new ArrayList<Task<T>>();
    for (Task<? extends T> task : tasks) {
      // Safe to coerce Task<? extends T> to Task<T>
      final Task<T> coercedTask = (Task<T>) task;
      taskList.add(coercedTask);
    }
    return tasksFromCollection(taskList);
  }

  @SuppressWarnings("unchecked")
  private Task<? extends Task<? extends T>>[] tasksFromCollection(Collection<? extends Task<? extends T>> tasks) {
    Task<? extends Task<? extends T>>[] tasksArr = new Task[tasks.size()];
    int i = 0;
    for (@SuppressWarnings("rawtypes") Task task: tasks) {
      tasksArr[i++] = task;
    }
    return tasksArr;
  }

  @Override
  protected Promise<List<T>> run(final Context context) throws Exception {
    if (_tasks.length == 0) {
      return Promises.value(Collections.emptyList());
    }

    final SettablePromise<List<T>> result = Promises.settable();

    @SuppressWarnings("unchecked")
    final PromiseListener<?> listener = resolvedPromise -> {
      boolean allEarlyFinish = true;
      final List<T> taskResult = new ArrayList<T>(_tasks.length);
      List<Throwable> errors = null;

      for (Task<?> task : _tasks) {
        if (task.isFailed()) {
          if (allEarlyFinish && ResultType.fromTask(task) != ResultType.EARLY_FINISH) {
            allEarlyFinish = false;
          }
          if (errors == null) {
            errors = new ArrayList<Throwable>();
          }
          errors.add(task.getError());
        } else {
          taskResult.add((T) task.get());
        }
      }
      if (errors != null) {
        result.fail(allEarlyFinish ? errors.get(0) : new MultiException("Multiple errors in 'ParTask' task.", errors));
      } else {
        result.done(taskResult);
      }
    };

    InternalUtil.after(listener, _tasks);

    for (Task<?> task : _tasks) {
      context.run(task);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Task<T>> getTasks() {
    List<Task<T>> tasks = new ArrayList<>(_tasks.length);
    for (Task<?> task : _tasks) {
      tasks.add((Task<T>) task);
    }
    return tasks;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<T> getSuccessful() {
    if (!this.isFailed()) {
      return this.get();
    }

    final List<T> taskResult = new ArrayList<>();
    for (Task<?> task : _tasks) {
      if (!task.isFailed()) {
        taskResult.add((T) task.get());
      }
    }
    return taskResult;
  }
}
