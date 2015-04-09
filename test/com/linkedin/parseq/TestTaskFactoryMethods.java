package com.linkedin.parseq;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.testng.annotations.Test;

import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestTaskFactoryMethods extends BaseEngineTest {

  @Test
  public void testAction() {
    final AtomicReference<String> variable = new AtomicReference<String>();
    Task<Void> task = Task.action(() -> variable.set("value"));
    runAndWait("TestTaskFactoryMethods.testAction", task);
    assertEquals(variable.get(), "value");

    assertEquals(countTasks(task.getTrace()), 1);
  }

  @Test
  public void testValue() {
    Task<String> task = Task.value("value");
    runAndWait("TestTaskFactoryMethods.testValue", task);
    assertEquals(task.get(), "value");

    assertEquals(countTasks(task.getTrace()), 1);
  }

  @Test
  public void testFailure() {
    Exception e = new Exception("ups!");
    Task<?> task = Task.failure(e);
    try {
      runAndWait("TestTaskFactoryMethods.testFailure", task);
      fail("should have failed");
    } catch (Exception ex) {
      assertEquals(task.getError(), e);
    }

    assertEquals(countTasks(task.getTrace()), 1);
  }

  @Test
  public void testCallable() {
    Task<UUID> task = Task.callable(UUID::randomUUID);
    runAndWait("TestTaskFactoryMethods.testCallable", task);
    assertNotNull(task.get());

    assertEquals(countTasks(task.getTrace()), 1);
  }

  @Test
  public void testAsync() {
    final SettablePromise<String> promise = Promises.settable();
    Task<String> task = Task.async(() -> promise);
    getScheduler().schedule(() -> promise.done("done"), 10, TimeUnit.MILLISECONDS);
    String value = runAndWait("TestTaskFactoryMethods.testAsync", task);
    assertEquals(value, "done");

    assertEquals(countTasks(task.getTrace()), 1);
  }

  @Test
  public void testAsyncWithContext() {
    final Task<String> t = Task.callable(() -> "done");
    Task<String> task = Task.async(ctx -> {
      ctx.run(t);
      return t;
    });
    String value = runAndWait("TestTaskFactoryMethods.testAsyncWithContext", task);
    assertEquals(value, "done");

    assertEquals(countTasks(task.getTrace()), 2);
  }

  @Test
  public void testBlocking() {
    TestingExecutorService es = new TestingExecutorService(Executors.newSingleThreadExecutor());
    try {
      Task<String> task = Task.blocking(() -> "from blocking", es);
      runAndWait("TestTaskFactoryMethods.testBlocking", task);
      assertEquals(task.get(), "from blocking");
      assertEquals(es.getCount(), 1);

      assertEquals(countTasks(task.getTrace()), 1);

    } finally {
      es.shutdown();
    }
  }

  @Test
  public void testPar2() {
    final AtomicInteger sum = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2)).andThen(t -> t.forEach(i -> sum.addAndGet((int) i)));

    runAndWait("TestTaskFactoryMethods.testPar2", task);
    assertEquals(sum.get(), 1 + 2);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 2);
  }

  @Test
  public void testPar3() {
    final AtomicInteger sum = new AtomicInteger();
    Task<?> task =
        Task.par(Task.value(1), Task.value(2), Task.value(3)).andThen(t -> t.forEach(i -> sum.addAndGet((int) i)));

    runAndWait("TestTaskFactoryMethods.testPar3", task);
    assertEquals(sum.get(), 1 + 2 + 3);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 3);
  }

  @Test
  public void testPar4() {
    final AtomicInteger sum = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4))
        .andThen(t -> t.forEach(i -> sum.addAndGet((int) i)));

    runAndWait("TestTaskFactoryMethods.testPar4", task);
    assertEquals(sum.get(), 1 + 2 + 3 + 4);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 4);
  }

  @Test
  public void testPar5() {
    final AtomicInteger sum = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5))
        .andThen(t -> t.forEach(i -> sum.addAndGet((int) i)));

    runAndWait("TestTaskFactoryMethods.testPar5", task);
    assertEquals(sum.get(), 1 + 2 + 3 + 4 + 5);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 5);
  }

  @Test
  public void testPar6() {
    final AtomicInteger sum = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6))
        .andThen(t -> t.forEach(i -> sum.addAndGet((int) i)));

    runAndWait("TestTaskFactoryMethods.testPar6", task);
    assertEquals(sum.get(), 1 + 2 + 3 + 4 + 5 + 6);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 6);
  }

  @Test
  public void testPar7() {
    final AtomicInteger sum = new AtomicInteger();
    Task<?> task = Task
        .par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7))
        .andThen(t -> t.forEach(i -> sum.addAndGet((int) i)));

    runAndWait("TestTaskFactoryMethods.testPar7", task);
    assertEquals(sum.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 7);
  }

  @Test
  public void testPar8() {
    final AtomicInteger sum = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6),
        Task.value(7), Task.value(8)).andThen(t -> t.forEach(i -> sum.addAndGet((int) i)));

    runAndWait("TestTaskFactoryMethods.testPar8", task);
    assertEquals(sum.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 8);
  }

  @Test
  public void testPar9() {
    final AtomicInteger sum = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6),
        Task.value(7), Task.value(8), Task.value(9)).andThen(t -> t.forEach(i -> sum.addAndGet((int) i)));

    runAndWait("TestTaskFactoryMethods.testPar9", task);
    assertEquals(sum.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 9);
  }
}
