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

import com.linkedin.parseq.internal.PlanCompletionListener;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.internal.ContextImpl;
import com.linkedin.parseq.internal.InternalUtil;
import com.linkedin.parseq.internal.PlanContext;
import com.linkedin.parseq.internal.PlanDeactivationListener;
import com.linkedin.parseq.promise.PromiseListener;


/**
 * An object that can run a set {@link Task}s. Use {@link EngineBuilder} to
 * create Engine instances.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
class EngineImpl implements Engine {

  public static final String MAX_RELATIONSHIPS_PER_TRACE = "_MaxRelationshipsPerTrace_";
  private static final int DEFUALT_MAX_RELATIONSHIPS_PER_TRACE = 4096;

  private static final State INIT = new State(StateName.RUN, 0);
  private static final State TERMINATED = new State(StateName.TERMINATED, 0);

  private enum StateName {
    RUN,
    SHUTDOWN,
    TERMINATED
  }

  private final Executor _taskExecutor;
  private final DelayedExecutor _timerExecutor;
  private final ILoggerFactory _loggerFactory;

  private final AtomicReference<State> _stateRef = new AtomicReference<State>(INIT);
  private final CountDownLatch _terminated = new CountDownLatch(1);

  private final Map<String, Object> _properties;

  private final int _maxRelationshipsPerTrace;

  private final PlanDeactivationListener _planDeactivationListener;
  private final PlanCompletionListener _planCompletionListener;

  private final PromiseListener<Object> _taskDoneListener = resolvedPromise -> {
    assert _stateRef.get()._pendingCount > 0;
    assert _stateRef.get()._stateName != StateName.TERMINATED;

    State currState;
    State newState;
    do {
      currState = _stateRef.get();
      newState = new State(currState._stateName, currState._pendingCount - 1);
    } while (!_stateRef.compareAndSet(currState, newState));

    if (newState._stateName == StateName.SHUTDOWN && newState._pendingCount == 0) {
      tryTransitionTerminate();
    }
  };

  // Cache these, since we'll use them frequently and they can be precomputed.
  private final Logger _allLogger;
  private final Logger _rootLogger;

  EngineImpl(final Executor taskExecutor, final DelayedExecutor timerExecutor,
      final ILoggerFactory loggerFactory, final Map<String, Object> properties,
      final PlanDeactivationListener planActivityListener,
      final PlanCompletionListener planCompletionListener) {
    _taskExecutor = taskExecutor;
    _timerExecutor = timerExecutor;
    _loggerFactory = loggerFactory;
    _properties = properties;
    _planDeactivationListener = planActivityListener;
    _planCompletionListener = planCompletionListener;

    _allLogger = loggerFactory.getLogger(LOGGER_BASE + ":all");
    _rootLogger = loggerFactory.getLogger(LOGGER_BASE + ":root");

    if (_properties.containsKey(MAX_RELATIONSHIPS_PER_TRACE)) {
      _maxRelationshipsPerTrace = (Integer) getProperty(MAX_RELATIONSHIPS_PER_TRACE);
    } else {
      _maxRelationshipsPerTrace = DEFUALT_MAX_RELATIONSHIPS_PER_TRACE;
    }
  }

  @Override
  public Object getProperty(String key) {
    return _properties.get(key);
  }

  @Override
  public void run(final Task<?> task) {
    run(task, task.getClass().getName());
  }

  @Override
  public void run(final Task<?> task, final String planClass) {
    ArgumentUtil.requireNotNull(task, "task");
    ArgumentUtil.requireNotNull(planClass, "planClass");

    State currState;
    State newState;
    do {
      currState = _stateRef.get();
      if (currState._stateName != StateName.RUN) {
        task.cancel(new EngineShutdownException("Task submitted after engine shutdown"));
        return;
      }

      newState = new State(StateName.RUN, currState._pendingCount + 1);
    } while (!_stateRef.compareAndSet(currState, newState));

    PlanContext planContext = new PlanContext(this, _taskExecutor, _timerExecutor, _loggerFactory, _allLogger,
        _rootLogger, planClass, task, _maxRelationshipsPerTrace, _planDeactivationListener, _planCompletionListener);
    new ContextImpl(planContext, task).runTask();

    InternalUtil.unwildcardTask(task).addListener(_taskDoneListener);
  }

  @Override
  public void shutdown() {
    if (tryTransitionShutdown()) {
      tryTransitionTerminate();
    }
  }

  @Override
  public boolean isShutdown() {
    return _stateRef.get()._stateName != StateName.RUN;
  }

  @Override
  public boolean isTerminated() {
    return _stateRef.get()._stateName == StateName.TERMINATED;
  }

  @Override
  public boolean awaitTermination(final int time, final TimeUnit unit) throws InterruptedException {
    return _terminated.await(time, unit);
  }

  private boolean tryTransitionShutdown() {
    State currState;
    State newState;
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
