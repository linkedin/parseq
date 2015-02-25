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

import com.linkedin.parseq.collection.ParSeqCollections;
import com.linkedin.parseq.internal.InternalUtil;
import com.linkedin.parseq.internal.SystemHiddenTask;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.ResultType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Task} that will run all of the constructor-supplied tasks in parallel.
 * <p/>
 * Use {@link Tasks#par(Task[])} or {@link Tasks#par(Iterable)} to create an
 * instance of this class.
 *
 * @deprecated  As of 2.0.0, replaced by {@link Task#par(Task, Task) Task.par} or
 * {@link ParSeqCollections#fromTasks(Task...) ParSeqCollections.fromTasks}.
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 * @see Task#par(Task, Task) Task.par
 * @see ParSeqCollections#fromTasks(Task...) ParSeqCollections.fromTasks
 */
/* package private */ class ParTaskImpl<T> extends SystemHiddenTask<List<T>> implements ParTask<T>
{
  private final List<Task<T>> _tasks;

  public ParTaskImpl(final String name, final Iterable<? extends Task<? extends T>> tasks)
  {
    super(name);
    List<Task<T>> taskList = new ArrayList<Task<T>>();
    for(Task<? extends T> task : tasks)
    {
      // Safe to coerce Task<? extends T> to Task<T>
      @SuppressWarnings("unchecked")
      final Task<T> coercedTask = (Task<T>)task;
      taskList.add(coercedTask);
    }

    if (taskList.isEmpty())
    {
      throw new IllegalArgumentException("No tasks to parallelize!");
    }

    _tasks = Collections.unmodifiableList(taskList);

  }

  @Override
  protected Promise<List<T>> run(final Context context) throws Exception
  {
    final SettablePromise<List<T>> result = Promises.settable();

    final PromiseListener<?> listener = new PromiseListener<Object>()
    {
      @Override
      public void onResolved(Promise<Object> resolvedPromise)
      {
        boolean allEarlyFinish = true;
        final List<T> taskResult = new ArrayList<T>();
        final List<Throwable> errors = new ArrayList<Throwable>();

        for (Task<? extends T> task : _tasks)
        {
          if (task.isFailed())
          {
            if (allEarlyFinish && ResultType.fromTask(task) != ResultType.EARLY_FINISH)
            {
              allEarlyFinish = false;
            }
            errors.add(task.getError());
          }
          else
          {
            taskResult.add(task.get());
          }
        }
        if (!errors.isEmpty())
        {
          result.fail(allEarlyFinish ? errors.get(0) : new MultiException("Multiple errors in 'ParTask' task.", errors));
        }
        else
        {
          result.done(taskResult);
        }
      }
    };

    InternalUtil.after(listener, _tasks.toArray(new Task<?>[_tasks.size()]));

    for (Task<?> task : _tasks)
    {
      context.run(task);
    }

    return result;
  }

  @Override
  public List<Task<T>> getTasks()
  {
    return _tasks;
  }

  @Override
  public List<T> getSuccessful()
  {
    if(!this.isFailed())
    {
      return this.get();
    }

    final List<T> taskResult = new ArrayList<T>();
    for (Task<? extends T> task : _tasks)
    {
      if (!task.isFailed())
      {
        taskResult.add(task.get());
      }
    }
    return taskResult;
  }
}
