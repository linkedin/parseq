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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Chris Pettitt
 */
public class TestEngine
{
  private Engine _engine;
  private ScheduledExecutorService _scheduler;

  @BeforeMethod
  public void setUp() throws Exception
  {
    final int numCores = Runtime.getRuntime().availableProcessors();
    _scheduler = Executors.newScheduledThreadPool(numCores + 1);
    _engine = new EngineBuilder()
        .setTaskExecutor(_scheduler)
        .setTimerScheduler(_scheduler)
        .build();
  }

  @AfterMethod
  public void tearDown() throws Exception
  {
    _engine.shutdown();
    _engine.awaitTermination(50, TimeUnit.MILLISECONDS);
    _engine = null;
    _scheduler.shutdownNow();
    _scheduler = null;
  }


  @Test
  public void testShutdownWithNoTasks() throws InterruptedException
  {
    _engine.shutdown();
    assertTrue(_engine.isShutdown());
    assertTrue(_engine.awaitTermination(50, TimeUnit.MILLISECONDS));
    assertTrue(_engine.isTerminated());
    assertTrue(_engine.isShutdown());
  }

  @Test
  public void testShutdownThenRunTask() throws InterruptedException
  {
    _engine.shutdown();

    final Task<String> task = TestUtil.value("task executed");
    _engine.run(task);

    // Task should be cancelled immediately
    assertTrue(task.await(50, TimeUnit.MILLISECONDS));
    assertTrue(task.isFailed());
  }

  @Test
  public void testShutdownWithRunningTask() throws InterruptedException
  {
    final CountDownLatch finishLatch = new CountDownLatch(1);
    final String taskValue = "task executed";
    final Task<String> task = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception
      {
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
  public void testShutdownWithRunningAndSuccessorTask() throws InterruptedException
  {
    final CountDownLatch finishLatch = new CountDownLatch(1);

    final String predValue = "task executed";
    final String sucValue = "task executed";

    final Task<String> predTask = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception
      {
        finishLatch.await();
        return Promises.value(predValue);
      }
    };
    final Task<String> sucTask = TestUtil.value(sucValue);
    final Task<String> seq = Tasks.seq(predTask, sucTask);

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
}
