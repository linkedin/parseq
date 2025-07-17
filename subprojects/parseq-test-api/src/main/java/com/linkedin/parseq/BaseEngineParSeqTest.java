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

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.ParSeqUnitTestHelper;
import com.linkedin.parseq.Task;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * This is a custom implementation of {@link com.linkedin.parseq.BaseEngineTest}. This custom
 * implementation is necessary to avoid race-conditions that we have in BaseEngineTest when running tests
 * in parallel. It's a variant of {@link com.linkedin.parseq.BaseEngineTest}, and has the following
 * differences:
 *
 * <p>1) Setup and tearDown are executed at test-suite level. Whereas {@link
 * com.linkedin.parseq.BaseEngineTest} executes setup()/teardown() at class level (which can cause
 * race-conditions if two or more tests from the same class are executed in parallel).
 *
 * <p>2) It uses mutex locking mechanism to set up and tear down {@link ParSeqUnitTestHelper} to
 * avoid race-condition on ParSeqUnitTestHelper instance (it's a common resource among tests).
 *
 * <p>3) Default timeout for runAndWait() method is 10 seconds (which is needed by majority of the tests).
 * Tests can override this by using the runAndWait() method that accepts timeout param.
 *
 */
public class BaseEngineParSeqTest {
  private static final Object MUTEX = new Object();
  private static final int RUN_AND_WAIT_TIMEOUT_SECONDS = 10;
  private static final ParSeqUnitTestHelper PAR_SEQ_UNIT_TEST_HELPER = new ParSeqUnitTestHelper();

  /*
   * Get ParSeqUnitTestHelper
   *
   * @return ParSeqUnitTestHelper instance.
   */
  protected static ParSeqUnitTestHelper getParSeqUnitTestHelper() {
    return PAR_SEQ_UNIT_TEST_HELPER;
  }

  /**
   * Setup ParSeqUnitTestHelper
   *
   * @throws Exception
   */
  @BeforeSuite
  public void setUpParSeqHelper() throws Exception {
    synchronized (MUTEX) {
      getParSeqUnitTestHelper().setUp();
    }
  }

  /**
   * Tear down ParSeqUnitTestHelper
   *
   * @throws Exception
   */
  @AfterSuite
  public void tearDownParSeqHelper() throws Exception {
    synchronized (MUTEX) {
      if (getParSeqUnitTestHelper().getEngine() != null) {
        getParSeqUnitTestHelper().tearDown();
      } else {
        throw new RuntimeException(
            "Tried to shut down Engine but it either has not even been created or "
                + "has already been shut down, in "
                + this.getClass().getName());
      }
    }
  }

  /**
   * Run a parseq task and wait for it to complete, including all side-effects (e.g.
   * Task.withSafeSideEffect). The latter is necessary to run tests in parallel without any
   * race-conditions.
   *
   * <p>Uses a default timeout (10 seconds).
   *
   * @param <T> The type of the task.
   * @param task The task to run.
   * @return The same task after task completion, it is safe to call task.get() after running this
   *     method.
   */
  protected <T> T runAndWait(@Nonnull Task<T> task) {
    return runAndWait(task, RUN_AND_WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
  }

  /**
   * Run a parseq task and wait for it to complete, including all side-effects (e.g.
   * Task.withSafeSideEffect). The latter is necessary to run tests in parallel without any
   * race-conditions.
   *
   * <p>Uses a custom timeout provided by the caller.
   *
   * @param <T> The type of the task.
   * @param task The task to run.
   * @param timeOut the amount of time to wait for task completion.
   * @param timeUnit the timeUnit (seconds, milliseconds, etc) for timeOut param.
   * @return The same task after task completion, it is safe to call task.get() after running this
   *     method.
   */
  protected <T> T runAndWait(@Nonnull Task<T> task, int timeOut, @Nonnull TimeUnit timeUnit) {
    synchronized (MUTEX) {
      return getParSeqUnitTestHelper().runAndWaitForPlanToComplete(this.getClass().getName(), task, timeOut, timeUnit);
    }
  }

  /**
   * Run a parseq task and wait for it to complete, including all side-effects (e.g.
   * Task.withSafeSideEffect). The latter is necessary to run tests in parallel without any
   * race-conditions.
   *
   * <p>Also verify that the task threw an exception and then return that Exception.
   *
   * <p>Use a default timeout (10 seconds).
   *
   * @param task The Task that should be run.
   * @param exceptionClass The class of Exception that should be thrown when the task completes.
   * @param <T> The type of Exception that should be returned.
   * @return The exception that was encountered while the Task was running.
   */
  protected <T extends Throwable> T runAndWaitException(@Nonnull Task<?> task, @Nonnull Class<T> exceptionClass) {
    synchronized (MUTEX) {
      return getParSeqUnitTestHelper()
          .runAndWaitExceptionOnPlanCompletion(
              this.getClass().getName(), task, exceptionClass, RUN_AND_WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
  }

  /**
   * Get the instance of parseq engine used for running tasks in tests.
   *
   * @return instance of {@link Engine} used for running tasks in tests.
   */
  protected Engine getEngine() {
    return getParSeqUnitTestHelper().getEngine();
  }
}
