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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestEngineConcurrentPlans {
  private Engine _engine;
  private ScheduledExecutorService _scheduler;

  @BeforeMethod
  public void setUp() throws Exception {
    final int numCores = Runtime.getRuntime().availableProcessors();
    _scheduler = Executors.newScheduledThreadPool(numCores + 1);
    _engine = new EngineBuilder().setTaskExecutor(_scheduler).setTimerScheduler(_scheduler)
        .setEngineProperty(Engine.MAX_CONCURRENT_PLANS, 10).build();
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
  public void testRunWithinCapacity() throws InterruptedException {

    final AtomicInteger counter = new AtomicInteger(0);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(counter::incrementAndGet);
    }

    for (int i = 0; i < 10; i++) {
      _engine.run(tasks[i]);
    }

    assertTrue(tasks[9].await(5, TimeUnit.SECONDS));
    assertEquals(counter.get(), 10);
  }

  @Test
  public void testTryRunWithinCapacity() throws InterruptedException {

    final AtomicInteger counter = new AtomicInteger(0);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(counter::incrementAndGet);
    }

    for (int i = 0; i < 10; i++) {
      assertTrue(_engine.tryRun(tasks[i]));
    }

    assertTrue(tasks[9].await(5, TimeUnit.SECONDS));
    assertEquals(counter.get(), 10);
  }

  @Test
  public void testTimeBoundedTryRunWithinCapacity() throws InterruptedException {

    final AtomicInteger counter = new AtomicInteger(0);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(counter::incrementAndGet);
    }

    for (int i = 0; i < 10; i++) {
      assertTrue(_engine.tryRun(tasks[i], 10, TimeUnit.MILLISECONDS));
    }

    assertTrue(tasks[9].await(5, TimeUnit.SECONDS));
    assertEquals(counter.get(), 10);
  }

  @Test
  public void testBlocking() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(latch::await);
    }

    //within capacity
    for (int i = 0; i < 10; i++) {
      _engine.run(tasks[i]);
    }

    //blocking one
    assertFalse(_engine.tryRun(Task.action(() -> {})));

    //release tasks
    latch.countDown();

    //wait for tasks to finish
    assertTrue(tasks[9].await(5, TimeUnit.SECONDS));

    //should be unblocked
    assertTrue(_engine.tryRun(Task.action(() -> {})));
  }

  @Test
  public void testTimeBoundedBlocking() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(latch::await);
    }

    //within capacity
    for (int i = 0; i < 10; i++) {
      _engine.run(tasks[i]);
    }

    //time bounded blocking
    assertFalse(_engine.tryRun(Task.action(() -> {}), 10, TimeUnit.MILLISECONDS));

    //release tasks
    latch.countDown();

    //wait for tasks to finish
    assertTrue(tasks[9].await(5, TimeUnit.SECONDS));

    //should be unblocked
    assertTrue(_engine.tryRun(Task.action(() -> {})));
  }

}
