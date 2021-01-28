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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.TraceBuilder;
import com.linkedin.parseq.After;
import com.linkedin.parseq.Cancellable;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.Exceptions;
import com.linkedin.parseq.Task;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class ContextImpl implements Context, Cancellable {
  private static final Task<?> NO_PARENT = null;
  private static final List<Task<?>> NO_PREDECESSORS = Collections.emptyList();

  /**
   * Plan level configuration and facilities.
   */
  private final PlanContext _planContext;

  private final Task<Object> _task;

  // A thread local that holds the root task iff the root task is being executed
  // on the current thread. In all other cases, this thread local will hold a
  // null value.
  private static final ThreadLocal<Task<?>> _inTask = new ThreadLocal<Task<?>>();

  private final Task<?> _parent;
  private final List<Task<?>> _predecessorTasks;

  private final ConcurrentLinkedQueue<Cancellable> _cancellables = new ConcurrentLinkedQueue<Cancellable>();

  public ContextImpl(final PlanContext planContext, final Task<?> task) {
    this(planContext, task, NO_PARENT, NO_PREDECESSORS);
  }

  private ContextImpl(final PlanContext planContext, final Task<?> task, final Task<?> parent,
      final List<Task<?>> predecessorTasks) {
    _planContext = planContext;
    _task = InternalUtil.unwildcardTask(task);
    _parent = parent;
    _predecessorTasks = predecessorTasks;
  }

  public void runTask() {
    // Cancel everything created by this task once it finishes
    _task.addListener(resolvedPromise -> {
        for (Iterator<Cancellable> it = _cancellables.iterator(); it.hasNext();) {
          final Cancellable cancellable = it.next();
          cancellable.cancel(Exceptions.EARLY_FINISH_EXCEPTION);
          it.remove();
        }
      });

    _planContext.execute(new PrioritizableRunnable() {
      @Override
      public void run() {
        _inTask.set(_task);
        try {
          _task.contextRun(ContextImpl.this, _parent, _predecessorTasks);
        } finally {
          _inTask.remove();
        }
      }

      @Override
      public int getPriority() {
        return _task.getPriority();
      }
    });
  }

  @Override
  public Cancellable createTimer(final long time, final TimeUnit unit, final Task<?> task) {
    checkInTask();
    final Cancellable cancellable = _planContext.schedule(time, unit, new Runnable() {
      @Override
      public void run() {
        runSubTask(task, NO_PREDECESSORS);
      }
    });
    _cancellables.add(cancellable);
    return cancellable;
  }

  @Override
  public void run(final Task<?>... tasks) {
    checkInTask();
    for (final Task<?> task : tasks) {
      runSubTask(task, NO_PREDECESSORS);
    }
  }

  /**
   * This method submits the given task to the running queue and return.
   * The tasks will be executed asynchronously.
   *
   * @param task the task to be submitted
   */
  public void submitToExecutorAsync(Task<?> task) {
    // Spawn a subContext to drive the running of submitted task later
    final ContextImpl subContext = new ContextImpl(_planContext, task, _parent, _predecessorTasks);
    // Note:
    // (1) We allow task async submission even current task is considered done(promise resolved),
    //  so no Early Finish check during submission.

    // (2) Also since we don't assume when the async-submitted task will be run
    // which means it could run even after the task which initiated it was resolved.
    // Therefore we also do not add to _cancellable so it won't get cancelled if the task initiates it got resolved
      _planContext.submitToPlanAsync(new PrioritizableRunnable() {
        @Override
        public void run() {
          subContext.runTask();
        }
        @Override
        public int getPriority() {
          return task.getPriority();
        }
      });
  }

  public void scheduleToRun(Task<?>... tasks) {
    for (final Task<?> task: tasks) {
      submitToExecutorAsync(task);
    }
  }

  @Override
  public void runSideEffect(final Task<?>... tasks) {
    checkInTask();
    for (final Task<?> task : tasks) {
      runSideEffectSubTask(task, NO_PREDECESSORS);
    }
  }

  @Override
  public After after(final Promise<?>... promises) {
    checkInTask();

    final List<Task<?>> tmpPredecessorTasks = new ArrayList<Task<?>>();
    for (Promise<?> promise : promises) {
      if (promise instanceof Task) {
        tmpPredecessorTasks.add((Task<?>) promise);
      }
    }
    final List<Task<?>> predecessorTasks = Collections.unmodifiableList(tmpPredecessorTasks);

    return new After() {

      @Override
      public void run(final Task<?> task) {
        InternalUtil.after(resolvedPromise -> runSubTask(task, predecessorTasks), promises);
      }

      @Override
      public void run(final Supplier<Task<?>> taskSupplier) {
        InternalUtil.after(resolvedPromise -> {
          Task<?> task = taskSupplier.get();
          if (task != null) {
            runSubTask(task, predecessorTasks);
          }
        } , promises);
      }
    };
  }

  @Override
  public boolean cancel(Exception reason) {
    boolean result = _task.cancel(reason);
    //run the task to capture the trace data
    _task.contextRun(this, _parent, _predecessorTasks);
    return result;
  }

  @Override
  public Object getEngineProperty(String key) {
    return _planContext.getEngineProperty(key);
  }

  private ContextImpl createSubContext(final Task<?> task, final List<Task<?>> predecessors) {
    return new ContextImpl(_planContext, task, _task, predecessors);
  }

  private void runSubTask(final Task<?> task, final List<Task<?>> predecessors) {
    final ContextImpl subContext = createSubContext(task, predecessors);
    if (!isDone()) {
      _cancellables.add(subContext);
      subContext.runTask();
    } else {
      subContext.cancel(Exceptions.EARLY_FINISH_EXCEPTION);
    }
  }

  private void runSideEffectSubTask(final Task<?> taskWrapper, final List<Task<?>> predecessors) {
    PlanContext subPlan = _planContext.fork(taskWrapper);
    if (subPlan != null) {
      new ContextImpl(subPlan, taskWrapper, _task, predecessors).runTask();
    } else {
      taskWrapper.cancel(new IllegalStateException("Plan is already completed"));
    }
  }

  private boolean isDone() {
    return _task.isDone();
  }

  private void checkInTask() {
    Task<?> t = _inTask.get();
    if (t != _task) {
      throw new IllegalStateException("Context method invoked while not in context's task");
    }
  }

  @Override
  public TraceBuilder getTraceBuilder() {
    return _planContext.getRelationshipsBuilder();
  }

  @Override
  public ShallowTraceBuilder getShallowTraceBuilder() {
    return _task.getShallowTraceBuilder();
  }

  @Override
  public Long getPlanId() {
    return _planContext.getId();
  }

  public Long getTaskId() {
    return _task.getId();
  }

  @Override
  public TaskLogger getTaskLogger() {
    return _planContext.getTaskLogger();
  }

  @Override
  public String getPlanClass() {
    return _planContext.getPlanClass();
  }

}

