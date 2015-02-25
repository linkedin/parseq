package com.linkedin.parseq;

import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Zhenkai Zhu
 */
public class TestTask extends BaseEngineTest
{
  @Test
  public void testMap()
  {

    Task<Integer> task = getTask().map("strlen", String::length);

    runAndWait("TestTask.testMap", task);
    Assert.assertTrue(task.isDone());
    Assert.assertEquals((int)task.get(), 12);
  }

  @Test
  public void testFlatMap()
  {
    Task<String> task = getTask().flatMap("transform", str ->
        Task.callable("strlenstr", () -> String.valueOf(str.length())));

    runAndWait("TestTask.testFlatMap", task);
    Assert.assertEquals(task.get(), "12");
  }

  @Test
  public void testAndThenConsumer()
  {
    // Consumer
    final AtomicReference<String> consumer = new AtomicReference<String>();
    Task<String> task1 = getTask().andThen("consume", consumer::set);
    runAndWait("TestTask.testAndThenConsumer", task1);
    Assert.assertEquals(task1.get(), "hello world!");
    Assert.assertEquals(consumer.get(), "hello world!");
  }

  @Test
  public void testAndThenTask()
  {
    // Task
    Task<Integer> task2 = getTask().andThen("seq", Task.callable("life", () -> 42));
    runAndWait("TestTask.testAndThenTask", task2);
    Assert.assertEquals((int) task2.get(), 42);
  }

  @Test
  public void testRecover()
  {
    Task<Integer> success = getTask().map("strlen", String::length)
        .recover("recover", e -> -1);
    runAndWait("TestTask.testRecoverSuccess", success);
    Assert.assertEquals((int)success.get(), 12);

    Task<Integer> failure = getFailureTask().map("strlen", String::length)
        .recover("recover", e -> -1);
    runAndWait("TestTask.testRecoverFailure", failure);
    Assert.assertEquals((int)failure.get(), -1);
  }

  @Test
  public void testNoRecover()
  {
    Task<Integer> task = getFailureTask().map("strlen", String::length);
    try
    {
      runAndWait("TestTask.testNoRecover", task);
      Assert.fail("should have failed");
    }
    catch (Exception ex)
    {
      Assert.assertEquals(ex.getCause().getMessage(), "task failed!");
    }
  }

  @Test
  public void testTry()
  {
    Task<Try<Integer>> success = getTask().map("strlen", String::length)
        .withTry();
    runAndWait("TestTask.testTrySuccess", success);
    Assert.assertFalse(success.get().isFailed());
    Assert.assertEquals((int)success.get().get(), 12);

    Task<Try<Integer>> failure = getFailureTask().map("strlen", String::length)
        .withTry();
    runAndWait("TestTask.testTryFailure", failure);
    Assert.assertTrue(failure.get().isFailed());
    Assert.assertEquals(failure.get().getError().getMessage(), "task failed!");

  }

  @Test
  public void testRecoverWith()
  {
    Task<Integer> success = getTask().map("strlen", String::length)
        .recoverWith("recoverWith", e -> Task.callable("recover failure", () -> {
          throw new RuntimeException("recover failed!");
        }));
    runAndWait("TestTask.testRecoverWithSuccess", success);
    Assert.assertEquals((int) success.get(), 12);

    Task<Integer> failure = getFailureTask().map("strlen", String::length)
        .recoverWith("recoverWith", e -> Task.callable("recover failure", () -> {
          throw new RuntimeException("recover failed!");
        }));
    try
    {
      runAndWait("TestTask.testRecoverWithFailure", failure);
      Assert.fail("should have failed");
    }
    catch (Exception ex)
    {
      // fail with throwable from recovery function
      Assert.assertEquals(ex.getCause().getMessage(), "recover failed!");
    }

    Task<Integer> recovered = getFailureTask().map("strlen", String::length)
        .recoverWith("recoverWith", e -> Task.callable("recover success", () -> -1));
    runAndWait("TestTask.testRecoverWithRecoverd", recovered);
    Assert.assertEquals((int)recovered.get(), -1);
  }

  // TODO: fallback fail with java.lang.IllegalStateException:
  // Detected a cycle that includes: [com.linkedin.parseq.FusionTask@5b4f424e, com.linkedin.parseq.Task$6@5603bb00]
  // understand and fix
  @Test
  public void testFallBackTo()
  {
    Task<Integer> success = getTask().map("strlen", String::length)
        .fallBackTo("fallBackTo", e -> Task.callable("recover failure", () -> {
          throw new RuntimeException("recover failed!");
        }));
    runAndWait("TestTask.testFallBackToSuccess", success);
    Assert.assertEquals((int) success.get(), 12);

    Task<Integer> failure = getFailureTask().map("strlen", String::length)
        .fallBackTo("fallBackTo", e -> Task.callable("recover failure", () -> {
          throw new RuntimeException("recover failed!");
        }));
    try
    {
      runAndWait("TestTask.testFallBAckToFailure", failure);
      Assert.fail("should have failed!");
    }
    catch (Exception ex)
    {
      // fail with original throwable
      Assert.assertEquals(ex.getCause().getMessage(), "task failed!");
    }

    Task<Integer> recovered = getFailureTask().map("strlen", String::length)
        .fallBackTo("fallBackTo", e -> Task.callable("recover success", () -> -1));
    runAndWait("TestTask.testFallBAckToFailure", recovered);
    Assert.assertEquals((int)recovered.get(), -1);
  }

