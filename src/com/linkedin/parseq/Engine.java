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

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.internal.ContextImpl;
import com.linkedin.parseq.internal.PlanCompletionListener;
import com.linkedin.parseq.internal.PlanDeactivationListener;
import com.linkedin.parseq.internal.PlanContext;


/**
 * An object that can run a set {@link Task}s. Use {@link EngineBuilder} to
 * create Engine instances.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class Engine {
  public static final String LOGGER_BASE = Engine.class.getName();

  public static final String MAX_RELATIONSHIPS_PER_TRACE = "_MaxRelationshipsPerTrace_";
  private static final int DEFUALT_MAX_RELATIONSHIPS_PER_TRACE = 4096;

  public static final String MAX_CONCURRENT_PLANS = "_MaxConcurrentPlans_";
  private static final int DEFUALT_MAX_CONCURRENT_PLANS = Integer.MAX_VALUE;

  public static final String DRAIN_SERIAL_EXECUTOR_QUEUE = "_DrainSerialExecutorQueue_";
  private static final boolean DEFUALT_DRAIN_SERIAL_EXECUTOR_QUEUE = true;

  private static final State INIT = new State(StateName.RUN, 0);
  private static final State TERMINATED = new State(StateName.TERMINATED, 0);
  private static final Logger LOG = LoggerFactory.getLogger(LOGGER_BASE);

  private static enum StateName {
    RUN,
    SHUTDOWN,
    TERMINATED
  }

  private final Executor _taskExecutor;
  private final DelayedExecutor _timerExecutor;
  private final ILoggerFactory _loggerFactory;
  private final TaskQueueFactory _taskQueueFactory;

  private final AtomicReference<State> _stateRef = new AtomicReference<State>(INIT);
  private final CountDownLatch _terminated = new CountDownLatch(1);

  private final Map<String, Object> _properties;

  private final int _maxRelationshipsPerTrace;

  private final int _maxConcurrentPlans;
  private final Semaphore _concurrentPlans;

  private final boolean _drainSerialExecutorQueue;

  private final PlanDeactivationListener _planDeactivationListener;
  private final PlanCompletionListener _planCompletionListener;

  private final PlanCompletionListener _taskDoneListener;

  // Cache these, since we'll use them frequently and they can be precomputed.
  private final Logger _allLogger;
  private final Logger _rootLogger;

  /* package private */ Engine(final Executor taskExecutor, final DelayedExecutor timerExecutor,
      final ILoggerFactory loggerFactory, final Map<String, Object> properties,
      final PlanDeactivationListener planActivityListener,
      final PlanCompletionListener planCompletionListener,
      final TaskQueueFactory taskQueueFactory) {
    _taskExecutor = taskExecutor;
    _timerExecutor = timerExecutor;
    _loggerFactory = loggerFactory;
    _properties = properties;
    _planDeactivationListener = planActivityListener;
    _taskQueueFactory = taskQueueFactory;

    _allLogger = loggerFactory.getLogger(LOGGER_BASE + ":all");
    _rootLogger = loggerFactory.getLogger(LOGGER_BASE + ":root");

    if (_properties.containsKey(MAX_RELATIONSHIPS_PER_TRACE)) {
      _maxRelationshipsPerTrace = (Integer) getProperty(MAX_RELATIONSHIPS_PER_TRACE);
    } else {
      _maxRelationshipsPerTrace = DEFUALT_MAX_RELATIONSHIPS_PER_TRACE;
    }

    if (_properties.containsKey(MAX_CONCURRENT_PLANS)) {
      _maxConcurrentPlans = (Integer) getProperty(MAX_CONCURRENT_PLANS);
    } else {
      _maxConcurrentPlans = DEFUALT_MAX_CONCURRENT_PLANS;
    }
    _concurrentPlans = new Semaphore(_maxConcurrentPlans);

    if (_properties.containsKey(DRAIN_SERIAL_EXECUTOR_QUEUE)) {
      _drainSerialExecutorQueue = (Boolean) getProperty(DRAIN_SERIAL_EXECUTOR_QUEUE);
    } else {
      _drainSerialExecutorQueue = DEFUALT_DRAIN_SERIAL_EXECUTOR_QUEUE;
    }

    _taskDoneListener = resolvedPromise -> {
      assert _stateRef.get()._pendingCount > 0;
      assert _stateRef.get()._stateName != StateName.TERMINATED;

      State currState;
      State newState;
      do {
        currState = _stateRef.get();
        newState = new State(currState._stateName, currState._pendingCount - 1);
      } while (!_stateRef.compareAndSet(currState, newState));

      _concurrentPlans.release();

      if (newState._stateName == StateName.SHUTDOWN && newState._pendingCount == 0) {
        tryTransitionTerminate();
      }
    };

    _planCompletionListener = planContext -> {
      try {
        planCompletionListener.onPlanCompleted(planContext);
      } catch (Throwable t) {
        LOG.error("Uncaught throwable from custom PlanCompletionListener.", t);
      } finally {
        _taskDoneListener.onPlanCompleted(planContext);
      }
    };

  }

  public Object getProperty(String key) {
    return _properties.get(key);
  }

  private final String defaultPlanClass(final Task<?> task) {
    return task.getClass().getName();
  }

  /**
   * Runs the given task. Task passed in as a parameter becomes a root on a new Plan.
   * All tasks created and started as a consequence of a root task will belong to that plan and will share a Trace.
   * <p>
   * This method throws {@code IllegalStateException} if Engine does not have capacity to run the task.
   * Engine's capacity is specified by a {@value #MAX_CONCURRENT_PLANS} configuration property. Use
   * {@link EngineBuilder#setEngineProperty(String, Object)} to set this property.
   * For the sake of backwards compatibility default value for a {@value #MAX_CONCURRENT_PLANS} is
   * {@value #DEFUALT_MAX_CONCURRENT_PLANS} which essentially means "unbounded capacity".
   *
   * @param task the task to run
   * @throws IllegalStateException
   */
  public void run(final Task<?> task) {
    run(task, defaultPlanClass(task));
  }

  /**
   * Runs the given task. Task passed in as a parameter becomes a root on a new Plan.
   * All tasks created and started as a consequence of a root task will belong to that plan and will share a Trace.
   * <p>
   * This method throws {@code IllegalStateException} if Engine does not have capacity to run the task.
   * Engine's capacity is specified by a {@value #MAX_CONCURRENT_PLANS} configuration property. Use
   * {@link EngineBuilder#setEngineProperty(String, Object)} to set this property.
   * For the sake of backwards compatibility default value for a {@value #MAX_CONCURRENT_PLANS} is
   * {@value #DEFUALT_MAX_CONCURRENT_PLANS} which essentially means "unbounded capacity".
   *
   * @param task the task to run
   * @param planClass string that identifies a "class" of the Plan. Plan class ends up in a ParSeq
   * Trace and can be used to group traces into "classes" when traces are statistically analyzed.
   * @throws IllegalStateException
   */
  public void run(final Task<?> task, final String planClass) {
    if (!tryRun(task, planClass)) {
      throw new IllegalStateException("Starting new plan rejected, exceeded limit of concurrent plans: " + _maxConcurrentPlans);
    }
  }

  /**
   * Runs the given task. Task passed in as a parameter becomes a root on a new Plan.
   * All tasks created and started as a consequence of a root task will belong to that plan and will share a Trace.
   * <p>
   * This method blocks until Engine has a capacity to run the task. Engine's capacity is
   * specified by a {@value #MAX_CONCURRENT_PLANS} configuration property. Use
   * {@link EngineBuilder#setEngineProperty(String, Object)} to set this property.
   * For the sake of backwards compatibility default value for a {@value #MAX_CONCURRENT_PLANS} is
   * {@value #DEFUALT_MAX_CONCURRENT_PLANS} which essentially means "unbounded capacity".
   *
   * @param task the task to run
   */
  public void blockingRun(final Task<?> task) {
    blockingRun(task, defaultPlanClass(task));
  }

  /**
   * Runs the given task. Task passed in as a parameter becomes a root on a new Plan.
   * All tasks created and started as a consequence of a root task will belong to that plan and will share a Trace.
   * <p>
   * This method blocks until Engine has a capacity to run the task. Engine's capacity is
   * specified by a {@value #MAX_CONCURRENT_PLANS} configuration property. Use
   * {@link EngineBuilder#setEngineProperty(String, Object)} to set this property.
   * For the sake of backwards compatibility default value for a {@value #MAX_CONCURRENT_PLANS} is
   * {@value #DEFUALT_MAX_CONCURRENT_PLANS} which essentially means "unbounded capacity".
   *
   * @param task the task to run
   * @param planClass string that identifies a "class" of the Plan. Plan class ends up in a ParSeq
   * Trace and can be used to group traces into "classes" when traces are statistically analyzed.
   */
  public void blockingRun(final Task<?> task, final String planClass) {
    try {
      _concurrentPlans.acquire();
      runWithPermit(task, planClass);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Runs the given task if Engine has a capacity to start new plan as specified by
   * {@value #MAX_CONCURRENT_PLANS} configuration parameter.
   * For the sake of backwards compatibility default value for a {@value #MAX_CONCURRENT_PLANS} is
   * {@value #DEFUALT_MAX_CONCURRENT_PLANS} which essentially means "unbounded capacity".
   * Task passed in as a parameter becomes a root on a new Plan.
   * All tasks created and started as a consequence of a root task will belong to that plan and will share a Trace.
   * This method returns immediately and does not block. It returns {@code true} if Plan was successfully started.
   * @param task the task to run
   * @return true if Plan was started
   */
  public boolean tryRun(final Task<?> task) {
    return tryRun(task,  defaultPlanClass(task));
  }

  /**
   * Runs the given task if Engine has a capacity to start new plan as specified by
   * {@value #MAX_CONCURRENT_PLANS} configuration parameter.
   * For the sake of backwards compatibility default value for a {@value #MAX_CONCURRENT_PLANS} is
   * {@value #DEFUALT_MAX_CONCURRENT_PLANS} which essentially means "unbounded capacity".
   * Task passed in as a parameter becomes a root on a new Plan.
   * All tasks created and started as a consequence of a root task will belong to that plan and will share a Trace.
   * This method returns immediately and does not block. It returns {@code true} if Plan was successfully started.
   * @param task the task to run
   * @param planClass string that identifies a "class" of the Plan
   * @return true if Plan was started
   */
  public boolean tryRun(final Task<?> task, final String planClass) {
    if (_concurrentPlans.tryAcquire()) {
      runWithPermit(task, planClass);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Runs the given task if Engine has a capacity to start new plan as specified by
   * {@value #MAX_CONCURRENT_PLANS} configuration parameter within specified amount of time.
   * For the sake of backwards compatibility default value for a {@value #MAX_CONCURRENT_PLANS} is
   * {@value #DEFUALT_MAX_CONCURRENT_PLANS} which essentially means "unbounded capacity".
   * Task passed in as a parameter becomes a root on a new Plan.
   * All tasks created and started as a consequence of a root task will belong to that plan and will share a Trace.
   * If Engine does not have capacity to start the task, this method will block up to specified amount of
   * time waiting for other concurrently running Plans to complete. If there is no capacity to start the task
   * within specified amount of time this method will return false. It returns {@code true} if Plan was successfully started.
   * @param task the task to run
   * @param timeout amount of time to wait for Engine's capacity to run the task
   * @param unit
   * @return true if Plan was started within the given waiting time and the current thread has not
   * been {@linkplain Thread#interrupt interrupted}.
   */
  public boolean tryRun(final Task<?> task, final long timeout, final TimeUnit unit) throws InterruptedException {
    return tryRun(task,  defaultPlanClass(task), timeout, unit);
  }

  /**
   * Runs the given task if Engine has a capacity to start new plan as specified by
   * {@value #MAX_CONCURRENT_PLANS} configuration parameter within specified amount of time.
   * For the sake of backwards compatibility default value for a {@value #MAX_CONCURRENT_PLANS} is
   * {@value #DEFUALT_MAX_CONCURRENT_PLANS} which essentially means "unbounded capacity".
   * Task passed in as a parameter becomes a root on a new Plan.
   * All tasks created and started as a consequence of a root task will belong to that plan and will share a Trace.
   * If Engine does not have capacity to start the task, this method will block up to specified amount of
   * time waiting for other concurrently running Plans to complete. If there is no capacity to start the task
   * within specified amount of time this method will return false. It returns {@code true} if Plan was successfully started.
   * @param task the task to run
   * @param planClass string that identifies a "class" of the Plan
   * @param timeout amount of time to wait for Engine's capacity to run the task
   * @param unit
   * @return true if Plan was started within the given waiting time and the current thread has not
   * been {@linkplain Thread#interrupt interrupted}.
  */
  public boolean tryRun(final Task<?> task, final String planClass, final long timeout, final TimeUnit unit) throws InterruptedException {
    if (_concurrentPlans.tryAcquire(timeout, unit)) {
      runWithPermit(task, planClass);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Runs the given task with its own context. Use {@code Tasks.seq} and
   * {@code Tasks.par} to create and run composite tasks.
   *
   * @param task the task to run
   */
  private void runWithPermit(final Task<?> task, final String planClass) {
    ArgumentUtil.requireNotNull(task, "task");
    ArgumentUtil.requireNotNull(planClass, "planClass");
    State currState, newState;
    do {
      currState = _stateRef.get();
      if (currState._stateName != StateName.RUN) {
        task.cancel(new EngineShutdownException("Task submitted after engine shutdown"));
        return;
      }
      newState = new State(StateName.RUN, currState._pendingCount + 1);
    } while (!_stateRef.compareAndSet(currState, newState));

    PlanContext planContext = new PlanContext(this, _taskExecutor, _timerExecutor, _loggerFactory, _allLogger,
        _rootLogger, planClass, task, _maxRelationshipsPerTrace, _planDeactivationListener, _planCompletionListener,
        _taskQueueFactory.newTaskQueue(), _drainSerialExecutorQueue);
    new ContextImpl(planContext, task).runTask();
  }

  /**
   * If the engine is currently running, this method will initiate an orderly
   * shutdown. No new tasks will be accepted, but already running tasks will be
   * allowed to finish. Use {@link #awaitTermination(int, java.util.concurrent.TimeUnit)}
   * to wait for the engine to shutdown.
   * <p>
   * If the engine is already shutting down or stopped this method will have
   * no effect.
   */
  public void shutdown() {
    if (tryTransitionShutdown()) {
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
  public boolean isShutdown() {
    return _stateRef.get()._stateName != StateName.RUN;
  }

  /**
   * Returns {@code true} if the engine has completely stopped. Use
   * {@link #awaitTermination(int, java.util.concurrent.TimeUnit)} to wait for
   * the engine to terminate. Use {@link #shutdown()} to start engine shutdown.
   *
   * @return {@code true} if the engine has completed stopped.
   */
  public boolean isTerminated() {
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
  public boolean awaitTermination(final int time, final TimeUnit unit) throws InterruptedException {
    return _terminated.await(time, unit);
  }

  private boolean tryTransitionShutdown() {
    State currState, newState;
    do {
      currState = _stateRef.get();
      if (currState._stateName != StateName.RUN) {
        return false;
      }
      newState = new State(StateName.SHUTDOWN, currState._pendingCount);
    } while (!_stateRef.compareAndSet(currState, newState));
    return true;
  }

  private void tryTransitionTerminate() {
    State currState;
    do {
      currState = _stateRef.get();
      if (currState._stateName != StateName.SHUTDOWN || currState._pendingCount != 0) {
        return;
      }
    } while (!_stateRef.compareAndSet(currState, TERMINATED));

    _terminated.countDown();
  }

  private static class State {
    private final StateName _stateName;
    private final long _pendingCount;

    private State(final StateName stateName, final long pendingCount) {
      _pendingCount = pendingCount;
      _stateName = stateName;
    }
  }
}
