package com.linkedin.parseq;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.linkedin.parseq.trace.Trace;
import org.testng.annotations.Test;

import com.linkedin.parseq.function.Failure;
import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;

import static org.testng.Assert.*;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public abstract class AbstractTaskTest extends BaseEngineTest {

  public void testMap(int expectedNumberOfTasks) {
    Task<Integer> task = getSuccessTask().map(String::length);

    runAndWait("AbstractTaskTest.testMap", task);
    assertTrue(task.isDone());
    assertEquals((int) task.get(), TASK_VALUE.length());

    assertEquals(countTasks(task.getTrace()), expectedNumberOfTasks);
  }

  public void testFlatMapFuncReturnNulll() {
    Task<String> task = getSuccessTask().flatMap(str -> null);
    runAndWaitException("AbstractTaskTest.testFlatMapFuncReturnNulll", task, RuntimeException.class);
    assertTrue(task.getError().getMessage().contains("returned null"));
  }

  public void testFlattenTaskReturnNulll() {
    Function1<String, Task<String>> func = s -> null;
    Task<String> task = Task.flatten(getSuccessTask().map(func));
    runAndWaitException("AbstractTaskTest.testFlattenTaskReturnNulll", task, RuntimeException.class);
    assertTrue(task.getError().getMessage().contains("returned null"));
  }

  public void testRecoverWithFuncReturnNulll() {
    Task<String> task = getFailureTask().recoverWith(e -> null);
    runAndWaitException("AbstractTaskTest.testRecoverWithFuncReturnNulll", task, RuntimeException.class);
    assertTrue(task.getError().getMessage().contains("returned null"));
  }

  public void testWithSideEffectFuncReturnNulll() {
    Task<String> task = getSuccessTask().withSideEffect(str -> null);
    runAndWaitException("AbstractTaskTest.testWithSideEffectFuncReturnNulll", task, RuntimeException.class);
    assertTrue(task.getError().getMessage().contains("returned null"));
  }

  public void testFlatMap(int expectedNumberOfTasks) {
    Task<String> task = getSuccessTask().flatMap(str -> Task.callable("strlenstr", () -> String.valueOf(str.length())));

    runAndWait("AbstractTaskTest.testFlatMap", task);
    assertEquals(task.get(), String.valueOf(TASK_VALUE.length()));

    assertEquals(countTasks(task.getTrace()), expectedNumberOfTasks);
  }

  public void testAndThenConsumer(int expectedNumberOfTasks) {
    final AtomicReference<String> variable = new AtomicReference<String>();
    Task<String> task = getSuccessTask().andThen(variable::set);
    runAndWait("AbstractTaskTest.testAndThenConsumer", task);
    assertEquals(task.get(), TASK_VALUE);
    assertEquals(variable.get(), TASK_VALUE);

    assertEquals(countTasks(task.getTrace()), expectedNumberOfTasks);
  }

  public void testAndThenTask(int expectedNumberOfTasks) {
    Task<Integer> task = getSuccessTask().andThen(Task.callable("life", () -> 42));
    runAndWait("AbstractTaskTest.testAndThenTask", task);
    assertEquals((int) task.get(), 42);

    assertEquals(countTasks(task.getTrace()), expectedNumberOfTasks);
  }

  public void testRecover(int expectedNumberOfTasks) {
    Task<Integer> success = getSuccessTask().map("strlen", String::length).recover(e -> -1);
    runAndWait("AbstractTaskTest.testRecoverSuccess", success);
    assertEquals((int) success.get(), TASK_VALUE.length());
    assertEquals(countTasks(success.getTrace()), expectedNumberOfTasks);

    Task<Integer> failure = getFailureTask().map("strlen", String::length).recover(e -> -1);
    runAndWait("AbstractTaskTest.testRecoverFailure", failure);
    assertEquals((int) failure.get(), -1);
    assertEquals(countTasks(failure.getTrace()), expectedNumberOfTasks);
  }

  public void testRecoverWithExceptionFilter(int expectedNumberOfTasks) {
    Task<Integer> recoverd = getFailureTask()
            .map("strlen", String::length)
            .recover("", RuntimeException.class, e -> -1);
    runAndWait("AbstractTaskTest.recoverd", recoverd);
    assertEquals((int) recoverd.get(), -1);
    assertEquals(countTasks(recoverd.getTrace()), expectedNumberOfTasks);

    Task<Integer> notRecovered = getFailureTask()
            .map("strlen", String::length)
            .recover("", ArithmeticException.class, e -> -1);
    runAndWaitException("AbstractTaskTest.testRecoverWithExceptionFilterFailure", notRecovered, RuntimeException.class);
    assertTrue(notRecovered.isFailed());
    assertEquals(countTasks(notRecovered.getTrace()), expectedNumberOfTasks);

    Task<String> recoveredExceptionSubclassesForward = getFailureTask() // Failed with RuntimeError
            .recover("", Throwable.class, e -> "expected-value") // expected to recover, as RuntimeError is subclass of Throwable
            .recover("", RuntimeException.class, e -> "unexpected-value"); // no action, as already recovered at previous step
    runAndWait("AbstractTaskTest.recoveredExceptionSubclassesForward", recoveredExceptionSubclassesForward);
    assertEquals(recoveredExceptionSubclassesForward.get(), "expected-value");
    assertEquals(countTasks(recoveredExceptionSubclassesForward.getTrace()), expectedNumberOfTasks);

    Task<String> recoveredExceptionSubclassesBackwards = getFailureTask() // Failed with RuntimeError
            .recover("", RuntimeException.class, e -> "expected-value") // expected to recover, as exception class matches
            .recover("", Throwable.class, e -> "unexpected-value"); // no action, as already recovered at previous step
    runAndWait("AbstractTaskTest.recoveredExceptionSubclassesBackwards", recoveredExceptionSubclassesBackwards);
    assertEquals(recoveredExceptionSubclassesBackwards.get(), "expected-value");  // recovered with expected value
    assertEquals(countTasks(recoveredExceptionSubclassesBackwards.getTrace()), expectedNumberOfTasks);

    Task<String> recoveredUnrelatedExceptionFirst = getFailureTask() // Failed with RuntimeError
            .recover("", Error.class, e -> "unexpected-value") // no action, as the failed state is not of type Error
            .recover("", Throwable.class, e -> "expected-value"); // expected to recover, as exception class matches
    runAndWait("AbstractTaskTest.recoveredUnrelatedExceptionFirst", recoveredUnrelatedExceptionFirst);
    assertEquals(recoveredUnrelatedExceptionFirst.get(), "expected-value");  // recovered with expected value
    assertEquals(countTasks(recoveredUnrelatedExceptionFirst.getTrace()), expectedNumberOfTasks);

    Task<String> recoveredRecoveryThrows = getFailureTask() // Failed with RuntimeError
            .recover("", RuntimeException.class, e -> {throw new IllegalArgumentException();}) // exception class matches, but recovery throws another exception
            .recover("", IllegalArgumentException.class, e -> "expected-value"); // expected to recover, as exception class matches (see above)
    runAndWait("AbstractTaskTest.recoveredRecoveryThrows", recoveredRecoveryThrows);
    assertEquals(recoveredRecoveryThrows.get(), "expected-value");  // recovered with expected value
    assertEquals(countTasks(recoveredRecoveryThrows.getTrace()), expectedNumberOfTasks);

    Task<String> failedAfterRecoveryThrows = getFailureTask() // Failed with RuntimeError
            .recover("", RuntimeException.class, e -> {throw new IllegalArgumentException();}); // throws another exception while recovering
    IllegalArgumentException ex = runAndWaitException(failedAfterRecoveryThrows, IllegalArgumentException.class);
    assertNotNull(ex);
    assertEquals(countTasks(recoveredRecoveryThrows.getTrace()), expectedNumberOfTasks);
  }

  public void testCancelledRecover(int expectedNumberOfTasks) {
    Task<Integer> cancelled = getCancelledTask().map("strlen", String::length).recover(e -> -1);
    try {
      runAndWait("AbstractTaskTest.testCancelledRecover", cancelled);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(cancelled.isFailed());
      assertTrue(Exceptions.isCancellation(cancelled.getError()));
    }
    assertEquals(countTasks(cancelled.getTrace()), expectedNumberOfTasks);
  }

  public void testNoRecover(int expectedNumberOfTasks) {
    Task<Integer> task = getFailureTask().map("strlen", String::length);
    try {
      runAndWait("AbstractTaskTest.testNoRecover", task);
      fail("should have failed");
    } catch (Exception ex) {
      assertEquals(ex.getCause().getMessage(), TASK_ERROR_MESSAGE);
    }
    assertEquals(countTasks(task.getTrace()), expectedNumberOfTasks);
  }

  public void testToTry(int expectedNumberOfTasks) {
    Task<Try<Integer>> success = getSuccessTask().map("strlen", String::length).toTry();
    runAndWait("AbstractTaskTest.testToTrySuccess", success);
    assertFalse(success.get().isFailed());
    assertEquals((int) success.get().get(), TASK_VALUE.length());
    assertEquals(countTasks(success.getTrace()), expectedNumberOfTasks);

    Task<Try<Integer>> failure = getFailureTask().map("strlen", String::length).toTry();
    runAndWait("AbstractTaskTest.testToTryFailure", failure);
    assertTrue(failure.get().isFailed());
    assertEquals(failure.get().getError().getMessage(), TASK_ERROR_MESSAGE);
    assertEquals(countTasks(failure.getTrace()), expectedNumberOfTasks);
  }

  @Test
  public void testToTryCancelled() throws InterruptedException {
    Task<String> cancelMain = delayedValue("value", 6000, TimeUnit.MILLISECONDS);
    Task<Try<String>> task = cancelMain.toTry();

    run(task);
    assertTrue(cancelMain.cancel(new Exception("canceled")));
    task.await();
    assertTrue(task.isDone());
    assertTrue(task.isFailed());
    assertTrue(Exceptions.isCancellation(task.getError()));
    logTracingResults("AbstractTaskTest.testToTryCancelled", task);
  }

  public void testRecoverWithSuccess(int expectedNumberOfTasks) {
    Task<String> success = getSuccessTask().recoverWith(e -> Task.callable("recover failure", () -> {
      throw new RuntimeException("recover failed!");
    } ));
    runAndWait("AbstractTaskTest.testRecoverWithSuccess", success);
    assertEquals(success.get(), TASK_VALUE);
    assertEquals(countTasks(success.getTrace()), expectedNumberOfTasks);
  }

  public void testRecoverWithFailure(int expectedNumberOfTasks) {
    Task<String> failure = getFailureTask().recoverWith(e -> Task.callable("recover failure", () -> {
      throw new RuntimeException("recover failed!");
    } ));
    try {
      runAndWait("AbstractTaskTest.testRecoverWithFailure", failure);
      fail("should have failed");
    } catch (Exception ex) {
      // fail with throwable from recovery function
      assertEquals(ex.getCause().getMessage(), "recover failed!");
    }
    assertEquals(countTasks(failure.getTrace()), expectedNumberOfTasks);
  }

  public void testRecoverWithCancelled(int expectedNumberOfTasks) {
    Task<String> cancelled = getCancelledTask().recoverWith(e -> Task.callable("recover success", () -> "recovered"));
    try {
      runAndWait("AbstractTaskTest.testRecoverWithCancelled", cancelled);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(cancelled.isFailed());
      assertTrue(Exceptions.isCancellation(cancelled.getError()));
    }
    assertEquals(countTasks(cancelled.getTrace()), expectedNumberOfTasks);
  }

  public void testRecoverWithRecoverd(int expectedNumberOfTasks) {
    Task<String> recovered = getFailureTask().recoverWith(e -> Task.callable("recover success", () -> "recovered"));
    runAndWait("AbstractTaskTest.testRecoverWithRecoverd", recovered);
    assertEquals(recovered.get(), "recovered");
    assertEquals(countTasks(recovered.getTrace()), expectedNumberOfTasks);
  }

  @Test
  public void testWithTimeoutSuccess() {
    Task<Integer> success =
        getSuccessTask().andThen(delayedValue(0, 30, TimeUnit.MILLISECONDS)).withTimeout(100, TimeUnit.MILLISECONDS);
    runAndWait("AbstractTaskTest.testWithTimeoutSuccess", success);
    assertEquals((int) success.get(), 0);
    assertEquals(countTasks(success.getTrace()), 5);
  }

  @Test
  public void testWithTimeoutTwiceSuccess() {
    Task<Integer> success = getSuccessTask().andThen(delayedValue(0, 30, TimeUnit.MILLISECONDS))
        .withTimeout(100, TimeUnit.MILLISECONDS).withTimeout(5000, TimeUnit.MILLISECONDS);
    runAndWait("AbstractTaskTest.testWithTimeoutTwiceSuccess", success);
    assertEquals((int) success.get(), 0);
    assertEquals(countTasks(success.getTrace()), 7);
  }

  @Test
  public void testWithTimeoutFailure() {
    Task<Integer> failure =
        getSuccessTask().andThen(delayedValue(0, 110, TimeUnit.MILLISECONDS)).withTimeout(100, TimeUnit.MILLISECONDS);
    try {
      runAndWait("AbstractTaskTest.testWithTimeoutFailure", failure);
      fail("should have failed!");
    } catch (Exception ex) {
      assertEquals(ex.getCause().getClass(), Exceptions.TIMEOUT_EXCEPTION.getClass());
      assertEquals(ex.getCause().getMessage(), "task: 'andThen: 0 delayed 110 ms' withTimeout 100ms");
    }
    assertEquals(countTasks(failure.getTrace()), 5);
  }

  @Test
  public void testWithTimeoutTwiceFailure() {
    Task<Integer> failure = getSuccessTask().andThen(delayedValue(0, 2000, TimeUnit.MILLISECONDS))
        .withTimeout(5000, TimeUnit.MILLISECONDS).withTimeout(100, TimeUnit.MILLISECONDS);
    try {
      runAndWait("AbstractTaskTest.testWithTimeoutTwiceFailure", failure);
      fail("should have failed!");
    } catch (Exception ex) {
      assertEquals(ex.getCause().getClass(), Exceptions.TIMEOUT_EXCEPTION.getClass());
    }
    assertEquals(countTasks(failure.getTrace()), 7);
  }

  @Test
  public void testWithSideEffectPartial() {
    Task<String> fastMain = getSuccessTask();
    Task<String> slowSideEffect = delayedValue("slooow", 5100, TimeUnit.MILLISECONDS);
    Task<String> partial = fastMain.withSideEffect(s -> slowSideEffect);

    // ensure the whole task can finish before individual side effect task finishes
    runAndWait("AbstractTaskTest.testWithSideEffectPartial", partial);
    assertTrue(fastMain.isDone());
    assertTrue(partial.isDone());
    assertFalse(slowSideEffect.isDone());
  }

  public void testWithSideEffectFullCompletion(int expectedNumberOfTasks) throws Exception {
    Task<String> fastMain = getSuccessTask();
    Task<String> slowSideEffect = delayedValue("slow", 50, TimeUnit.MILLISECONDS);
    Task<String> full = fastMain.withSideEffect(s -> slowSideEffect);

    // ensure the side effect task will be run
    runAndWait("AbstractTaskTest.testWithSideEffectFullCompletion", full);
    slowSideEffect.await();
    assertTrue(full.isDone());
    assertTrue(fastMain.isDone());
    assertTrue(slowSideEffect.isDone());
    assertEquals(countTasks(full.getTrace()), expectedNumberOfTasks);
  }

  @Test
  public void testWithSideEffectCancel() throws Exception {
    Task<String> cancelMain = delayedValue("value", 6000, TimeUnit.MILLISECONDS);
    Task<String> fastSideEffect = getSuccessTask();
    Task<String> cancel = cancelMain.withSideEffect(s -> fastSideEffect);

    // test cancel, side effect task should not be run
    // add 10 ms delay so that we can reliably cancel it before it's run by the engine
    Task<String> mainTasks = delayedValue("value", 10, TimeUnit.MILLISECONDS).andThen(cancel);
    run(mainTasks);
    assertTrue(cancelMain.cancel(new Exception("canceled")));
    cancel.await();
    fastSideEffect.await(10, TimeUnit.MILLISECONDS);
    assertTrue(cancel.isDone());
    assertFalse(fastSideEffect.isDone());
    logTracingResults("AbstractTaskTest.testWithSideEffectCancel", mainTasks);
  }

  @Test
  public void testWithSideEffectFailure(int expectedNumberOfTasks) throws Exception {
    Task<String> failureMain = getFailureTask();
    Task<String> fastSideEffect = getSuccessTask();
    Task<String> failure = failureMain.withSideEffect(s -> fastSideEffect);

    // test failure, side effect task should not be run
    try {
      runAndWait("AbstractTaskTest.testWithSideEffectFailure", failure);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(failure.isFailed());
      fastSideEffect.await(10, TimeUnit.MILLISECONDS);
      assertFalse(fastSideEffect.isDone());
    }
    assertEquals(countTasks(failure.getTrace()), expectedNumberOfTasks);
  }

  @Test
  public void testWithSideEffectExceptionInFunction() {
    Task<String> successMain = getSuccessTask();
    Function1<? super String, Task<?>> func = s -> {
      throw new RuntimeException();
    };
    Task<String> successMap = getSuccessTask();

    runAndWaitException(successMain.withSideEffect(func).flatMap(s -> successMap), RuntimeException.class);
    assertFalse(successMap.isDone());
  }

  @Test
  public void testWithSafeSideEffectFailureInTaskProviderFunction() {
    Task<String> successMain = getSuccessTask();
    Function1<? super String, Task<?>> func = s -> {
      throw new RuntimeException();
    };
    Task<String> successMap = getSuccessTask();

    runAndWait(successMain
        .withSafeSideEffect(func)
        .flatMap(s -> successMap)
    );
    assertTrue(successMap.isDone());
    assertFalse(successMain.isFailed());
    assertFalse(successMap.isFailed());
  }

  @Test
  public void testStaticWithSafeSideEffectFailureInTaskProviderFunction() {
    Callable<Task<String>> func = () -> {
      throw new RuntimeException();
    };
    Task<String> successMap = getSuccessTask();

    runAndWait(Task.withSafeSideEffect(func).flatMap(s -> successMap));
    assertTrue(successMap.isDone());
    assertFalse(successMap.isFailed());
  }

  @Test
  public void testStaticWithSideEffectFullCompletion() throws Exception {
    Task<String> slowSideEffect = delayedValue("slow", 50, TimeUnit.MILLISECONDS);
    Task<Void> sideEffect = Task.withSideEffect(() -> slowSideEffect);

    run(sideEffect);
    sideEffect.await();
    assertTrue(sideEffect.isDone());
    assertNull(sideEffect.get());
  }

  @Test
  public void testStaticSideEffectFailureIsIgnored() throws Exception {
    Task<String> failureTask = getFailureTask();
    Task<Void> sideEffect = Task.withSideEffect(() -> failureTask);

    runAndWait(sideEffect);
    assertFalse(sideEffect.isFailed());
    assertNull(sideEffect.get());
    assertEquals(countTasks(sideEffect.getTrace()), 2);
  }

  public void testOnFailure(int expectedNumberOfTasks) {
    final AtomicReference<Throwable> variable = new AtomicReference<Throwable>();
    Task<String> success = getSuccessTask().onFailure(variable::set);
    runAndWait("AbstractTaskTest.testOnFailureSuccess", success);
    assertNull(variable.get());
    assertEquals(countTasks(success.getTrace()), expectedNumberOfTasks);

    Task<String> failure = getFailureTask().onFailure(variable::set);
    try {
      runAndWait("AbstractTaskTest.testRecoverFailure", failure);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(failure.isFailed());
    }
    assertEquals(variable.get().getMessage(), TASK_ERROR_MESSAGE);
    assertEquals(countTasks(failure.getTrace()), expectedNumberOfTasks);
  }

  public void testOnFailureWhenCancelled(int expectedNumberOfTasks) {
    final AtomicReference<Throwable> variable = new AtomicReference<Throwable>();
    Task<String> cancelled = getCancelledTask().onFailure(variable::set);
    try {
      runAndWait("AbstractTaskTest.testOnFailureWhenCancelled", cancelled);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(cancelled.isFailed());
      assertTrue(Exceptions.isCancellation(cancelled.getError()));
    }
    assertNull(variable.get());
    assertEquals(countTasks(cancelled.getTrace()), expectedNumberOfTasks);
  }

  @Test
  public void testTransformSuccessToSuccess() {
    Task<String> success = getSuccessTask();
    Task<String> transformed = success.transform(tryT -> Success.of(tryT.get() + "transformed"));
    runAndWait("AbstractTaskTest.testTransformSuccessToSuccess", transformed);
    assertEquals(transformed.get(), success.get() + "transformed");
  }

  @Test
  public void testTransformSuccessToFailure() {
    Task<String> success = getSuccessTask();
    final Exception failureReason = new Exception();
    Task<String> transformed = success.transform(tryT -> Failure.of(failureReason));
    try {
      runAndWait("AbstractTaskTest.testTransformSuccessToSuccess", transformed);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(transformed.isFailed());
    }
    assertSame(transformed.getError(), failureReason);
  }

  @Test
  public void testTransformFailureToSuccess() {
    Task<String> failure = getFailureTask();
    Task<String> transformed = failure.transform(tryT -> Success.of(tryT.getError().toString() + "transformed"));
    runAndWait("AbstractTaskTest.testTransformFailureToSuccess", transformed);
    assertEquals(transformed.get(), failure.getError().toString() + "transformed");
  }

  @Test
  public void testTransformFailureToFailure() {
    Task<String> failure = getFailureTask();
    final Exception failureReason = new Exception();
    Task<String> transformed = failure.transform(tryT -> Failure.of(failureReason));
    try {
      runAndWait("AbstractTaskTest.testTransformFailureToFailure", transformed);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(transformed.isFailed());
    }
    assertSame(transformed.getError(), failureReason);
  }

  @Test
  public void testTransformWith_SuccessToFailure() {
    String msg = "transform failed";
    Task<String> success = getSuccessTask();
    Task<Integer> transformed = success.transformWith(tryT ->
      Task.callable(() -> {throw new RuntimeException(msg);}));
    try {
      runAndWait("AbstractTaskTest.testTransformWith_SuccessToFailure", transformed);
    } catch (Exception ex) {
      assertEquals(ex.getCause().getMessage(), msg);
    }
  }

  @Test
  public void testTransformWith_SuccessToSuccess() {
    Task<String> success = getSuccessTask();
    Task<Integer> transformed = success.transformWith(tryT ->
        Task.callable(() -> tryT.get().length()));
    runAndWait("AbstractTaskTest.testTransformWith_SuccessToSuccess", transformed);
    assertEquals(transformed.get().intValue(), success.get().length());
  }

  @Test
  public void testTransformWith_FailureToSuccess() {
    int returnValue = 100;
    Task<String> failed = getFailureTask();
    Task<Integer> transformed = failed.transformWith(tryT ->
        Task.callable(() -> returnValue));
    runAndWait("AbstractTaskTest.testTransformWith_FailureToSuccess", transformed);
  }

  @Test
  public void testTransformWith_FailureToFailure() {
    String msg = "transform failed";
    Task<String> failed = getFailureTask();
    Task<Integer> transformed = failed.transformWith(tryT ->
        Task.callable(() -> {throw new RuntimeException(msg);}));
    try {
      runAndWait("AbstractTaskTest.testTransformWith_FailureToFailure", transformed);
    } catch (Exception ex) {
      assertEquals(ex.getCause().getMessage(), msg);
    }
  }

  @Test
  public void testTransformWith_Cancelled() {
    Task<String> cancelled = getCancelledTask().recoverWith(e -> Task.callable("recover success", () -> "recovered"));
    try {
      runAndWait("AbstractTaskTest.testTransformWith_Cancelled", cancelled);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(cancelled.isFailed());
      assertTrue(Exceptions.isCancellation(cancelled.getError()));
    }
  }

  @Test
  public void testFlatten() {
    Task<Task<String>> nested = Task.callable(() -> getSuccessTask());
    Task<String> flat = Task.flatten(nested);
    runAndWait("AbstractTaskTest.testFlatten", flat);
    assertEquals(flat.get(), TASK_VALUE);
  }

  @Test
  public void testFailureInNestedFlatMap() {
    final Exception failureReason = new Exception();

    Task<String> failing = getSuccessTask()
        .flatMap(Task::value)
        .flatMap(Task::value)
        .flatMap(i -> Task.failure(failureReason));

    Task<String> nested = failing
        .flatMap(Task::value)
        .flatMap(Task::value);

    try {
      runAndWait("AbstractTaskTest.testFailureInNestedFlatMap", nested);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(nested.isFailed());
    }
    assertSame(nested.getError(), failureReason);
  }


  @Test
  public void testFlattenFailure() {
    Task<Task<String>> nested = Task.callable(() -> getFailureTask());
    Task<String> flat = Task.flatten(nested);
    try {
      runAndWait("AbstractTaskTest.testFlattenFailure", flat);
      fail("should have failed");
    } catch (Exception ex) {
      assertTrue(flat.isFailed());
    }
    assertEquals(flat.getError().getMessage(), TASK_ERROR_MESSAGE);
  }

  @Test
  public void testTaskNameTruncation() {
    Task<?> t = Task.value(times("x", 4096), "hello");
    assertEquals(t.getName(), times("x", 1024));
  }

  private String times(String s, int times) {
    StringBuilder sb = new StringBuilder();
    for (int i=0; i < times; i++) {
      sb.append(s);
    }
    return sb.toString();
  }

  protected static final String TASK_VALUE = "value";
  protected static final String TASK_ERROR_MESSAGE = "error";

  abstract Task<String> getSuccessTask();

  abstract Task<String> getFailureTask();

  abstract Task<String> getCancelledTask();
}