  @Test
  public void testWithTimeout()
  {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    Task<Integer> success = getTask().andThen(getDelayTask(0, scheduler, 30))
        .withTimeout(100, TimeUnit.MILLISECONDS);
    runAndWait("TestTask.testWithTimeoutSuccess", success);
    Assert.assertEquals((int)success.get(), 0);

    Task<Integer> failure = getTask().andThen(getDelayTask(0, scheduler, 110)).withTimeout(100, TimeUnit.MILLISECONDS);
    try
    {
      runAndWait("TestTask.testWithTimeoutFailure", failure);
      Assert.fail("should have failed!");
    }
    catch (Exception ex)
    {
      Assert.assertSame(ex.getCause(), Exceptions.TIMEOUT_EXCEPTION);
    }
    scheduler.shutdown();
  }

  @Test(enabled = false)
  public void testWithSideEffectViaTask() throws Exception
  {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // side effect task
    Task<String> taskFastMain = getTask();
    Task<String> taskSlowSideEffect = getDelayTask("slooow", scheduler, 5100);
    Task<String> taskPartial = taskFastMain.withSideEffect(taskSlowSideEffect);

    Task<String> taskFastMain2 = getTask();
    Task<String> taskSlowSideEffect2 = getDelayTask("slow", scheduler, 50);
    Task<String> taskFull = taskFastMain2.withSideEffect(taskSlowSideEffect2);

    Task<String> taskCancelMain = getDelayTask("canceled", scheduler, 6000);
    Task<String> taskFastSideEffect = getTask();
    Task<String> taskCancel = taskCancelMain.withSideEffect(taskFastSideEffect);

    testWithSideEffect("viaTask", taskFastMain, taskSlowSideEffect, taskPartial,
        taskFastMain2, taskSlowSideEffect2, taskFull, taskCancelMain, taskFastSideEffect, taskCancel);

    scheduler.shutdown();

  }

  @Test(enabled = false)
  public void testWithSideEffectViaFunction() throws Exception
  {
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // side effect function
    Task<String> functionFastMain = getTask();
    Task<String> functionSlowSideEffect = getDelayTask("slooow", scheduler, 5100);
    Task<String> functionPartial = functionFastMain.withSideEffect(s -> functionSlowSideEffect);

    Task<String> functionFastMain2 = getTask();
    Task<String> functionSlowSideEffect2 = getDelayTask("slow", scheduler, 50);
    Task<String> functionFull = functionFastMain2.withSideEffect(s -> functionSlowSideEffect2);

    Task<String> functionCancelMain = getDelayTask("canceled", scheduler, 6000);
    Task<String> functionFastSideEffect = getTask();
    Task<String> functionCancel = functionCancelMain.withSideEffect(s -> functionFastSideEffect);

    testWithSideEffect("viaFunction", functionFastMain, functionSlowSideEffect, functionPartial,
        functionFastMain2, functionSlowSideEffect2, functionFull,
        functionCancelMain, functionFastSideEffect, functionCancel);

    scheduler.shutdown();

  }

  private void testWithSideEffect(String tag,
                                  Task<String> fastMain,
                                  Task<String> slowSideEffect,
                                  Task<String> partial,
                                  Task<String> fastMain2,
                                  Task<String> slowSideEffect2,
                                  Task<String> full,
                                  Task<String> cancelMain,
                                  Task<String> fastSideEffect,
                                  Task<String> cancel) throws Exception
  {
    // ensure the whole task can finish before individual side effect task finishes
    runAndWait("TestTask.testWithSideEffectPartial" + "_" + tag, partial);
    Assert.assertTrue(fastMain.isDone());
    Assert.assertTrue(partial.isDone());
    Assert.assertFalse(slowSideEffect.isDone());

    // ensure the side effect ask will be run
    runAndWait("TestTask.testWithSideEffectFullCompletion" + "_" + tag, full);
    slowSideEffect2.await();
    Assert.assertTrue(full.isDone());
    Assert.assertTrue(fastMain2.isDone());
    Assert.assertTrue(slowSideEffect2.isDone());

    // test cancel main
    getEngine().run(cancel);
    Assert.assertTrue(cancelMain.cancel(new Exception("canceled")));
    cancel.await();
    fastSideEffect.await(10, TimeUnit.MILLISECONDS);
    Assert.assertTrue(cancel.isDone());
    Assert.assertFalse(fastSideEffect.isDone());

  }

  private static Task<String> getTask()
  {
    return Task.callable("success", () -> "hello world!");
  }

  private static Task<String> getFailureTask()
  {
    return Task.callable("failure", () -> { throw new RuntimeException("task failed!");});
  }

  private static <T> Task<T> getDelayTask(T value, ScheduledExecutorService scheduler, long delay)
  {
    return new BaseTask<T>()
    {
      @Override
      protected Promise<? extends T> run(Context context) throws Throwable
      {
        final SettablePromise<T> promise = Promises.settable();
        scheduler.schedule(() -> promise.done(value), delay, TimeUnit.MILLISECONDS);
        return promise;
      }
    };
  }
}
