package com.linkedin.parseq;

import static org.testng.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.testng.annotations.Test;

import com.linkedin.parseq.internal.PlanContext;
import com.linkedin.parseq.internal.SerialExecutionException;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.Trace;

public class TestNoTracing extends BaseEngineTest {

  @FunctionalInterface
  private interface EngineConsumer {
    void accept(Engine engine) throws Exception;
  }

  @Override
  protected void customizeEngine(EngineBuilder builder) {
    builder.setEngineProperty(Engine.MAX_RELATIONSHIPS_PER_TRACE, 0);
    ListLoggerFactory loggers = new ListLoggerFactory();
    loggers.getLogger(Engine.LOGGER_BASE + ":all").setLogLevel(ListLogger.LEVEL_DEBUG);
    builder.setLoggerFactory(loggers);
  }

  private <T> void execute(Task<T> task) throws InterruptedException {
    getEngine().run(task, "disabled-plan");
    assertTrue(task.await(5, TimeUnit.SECONDS));
  }

  private void withDirectEngine(int limit, EngineConsumer body) throws Exception {
    withDirectEngine(limit, new ListLoggerFactory(), body);
  }

  private void withDirectEngine(int limit, ListLoggerFactory loggers, EngineConsumer body) throws Exception {
    Engine engine = new EngineBuilder().setTaskExecutor(Runnable::run).setTimerScheduler(getScheduler())
        .setLoggerFactory(loggers).setEngineProperty(Engine.MAX_RELATIONSHIPS_PER_TRACE, limit).build();
    try {
      body.accept(engine);
    } finally {
      engine.shutdown();
    }
  }

  private void assertDisabledTrace(Task<?> task) {
    Trace trace = task.getTrace();
    assertTrue(trace.getTraceMap().isEmpty());
    assertTrue(trace.getRelationships().isEmpty());
    assertEquals(trace.getPlanClass(), "none");
    assertEquals(trace.getPlanId(), Long.valueOf(0L));
    assertSame(task.getTrace(), trace);
    assertEquals(task.getShallowTrace(), task.getShallowTrace());
    assertEquals(task.getShallowTrace().getNativeId(), task.getId().longValue());
    assertEquals(task.getShallowTrace().getName(), task.getName());
    task.getShallowTraceBuilder().setName("ignored").addAttribute("ignored", "ignored");
    assertEquals(task.getShallowTrace().getName(), task.getName());
    assertFalse(task.getShallowTrace().getAttributes().containsKey("ignored"));
  }

  private void assertDisabled(Task<?> task) {
    assertDisabledTrace(task);
    assertEquals(task.getShallowTrace().getResultType(), ResultType.UNFINISHED);
    assertEquals(task.getShallowTrace().getNativeStartNanos(), -1L);
    assertEquals(task.getShallowTrace().getNativeEndNanos(), -1L);
  }

  @Test
  public void testFusionExecutesWithoutSerializingOrRecording() throws InterruptedException {
    AtomicInteger serialized = new AtomicInteger();
    Task<Integer> source = Task.value("source", 1);
    Task<Integer> middle = source.map("middle", n -> n + 1);
    Task<Integer> result = middle.map("result", n -> n + 1);
    for (Task<Integer> t : Arrays.asList(source, middle, result)) {
      t.setTraceValueSerializer(n -> { serialized.incrementAndGet(); return n.toString(); });
    }

    execute(result);

    assertEquals(result.get(), Integer.valueOf(3));
    assertEquals(middle.get(), Integer.valueOf(2));
    assertEquals(source.get(), Integer.valueOf(1));
    assertEquals(serialized.get(), 0);
    assertDisabled(source);
    assertDisabled(middle);
    assertDisabled(result);
  }

  @Test
  public void testAsyncFusionAndSharedPredecessorPreservePromises() throws InterruptedException {
    Task<Integer> source = Task.async("async", ctx -> Promises.value(2));
    Task<Integer> middle = source.map("middle", n -> n + 1);
    Task<Integer> left = middle.map("left", n -> n + 1);
    Task<Integer> right = middle.map("right", n -> n * 2);
    Task<Integer> result = Task.par(left, right).map("sum", (a, b) -> a + b);

    execute(result);

    assertEquals(result.get(), Integer.valueOf(10));
    assertEquals(middle.get(), Integer.valueOf(3));
    for (Task<?> t : Arrays.asList(source, middle, left, right, result)) {
      assertDisabled(t);
    }
  }

