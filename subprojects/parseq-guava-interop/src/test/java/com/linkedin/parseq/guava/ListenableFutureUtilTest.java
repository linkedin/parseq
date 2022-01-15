package com.linkedin.parseq.guava;

import com.linkedin.parseq.BaseEngineTest;
import com.google.common.util.concurrent.ListenableFuture;
import com.linkedin.parseq.Task;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * Unit tests for {@link ListenableFutureUtil}
 */
public class ListenableFutureUtilTest extends BaseEngineTest {

  private void runUntilComplete(Task task) throws Exception {
    this.getEngine().run(task);
    task.await();
  }

  @Test
  public void testFromListenableFuture() throws Exception {
    ListenableFutureUtil.SettableFuture<String> listenableFuture = new ListenableFutureUtil.SettableFuture<>();
    Task<String> task = ListenableFutureUtil.fromListenableFuture(listenableFuture);

    // Test cancel propagation from Task to ListenableFuture
    task.cancel(new RuntimeException());
    Assert.assertTrue(listenableFuture.isCancelled());

    listenableFuture = new ListenableFutureUtil.SettableFuture<>();
    task = ListenableFutureUtil.fromListenableFuture(listenableFuture);

    // Test successful completion of ListenableFuture.
    listenableFuture.set("COMPLETED");
    runUntilComplete(task);
    Assert.assertTrue(task.isDone());
    Assert.assertFalse(task.isFailed());
    Assert.assertEquals(task.get(), "COMPLETED");

    listenableFuture = new ListenableFutureUtil.SettableFuture<>();
    task = ListenableFutureUtil.fromListenableFuture(listenableFuture);

    // Test exceptional completion of ListenableFuture.
    listenableFuture.setException(new RuntimeException("Test"));
    runUntilComplete(task);
    Assert.assertTrue(task.isDone());
    Assert.assertTrue(task.isFailed());
    Assert.assertEquals(task.getError().getClass(), RuntimeException.class);
    Assert.assertEquals(task.getError().getMessage(), "Test");

    listenableFuture = new ListenableFutureUtil.SettableFuture<>();
    task = ListenableFutureUtil.fromListenableFuture(listenableFuture);

    // Test cancellation of ListenableFuture.
    listenableFuture.cancel(true);
    runUntilComplete(task);
    Assert.assertTrue(task.isDone());
    Assert.assertTrue(task.isFailed());
    Assert.assertEquals(task.getError().getCause().getClass(), CancellationException.class);
  }

  @Test
  public void testToListenableFuture() throws Exception {
    ListenableFutureUtil.SettableTask<String> task = new ListenableFutureUtil.SettableTask<>("test");
    ListenableFuture<String> future = ListenableFutureUtil.toListenableFuture(task);

    // Test cancel propagation from ListenableFuture to task
    future.cancel(true);
    Assert.assertTrue(task.isDone());
    Assert.assertTrue(task.isFailed());
    Assert.assertEquals(task.getError().getCause().getClass(), CancellationException.class);

    task = new ListenableFutureUtil.SettableTask<>("test");
    future = ListenableFutureUtil.toListenableFuture(task);

    // Test successful completion of task.
    task.getSettablePromise().done("COMPLETED");
    runUntilComplete(task);
    Assert.assertTrue(future.isDone());
    Assert.assertEquals(future.get(), "COMPLETED");

    task = new ListenableFutureUtil.SettableTask<>("test");
    future = ListenableFutureUtil.toListenableFuture(task);

    // Test exceptional completion of task.
    task.getSettablePromise().fail(new RuntimeException("Test"));
    runUntilComplete(task);
    Assert.assertTrue(future.isDone());
    Assert.assertTrue(future.isDone());
    try {
      future.get();
      Assert.fail("ExecutionException not thrown");
    } catch (ExecutionException e) {
      Assert.assertEquals(e.getCause().getClass(), RuntimeException.class);
      Assert.assertEquals(e.getCause().getMessage(), "Test");
    }

    task = new ListenableFutureUtil.SettableTask<>("test");
    future = ListenableFutureUtil.toListenableFuture(task);

    // Test cancellation of task.
    task.cancel(new RuntimeException("Cancelled"));
    Assert.assertTrue(future.isDone());
    Assert.assertTrue(future.isCancelled());
    try {
      future.get();
      Assert.fail("Cancellation Exception not thrown");
    } catch (CancellationException e) {
      // Ignored since we expected a cancellation exception!
    } catch (Throwable e) {
      Assert.fail("Unexpected Exception thrown", e);
    }
  }
}
