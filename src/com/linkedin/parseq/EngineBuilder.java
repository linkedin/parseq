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

import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.internal.CachedLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;


/**
 * A configurable builder that makes {@link Engine}s.
 * <p>
 * Minimum required configuration:
 * <ul>
 *   <li>taskExecutor</li>
 *   <li>timerScheduler</li>
 * </ul>
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class EngineBuilder {
  private Executor _taskExecutor;
  private DelayedExecutor _timerScheduler;
  private ILoggerFactory _loggerFactory = null;
  private PlanActivityListener _planActivityListener;

  private Map<String, Object> _properties = new HashMap<String, Object>();

  public EngineBuilder() {
  }

  /**
   * Sets plan activity listener for the engine. The listener will be notified
   * when plan becomes activated and deactivated.
   * <p>
   * Plan becomes activated when task belonging to it gets scheduled for execution
   * by the engine. It remains activated as long as there are other tasks belonging
   * to this plan that can be executed.
   * <p>
   * Plan becomes deactivated when there are no tasks that can be executed e.g.
   * asynchronous operations are in progress and subsequent tasks depend on their results.
   * <p>
   *
   * For given plan id methods on {@link PlanActivityListener} are always called sequentially
   * in the following order: {@code onPlanActivated(id), onPlanDeactivated(id),  (...),
   * onPlanActivated(id), onPlanDeactivated(id)}. For arbitrary plan ids methods on
   * {@code PlanActivityListener} can be called in parallel.
   *
   * @param planActivityListener the listener that will be notified when plan
   * is being activated and deactivated
   */
  public void setPlanActivityListener(PlanActivityListener planActivityListener) {
    ArgumentUtil.requireNotNull(planActivityListener, "planActivityListener");
    _planActivityListener = planActivityListener;
  }

  /**
   * Sets the task executor for the engine.
   * <p>
   * The lifecycle of the executor is not managed by the engine.
   *
   * @param taskExecutor the executor to use for the engine
   * @return this builder
   */
  public EngineBuilder setTaskExecutor(final Executor taskExecutor) {
    ArgumentUtil.requireNotNull(taskExecutor, "taskExecutor");
    _taskExecutor = taskExecutor;
    return this;
  }

  /**
   * Sets the timer scheduler for the engine.
   * <p>
   * The lifecycle of the scheduler is not managed by the engine.
   *
   * @param timerScheduler the scheduler to use for the engine
   * @return this builder
   */
  public EngineBuilder setTimerScheduler(final DelayedExecutor timerScheduler) {
    ArgumentUtil.requireNotNull(timerScheduler, "timerScheduler");
    _timerScheduler = timerScheduler;
    return this;
  }

  /**
   * Sets the timer scheduler for the engine.
   * <p>
   * The lifecycle of the scheduler is not managed by the engine.
   *
   * @param timerScheduler the scheduler to use for the engine
   * @return this builder
   */
  public EngineBuilder setTimerScheduler(final ScheduledExecutorService timerScheduler) {
    ArgumentUtil.requireNotNull(timerScheduler, "timerScheduler");
    setTimerScheduler(adaptTimerScheduler(timerScheduler));
    return this;
  }

  /**
   * Sets the logger factory that will be used by the engine. By default
   * the engine will use whatever log binding SLF4J has detected.
   *
   * @param loggerFactory the logger factory to used by the engine.
   * @return this builder
   */
  public EngineBuilder setLoggerFactory(final ILoggerFactory loggerFactory) {
    ArgumentUtil.requireNotNull(loggerFactory, "loggerFactory");
    _loggerFactory = loggerFactory;
    return this;
  }

  /**
   * Sets an engine related property on the engine.
   * That property can then be accessed by tasks via the Context.
   *
   * @param key
   * @param value
   * @return this builder
   */
  public EngineBuilder setEngineProperty(String key, Object value) {
    _properties.put(key, value);
    return this;
  }

  /**
   * Checks that the require configuration has been set and then constructs
   * and returns a new {@link Engine}.
   *
   * @return a new {@link Engine} using the configuration in this builder.
   * @throws IllegalStateException if the configuration in this builder is invalid.
   */
  public Engine build() {
    if (_taskExecutor == null) {
      throw new IllegalStateException("Task executor is required to create an Engine, but it is not set");
    }
    if (_timerScheduler == null) {
      throw new IllegalStateException("Timer scheduler is required to create an Engine, but it is not set");
    }
    Engine engine = new Engine(_taskExecutor, new IndirectDelayedExecutor(_timerScheduler),
        _loggerFactory != null ? _loggerFactory : new CachedLoggerFactory(LoggerFactory.getILoggerFactory()),
        _properties,
        _planActivityListener != null ? _planActivityListener : new PlanActivityListener() {
          @Override
          public void onPlanDeactivated(Long planId) {
          }
          @Override
          public void onPlanActivated(Long planId) {
          }
        });
    return engine;
  }

  /**
   * Helper method to convert a {@link ScheduledExecutorService} to a simpler
   * {@link DelayedExecutor}.
   *
   * @param timerScheduler the scheduler to convert
   * @return the converted scheduler
   */
  private static DelayedExecutor adaptTimerScheduler(final ScheduledExecutorService timerScheduler) {
    return new DelayedExecutorAdapter(timerScheduler);
  }
}
