package com.linkedin.parseq;

import com.linkedin.parseq.function.Try;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


public class TestFusionTask extends BaseEngineTest {
  @Test
  public void testMap() {
    Task<Integer> task = getFusionTask().map("strlen", String::length);

    runAndWait("TestFusionTask.testMap", task);
    assertTrue(task.isDone());
    assertEquals((int) task.get(), 6);

    assertEquals(countTasks(task.getTrace()), 1);
  }

  @Test
  public void testFlatMap() {
    Task<String> task =
        getFusionTask().flatMap("transform", str -> Task.callable("strlenstr", () -> String.valueOf(str.length())));

    runAndWait("TestFusionTask.testFlatMap", task);
    assertEquals(task.get(), "6");

    assertEquals(countTasks(task.getTrace()), 3);
  }

  @Test
  public void testAndThenConsumer() {
    // Consumer
    final AtomicReference<String> variable = new AtomicReference<String>();
    Task<String> task = getFusionTask().andThen("consume", variable::set);
    runAndWait("TestFusionTask.testAndThenConsumer", task);
    assertEquals(task.get(), "fusion");
    assertEquals(variable.get(), "fusion");

    assertEquals(countTasks(task.getTrace()), 1);
  }

  @Test
  public void testAndThenTask() {
    // Task
    Task<Integer> task = getFusionTask().andThen("seq", Task.callable("life", () -> 42));
    runAndWait("TestFusionTask.testAndThenTask", task);
    assertEquals((int) task.get(), 42);

    assertEquals(countTasks(task.getTrace()), 3);
  }

  @Test
  public void testRecover() {
    Task<Integer> success = getFusionTask().map("strlen", String::length).recover("recover", e -> -1);
    runAndWait("TestFusionTask.testRecoverSuccess", success);
    assertEquals((int) success.get(), 6);
    assertEquals(countTasks(success.getTrace()), 1);


    Task<Integer> failure = getFailureFusionTask().map("strlen", String::length).recover("recover", e -> -1);
    runAndWait("TestFusionTask.testRecoverFailure", failure);
    assertEquals((int) failure.get(), -1);
    assertEquals(countTasks(failure.getTrace()), 1);
  }

  @Test
  public void testNoRecover() {
    Task<Integer> task = getFailureFusionTask().map("strlen", String::length);
    try {
      runAndWait("TestFusionTask.testNoRecover", task);
      fail("should have failed");
    } catch (Exception ex) {
      assertEquals(ex.getCause().getMessage(), "fusion");
    }
    assertEquals(countTasks(task.getTrace()), 1);
  }

  @Test
  public void testTry() {
    Task<Try<Integer>> success = getFusionTask().map("strlen", String::length).withTry();
    runAndWait("TestFusionTask.testTrySuccess", success);
    assertFalse(success.get().isFailed());
    assertEquals((int) success.get().get(), 6);
    assertEquals(countTasks(success.getTrace()), 1);

    Task<Try<Integer>> failure = getFailureFusionTask().map("strlen", String::length).withTry();
    runAndWait("TestFusionTask.testTryFailure", failure);
    assertTrue(failure.get().isFailed());
    assertEquals(failure.get().getError().getMessage(), "fusion");
    assertEquals(countTasks(failure.getTrace()), 1);
  }

  @Test
  public void testRecoverWith() {
    Task<Integer> success =
        getFusionTask().map("strlen", String::length).recoverWith("recoverWith",
            e -> Task.callable("recover failure", () -> {
              throw new RuntimeException("recover failed!");
            }));
    runAndWait("TestFusionTask.testRecoverWithSuccess", success);
    assertEquals((int) success.get(), 6);
    assertEquals(countTasks(success.getTrace()), 2);

    Task<Integer> failure =
        getFailureFusionTask().map("strlen", String::length).recoverWith("recoverWith",
            e -> Task.callable("recover failure", () -> {
              throw new RuntimeException("recover failed!");
            }));
    try {
      runAndWait("TestFusionTask.testRecoverWithFailure", failure);
      fail("should have failed");
    } catch (Exception ex) {
      // fail with throwable from recovery function
      assertEquals(ex.getCause().getMessage(), "recover failed!");
    }
    assertEquals(countTasks(failure.getTrace()), 3);

    Task<Integer> recovered =
        getFailureFusionTask().map("strlen", String::length).recoverWith("recoverWith",
            e -> Task.callable("recover success", () -> -1));
    runAndWait("TestFusionTask.testRecoverWithRecoverd", recovered);
    assertEquals((int) recovered.get(), -1);
    assertEquals(countTasks(recovered.getTrace()), 3);
  }