  @Test
  public void testContextAttributesAndFailureDoNotRecord() throws InterruptedException {
    RuntimeException error = new RuntimeException("expected");
    Task<Integer> task = Task.async("failure", ctx -> {
      ctx.getShallowTraceBuilder().addAttribute("key", "value").setValue("value").setHidden(true)
          .setTaskType("custom").setResultType(ResultType.SUCCESS).setNativeStartNanos(123L);
      throw error;
    });

    execute(task);

    assertSame(task.getError(), error);
    assertTrue(task.getShallowTraceBuilder().getAttributes().isEmpty());
    assertNull(task.getShallowTraceBuilder().getValue());
    assertFalse(task.getShallowTraceBuilder().getHidden());
    assertNull(task.getShallowTraceBuilder().getTaskType());
    assertDisabled(task);
  }

  @Test
  public void testEarlyFinishCancelsChildWithoutRecording() throws InterruptedException {
    SettablePromise<Integer> pending = Promises.settable();
    Task<Integer> child = Task.async("child", ctx -> pending);
    Task<Integer> root = Task.async("root", ctx -> {
      ctx.run(child);
      return Promises.value(7);
    });

    execute(root);

    assertEquals(root.get(), Integer.valueOf(7));
    assertTrue(child.await(5, TimeUnit.SECONDS));
    assertTrue(Exceptions.isEarlyFinish(child.getError()));
    assertDisabled(root);
    assertSame(child.getTrace(), root.getTrace());
    assertDisabledTrace(child);
  }

  @Test
  public void testTimerAndSideEffectStillExecute() throws InterruptedException {
    Task<Integer> sideEffect = Task.value("side-effect", 9);
    Task<Integer> delayed = Task.value("delayed", 5).withDelay(1, TimeUnit.MILLISECONDS);
    Task<Integer> root = delayed.withSideEffect(n -> sideEffect);

    execute(root);

    assertEquals(root.get(), Integer.valueOf(5));
    assertTrue(sideEffect.await(5, TimeUnit.SECONDS));
    assertEquals(sideEffect.get(), Integer.valueOf(9));
    assertDisabled(delayed);
    assertDisabled(sideEffect);
  }

  @Test
  public void testCallableFlatMapAndTimeoutPreserveExecution() throws InterruptedException {
    Task<Integer> task = Task.callable("callable", () -> 2)
        .flatMap("flat-map", n -> Task.value("nested", n + 1))
        .withTimeout(5, TimeUnit.SECONDS);

    execute(task);

    assertEquals(task.get(), Integer.valueOf(3));
    assertDisabled(task);
  }

  @Test
  public void testLoggingCanBeEnabledDuringUntracedExecution() throws Exception {
    ListLoggerFactory loggers = new ListLoggerFactory();
    ListLogger logger = loggers.getLogger(Engine.LOGGER_BASE + ":all");
    AtomicInteger serialized = new AtomicInteger();
    Task<Integer> task = Task.callable("logged", () -> {
      logger.setLogLevel(ListLogger.LEVEL_TRACE);
      return 7;
    });
    task.setTraceValueSerializer(n -> { serialized.incrementAndGet(); return "log-value"; });
    withDirectEngine(0, loggers, engine -> {
      engine.run(task);
      assertEquals(task.get(), Integer.valueOf(7));
      assertEquals(serialized.get(), 1);
      assertEquals(logger.getEntries().size(), 1);
      assertTrue(logger.getEntries().get(0).getMessage().contains("Elapsed: 0ms"));
      assertTrue(logger.getEntries().get(0).getMessage().contains("Value: log-value"));
      assertNull(task.getShallowTrace().getValue());
    });
  }

  @Test
  public void testConstructionMetadataIsRetainedButFrozenDuringExecution() throws InterruptedException {
    Task<Integer> task = Task.async("metadata", ctx -> {
      ctx.getShallowTraceBuilder().setName("ignored").addAttribute("runtime", "ignored");
      return Promises.value(1);
    });
    task.getShallowTraceBuilder().setTaskType("custom").setHidden(true).addAttribute("before", "retained");
    Object metadata = task.getShallowTraceBuilder();

    execute(task);

    assertSame(task.getShallowTraceBuilder(), metadata);
    assertEquals(task.getShallowTrace().getName(), "metadata");
    assertEquals(task.getShallowTrace().getTaskType(), "custom");
    assertTrue(task.getShallowTrace().getHidden());
    assertEquals(task.getShallowTrace().getAttributes(), Collections.singletonMap("before", "retained"));
    assertDisabled(task);
  }

