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

import static org.testng.Assert.expectThrows;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.linkedin.parseq.internal.LIFOBiPriorityQueue;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;
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
    EngineBuilder builder = engineBuilder();
    if (track) {
      builder.setEngineProperty(Engine.TRACK_ACTIVE_PLANS, true);
    }
    _engine = builder.build();
    return _engine;
  }

  private EngineBuilder engineBuilder() {
    return new EngineBuilder().setTaskExecutor(_scheduler).setTimerScheduler(_scheduler);
  }

  @Test
  public void testCountAndPlansWhileRunningAndAfterCompletion() throws Exception {
    Engine engine = engine(true);
    SettablePromise<String> result = Promises.settable();
    Task<String> pending = Task.async("pendingTask", () -> result);

    engine.run(pending, "testPlanClass");

    assertEquals(1L, engine.getActivePlanCount());
    List<ActivePlanInfo> plans = engine.getActivePlans();
    assertEquals(1, plans.size());
    ActivePlanInfo plan = plans.get(0);
    assertEquals("testPlanClass", plan.getPlanClass());
    assertEquals("pendingTask", plan.getRootTaskName());
    long ageNanos = plan.getAge(TimeUnit.NANOSECONDS);
    assertEquals(TimeUnit.NANOSECONDS.toMillis(ageNanos), plan.getAge(TimeUnit.MILLISECONDS));
    expectThrows(NullPointerException.class, () -> plan.getAge(null));
    expectThrows(UnsupportedOperationException.class, plans::clear);

    result.done("done");
    assertTrue(pending.await(1, TimeUnit.SECONDS));
    waitForCount(engine, 0);
    assertTrue(engine.getActivePlans().isEmpty());
    assertEquals(pending.getTrace().getPlanId().longValue(), plan.getPlanId());
    assertEquals(ageNanos, plan.getAge(TimeUnit.NANOSECONDS));
  }

  @Test
  public void testPlansEmptyWhenTrackingDisabledButCountWorks() throws Exception {
    Engine engine = engine(false);
    SettablePromise<String> result = Promises.settable();
    Task<String> pending = Task.async("pendingTask", () -> result);

    engine.run(pending);

    assertEquals(1L, engine.getActivePlanCount());
    assertTrue(engine.getActivePlans().isEmpty());
    assertFalse(engine.getOldestActivePlanAge(TimeUnit.NANOSECONDS).isPresent());

    result.done("done");
    waitForCount(engine, 0);
  }

  @Test
  public void testOldestActivePlanAgeFollowsRemainingPlans() throws Exception {
    Engine engine = engine(true);
    expectThrows(NullPointerException.class, () -> engine.getOldestActivePlanAge(null));
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

  @Test
  public void testAlreadyResolvedRootsDoNotLeaveStalePlans() throws Exception {
    Engine engine = engine(true);
    Task<String> cancelled = Task.value("cancelled");
    assertTrue(cancelled.cancel(new RuntimeException("cancelled")));
    engine.run(cancelled);
    assertTrue(engine.getActivePlans().isEmpty());

    Task<String> completed = Task.value("completed");
    engine.run(completed);
    assertTrue(completed.await(2, TimeUnit.SECONDS));
    waitForCount(engine, 0);
    engine.run(completed);
    assertEquals(0L, engine.getActivePlanCount());
    assertTrue(engine.getActivePlans().isEmpty());
  }

  @Test
  public void testCancellationDuringListenerRegistration() throws Exception {
    Engine engine = engine(true);
    CountDownLatch listenerAttached = new CountDownLatch(1);
    CountDownLatch resumeSubmission = new CountDownLatch(1);
    AtomicBoolean firstListener = new AtomicBoolean(true);
    Task<String> root = new BaseTask<String>("root") {
      @Override
      public void addListener(PromiseListener<String> listener) {
        super.addListener(listener);
        if (firstListener.getAndSet(false)) {
          listenerAttached.countDown();
          try {
            assertTrue(resumeSubmission.await(5, TimeUnit.SECONDS));
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError(e);
          }
        }
      }

      @Override
      protected Promise<? extends String> run(Context context) {
        return Promises.value("done");
      }
    };

    Future<?> submission = _scheduler.submit(() -> engine.run(root));
    try {
      assertTrue(listenerAttached.await(2, TimeUnit.SECONDS));
      assertTrue(root.cancel(new RuntimeException("cancelled")));
      waitForCount(engine, 0);
    } finally {
      resumeSubmission.countDown();
    }
    submission.get(2, TimeUnit.SECONDS);
    assertTrue(engine.getActivePlans().isEmpty());
  }

  @Test
  public void testPlanStaysActiveUntilSideEffectCompletes() throws Exception {
    Engine engine = engine(true);
    SettablePromise<String> result = Promises.settable();
    Task<String> sideEffect = Task.async("sideEffect", () -> result);
    Task<String> root = Task.value("value").withSideEffect("fork", value -> sideEffect);
    engine.run(root);

    assertTrue(root.await(2, TimeUnit.SECONDS));
    assertEquals(1L, engine.getActivePlanCount());
    assertEquals(1, engine.getActivePlans().size());

    result.done("done");
    waitForCount(engine, 0);
    assertTrue(engine.getActivePlans().isEmpty());
  }

  @Test
  public void testBrokenTaskNameDoesNotPreventExecution() throws Exception {
    Engine engine = engine(true);
    SettablePromise<String> result = Promises.settable();
    AtomicBoolean failNextNameRead = new AtomicBoolean();
    Task<String> root = new BaseTask<String>("root") {
      @Override
      public String getName() {
        if (failNextNameRead.getAndSet(false)) {
          throw new IllegalStateException("name unavailable");
        }
        return super.getName();
      }

      @Override
      protected Promise<? extends String> run(Context context) {
        return result;
      }
    };

    failNextNameRead.set(true);
    engine.run(root);
    assertEquals(root.getClass().getName(), engine.getActivePlans().get(0).getRootTaskName());
    result.done("done");
    waitForCount(engine, 0);
    assertEquals("done", root.get());
  }

  @Test
  public void testConstructionFailureReleasesTrackingAndCapacity() throws Exception {
    AtomicBoolean firstQueue = new AtomicBoolean(true);
    Engine engine = engineBuilder().setEngineProperty(Engine.TRACK_ACTIVE_PLANS, true)
        .setEngineProperty(Engine.MAX_CONCURRENT_PLANS, 1)
        .setTaskQueueFactory(() -> {
          if (firstQueue.getAndSet(false)) {
            throw new IllegalStateException("queue creation failed");
          }
          return new LIFOBiPriorityQueue<>();
        }).build();
    _engine = engine;

    expectThrows(IllegalStateException.class, () -> engine.run(Task.value("first")));
    assertEquals(0L, engine.getActivePlanCount());
    assertTrue(engine.getActivePlans().isEmpty());
    SettablePromise<String> result = Promises.settable();
    assertTrue(engine.tryRun(Task.async("next", () -> result)));
    assertFalse(engine.tryRun(Task.value("over capacity")));
    assertEquals(1, engine.getActivePlans().size());
    result.done("done");
    waitForCount(engine, 0);
  }

  @Test
  public void testExecutorRejectionAndThrowingCompletionListenerCleanUp() throws Exception {
    _engine = engineBuilder().setEngineProperty(Engine.TRACK_ACTIVE_PLANS, true)
        .setTaskExecutor(command -> { throw new RejectedExecutionException("rejected"); })
        .setPlanCompletionListener(plan -> { throw new IllegalStateException("listener failed"); }).build();
    Task<String> task = Task.value("rejected");
    _engine.run(task);

    assertTrue(task.await(2, TimeUnit.SECONDS));
    assertTrue(task.isFailed());
    waitForCount(_engine, 0);
    assertTrue(_engine.getActivePlans().isEmpty());
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
  public void testPlanIsNotTrackedWhenEngineRefusesTask() {
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
