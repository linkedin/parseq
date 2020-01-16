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

import static com.linkedin.parseq.Task.value;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.testng.annotations.Test;

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class TestTasks extends BaseEngineTest {

  private static class UnloggableException extends Exception {
    @Override
    public String toString() {
      throw new RuntimeException("Cannot log an UnloggableException!");
    }
  }

  @Test
  public void testTaskThatThrows() throws InterruptedException {
    final Exception error = new Exception();

    final Task<Object> task = new BaseTask<Object>() {
      @Override
      protected Promise<Object> run(final Context context) throws Exception {
        throw error;
      }
    };

    try {
      runAndWait("TestTasks.testTaskThatThrows", task);
      fail("task should finish with Exception");
    } catch (Throwable t) {
      assertEquals(error, task.getError());
    }

    assertTrue(task.isFailed());
  }

  @Test
  public void testTaskThatThrowsUnloggableException() throws InterruptedException {
    final Exception error = new UnloggableException();

    final Task<Object> task = new BaseTask<Object>() {
      @Override
      protected Promise<Object> run(final Context context) throws Exception {
        throw error;
      }
    };

    try {
      runAndWait("TestTasks.testTaskThatThrowsUnloggableException", task);
      fail("task should finish with Exception");
    } catch (Throwable t) {
      assertEquals(error, task.getError());
    }

    assertTrue(task.isFailed());
  }

  @Test
  public void testAwait() throws InterruptedException {
    final String value = "value";
    final Task<String> task = value("value", value);
    final AtomicReference<Boolean> resultRef = new AtomicReference<Boolean>(false);

    task.addListener(new PromiseListener<String>() {
      @Override
      public void onResolved(Promise<String> stringPromise) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          //ignore
        } finally {
          resultRef.set(true);
        }
      }
    });

    runAndWait("TestTasks.testAwait", task);
    assertEquals(Boolean.TRUE, resultRef.get());
  }

  @Test
  public void testToCompletionStage() throws InterruptedException, ExecutionException {
    String result = "CompletionStageResult";
    Task<String> task = Task.fromCompletionStage(() ->{
      CompletableFuture<String> completableFuture
          = CompletableFuture.supplyAsync(() -> result);
      return completableFuture;
    });
    CompletionStage<String> future = task.toCompletionStage();
    runAndWait("TestTasks.testToCompletionStage", task);
    future.whenComplete((r, ex) -> assertEquals(result, r));
  }


  @Test
  public void testFromCompletionStage() {
    String result = "FromCompletionStageResult";
    Task<String> task = Task.fromCompletionStage(() ->{
      CompletableFuture<String> completableFuture
          = CompletableFuture.supplyAsync(() -> result);
      return completableFuture;
    });
    runAndWait("testFromCompletionStage", task);
    assertEquals(result, task.get());
  }

  @Test
  public void testFromCompletionStageWithTimeConsumingFuture() throws InterruptedException {
    String result = "FromCompletionStageResult";
    Task<String> task = Task.fromCompletionStage(() ->{
      CompletableFuture<String> completableFuture
          = CompletableFuture.supplyAsync(() -> {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        return result;
      });
      return completableFuture;
    });
    runAndWait("testFromCompletionStageWithTimeConsumingFuture", task);
    assertEquals(result, task.get());
  }

  @Test
  public void testFromCompletionStageWithCompletionStageException() {
    Task<String> task = Task.fromCompletionStage(() ->{
      CompletableFuture<String> completableFuture
          = CompletableFuture.supplyAsync(() -> {
        throw new RuntimeException();
      });
      return completableFuture;
    });
    runAndWaitException("testFromCompletionStageWithCompletionStageException", task, CompletionException.class);
  }

  @Test
  public void testFromCompletionStageWithCallableException() {
    Task<String> task = Task.fromCompletionStage(() ->{
      throw new RuntimeException();
    });
    runAndWaitException("testFromCompletionStageWithCallableException", task, RuntimeException.class);
  }

  @Test
  public void testSideEffectPartialCompletion() throws InterruptedException {
    // ensure that the whole can finish before the individual side effect task finishes.
    Task<String> fastTask = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        return Promises.value("fast");
      }
    };

    // this task will not complete.
    Task<String> settableTask = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        return Promises.settable();
      }
    };

    Task<String> withSideEffect = fastTask.withSideEffect(x -> settableTask);
    runAndWait("TestTasks.testSideEffectPartialCompletion", withSideEffect);
    assertTrue(withSideEffect.isDone());
    assertTrue(fastTask.isDone());
    assertFalse(settableTask.isDone());
  }

  @Test
  public void testSideEffectFullCompletion() throws InterruptedException {
    // ensure that the individual side effect task will be run
    Task<String> taskOne = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        return Promises.value("one");
      }
    };

    Task<String> taskTwo = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        return Promises.value("two");
      }
    };

    Task<String> withSideEffect = taskOne.withSideEffect(x -> taskTwo);
    runAndWait("TestTasks.testSideEffectFullCompletion", withSideEffect);
    taskTwo.await();
    assertTrue(withSideEffect.isDone());
    assertTrue(taskTwo.isDone());
  }

  @Test
  public void testSideEffectCancelled() throws InterruptedException {
    // this task will not complete.
    Task<String> settableTask = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        return Promises.settable();
      }
    };

    Task<String> fastTask = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        return Promises.value("fast");
      }
    };

    Task<String> withSideEffect = settableTask.withSideEffect(x -> fastTask);
    // add 10 ms delay so that we can cancel settableTask reliably
    getEngine().run(delayedValue("value", 10, TimeUnit.MILLISECONDS).andThen(withSideEffect));
    assertTrue(settableTask.cancel(new Exception("task cancelled")));
    withSideEffect.await();
    fastTask.await(10, TimeUnit.MILLISECONDS);
    assertTrue(withSideEffect.isDone());
    assertFalse(fastTask.isDone());
  }

  @Test
  public void testTimeoutTaskWithTimeout() throws InterruptedException {
    // This task will not complete on its own, which allows us to test the timeout
    final Task<String> task = new BaseTask<String>("task") {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception {
        return Promises.settable();
      }
    };

    final Task<String> timeoutTask = task.withTimeout(200, TimeUnit.MILLISECONDS);

    try {
      runAndWait("TestTasks.testTimeoutTaskWithTimeout", timeoutTask);
      fail("task should finish with Error");
    } catch (Throwable t) {
      assertTrue(timeoutTask.getError() instanceof TimeoutException);
    }

    assertTrue(timeoutTask.isFailed());

    assertTrue(task.await(5, TimeUnit.SECONDS));

    // The original task should also be failed - this time with an early finish
    // exception.
    assertTrue(task.isFailed());
    assertTrue(Exceptions.isEarlyFinish(task.getError()));
  }

  @Test
  public void testTimeoutTaskWithoutTimeout() throws InterruptedException {
    final String value = "value";
    final Task<String> task = Task.callable("task", () -> value);

    final Task<String> timeoutTask = task.withTimeout(200, TimeUnit.MILLISECONDS);

    runAndWait("TestTasks.testTimeoutTaskWithoutTimeout", timeoutTask);

    assertEquals(value, task.get());

    // The wrapping task should get the same value as the wrapped task
    assertEquals(value, timeoutTask.get());
  }

  @Test
  public void testTimeoutTaskWithError() throws InterruptedException {
    final Exception error = new Exception();
    final Task<String> task = Task.callable("task", () -> {
      throw error;
    } );

    final Task<String> timeoutTask = task.withTimeout(2000, TimeUnit.MILLISECONDS);

    getEngine().run(timeoutTask);

    // We should complete with an error almost immediately
    assertTrue(timeoutTask.await(100, TimeUnit.MILLISECONDS));

    assertTrue(timeoutTask.isFailed());
    assertEquals(error, timeoutTask.getError());
  }

  /**
   * Test scenario in which there are many TimeoutWithErrorTasks scheduled for execution
   * e.g. by using Tasks.par().
   */
  @Test
  public void testManyTimeoutTaskWithoutTimeoutOnAQueue() throws InterruptedException, IOException {
    final String value = "value";
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    List<Task<String>> tasks = new ArrayList<Task<String>>();
    for (int i = 0; i < 50; i++) {

      // Task which simulates doing something for 0.5ms and setting response
      // asynchronously after 5ms.
      Task<String> t = new BaseTask<String>("test") {
        @Override
        protected Promise<? extends String> run(Context context) throws Throwable {
          final SettablePromise<String> result = Promises.settable();
          Thread.sleep(0, 500000);
          scheduler.schedule(new Runnable() {
            @Override
            public void run() {
              result.done(value);
            }
          }, 5, TimeUnit.MILLISECONDS);
          return result;
        }
      };
      tasks.add(t.withTimeout(50, TimeUnit.MILLISECONDS));
    }

    // final task runs all the tasks in parallel
    final Task<?> timeoutTask = Tasks.par(tasks);

    runAndWait("TestTasks.testManyTimeoutTaskWithoutTimeoutOnAQueue", timeoutTask);

    scheduler.shutdown();

    //tasks should not time out
    assertEquals(false, timeoutTask.isFailed());
  }

  @Test
  public void testDelayTaskCompleted() throws InterruptedException {
    final Task<Integer> task = Task.callable(() -> 1234);
    final Task<Integer> taskWithDelay = task.withDelay(1, TimeUnit.SECONDS);

    getEngine().run(taskWithDelay);

    taskWithDelay.await(200, TimeUnit.MILLISECONDS);
    // Both tasks should not be completed yet, since the delay is currently still ongoing.
    assertFalse(taskWithDelay.isDone());
    assertFalse(task.isDone());

    taskWithDelay.await(3, TimeUnit.SECONDS);
    // Both tasks should now be completed.
    assertTrue(taskWithDelay.isDone());
    assertTrue(task.isDone());
    // Both tasks should not have failed.
    assertFalse(task.isFailed());
    assertFalse(taskWithDelay.isFailed());

    // The promise should be resolved and the underlying task's value should be cascaded to the top level task.
    assertEquals(1234, taskWithDelay.get().intValue());
  }

  @Test
  public void testDelayTaskFailed() {
    IllegalArgumentException exception = new IllegalArgumentException("Oops!");
    final Task<Integer> task = Task.callable(() -> {
      throw exception;
    });
    final Task<Integer> taskWithDelay = task.withDelay(200, TimeUnit.MILLISECONDS);

    IllegalArgumentException actualException = runAndWaitException(taskWithDelay, IllegalArgumentException.class);

    assertEquals(exception, actualException);

    // Both tasks should be completed.
    assertTrue(task.isDone());
    assertTrue(taskWithDelay.isDone());

    // Both tasks should have failed.
    assertTrue(task.isFailed());
    assertTrue(taskWithDelay.isFailed());
  }

  @Test
  public void testSetPriorityBelowMinValue() {
    try {
      TestUtil.noop().setPriority(Priority.MIN_PRIORITY - 1);
      fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Expected case
    }
  }

  @Test
  public void testSetPriorityAboveMaxValue() {
    try {
      TestUtil.noop().setPriority(Priority.MAX_PRIORITY + 1);
      fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Expected case
    }
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testThrowableCallableNoError() throws InterruptedException {
    final Integer magic = 0x5f3759df;
    final ThrowableCallable<Integer> callable = new ThrowableCallable<Integer>() {
      @Override
      public Integer call() throws Throwable {
        return magic;
      }
    };
    final Task<Integer> task = Tasks.callable("magic", callable);

    getEngine().run(task);
    task.await(100, TimeUnit.MILLISECONDS);

    assertTrue(task.isDone());
    assertFalse(task.isFailed());
    assertEquals(magic, task.get());
    assertEquals("magic", task.getName());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testThrowableCallableWithError() throws InterruptedException {
    final Throwable throwable = new Throwable();
    final ThrowableCallable<Integer> callable = new ThrowableCallable<Integer>() {
      @Override
      public Integer call() throws Throwable {
        throw throwable;
      }
    };
    final Task<Integer> task = Tasks.callable("error", callable);

    getEngine().run(task);
    task.await(100, TimeUnit.MILLISECONDS);

    assertTrue(task.isDone());
    assertTrue(task.isFailed());
    assertEquals("Throwable should not be wrapped", throwable, task.getError());
    assertEquals("error", task.getName());
  }
}