  @Test
  public void testCancellationOverrideIsPreservedAndFrozen() {
    AtomicInteger cancels = new AtomicInteger();
    RuntimeException reason = new RuntimeException("cancel");
    Task<Integer> task = new BaseTask<Integer>("cancelled") {
      @Override
      protected Promise<Integer> run(Context ctx) {
        return Promises.value(1);
      }

      @Override
      public boolean cancel(Exception error) {
        cancels.incrementAndGet();
        return super.cancel(error);
      }
    };

    assertTrue(PlanContext.cancelDisabled(task, reason));

    assertEquals(cancels.get(), 1);
    assertTrue(Exceptions.isCancellation(task.getError()));
    assertDisabledTrace(task);
  }

  @Test
  public void testCancellationBeforePlanRetainsMetadataAndThenFreezes() throws Exception {
    Task<Integer> task = Task.value("cancelled-before-plan", 1);
    assertTrue(task.cancel(new RuntimeException("before")));
    String value = task.getShallowTrace().getValue();

    withDirectEngine(0, engine -> {
      engine.run(task, "disabled-plan");
      task.getShallowTraceBuilder().setName("ignored").setValue("ignored").addAttribute("ignored", "ignored");

      assertEquals(task.getShallowTrace().getResultType(), ResultType.ERROR);
      assertEquals(task.getShallowTrace().getName(), "cancelled-before-plan");
      assertEquals(task.getShallowTrace().getValue(), value);
      assertTrue(task.getShallowTrace().getAttributes().isEmpty());
      assertTrue(task.getTrace().getTraceMap().isEmpty());
      assertEquals(task.getTrace().getPlanClass(), "none");
    });
  }

  @Test
  public void testCompletedUntracedTaskCanBeReusedByTracedPlan() throws Exception {
    Task<Integer> reused = Task.value("reused", 3);
    execute(reused);
    Task<Integer> parent = Task.async("traced-parent", ctx -> {
      ctx.run(reused);
      return Promises.value(4);
    });
    withDirectEngine(100, traced -> {
      traced.run(parent);
      assertEquals(parent.get(), Integer.valueOf(4));
      assertTrue(parent.getTrace().getTraceMap().containsKey(reused.getId()));
      assertEquals(parent.getTrace().getTraceMap().get(reused.getId()).getName(), "reused");
      assertEquals(parent.getTrace().getTraceMap().get(parent.getId()).getResultType(), ResultType.SUCCESS);
      assertEquals(parent.getTrace().getRelationships().size(), 1);
      assertDisabled(reused);
    });
  }

  @Test
  public void testUnstartedRootCancellationOnExecutorFailureDoesNotRecord() {
    Engine engine = new EngineBuilder().setTaskExecutor(command -> {
      throw new RejectedExecutionException("expected");
    }).setTimerScheduler(getScheduler()).setEngineProperty(Engine.MAX_RELATIONSHIPS_PER_TRACE, 0).build();
    Task<Integer> task = Task.value("executor-rejected", 1);
    try {
      engine.run(task, "disabled-plan");
      assertTrue(task.isFailed());
      assertTrue(task.getError().getCause() instanceof SerialExecutionException);
      assertDisabledTrace(task);
    } finally {
      engine.shutdown();
    }
  }

  @Test
  public void testCompletedPlanRejectsSideEffectWithoutRecording() throws Exception {
    Task<Integer> sideEffect = Task.value("rejected-side-effect", 2);
    Task<Integer> root = new BaseTask<Integer>("completed-root") {
      @Override
      protected Promise<Integer> run(Context ctx) {
        getSettableDelegate().done(1);
        ctx.runSideEffect(sideEffect);
        return Promises.value(1);
      }
    };
    withDirectEngine(0, engine -> {
      engine.run(root, "disabled-plan");
      assertEquals(root.get(), Integer.valueOf(1));
      assertTrue(Exceptions.isCancellation(sideEffect.getError()));
      assertTrue(sideEffect.getError().getCause() instanceof IllegalStateException);
      assertDisabledTrace(sideEffect);
    });
  }

  @Test
  public void testNegativeTraceLimitPreservesShallowRecordingAndSerializers() throws Exception {
    AtomicInteger serialized = new AtomicInteger();
    Task<Integer> task = Task.callable("negative-limit", () -> 1);
    task.setTraceValueSerializer(n -> { serialized.incrementAndGet(); return "recorded"; });
    withDirectEngine(-1, engine -> {
      engine.run(task);
      assertEquals(task.get(), Integer.valueOf(1));
      assertEquals(serialized.get(), 1);
      assertEquals(task.getShallowTrace().getResultType(), ResultType.SUCCESS);
      assertEquals(task.getShallowTrace().getValue(), "recorded");
      assertNotEquals(task.getShallowTrace().getNativeStartNanos(), -1L);
      assertTrue(task.getTrace().getTraceMap().isEmpty());
      task.getShallowTraceBuilder().setName("still-mutable");
      assertEquals(task.getShallowTrace().getName(), "still-mutable");
    });
  }

