package com.linkedin.parseq;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.testng.annotations.Test;

import com.linkedin.parseq.function.Try;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public abstract class AbstractTaskTest extends BaseEngineTest {

  public void testMap(int expectedNumberOfTasks) {
    Task<Integer> task = getSuccessTask().map("strlen", String::length);

    runAndWait("TestFusionTask.testMap", task);
    assertTrue(task.isDone());
    assertEquals((int) task.get(), TASK_VALUE.length());

    assertEquals(countTasks(task.getTrace()), expectedNumberOfTasks);
  }

  public void testFlatMap(int expectedNumberOfTasks) {
    Task<String> task =
        getSuccessTask().flatMap("transform", str -> Task.callable("strlenstr", () -> String.valueOf(str.length())));

    runAndWait("TestFusionTask.testFlatMap", task);
    assertEquals(task.get(), String.valueOf(TASK_VALUE.length()));

    assertEquals(countTasks(task.getTrace()), expectedNumberOfTasks);
  }

  public void testAndThenConsumer(int expectedNumberOfTasks) {
    // Consumer
    final AtomicReference<String> variable = new AtomicReference<String>();
    Task<String> task = getSuccessTask().andThen("consume", variable::set);
    runAndWait("TestFusionTask.testAndThenConsumer", task);
    assertEquals(task.get(), TASK_VALUE);
    assertEquals(variable.get(), TASK_VALUE);

    assertEquals(countTasks(task.getTrace()), expectedNumberOfTasks);
  }

  public void testAndThenTask(int expectedNumberOfTasks) {
    // Task
    Task<Integer> task = getSuccessTask().andThen("seq", Task.callable("life", () -> 42));
    runAndWait("TestFusionTask.testAndThenTask", task);
    assertEquals((int) task.get(), 42);

    assertEquals(countTasks(task.getTrace()), expectedNumberOfTasks);
  }

  public void testRecover(int expectedNumberOfTasks) {
    Task<Integer> success = getSuccessTask().map("strlen", String::length).recover("recover", e -> -1);
    runAndWait("TestFusionTask.testRecoverSuccess", success);
    assertEquals((int) success.get(), TASK_VALUE.length());
    assertEquals(countTasks(success.getTrace()), expectedNumberOfTasks);


    Task<Integer> failure = getFailureTask().map("strlen", String::length).recover("recover", e -> -1);
    runAndWait("TestFusionTask.testRecoverFailure", failure);
    assertEquals((int) failure.get(), -1);
    assertEquals(countTasks(failure.getTrace()), expectedNumberOfTasks);
  }

  public void testNoRecover(int expectedNumberOfTasks) {
    Task<Integer> task = getFailureTask().map("strlen", String::length);
    try {
      runAndWait("TestFusionTask.testNoRecover", task);
      fail("should have failed");
    } catch (Exception ex) {
      assertEquals(ex.getCause().getMessage(), TASK_ERROR_MESSAGE);
    }
    assertEquals(countTasks(task.getTrace()), expectedNumberOfTasks);
  }

  public void testTry(int expectedNumberOfTasks) {
    Task<Try<Integer>> success = getSuccessTask().map("strlen", String::length).withTry();
    runAndWait("TestFusionTask.testTrySuccess", success);
    assertFalse(success.get().isFailed());
    assertEquals((int) success.get().get(), TASK_VALUE.length());
    assertEquals(countTasks(success.getTrace()), expectedNumberOfTasks);

    Task<Try<Integer>> failure = getFailureTask().map("strlen", String::length).withTry();
    runAndWait("TestFusionTask.testTryFailure", failure);
    assertTrue(failure.get().isFailed());
    assertEquals(failure.get().getError().getMessage(), TASK_ERROR_MESSAGE);
    assertEquals(countTasks(failure.getTrace()), expectedNumberOfTasks);
  }

  public void testRecoverWithSuccess(int expectedNumberOfTasks) {
    Task<String> success =
        getSuccessTask().recoverWith("recoverWith",
            e -> Task.callable("recover failure", () -> {
              throw new RuntimeException("recover failed!");
            }));
    runAndWait("TestFusionTask.testRecoverWithSuccess", success);
    assertEquals(success.get(), TASK_VALUE);
    assertEquals(countTasks(success.getTrace()), expectedNumberOfTasks);
  }

  public void testRecoverWithFailure(int expectedNumberOfTasks) {
    Task<String> failure =
        getFailureTask().recoverWith("recoverWith",
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
    assertEquals(countTasks(failure.getTrace()), expectedNumberOfTasks);
  }

  public void testRecoverWithRecoverd(int expectedNumberOfTasks) {
    Task<String> recovered =
        getFailureTask().recoverWith("recoverWith",
            e -> Task.callable("recover success", () -> "recovered"));
    runAndWait("TestFusionTask.testRecoverWithRecoverd", recovered);
    assertEquals(recovered.get(), "recovered");
    assertEquals(countTasks(recovered.getTrace()), expectedNumberOfTasks);
  }

  public void testWithTimeout(int expectedNumberOfTasks) {
    Task<Integer> success =
        getSuccessTask().andThen(delayedValue(0, 30, TimeUnit.MILLISECONDS)).withTimeout(100, TimeUnit.MILLISECONDS);
    runAndWait("TestFusionTask.testWithTimeoutSuccess", success);
    assertEquals((int) success.get(), 0);
    assertEquals(countTasks(success.getTrace()), expectedNumberOfTasks);

    Task<Integer> failure =
        getSuccessTask().andThen(delayedValue(0, 110, TimeUnit.MILLISECONDS)).withTimeout(100, TimeUnit.MILLISECONDS);
    try {
      runAndWait("TestFusionTask.testWithTimeoutFailure", failure);
      fail("should have failed!");
    } catch (Exception ex) {
      assertSame(ex.getCause(), Exceptions.TIMEOUT_EXCEPTION);
    }
    assertEquals(countTasks(failure.getTrace()), expectedNumberOfTasks);
  }

  public void testWithSideEffectPartial(int expectedNumberOfTasks) {
    Task<String> fastMain = getSuccessTask();
    Task<String> slowSideEffect = delayedValue("slooow", 5100, TimeUnit.MILLISECONDS);
    Task<String> partial = fastMain.withSideEffect(s -> slowSideEffect);

    // ensure the whole task can finish before individual side effect task finishes
    runAndWait("TestFusionTask.testWithSideEffectPartial", partial);
    assertTrue(fastMain.isDone());
    assertTrue(partial.isDone());
    assertFalse(slowSideEffect.isDone());
    assertEquals(countTasks(partial.getTrace()), expectedNumberOfTasks);
  }

  public void testWithSideEffectFullCompletion(int expectedNumberOfTasks) throws Exception {
    Task<String> fastMain = getSuccessTask();
    Task<String> slowSideEffect = delayedValue("slow", 50, TimeUnit.MILLISECONDS);
    Task<String> full = fastMain.withSideEffect(s -> slowSideEffect);


    // ensure the side effect task will be run
    runAndWait("TestFusionTask.testWithSideEffectFullCompletion", full);
    slowSideEffect.await();
    assertTrue(full.isDone());
    assertTrue(fastMain.isDone());
    assertTrue(slowSideEffect.isDone());
    assertEquals(countTasks(full.getTrace()), expectedNumberOfTasks);
  }

  @Test
  public void testWithSideEffectCalcel() throws Exception {
    Task<String> cancelMain = delayedValue("canceled", 6000, TimeUnit.MILLISECONDS);
    Task<String> fastSideEffect = getSuccessTask();
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

  public void testWithSideEffectFailure(int expectedNumberOfTasks) throws Exception {
    Task<String> failureMain = getFailureTask();
    Task<String> fastSideEffect = getSuccessTask();
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
    assertEquals(countTasks(failure.getTrace()), expectedNumberOfTasks);
  }

  public void testOnFailure(int expectedNumberOfTasks) {
    final AtomicReference<Throwable> variable = new AtomicReference<Throwable>();
    Task<String> success = getSuccessTask().onFailure("on failure", variable::set);
    runAndWait("TestFusionTask.testOnFailureSuccess", success);
    assertNull(variable.get());
    assertEquals(countTasks(success.getTrace()), expectedNumberOfTasks);

    Task<String> failure = getFailureTask().onFailure("on failure", variable::set);
    try {
      runAndWait("TestFusionTask.testRecoverFailure", failure);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(failure.isFailed());
    }
    assertEquals(variable.get().getMessage(), TASK_ERROR_MESSAGE);
    assertEquals(countTasks(failure.getTrace()), expectedNumberOfTasks);
  }

  protected static final String TASK_VALUE = "value";
  protected static final String TASK_ERROR_MESSAGE = "error";

  abstract Task<String> getSuccessTask();

  abstract Task<String> getFailureTask();
}
