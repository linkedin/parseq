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

import com.linkedin.parseq.internal.DefaultPlanBasedRateLimiter;
import com.linkedin.parseq.internal.PlanBasedRateLimiter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;


/**
 * @author Min Chen (mnchen@linkedin.com)
 */
public class TestEnginePlanBasedRateLimit {
  private Engine _engine;
  private ScheduledExecutorService _scheduler;

  @BeforeMethod
  public void setUp() throws Exception {
    final int numCores = Runtime.getRuntime().availableProcessors();
    _scheduler = Executors.newScheduledThreadPool(numCores + 1);
    Map<String, Integer> planConcurrencyConfig = new HashMap<>();
    planConcurrencyConfig.put("evenPlan", 5);
    planConcurrencyConfig.put("oddPlan", 5);
    PlanBasedRateLimiter rateLimiter = new DefaultPlanBasedRateLimiter(planConcurrencyConfig);
    _engine = new EngineBuilder().setTaskExecutor(_scheduler).setTimerScheduler(_scheduler)
        .setEngineProperty(Engine.MAX_CONCURRENT_PLANS, 12).setPlanClassRateLimiter(rateLimiter).build();
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
      _engine.run(tasks[i], (i % 2 == 0) ? "evenPlan" : "oddPlan");
    }

    assertTrue(awaitAll(tasks));
    assertEquals(10, counter.get());
  }

  private boolean awaitAll(Task<?>[] tasks) throws InterruptedException {
    for (int i = 0; i < tasks.length; i++) {
      if (!tasks[i].await(5, TimeUnit.SECONDS)) {
        return false;
      }
    }
    return true;
  }

  @Test
  public void testTryRunWithinCapacity() throws InterruptedException {

    final AtomicInteger counter = new AtomicInteger(0);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(counter::incrementAndGet);
    }

    for (int i = 0; i < 10; i++) {
      assertTrue(_engine.tryRun(tasks[i], (i % 2 == 0) ? "evenPlan" : "oddPlan"));
    }

    assertTrue(awaitAll(tasks));
    assertEquals(10, counter.get());
  }

  @Test
  public void testBlockingRunWithinCapacity() throws InterruptedException {

    final AtomicInteger counter = new AtomicInteger(0);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(counter::incrementAndGet);
    }

    for (int i = 0; i < 10; i++) {
      _engine.blockingRun(tasks[i], (i % 2 == 0) ? "evenPlan" : "oddPlan");
    }

    assertTrue(awaitAll(tasks));
    assertEquals(10, counter.get());
  }

  @Test
  public void testTimeBoundedTryRunWithinCapacity() throws InterruptedException {

    final AtomicInteger counter = new AtomicInteger(0);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(counter::incrementAndGet);
    }

    for (int i = 0; i < 10; i++) {
      assertTrue(_engine.tryRun(tasks[i], (i % 2 == 0) ? "evenPlan" : "oddPlan",
          10, TimeUnit.MILLISECONDS));
    }

    assertTrue(awaitAll(tasks));
    assertEquals(10, counter.get());
  }

  @Test
  public void testRunOverCapacity() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(latch::await);
    }

    //within capacity
    for (int i = 0; i < 10; i++) {
      _engine.run(tasks[i], (i % 2 == 0) ? "evenPlan" : "oddPlan");
    }

    try {
      _engine.run(Task.action(() -> {}), "oddPlan");
      fail();
    } catch (IllegalStateException e) {
      //expected
    }

    //release tasks
    latch.countDown();

    //wait for tasks to finish
    assertTrue(awaitAll(tasks));

    //should be unblocked
    _engine.run(Task.action(() -> {}), "oddPlan");
  }

  @Test
  public void testTryRunOverCapacity() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(latch::await);
    }

    //within capacity
    for (int i = 0; i < 10; i++) {
      assertTrue(_engine.tryRun(tasks[i], (i % 2 == 0) ? "evenPlan" : "oddPlan"));
    }

    assertFalse(_engine.tryRun(Task.action(() -> {}), "oddPlan"));

    //release tasks
    latch.countDown();

    //wait for tasks to finish
    assertTrue(awaitAll(tasks));

    //should be unblocked
    assertTrue(_engine.tryRun(Task.action(() -> {})));
  }

  @Test
  public void testTimeBoundedTryRunOverCapacity() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(latch::await);
    }

    //within capacity
    for (int i = 0; i < 10; i++) {
      assertTrue(_engine.tryRun(tasks[i], (i % 2 == 0) ? "evenPlan" : "oddPlan", 10, TimeUnit.MILLISECONDS));
    }

    assertFalse(_engine.tryRun(Task.action(() -> {}), "oddPlan", 10, TimeUnit.MILLISECONDS));

    //release tasks
    latch.countDown();

    //wait for tasks to finish
    assertTrue(awaitAll(tasks));

    //should be unblocked
    assertTrue(_engine.tryRun(Task.action(() -> {}), "oddPlan", 10, TimeUnit.MILLISECONDS));
  }

  @Test
  public void testBlockingRunOverCapacity() throws InterruptedException {

    final CountDownLatch latch = new CountDownLatch(1);

    Task<?>[] tasks = new Task<?>[10];

    for (int i = 0; i < 10; i++) {
      tasks[i] = Task.action(latch::await);
    }

    //within capacity
    for (int i = 0; i < 10; i++) {
      _engine.blockingRun(tasks[i], (i % 2 == 0) ? "evenPlan" : "oddPlan");
    }

    final CountDownLatch blockedTaskLatch = new CountDownLatch(1);
    final CountDownLatch blockedThreadLatch = new CountDownLatch(1);
    new Thread(() -> {
      blockedThreadLatch.countDown();
      _engine.blockingRun(Task.action(() -> { blockedTaskLatch.countDown(); }), "oddPlan");
    }).start();

    //first wait for a background thread to reach _engine.run()
    assertTrue(blockedThreadLatch.await(5, TimeUnit.SECONDS));

    //sleep for 200ms to make sure background thread executed _engine.run()
    Thread.sleep(200);

    //verify background tasks didn't run
    assertEquals(1L, blockedTaskLatch.getCount());

    //release tasks
    latch.countDown();

    //background thread should be unblocked and background task should eventually run
    assertTrue(blockedTaskLatch.await(5, TimeUnit.SECONDS));
  }

}
