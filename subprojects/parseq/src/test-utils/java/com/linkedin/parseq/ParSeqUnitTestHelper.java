/*
 * Copyright 2017 LinkedIn, Inc
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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.internal.PlanCompletionListener;
import com.linkedin.parseq.internal.PlanContext;
import com.linkedin.parseq.internal.TimeUnitHelper;
import com.linkedin.parseq.promise.PromiseException;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.TraceUtil;


/**
 * A helper class for ParSeq unit tests.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class ParSeqUnitTestHelper {
  private static final Logger LOG = LoggerFactory.getLogger(ParSeqUnitTestHelper.class.getName());

  private final Consumer<EngineBuilder> _engineCustomizer;

  private volatile ScheduledExecutorService _scheduler;
  private volatile ExecutorService _asyncExecutor;
  private volatile Engine _engine;
  private volatile ListLoggerFactory _loggerFactory;
  private volatile TaskDoneListener _taskDoneListener;

  public ParSeqUnitTestHelper() {
    this(engineBuilder -> {});
  }

  public ParSeqUnitTestHelper(Consumer<EngineBuilder> engineCustomizer) {
    _engineCustomizer = engineCustomizer;
  }

  /**
   * Creates Engine instance to be used for testing.
   */
  @SuppressWarnings("deprecation")
  public void setUp() throws Exception {
    final int numCores = Runtime.getRuntime().availableProcessors();
    _scheduler = Executors.newScheduledThreadPool(numCores + 1);
    _asyncExecutor = Executors.newFixedThreadPool(2);
    _loggerFactory = new ListLoggerFactory();
    EngineBuilder engineBuilder =
        new EngineBuilder().setTaskExecutor(_scheduler).setTimerScheduler(_scheduler).setLoggerFactory(_loggerFactory);
    AsyncCallableTask.register(engineBuilder, _asyncExecutor);
    _engineCustomizer.accept(engineBuilder);

    // Add taskDoneListener to engine builder.
    _taskDoneListener = new TaskDoneListener();
    PlanCompletionListener planCompletionListener = engineBuilder.getPlanCompletionListener();
    if (planCompletionListener == null) {
      engineBuilder.setPlanCompletionListener(_taskDoneListener);
    } else {
      engineBuilder.setPlanCompletionListener(planContext -> {
        try {
          planCompletionListener.onPlanCompleted(planContext);
        } catch (Throwable t) {
          LOG.error("Uncaught exception from custom planCompletionListener.", t);
        } finally {
          _taskDoneListener.onPlanCompleted(planContext);
        }
      });
    }
    _engine = engineBuilder.build();
  }

  /**
   * Equivalent to {@code tearDown(200, TimeUnit.MILLISECONDS);}.
   * @see #tearDown(int, TimeUnit)
   */
  public void tearDown() throws Exception {
    tearDown(200, TimeUnit.MILLISECONDS);
  }

  public void tearDown(final int time, final TimeUnit unit) throws Exception {
    _engine.shutdown();
    _engine.awaitTermination(time, unit);
    _engine = null;
    _scheduler.shutdownNow();
    _scheduler = null;
    _asyncExecutor.shutdownNow();
    _asyncExecutor = null;
    _loggerFactory.reset();
    _loggerFactory = null;
  }

  public Engine getEngine() {
    return _engine;
  }

  public ScheduledExecutorService getScheduler() {
    return _scheduler;
  }

  /**
   * Equivalent to {@code runAndWait(this.getClass().getName(), task)}.
   * @see #runAndWait(String, Task, long, TimeUnit)
   */
  public <T> T runAndWait(Task<T> task) {
    return runAndWait("runAndWait", task);
  }

  /**
   * Equivalent to {@code runAndWait(this.getClass().getName(), task, 5, TimeUnit.SECONDS)}.
   * @see #runAndWait(String, Task, long, TimeUnit)
   */
  public <T> T runAndWait(Task<T> task, long time, TimeUnit timeUnit) {
    return runAndWait("runAndWait", task, time, timeUnit);
  }

  /**
   * Equivalent to {@code runAndWait(desc, task, 5, TimeUnit.SECONDS)}.
   * @see #runAndWait(String, Task, long, TimeUnit)
   */
  public <T> T runAndWait(final String desc, Task<T> task) {
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
  public <T> T runAndWait(final String desc, Task<T> task, long time, TimeUnit timeUnit) {
    try {
      _engine.run(task);
      boolean result = task.await(time, timeUnit);
      if (!result) {
        throw new AssertionError("Expected task result to be successful");
      }
      return task.get();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      logTracingResults(desc, task);
    }
  }

  /**
   * Runs task, verifies that the entire plan(including side-effect tasks)
   * finishes within specified amount of time, logs trace from the task execution
   * and return value which task completed with.
   * If task completes with an exception, it is re-thrown by this method.
   *
   * @param desc description of a test
   * @param task task to run
   * @param time amount of time to wait for task completion
   * @param timeUnit unit of time
   * @param <T> task result type
   * @return value task was completed with or exception is being thrown if task failed
   */
  public <T> T runAndWaitForPlanToComplete(final String desc, Task<T> task, long time, TimeUnit timeUnit) {
    try {
      _engine.run(task);
      _taskDoneListener.await(task, time, timeUnit);
      return task.get();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      logTracingResults(desc, task);
    }
  }

  public <T> T runAndWaitForPlanToComplete(Task<T> task, long time, TimeUnit timeUnit) {
    return runAndWaitForPlanToComplete("runAndWaitForPlanToComplete", task, time, timeUnit);
  }

  /**
   * Runs a task and verifies that it finishes with an error.
   * @param desc description of a test
   * @param task task to run
   * @param exceptionClass expected exception class
   * @param time amount of time to wait for task completion
   * @param timeUnit unit of time
   * @param <T> expected exception type
   * @return error returned by the task
   */
  public <T extends Throwable> T runAndWaitException(final String desc, Task<?> task, Class<T> exceptionClass,
      long time, TimeUnit timeUnit) {
    try {
      runAndWait(desc, task, time, timeUnit);
      throw new AssertionError("An exception is expected, but the task succeeded");
    } catch (PromiseException pe) {
      Throwable cause = pe.getCause();
      assertEquals(cause.getClass(), exceptionClass);
      return exceptionClass.cast(cause);
    } finally {
      logTracingResults(desc, task);
    }
  }

  //We don't want to use TestNG assertions to make the test utilities
  // class useful for non TestNG users (for example, JUnit).
  //Hence, we're writing our own private assertEquals method
  static void assertEquals(Object o1, Object o2) {
    if ((o1 == null && o2 != null) || (o1 != null && !o1.equals(o2))) {
      throw new AssertionError("Object " + o1 + " is expected to be equal to object: " + o2);
    }
  }

  /**
   * Equivalent to {@code runAndWaitException(desc, task, exceptionClass, 5, TimeUnit.SECONDS)}.
   * @see #runAndWaitException(String, Task, Class, long, TimeUnit)
   */
  public <T extends Throwable> T runAndWaitException(final String desc, Task<?> task, Class<T> exceptionClass) {
    return runAndWaitException(desc, task, exceptionClass, 5, TimeUnit.SECONDS);
  }

  /**
   * Equivalent to {@code runAndWaitException(this.getClass().getName(), task, exceptionClass)}.
   * @see #runAndWaitException(String, Task, Class, long, TimeUnit)
   */
  public <T extends Throwable> T runAndWaitException(Task<?> task, Class<T> exceptionClass) {
    return runAndWaitException("runAndWaitException", task, exceptionClass);
  }

  /**
   * Equivalent to {@code runAndWaitException(this.getClass().getName(), task, exceptionClass, time, timeUnit)}.
   * @see #runAndWaitException(String, Task, Class, long, TimeUnit)
   */
  public <T extends Throwable> T runAndWaitException(Task<?> task, Class<T> exceptionClass, long time, TimeUnit timeUnit) {
    return runAndWaitException("runAndWaitException", task, exceptionClass, time, timeUnit);
  }

  /**
   * Runs task.
   * @param task task to run
   */
  public void run(Task<?> task) {
    _engine.run(task);
  }

  public void logTracingResults(final String test, final Task<?> task) {
    try {
      LOG.info("Trace [" + test + "]:\n" + TraceUtil.getJsonTrace(task));
    } catch (IOException e) {
      LOG.error("Failed to encode JSON");
    }
  }

  public void setLogLevel(final String loggerName, final int level) {
    _loggerFactory.getLogger(loggerName).setLogLevel(level);
  }

  public List<ListLogger.Entry> getLogEntries(final String loggerName) {
    return _loggerFactory.getLogger(loggerName).getEntries();
  }

  public void resetLoggers() {
    _loggerFactory.reset();
  }

  /**
   * Returns task which completes with given value after specified period
   * of time. Timer starts counting the moment this method is invoked.
   */
  public <T> Task<T> delayedValue(T value, long time, TimeUnit timeUnit) {
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
  public <T> Task<T> delayedFailure(Throwable error, long time, TimeUnit timeUnit) {
    return Task.async(error.toString() + " delayed " + time + " " + TimeUnitHelper.toString(timeUnit), () -> {
      final SettablePromise<T> promise = Promises.settable();
      _scheduler.schedule(() -> promise.fail(error), time, timeUnit);
      return promise;
    });
  }

  public int countTasks(Trace trace) {
    return trace.getTraceMap().size();
  }

  private static final class TaskDoneListener implements PlanCompletionListener {

    private final ConcurrentMap<Task<?>, CountDownLatch> _taskDoneLatch = new ConcurrentHashMap<>();

    @Override
    public void onPlanCompleted(PlanContext planContext) {
      CountDownLatch latch = _taskDoneLatch.computeIfAbsent(planContext.getRootTask(), key -> new CountDownLatch(1));
      latch.countDown();
    }

    public void await(Task<?> root, long timeout, TimeUnit unit) throws InterruptedException {
      CountDownLatch latch = _taskDoneLatch.computeIfAbsent(root, key -> new CountDownLatch(1));
      latch.await(timeout, unit);
    }
  }

}