  @Test
  public void testWithTimeout() {
    Task<Integer> success =
        getFusionTask().andThen(delayedValue(0, 30, TimeUnit.MILLISECONDS)).withTimeout(100, TimeUnit.MILLISECONDS);
    runAndWait("TestFusionTask.testWithTimeoutSuccess", success);
    assertEquals((int) success.get(), 0);
    assertEquals(countTasks(success.getTrace()), 3);

    Task<Integer> failure =
        getFusionTask().andThen(delayedValue(0, 110, TimeUnit.MILLISECONDS)).withTimeout(100, TimeUnit.MILLISECONDS);
    try {
      runAndWait("TestFusionTask.testWithTimeoutFailure", failure);
      fail("should have failed!");
    } catch (Exception ex) {
      assertSame(ex.getCause(), Exceptions.TIMEOUT_EXCEPTION);
    }
    assertEquals(countTasks(failure.getTrace()), 3);
  }

  @Test
  public void testWithSideEffectPartial() {
    Task<String> fastMain = getFusionTask();
    Task<String> slowSideEffect = delayedValue("slooow", 5100, TimeUnit.MILLISECONDS);
    Task<String> partial = fastMain.withSideEffect(s -> slowSideEffect);

    // ensure the whole task can finish before individual side effect task finishes
    runAndWait("TestFusionTask.testWithSideEffectPartial", partial);
    assertTrue(fastMain.isDone());
    assertTrue(partial.isDone());
    assertFalse(slowSideEffect.isDone());
    assertEquals(countTasks(partial.getTrace()), 4);
  }

  @Test
  public void testWithSideEffectFullCompletion() throws Exception {
    Task<String> fastMain = getFusionTask();
    Task<String> slowSideEffect = delayedValue("slow", 50, TimeUnit.MILLISECONDS);
    Task<String> full = fastMain.withSideEffect(s -> slowSideEffect);


    // ensure the side effect task will be run
    runAndWait("TestFusionTask.testWithSideEffectFullCompletion", full);
    slowSideEffect.await();
    assertTrue(full.isDone());
    assertTrue(fastMain.isDone());
    assertTrue(slowSideEffect.isDone());
    assertEquals(countTasks(full.getTrace()), 4);
  }

  @Test
  public void testWithSideEffectCalcel() throws Exception {
    Task<String> cancelMain = delayedValue("canceled", 6000, TimeUnit.MILLISECONDS);
    Task<String> fastSideEffect = getFusionTask();
    Task<String> cancel = cancelMain.withSideEffect(s -> fastSideEffect);

    // test cancel, side effect task should not be run
    // add 10 ms delay so that we can reliably cancel it before it's run by the engine
    run(delayedValue(0, 10, TimeUnit.MILLISECONDS).andThen(cancel));
    assertTrue(cancelMain.cancel(new Exception("canceled")));
    cancel.await();
    fastSideEffect.await(10, TimeUnit.MILLISECONDS);
    assertTrue(cancel.isDone());
    assertFalse(fastSideEffect.isDone());
    logTracingResults("TestFusionTask.testWithSideEffectCalcel", cancel);
  }

  @Test
  public void testWithSideEffectFailure() throws Exception {
    Task<String> failureMain = getFailureFusionTask();
    Task<String> fastSideEffect = getFusionTask();
    Task<String> failure = failureMain.withSideEffect(s -> fastSideEffect);

    // test failure, side effect task should not be run
    try {
      runAndWait("TestFusionTask.testWithSideEffectFailure", failure);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(failure.isFailed());
      fastSideEffect.await(10, TimeUnit.MILLISECONDS);
      assertFalse(fastSideEffect.isDone());
    }
    assertEquals(countTasks(failure.getTrace()), 2);
  }

  @Test
  public void testOnFailure() {
    final AtomicReference<Throwable> variable = new AtomicReference<Throwable>();
    Task<String> success = getFusionTask().onFailure("on failure", variable::set);
    runAndWait("TestFusionTask.testOnFailureSuccess", success);
    assertNull(variable.get());
    assertEquals(countTasks(success.getTrace()), 1);

    Task<String> failure = getFailureFusionTask().onFailure("on failure", variable::set);
    try {
      runAndWait("TestFusionTask.testRecoverFailure", failure);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(failure.isFailed());
    }
    assertEquals(variable.get().getMessage(), "fusion");
    assertEquals(countTasks(failure.getTrace()), 1);
  }

  private Task<String> getFusionTask() {
    return Task.value("success", "fusion");
  }

  private Task<String> getFailureFusionTask() {
    return Task.failure("failure", new RuntimeException("fusion"));
  }

}
