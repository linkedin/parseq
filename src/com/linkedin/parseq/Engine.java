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

import com.linkedin.parseq.internal.ContextImpl;
import com.linkedin.parseq.internal.InternalUtil;
import com.linkedin.parseq.internal.PlanContext;
import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.internal.trace.DisabledTraceCapturer;
import com.linkedin.parseq.internal.trace.EnabledTraceCapturer;
import com.linkedin.parseq.internal.trace.TraceCapturer;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An object that can run a set {@link Task}s. Use {@link EngineBuilder} to
 * create Engine instances.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class Engine
{
  private static final String LOGGER_BASE = Engine.class.getName();

  private static final State INIT = new State(StateName.RUN, 0);
  private static final State TERMINATED = new State(StateName.TERMINATED, 0);

  private static enum StateName { RUN, SHUTDOWN, TERMINATED }

  private final AtomicLong NEXT_PLAN_ID = new AtomicLong();

  private final Executor _taskExecutor;
  private final DelayedExecutor _timerExecutor;
  private final ILoggerFactory _loggerFactory;

  private final AtomicReference<State> _stateRef = new AtomicReference<State>(INIT);
  private final CountDownLatch _terminated = new CountDownLatch(1);

  private final Map<String, Object > _properties;

  private final PromiseListener<Object> _taskDoneListener = new PromiseListener<Object>()
  {
    @Override
    public void onResolved(Promise<Object> resolvedPromise)
    {
      assert _stateRef.get()._pendingCount > 0;
      assert _stateRef.get()._stateName != StateName.TERMINATED;

      State currState, newState;
      do
      {
        currState = _stateRef.get();
        newState = new State(currState._stateName, currState._pendingCount - 1);
      }
      while (!_stateRef.compareAndSet(currState, newState));

      if (newState._stateName == StateName.SHUTDOWN && newState._pendingCount == 0)
      {
        tryTransitionTerminate();
      }
    }
  };

  // Cache these, since we'll use them frequently and they can be precomputed.
  private final Logger _allLogger;
  private final Logger _rootLogger;

  /* package private */ Engine(final Executor taskExecutor,
                               final DelayedExecutor timerExecutor,
                               final ILoggerFactory loggerFactory,
                               final Map<String, Object> properties)
  {
    _taskExecutor = taskExecutor;
    _timerExecutor = timerExecutor;
    _loggerFactory = loggerFactory;
    _properties = properties;

    _allLogger = loggerFactory.getLogger(LOGGER_BASE + ":all");
    _rootLogger = loggerFactory.getLogger(LOGGER_BASE + ":root");
  }

  public Object getProperty(String key)
  {
    return _properties.get(key);
  }

  /**
   * Runs the given task with its own context. Use {@code Tasks.seq} and
   * {@code Tasks.par} to create and run composite tasks. This method does not
   * enable tracing. Use {@link #run(Task, boolean)} to enable tracing.
   *
   * @param task the task to run
   */
  public void run(final Task<?> task)
  {
    run(task, false);
  }

  /**
   * Runs the given task with its own context. Use {@code Tasks.seq} and
   * {@code Tasks.par} to create and run composite tasks.
   *
   * @param task the task to run
   * @param enableTrace true if tracing should be enabled
   */
  public void run(final Task<?> task, boolean enableTrace)
  {
    State currState, newState;
    do
    {
      currState = _stateRef.get();
      if (currState._stateName != StateName.RUN)
      {
        task.cancel(new EngineShutdownException("Task submitted after engine shutdown"));
        return;
      }

      newState = new State(StateName.RUN, currState._pendingCount + 1);
    } while (!_stateRef.compareAndSet(currState, newState));

    final long planId = NEXT_PLAN_ID.getAndIncrement();
    final Logger planLogger = _loggerFactory.getLogger(LOGGER_BASE + ":planClass=" + task.getClass().getName());
    final TaskLogger taskLogger = new TaskLogger(planId, task, _allLogger, _rootLogger, planLogger);
    final TraceCapturer traceCapturer = enableTrace ? new EnabledTraceCapturer(task) : DisabledTraceCapturer.INSTANCE;
    final int taskId = traceCapturer.registerTask(task);
    new ContextImpl(new PlanContext(planId, this, _taskExecutor, _timerExecutor, traceCapturer, taskLogger), task, taskId).runTask();

    InternalUtil.unwildcardTask(task).addListener(_taskDoneListener);
  }

  /**
   * If the engine is currently running, this method will initiate an orderly
   * shutdown. No new tasks will be accepted, but already running tasks will be
   * allowed to finish. Use {@link #awaitTermination(int, java.util.concurrent.TimeUnit)}
   * to wait for the engine to shutdown.
   * <p/>
   * If the engine is already shutting down or stopped this method will have
   * no effect.
   */
  public void shutdown()
  {
    if (tryTransitionShutdown())
    {
      tryTransitionTerminate();
    }
  }

  /**
   * Returns {@code true} if engine shutdown has been started or if the engine
   * is terminated. Use {@link #isTerminated()} to determine if the engine is
   * actually stopped and {@link #awaitTermination(int, java.util.concurrent.TimeUnit)}
   * to wait for the engine to stop.
   *
   * @return {@code true} if the engine has started shutting down or if it has
   *         finished shutting down.
   */
  public boolean isShutdown()
  {
    return _stateRef.get()._stateName != StateName.RUN;
  }

  /**
   * Returns {@code true} if the engine has completely stopped. Use
   * {@link #awaitTermination(int, java.util.concurrent.TimeUnit)} to wait for
   * the engine to terminate. Use {@link #shutdown()} to start engine shutdown.
   *
   * @return {@code true} if the engine has completed stopped.
   */
  public boolean isTerminated()
  {
    return _stateRef.get()._stateName == StateName.TERMINATED;
  }

  /**
   * Waits for the engine to stop. Use {@link #shutdown()} to initiate
   * shutdown.
   *
   * @param time the amount of time to wait
   * @param unit the unit for the time to wait
   * @return {@code true} if shutdown completed within the specified time or
   *         {@code false} if not.
   * @throws InterruptedException if this thread is interrupted while waiting
   *         for the engine to stop.
   */
  public boolean awaitTermination(final int time, final TimeUnit unit) throws InterruptedException
  {
    return _terminated.await(time, unit);
  }

  private boolean tryTransitionShutdown()
  {
    State currState, newState;
    do
    {
      currState = _stateRef.get();
      if (currState._stateName != StateName.RUN)
      {
        return false;
      }
      newState = new State(StateName.SHUTDOWN, currState._pendingCount);
    }
    while (!_stateRef.compareAndSet(currState, newState));
    return true;
  }

  private void tryTransitionTerminate()
  {
    State currState;
    do
    {
      currState = _stateRef.get();
      if (currState._stateName != StateName.SHUTDOWN ||
          currState._pendingCount != 0)
      {
        return;
      }
    }
    while (!_stateRef.compareAndSet(currState, TERMINATED));

    _terminated.countDown();
  }

  private static class State
  {
    private final StateName _stateName;
    private final long _pendingCount;

    private State(final StateName stateName, final long pendingCount)
    {
      _pendingCount = pendingCount;
      _stateName = stateName;
    }
  }
}
