/*
 * Copyright 2026 LinkedIn, Inc
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

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


public class TestEngineActivePlans {
  private ScheduledExecutorService _scheduler;
  private Engine _engine;

  @BeforeMethod
  public void setUp() {
    _scheduler = Executors.newScheduledThreadPool(2);
  }

  @AfterMethod
  public void tearDown() throws Exception {
    _engine.shutdown();
    _engine.awaitTermination(1, TimeUnit.SECONDS);
    _scheduler.shutdownNow();
  }

  private Engine engine(boolean track) {
    EngineBuilder builder = new EngineBuilder().setTaskExecutor(_scheduler).setTimerScheduler(_scheduler);
    if (track) {
      builder.setEngineProperty(Engine.TRACK_ACTIVE_PLANS, true);
    }
    _engine = builder.build();
    return _engine;
  }

  @Test
  public void testCountAndPlansWhileRunningAndAfterCompletion() throws Exception {
    Engine engine = engine(true);
    Task<String> pending = Task.async("pendingTask", () -> com.linkedin.parseq.promise.Promises.<String>settable());

    engine.run(pending, "testPlanClass");

    assertEquals(1L, engine.getActivePlanCount());
    List<ActivePlanInfo> plans = engine.getActivePlans();
    assertEquals(1, plans.size());
    assertEquals("testPlanClass", plans.get(0).getPlanClass());
    assertEquals("pendingTask", plans.get(0).getRootTaskName());
    assertTrue(plans.get(0).getAge(TimeUnit.NANOSECONDS) >= 0);

    pending.cancel(new RuntimeException("done"));
    assertTrue(pending.await(1, TimeUnit.SECONDS));
    waitForCount(engine, 0);
    assertTrue(engine.getActivePlans().isEmpty());
  }

  @Test
  public void testPlansEmptyWhenTrackingDisabledButCountWorks() throws Exception {
    Engine engine = engine(false);
    Task<String> pending = Task.async("pendingTask", () -> com.linkedin.parseq.promise.Promises.<String>settable());

    engine.run(pending);

    assertEquals(1L, engine.getActivePlanCount());
    assertTrue(engine.getActivePlans().isEmpty());
    assertFalse(engine.getOldestActivePlanAge(TimeUnit.NANOSECONDS).isPresent());

    pending.cancel(new RuntimeException("done"));
    waitForCount(engine, 0);
  }

  @Test
  public void testOldestActivePlanAgeFollowsRemainingPlans() throws Exception {
    Engine engine = engine(true);
    assertEquals(0L, engine.getOldestActivePlanAge(TimeUnit.NANOSECONDS).getAsLong());

    SettablePromise<String> first = Promises.settable();
    SettablePromise<String> second = Promises.settable();
    engine.run(Task.async("first", () -> first));
    engine.run(Task.async("second", () -> second));
    assertOldestAgeMatchesPlan(engine, "first");

    first.done("done");
    waitForCount(engine, 1);
    assertOldestAgeMatchesPlan(engine, "second");

    second.done("done");
    waitForCount(engine, 0);
    assertEquals(0L, engine.getOldestActivePlanAge(TimeUnit.NANOSECONDS).getAsLong());
  }

  private static void assertOldestAgeMatchesPlan(Engine engine, String taskName) {
    long before = planAge(engine, taskName);
    long oldest = engine.getOldestActivePlanAge(TimeUnit.NANOSECONDS).getAsLong();
    assertTrue(oldest >= before);
    assertTrue(oldest <= planAge(engine, taskName));
  }

  private static long planAge(Engine engine, String taskName) {
    return engine.getActivePlans().stream().filter(plan -> plan.getRootTaskName().equals(taskName)).findFirst().get()
        .getAge(TimeUnit.NANOSECONDS);
  }

  @Test
  public void testTrackedPlanIsRemovedWhenEngineRefusesTask() throws Exception {
    Engine engine = engine(true);
    engine.shutdown();

    engine.run(Task.value("x"));

    assertEquals(0L, engine.getActivePlanCount());
    assertTrue(engine.getActivePlans().isEmpty());
  }

  private static void waitForCount(Engine engine, long expected) throws InterruptedException {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
    while (engine.getActivePlanCount() != expected && System.nanoTime() < deadline) {
      Thread.sleep(5);
    }
    assertEquals(expected, engine.getActivePlanCount());
  }
}
