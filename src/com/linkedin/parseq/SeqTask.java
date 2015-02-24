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

import com.linkedin.parseq.internal.SystemHiddenTask;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Task} that will run the constructor-supplied tasks one after the other.
 * <p/>
 * Use {@link Tasks#seq(Iterable)} or {@link Tasks#seq(java.lang.Iterable)} to create
 * instances of this class.
 *
 * @deprecated  As of 2.0.0, replaced by {@link Task#map(String, java.util.function.Function) Task.map},
 * {@link Task#flatMap(String, java.util.function.Function) Task.flatMap},
 * {@link Task#andThen(String, Task) Task.andThen} and other methods in {@link Task}.
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 * @see Task#map(String, java.util.function.Function) Task.map
 * @see Task#flatMap(String, java.util.function.Function) Task.flatMap
 * @see Task#andThen(String, Task) Task.andThen
 * @see Task
 */
/* package private */ class SeqTask<T> extends SystemHiddenTask<T>
{
  private volatile List<Task<?>> _tasks;

  public SeqTask(final String name, final Iterable<? extends Task<?>> tasks)
  {
    super(name);
    List<Task<?>> taskList = new ArrayList<Task<?>>();
    for(Task<?> task : tasks)
    {
      taskList.add(task);
    }

    if (taskList.size() == 0)
    {
      throw new IllegalArgumentException("No tasks to sequence!");
    }

    _tasks = Collections.unmodifiableList(taskList);
  }

  @Override
  protected Promise<? extends T> run(final Context context) throws Exception
  {
    final SettablePromise<T> result = Promises.settable();

    Task<?> prevTask = _tasks.get(0);
    for (int i = 1; i < _tasks.size(); i++)
    {
      final Task<?> currTask = _tasks.get(i);
      context.after(prevTask).run(currTask);
      prevTask = currTask;
    }

    // This is unsafe, but we don't have the ability to do type checking
    // with varargs.
    @SuppressWarnings("unchecked")
    final Task<T> typedPrevTask = (Task<T>)prevTask;
    Promises.propagateResult(typedPrevTask, result);
    context.run(_tasks.get(0));

    _tasks = null;

    return result;
  }
}
