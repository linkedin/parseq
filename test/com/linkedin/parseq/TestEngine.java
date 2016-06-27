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

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.linkedin.parseq.TestUtil.withDisabledLogging;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;


/**
 * @author Chris Pettitt
 */
public class TestEngine {
  private Engine _engine;
  private ScheduledExecutorService _scheduler;

  @BeforeMethod
  public void setUp() throws Exception {
    final int numCores = Runtime.getRuntime().availableProcessors();
    _scheduler = Executors.newScheduledThreadPool(numCores + 1);
    _engine = new EngineBuilder().setTaskExecutor(_scheduler).setTimerScheduler(_scheduler).build();
  }

  @AfterMethod
  public void tearDown() throws Exception {
    _engine.shutdown();
    _engine.awaitTermination(50, TimeUnit.MILLISECONDS);
    _engine = null;
    _scheduler.shutdownNow();
    _scheduler = null;
  }

  @Test
  public void testShutdownWithNoTasks() throws InterruptedException {
    _engine.shutdown();
    assertTrue(_engine.isShutdown());
    assertTrue(_engine.awaitTermination(50, TimeUnit.MILLISECONDS));
    assertTrue(_engine.isTerminated());
    assertTrue(_engine.isShutdown());
  }

  @Test
  public void testShutdownThenRunTask() throws InterruptedException {
    _engine.shutdown();

    final Task<String> task = Task.value("task executed");
    _engine.run(task);

    // Task should be cancelled immediately
    assertTrue(task.await(50, TimeUnit.MILLISECONDS));
    assertTrue(task.isFailed());
  }

  @Test
  public void testShutdownWithRunningTask() throws InterruptedException {
    final CountDownLatch finishLatch = new CountDownLatch(1);
    final String taskValue = "task executed";
    final Task<String> task = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception {
        finishLatch.await();
        return Promises.value(taskValue);
      }
    };

    _engine.run(task);
    _engine.shutdown();

    // shutdown should not complete until after our task is done
    assertFalse(_engine.awaitTermination(50, TimeUnit.MILLISECONDS));
    assertTrue(_engine.isShutdown());
    assertFalse(_engine.isTerminated());
    finishLatch.countDown();
    assertTrue(_engine.awaitTermination(50, TimeUnit.MILLISECONDS));
    assertTrue(_engine.isShutdown());
    assertTrue(_engine.isTerminated());

    // Task should finish shortly
    assertTrue(task.await(50, TimeUnit.MILLISECONDS));
    assertEquals(taskValue, task.get());
  }

  @Test
  public void testShutdownWithRunningAndSuccessorTask() throws InterruptedException {
    final CountDownLatch finishLatch = new CountDownLatch(1);

    final String predValue = "task executed";
    final String sucValue = "task executed";

    final Task<String> predTask = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception {
        finishLatch.await();
        return Promises.value(predValue);
      }
    };
    final Task<String> sucTask = Task.value(sucValue);
    final Task<String> seq = predTask.andThen(sucTask);

    _engine.run(seq);
    _engine.shutdown();

    // shutdown should not complete until after our task is done
    assertFalse(_engine.awaitTermination(50, TimeUnit.MILLISECONDS));
    assertTrue(_engine.isShutdown());
    assertFalse(_engine.isTerminated());
    finishLatch.countDown();
    assertTrue(_engine.awaitTermination(50, TimeUnit.MILLISECONDS));
    assertTrue(_engine.isShutdown());
    assertTrue(_engine.isTerminated());

    // Tasks should finish shortly
    assertTrue(predTask.await(50, TimeUnit.MILLISECONDS));
    assertEquals(predValue, predTask.get());

    assertTrue(sucTask.await(50, TimeUnit.MILLISECONDS));
    assertEquals(sucValue, sucTask.get());
  }

  @Test
  public void testFailPlanExecution() throws InterruptedException {
    // This test ensures that if execution of a plan's serial executor loop
    // fails, e.g. in the case that the underlying executor is saturated, that
    // we fail the plan. To simplify this test, we constructor our own executor
    // instead of using the default executor set up for test.
    final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
        new ArrayBlockingQueue<Runnable>(1), new ThreadPoolExecutor.AbortPolicy());
    final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    try {
      final Engine engine =
          new EngineBuilder().setTaskExecutor(executorService).setTimerScheduler(scheduledExecutorService).build();

      // First we submit two tasks that will never finish. This saturates the
      // underlying executor by using its only thread and saturating its
      // single slot queue.
      engine.run(neverEndingBlockingTask());
      engine.run(neverEndingBlockingTask());

      // Now we submit another task. The execution loop for this task will fail
      // during submit to the underlying executor. We expect that it will be
      // cancelled.
      final Task<?> task = neverEndingBlockingTask();
      withDisabledLogging(new Runnable() {
        @Override
        public void run() {
          engine.run(task);
          try {
            assertTrue(task.await(5, TimeUnit.SECONDS));
          } catch (InterruptedException e) {
            // Ignore.
          }
        }
      });
      assertTrue(task.isFailed());
      assertTrue(
          "Expected underlying exception to be instance of RejectedExecutionException, but was: "
              + task.getError().getCause().getCause(),
          task.getError().getCause().getCause() instanceof RejectedExecutionException);

      engine.shutdown();
    } finally {
      scheduledExecutorService.shutdownNow();
      executorService.shutdownNow();
    }
  }

  /**
   * A task that blocks forever when it is executed, tying up whatever thread
   * executes it.
   */
  private Task<?> neverEndingBlockingTask() {
    return new BaseTask<Object>() {
      @Override
      protected Promise<?> run(Context context) throws Throwable {
        new CountDownLatch(1).await();
        return Promises.value("A value that should never be seen!");
      }
    };
  }
}
