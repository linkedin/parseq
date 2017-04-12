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

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.trace.Trace;


/**
 * A parent class for base unit test classes. It contains definitions
 * for all helper methods and delegates them to instance of
 * {@link ParSeqUnitTestHelper}.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public abstract class AbstractBaseEngineTest {

  private final ParSeqUnitTestHelper _parSeqUnitTestHelper;

  public AbstractBaseEngineTest() {
    _parSeqUnitTestHelper = new ParSeqUnitTestHelper(this::customizeEngine);
  }

  abstract protected void customizeEngine(EngineBuilder engineBuilder);

  protected ParSeqUnitTestHelper getParSeqUnitTestHelper() {
    return _parSeqUnitTestHelper;
  }

  protected Engine getEngine() {
    return _parSeqUnitTestHelper.getEngine();
  }

  protected ScheduledExecutorService getScheduler() {
    return _parSeqUnitTestHelper.getScheduler();
  }

  /**
   * Equivalent to {@code runAndWait("runAndWait", task)}.
   * @see #runAndWait(String, Task, long, TimeUnit)
   */
  protected <T> T runAndWait(Task<T> task) {
    return _parSeqUnitTestHelper.runAndWait(this.getClass().getName(), task);
  }

  /**
   * Equivalent to {@code runAndWait("runAndWait", task, 5, TimeUnit.SECONDS)}.
   * @see #runAndWait(String, Task, long, TimeUnit)
   */
  protected <T> T runAndWait(Task<T> task, long time, TimeUnit timeUnit) {
    return _parSeqUnitTestHelper.runAndWait(this.getClass().getName(), task, time, timeUnit);
  }

  /**
   * Equivalent to {@code runAndWait(desc, task, 5, TimeUnit.SECONDS)}.
   * @see #runAndWait(String, Task, long, TimeUnit)
   */
  protected <T> T runAndWait(final String desc, Task<T> task) {
    return _parSeqUnitTestHelper.runAndWait(desc, task);
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
    return _parSeqUnitTestHelper.runAndWait(desc, task, time, timeUnit);
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
  protected <T> T runAndWaitForPlanToComplete(final String desc, Task<T> task, long time, TimeUnit timeUnit) {
    return _parSeqUnitTestHelper.runAndWaitForPlanToComplete(desc, task, time, timeUnit);
  }

  protected <T> T runAndWaitForPlanToComplete(Task<T> task, long time, TimeUnit timeUnit) {
    return _parSeqUnitTestHelper.runAndWaitForPlanToComplete(this.getClass().getName(), task, time, timeUnit);
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
  protected <T extends Throwable> T runAndWaitException(final String desc, Task<?> task, Class<T> exceptionClass,
      long time, TimeUnit timeUnit) {
    return _parSeqUnitTestHelper.runAndWaitException(desc, task, exceptionClass, time, timeUnit);
  }

  /**
   * Equivalent to {@code runAndWaitException(desc, task, exceptionClass, 5, TimeUnit.SECONDS)}.
   * @see #runAndWaitException(String, Task, Class, long, TimeUnit)
   */
  protected <T extends Throwable> T runAndWaitException(final String desc, Task<?> task, Class<T> exceptionClass) {
    return runAndWaitException(desc, task, exceptionClass, 5, TimeUnit.SECONDS);
  }

  /**
   * Equivalent to {@code runAndWaitException("runAndWaitException", task, exceptionClass)}.
   * @see #runAndWaitException(String, Task, Class, long, TimeUnit)
   */
  protected <T extends Throwable> T runAndWaitException(Task<?> task, Class<T> exceptionClass) {
    return runAndWaitException(this.getClass().getName(), task, exceptionClass);
  }

  /**
   * Equivalent to {@code runAndWaitException("runAndWaitException", task, exceptionClass, time, timeUnit)}.
   * @see #runAndWaitException(String, Task, Class, long, TimeUnit)
   */
  protected <T extends Throwable> T runAndWaitException(Task<?> task, Class<T> exceptionClass, long time, TimeUnit timeUnit) {
    return runAndWaitException(this.getClass().getName(), task, exceptionClass, time, timeUnit);
  }

  /**
   * Runs task.
   * @param task task to run
   */
  protected void run(Task<?> task) {
    _parSeqUnitTestHelper.run(task);
  }

  protected void logTracingResults(final String test, final Task<?> task) {
    _parSeqUnitTestHelper.logTracingResults(test, task);
  }

  protected void setLogLevel(final String loggerName, final int level) {
    _parSeqUnitTestHelper.setLogLevel(loggerName, level);
  }

  protected List<ListLogger.Entry> getLogEntries(final String loggerName) {
    return _parSeqUnitTestHelper.getLogEntries(loggerName);
  }

  protected void resetLoggers() {
    _parSeqUnitTestHelper.resetLoggers();
  }

  /**
   * Returns task which completes with given value after specified period
   * of time. Timer starts counting the moment this method is invoked.
   */
  protected <T> Task<T> delayedValue(T value, long time, TimeUnit timeUnit) {
    return _parSeqUnitTestHelper.delayedValue(value, time, timeUnit);
  }

  /**
   * Returns task which fails with given error after specified period
   * of time. Timer starts counting the moment this method is invoked.
   */
  protected <T> Task<T> delayedFailure(Throwable error, long time, TimeUnit timeUnit) {
    return _parSeqUnitTestHelper.delayedFailure(error, time, timeUnit);
  }

  protected int countTasks(Trace trace) {
    return _parSeqUnitTestHelper.countTasks(trace);
  }
}