  @Test
  public void testTracedExecutionRacingUntracedCancellationDoesNotLoseTracing() throws Exception {
    AtomicReference<Task<Integer>> task = new AtomicReference<>();
    AtomicReference<Throwable> failure = new AtomicReference<>();
    CyclicBarrier barrier = new CyclicBarrier(3);
    withDirectEngine(100, traced -> {
      Thread cancelling = racingThread(barrier, () -> PlanContext.cancelDisabled(task.get(), Exceptions.EARLY_FINISH_EXCEPTION), failure);
      Thread executing = racingThread(barrier, () -> traced.run(task.get()), failure);
      cancelling.start();
      executing.start();
      try {
        for (int i = 0; i < 500; i++) {
          task.set(Task.async("raced", ctx -> Promises.value(1)));
          barrier.await(5, TimeUnit.SECONDS);
          barrier.await(5, TimeUnit.SECONDS);
          assertNull(failure.get());
          Task<Integer> current = task.get();
          assertTrue(current.isDone());
          if (current.getTraceBuilder().isEnabled()) {
            assertTrue(current.getTrace().getTraceMap().containsKey(current.getId()));
            assertNotEquals(current.getShallowTrace().getResultType(), ResultType.UNFINISHED);
            assertNotEquals(current.getShallowTrace().getNativeStartNanos(), -1L);
          } else {
            assertDisabledTrace(current);
          }
        }
      } finally {
        cancelling.interrupt();
        executing.interrupt();
        cancelling.join(5000);
        executing.join(5000);
      }
    });
  }

  @Test
  public void testPlansForksAndShutdownRejectionsShareDisabledTrace() throws Exception {
    Task<Integer> first = Task.value("first", 1);
    Task<Integer> sideEffect = Task.value("side-effect", 3);
    Task<Integer> second = Task.value("second", 2).withSideEffect(n -> sideEffect);
    Task<Integer> rejected = Task.value("rejected", 4);
    withDirectEngine(0, engine -> {
      engine.run(first, "first-plan");
      engine.run(second, "second-plan");
      assertTrue(sideEffect.isDone());
      engine.shutdown();
      engine.run(rejected);

      assertTrue(rejected.isFailed());
      assertTrue(Exceptions.isCancellation(rejected.getError()));
      assertTrue(rejected.getError().getCause() instanceof EngineShutdownException);
      for (Task<?> task : Arrays.asList(first, second, sideEffect, rejected)) {
        assertSame(task.getTraceBuilder(), PlanContext.DISABLED_TRACE_BUILDER);
        assertSame(task.getTrace(), first.getTrace());
      }
      assertDisabled(first);
      assertDisabled(second);
      assertDisabled(sideEffect);
      assertDisabledTrace(rejected);
    });
  }

  @Test
  public void testDisabledPlansKeepExecutionIdentityOutsideTrace() throws Exception {
    AtomicReference<Long> firstId = new AtomicReference<>();
    AtomicReference<Long> secondId = new AtomicReference<>();
    Task<Integer> first = Task.async("first", ctx -> {
      assertEquals(ctx.getPlanClass(), "first-plan");
      firstId.set(ctx.getPlanId());
      return Promises.value(1);
    });
    Task<Integer> second = Task.async("second", ctx -> {
      assertEquals(ctx.getPlanClass(), "second-plan");
      secondId.set(ctx.getPlanId());
      return Promises.value(2);
    });
    withDirectEngine(0, engine -> {
      engine.run(first, "first-plan");
      engine.run(second, "second-plan");
      assertEquals(first.get(), Integer.valueOf(1));
      assertEquals(second.get(), Integer.valueOf(2));
      assertNotEquals(firstId.get(), secondId.get());
      assertNotEquals(firstId.get(), Long.valueOf(0L));
      assertNotEquals(secondId.get(), Long.valueOf(0L));
    });
  }

  private Thread racingThread(CyclicBarrier barrier, Runnable action, AtomicReference<Throwable> failure) {
    return new Thread(() -> {
      try {
        for (int i = 0; i < 500; i++) {
          barrier.await(5, TimeUnit.SECONDS);
          action.run();
          barrier.await(5, TimeUnit.SECONDS);
        }
      } catch (Throwable error) {
        failure.compareAndSet(null, error);
      }
    });
  }
}
