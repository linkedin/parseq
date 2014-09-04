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

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link Task} that will attempt to run the given task within the given
 * timeout. If the timeout expires then this task will fail with a
 * {@link TimeoutException}.
 * <p/>
 * Use {@link Tasks#timeoutWithError(String, long, java.util.concurrent.TimeUnit, Task)}
 * to create an instances of this class.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
/* package private */ class TimeoutWithErrorTask<T> extends BaseTask<T>
{
  private final long _time;
  private final TimeUnit _unit;
  private final Task<T> _task;

  public TimeoutWithErrorTask(final String name, final long time,
                              final TimeUnit unit, final Task<T> task)
  {
    super(name);
    _time = time;
    _unit = unit;
    _task = task;
    setPriority(task.getPriority());
  }

  @Override
  protected Promise<? extends T> run(final Context context) throws Exception
  {
    final SettablePromise<T> result = Promises.settable();
    final AtomicBoolean committed = new AtomicBoolean();

    final Task<?> timeoutTask = Tasks.action("timeoutTimer", new Runnable()
    {
      @Override
      public void run()
      {
        if (committed.compareAndSet(false, true))
        {
          result.fail(new TimeoutException());
        }
      }
    });

    //timeout tasks should run as early as possible
    timeoutTask.setPriority(Priority.MAX_PRIORITY);
    context.createTimer(_time, _unit, timeoutTask);

    //set priority of the task for which we just scheduled timeout
    //to be executed next, unless there exist other tasks with higher priority
    //e.g. other timeouts
    _task.setPriority(higherThan(getPriority()));
    context.run(_task);

    _task.addListener(new PromiseListener<T>()
    {
      @Override
      public void onResolved(Promise<T> resolvedPromise)
      {
        if (committed.compareAndSet(false, true))
        {
          Promises.propagateResult(_task, result);
        }
      }
    });

    return result;
  }

  /**
   * Returns priority higher than given priority. If given
   * priority has max value, then max is returned.
   */
  private static int higherThan(int priority) {
    if (priority == Priority.MAX_PRIORITY) {
      return Priority.MAX_PRIORITY;
    } else {
      return priority + 1;
    }
  }
}
