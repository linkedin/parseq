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

package com.linkedin.parseq.internal;

import com.linkedin.parseq.After;
import com.linkedin.parseq.Cancellable;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.EarlyFinishException;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.internal.trace.TraceCapturer;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.trace.Trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class ContextImpl implements InternalContext, Context, Cancellable
{
  private static final Integer NO_PARENT = null;
  private static final List<Integer> NO_PREDECESSORS = Collections.emptyList();

  private static final Exception EARLY_FINISH_EXCEPTION;

  static
  {
    EARLY_FINISH_EXCEPTION = new EarlyFinishException("Task cancelled because parent was already finished");

    // Clear out everything but the last frame
    final StackTraceElement[] stackTrace = EARLY_FINISH_EXCEPTION.getStackTrace();
    if (stackTrace.length > 0)
    {
      EARLY_FINISH_EXCEPTION.setStackTrace(Arrays.copyOf(EARLY_FINISH_EXCEPTION.getStackTrace(), 1));
    }
  }

  /**
   * Plan level configuration and facilities.
   */
  private final PlanContext _planContext;

  /**
   * The task managed by this context
   */
  private final Task<Object> _task;

  /**
   * The id of this task for tracing purposes.
   */
  private final int _taskId;

  /**
   * The id of the parent of this task, for tracing purposes. {@code null} if
   * this task has no parent (i.e. it is the plan's root).
   */
  private final Integer _parentId;

  /**
   * The ids of all predecessors of this task, for tracing purposes.
   */
  private final List<Integer> _predecessorIds;

  /**
   * A thread local that holds the root task iff the root task is being executed
   * on the current thread. In all other cases, this thread local will hold a
   * null value.
   */
  private final ThreadLocal<Task<?>> _inTask = new ThreadLocal<Task<?>>();


  /**
   * A list of tasks / timers that should be cancelled when the task managed by
   * this context completes.
   */
  private final ConcurrentLinkedQueue<Cancellable> _cancellables = new ConcurrentLinkedQueue<Cancellable>();

  public ContextImpl(final PlanContext planContext,
                     final Task<?> task,
                     final int taskId)
  {
    this(planContext, task, taskId, NO_PARENT, NO_PREDECESSORS);
  }

  private ContextImpl(final PlanContext planContext,
                      final Task<?> task,
                      final int taskId,
                      final Integer parentId,
                      final List<Integer> predecessorIds)
  {
    _planContext = planContext;
    _task = InternalUtil.unwildcardTask(task);
    _taskId = taskId;
    _parentId = parentId;
    _predecessorIds = predecessorIds;
  }

  public void runTask()
  {
    // Cancel everything created by this task once it finishes
    _task.addListener(new PromiseListener<Object>()
    {
      @Override
      public void onResolved(Promise<Object> resolvedPromise)
      {
        Cancellable cancellable;
        while ((cancellable = _cancellables.poll()) != null)
        {
          cancellable.cancel(EARLY_FINISH_EXCEPTION);
        }
      }
    });

    _planContext.execute(new PrioritizableRunnable()
    {
      @Override
      public void run()
      {
        if (_task.assignContext(ContextImpl.this))
        {
          if (_parentId != null)
          {
            getTraceCapturer().setParent(_taskId, _parentId);
          }
          getTraceCapturer().addPredecessors(_taskId, _predecessorIds);

          if (_planContext.getTaskLogger().isEnabled(_task))
          {
            _task.addTaskListener(_planContext.getTaskLogger());
          }

          _inTask.set(_task);
          try
          {
            _task.contextRun();
          }
          finally
          {
            _inTask.remove();
          }
        }
        else
        {
          if (_parentId != null)
          {
            getTraceCapturer().addPotentialParent(_taskId, _parentId);
          }
          getTraceCapturer().addPotentialPredecessors(_taskId, _predecessorIds);
        }
      }

      @Override
      public int getPriority()
      {
        return _task.getPriority();
      }
    });
  }

  @Override
  public Cancellable createTimer(final long time, final TimeUnit unit,
                                 final Task<?> task)
  {
    checkInTask();
    final int taskId = getTraceCapturer().registerTask(task);
    final Cancellable cancellable = _planContext.schedule(time, unit, new Runnable()
    {
      @Override
      public void run()
      {
        runSubTask(task, taskId, NO_PREDECESSORS);
      }
    });
    _cancellables.add(cancellable);
    getTraceCapturer().addPotentialParent(_taskId, taskId);
    return cancellable;
  }

  @Override
  public void run(final Task<?>... tasks)
  {
    checkInTask();
    for (final Task<?> task : tasks)
    {
      final int taskId = getTraceCapturer().registerTask(task);
      runSubTask(task, taskId, NO_PREDECESSORS);
      getTraceCapturer().addPotentialParent(_taskId, taskId);
    }
  }

  @Override
  public After after(final Promise<?>... promises)
  {
    checkInTask();

    final List<Integer> predecessorIds = new ArrayList<Integer>(promises.length);
    for (Promise<?> promise : promises)
    {
      if (promise instanceof Task)
      {
        predecessorIds.add(getTraceCapturer().registerTask((Task)promise));
      }
    }

    return new After()
    {
      @Override
      public void run(final Task<?> task)
      {
        final int taskId = getTraceCapturer().registerTask(task);
        InternalUtil.after(new PromiseListener()
        {
          @Override
          public void onResolved(Promise resolvedPromise)
          {
            runSubTask(task, taskId, predecessorIds);
          }
        }, promises);

        getTraceCapturer().addPotentialPredecessors(taskId, predecessorIds);
      }
    };
  }

  @Override
  public boolean cancel(Exception reason)
  {
    return _task.cancel(reason);
  }

  @Override
  public Object getEngineProperty(String key)
  {
    return _planContext.getEngineProperty(key);
  }

  @Override
  public Trace getTrace()
  {
    return getTraceCapturer().getTrace();
  }

  private ContextImpl createSubContext(final Task<?> task, final int taskId, final List<Integer> predecessorIds)
  {
    return new ContextImpl(_planContext, task, taskId, _taskId, predecessorIds);
  }

  private void runSubTask(final Task<?> task, final int taskId, final List<Integer> predecessorIds)
  {
    final ContextImpl subContext = createSubContext(task, taskId, predecessorIds);
    if (!isDone())
    {
      _cancellables.add(subContext);
      subContext.runTask();
    } else
    {
      subContext.cancel(EARLY_FINISH_EXCEPTION);
    }
  }

  private boolean isDone()
  {
    return _task.isDone();
  }

  private void checkInTask()
  {
    if (_inTask.get() != _task)
    {
      throw new IllegalStateException("Context method invoked while not in context's task");
    }
  }

  private TraceCapturer getTraceCapturer()
  {
    return _planContext.getTraceCapturer();
  }
}
