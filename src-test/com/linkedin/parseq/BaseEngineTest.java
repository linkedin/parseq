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

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.linkedin.parseq.internal.TimeUnitHelper;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.TraceUtil;


/**
 * A base class that builds an Engine with default configuration.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class BaseEngineTest {
  private static final Logger LOG = LoggerFactory.getLogger(BaseEngineTest.class.getName());

  private ScheduledExecutorService _scheduler;
  private ExecutorService _asyncExecutor;
  private Engine _engine;
  private ListLoggerFactory _loggerFactory;

  @SuppressWarnings("deprecation")
  @BeforeMethod
  public void setUp() throws Exception {
    final int numCores = Runtime.getRuntime().availableProcessors();
    _scheduler = Executors.newScheduledThreadPool(numCores + 1);
    _asyncExecutor = Executors.newFixedThreadPool(2);
    _loggerFactory = new ListLoggerFactory();
    EngineBuilder engineBuilder =
        new EngineBuilder().setTaskExecutor(_scheduler).setTimerScheduler(_scheduler).setLoggerFactory(_loggerFactory);
    AsyncCallableTask.register(engineBuilder, _asyncExecutor);
    customizeEngine(engineBuilder);
    _engine = engineBuilder.build();
  }

  @AfterMethod
  public void tearDown() throws Exception {
    _engine.shutdown();
    _engine.awaitTermination(200, TimeUnit.MILLISECONDS);
    _engine = null;
    _scheduler.shutdownNow();
    _scheduler = null;
    _asyncExecutor.shutdownNow();
    _asyncExecutor = null;
    _loggerFactory.reset();
    _loggerFactory = null;
  }

  protected void customizeEngine(EngineBuilder engineBuilder) {
  }

  protected Engine getEngine() {
    return _engine;
  }

  protected ScheduledExecutorService getScheduler() {
    return _scheduler;
  }
  
  /**
   * Equivalent to {@code runAndWait("runAndWait", task)}.
   * @see #runAndWait(String, Task)
   */
  protected <T> T runAndWait(Task<T> task) {
    return runAndWait("runAndWait", task);
  }

  /**
   * Equivalent to {@code runAndWait(desc, task, 5, TimeUnit.SECONDS)}.
   * @see #runAndWait(String, Task, long, TimeUnit)
   */
  protected <T> T runAndWait(final String desc, Task<T> task) {
    return runAndWait(desc, task, 5, TimeUnit.SECONDS);
  }

  /**
   * Runs task, verifies that task finishes within specified amount of time,
   * logs trace from the task execution and return value which task completed with.
   * If task completes with an exception, it is re-thrown by this method.
   *
   * @param desc description of a test
   * @param task task to run
   * @param time amount of time to wait for task completion
   * @param timeUnit unit of time
   * @return value task was completed with or exception is being thrown if task failed
   */
  protected <T> T runAndWait(final String desc, Task<T> task, long time, TimeUnit timeUnit) {
    try {
      _engine.run(task);
      assertTrue(task.await(time, timeUnit));
      return task.get();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      logTracingResults(desc, task);
    }
  }
  
  /**
   * Runs a task and verifies that it finishes with an error.
   * @param task task to run
   * @param exceptionClass expected exception class
   * @param <T> expected exception type
   * @return exception thrown by the task
   */
  protected <T extends Exception> T runAndWaitException(Task<?> task, Class<T> exceptionClass) {
    try {
      runAndWait("exception task", task);
      fail("An exception is expected, but the task succeeded");
      // just to make the compiler happy, we will never get here
      return null;
    } catch (PromiseException pe) {
      Throwable cause = pe.getCause();
      assertEquals(cause.getClass(), exceptionClass);
      return (T) cause;
    }
  }

  /**
   * Runs task.
   * @param task task to run
   */
  protected void run(Task<?> task) {
    _engine.run(task);
  }

  protected void logTracingResults(final String test, final Task<?> task) {
    try {
      LOG.info("Trace [" + test + "]:\n" + TraceUtil.getJsonTrace(task));
    } catch (IOException e) {
      LOG.error("Failed to encode JSON");
    }
  }

  protected void setLogLevel(final String loggerName, final int level) {
    _loggerFactory.getLogger(loggerName).setLogLevel(level);
  }

  protected List<ListLogger.Entry> getLogEntries(final String loggerName) {
    return _loggerFactory.getLogger(loggerName).getEntries();
  }

  protected void resetLoggers() {
    _loggerFactory.reset();
  }

  /**
   * Returns task which completes with given value after specified period
   * of time. Timer starts counting the moment this method is invoked.
   */
  protected <T> Task<T> delayedValue(T value, long time, TimeUnit timeUnit) {
    return Task.async(value.toString() + " delayed " + time + " " + TimeUnitHelper.toString(timeUnit), () -> {
      final SettablePromise<T> promise = Promises.settable();
      _scheduler.schedule(() -> promise.done(value), time, timeUnit);
      return promise;
    });
  }

  /**
   * Returns task which fails with given error after specified period
   * of time. Timer starts counting the moment this method is invoked.
   */
  protected <T> Task<T> delayedFailure(Throwable error, long time, TimeUnit timeUnit) {
    return Task.async(error.toString() + " delayed " + time + " " + TimeUnitHelper.toString(timeUnit), () -> {
      final SettablePromise<T> promise = Promises.settable();
      _scheduler.schedule(() -> promise.fail(error), time, timeUnit);
      return promise;
    });
  }

  protected int countTasks(Trace trace) {
    return trace.getTraceMap().size();
  }

}
