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
    Task<Integer> task = Task.failure(e);
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
  public void testPar2AndThen() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2)).andThen((a, b) -> value.set(a + b));

    runAndWait("TestTaskFactoryMethods.testPar2AndThen", task);
    assertEquals(value.get(), 1 + 2);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 2);
  }

  @Test
  public void testPar3AndThen() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3)).andThen((a, b, c) -> value.set(a + b + c));

    runAndWait("TestTaskFactoryMethods.testPar3AndThen", task);
    assertEquals(value.get(), 1 + 2 + 3);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 3);
  }

  @Test
  public void testPar4AndThen() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4)).andThen((a, b, c, d) -> value.set(a + b + c + d));

    runAndWait("TestTaskFactoryMethods.testPar4AndThen", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 4);
  }

  @Test
  public void testPar5AndThen() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5)).andThen((a, b, c, d, e) -> value.set(a + b + c + d + e));

    runAndWait("TestTaskFactoryMethods.testPar5AndThen", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4 + 5);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 5);
  }

  @Test
  public void testPar6AndThen() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6))
        .andThen((a, b, c, d, e, f) -> value.set(a + b + c + d + e + f));

    runAndWait("TestTaskFactoryMethods.testPar6AndThen", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4 + 5 + 6);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 6);
  }

  @Test
  public void testPar7AndThen() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7))
        .andThen((a, b, c, d, e, f, g) -> value.set(a + b + c + d + e + f + g));

    runAndWait("TestTaskFactoryMethods.testPar7AndThen", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 7);
  }

  @Test
  public void testPar8AndThen() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7), Task.value(8))
        .andThen((a, b, c, d, e, f, g, h) -> value.set(a + b + c + d + e + f + g + h));

    runAndWait("TestTaskFactoryMethods.testPar8AndThen", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 8);
  }

  @Test
  public void testPar9AndThen() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7), Task.value(8), Task.value(9))
        .andThen((a, b, c, d, e, f, g, h, i) -> value.set(a + b + c + d + e + f + g + h + i));

    runAndWait("TestTaskFactoryMethods.testPar9AndThen", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 9);
  }

  @Test
  public void testPar2AndThenDsc() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2)).andThen("test", (a, b) -> value.set(a + b));

    runAndWait("TestTaskFactoryMethods.testPar2AndThenDsc", task);
    assertEquals(value.get(), 1 + 2);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 2);
  }

  @Test
  public void testPar3AndThenDsc() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3)).andThen("test", (a, b, c) -> value.set(a + b + c));

    runAndWait("TestTaskFactoryMethods.testPar3AndThenDsc", task);
    assertEquals(value.get(), 1 + 2 + 3);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 3);
  }

  @Test
  public void testPar4AndThenDsc() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4)).andThen("test", (a, b, c, d) -> value.set(a + b + c + d));

    runAndWait("TestTaskFactoryMethods.testPar4AndThenDsc", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 4);
  }

  @Test
  public void testPar5AndThenDsc() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5)).andThen("test", (a, b, c, d, e) -> value.set(a + b + c + d + e));

    runAndWait("TestTaskFactoryMethods.testPar5AndThenDsc", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4 + 5);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 5);
  }

  @Test
  public void testPar6AndThenDsc() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6))
        .andThen("test", (a, b, c, d, e, f) -> value.set(a + b + c + d + e + f));

    runAndWait("TestTaskFactoryMethods.testPar6AndThenDsc", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4 + 5 + 6);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 6);
  }

  @Test
  public void testPar7AndThenDsc() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7))
        .andThen("test", (a, b, c, d, e, f, g) -> value.set(a + b + c + d + e + f + g));

    runAndWait("TestTaskFactoryMethods.testPar7AndThenDsc", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 7);
  }

  @Test
  public void testPar8AndThenDsc() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7), Task.value(8))
        .andThen("test", (a, b, c, d, e, f, g, h) -> value.set(a + b + c + d + e + f + g + h));

    runAndWait("TestTaskFactoryMethods.testPar8AndThenDsc", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 8);
  }

  @Test
  public void testPar9AndThenDsc() {
    AtomicInteger value = new AtomicInteger();
    Task<?> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7), Task.value(8), Task.value(9))
        .andThen("test", (a, b, c, d, e, f, g, h, i) -> value.set(a + b + c + d + e + f + g + h + i));

    runAndWait("TestTaskFactoryMethods.testPar9AndThenDsc", task);
    assertEquals(value.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 9);
  }

  @Test
  public void testPar2() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2)).map((a, b) -> a + b);

    runAndWait("TestTaskFactoryMethods.testPar2", task);
    assertEquals((int)task.get(), 1 + 2);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 2);
  }

  @Test
  public void testPar3() {
    Task<Integer> task =
        Task.par(Task.value(1), Task.value(2), Task.value(3)).map((a, b, c) -> a + b + c);

    runAndWait("TestTaskFactoryMethods.testPar3", task);
    assertEquals((int)task.get(), 1 + 2 + 3);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 3);
  }

  @Test
  public void testPar4() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4))
        .map((a, b, c, d) -> a + b + c + d);

    runAndWait("TestTaskFactoryMethods.testPar4", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 4);
  }

  @Test
  public void testPar5() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5))
        .map((a, b, c, d, e) -> a + b + c + d + e);

    runAndWait("TestTaskFactoryMethods.testPar5", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 5);
  }

  @Test
  public void testPar6() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6))
        .map((a, b, c, d, e, f) -> a + b + c + d + e + f);

    runAndWait("TestTaskFactoryMethods.testPar6", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 6);
  }

  @Test
  public void testPar7() {
    Task<Integer> task = Task
        .par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7))
          .map((a, b, c, d, e, f, g) -> a + b + c + d + e + f + g);

    runAndWait("TestTaskFactoryMethods.testPar7", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 7);
  }

  @Test
  public void testPar8() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6),
        Task.value(7), Task.value(8))
        .map((a, b, c, d, e, f, g, h) -> a + b + c + d + e + f + g + h);

    runAndWait("TestTaskFactoryMethods.testPar8", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 8);
  }

  @Test
  public void testPar9() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6),
        Task.value(7), Task.value(8), Task.value(9))
          .map((a, b, c, d, e, f, g, h, i) -> a + b + c + d + e + f + g + h + i);

    runAndWait("TestTaskFactoryMethods.testPar9", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 9);
  }

  @Test
  public void testPar2Dsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2)).map("test", (a, b) -> a + b);

    runAndWait("TestTaskFactoryMethods.testPar2Dsc", task);
    assertEquals((int)task.get(), 1 + 2);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 2);
  }

  @Test
  public void testPar3Dsc() {
    Task<Integer> task =
        Task.par(Task.value(1), Task.value(2), Task.value(3)).map("test", (a, b, c) -> a + b + c);

    runAndWait("TestTaskFactoryMethods.testPar3Dsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 3);
  }

  @Test
  public void testPar4Dsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4))
        .map("test", (a, b, c, d) -> a + b + c + d);

    runAndWait("TestTaskFactoryMethods.testPar4Dsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 4);
  }

  @Test
  public void testPar5Dsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5))
        .map("test", (a, b, c, d, e) -> a + b + c + d + e);

    runAndWait("TestTaskFactoryMethods.testPar5Dsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 5);
  }

  @Test
  public void testPar6Dsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6))
        .map("test", (a, b, c, d, e, f) -> a + b + c + d + e + f);

    runAndWait("TestTaskFactoryMethods.testPar6Dsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 6);
  }

  @Test
  public void testPar7Dsc() {
    Task<Integer> task = Task
        .par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7))
          .map("test", (a, b, c, d, e, f, g) -> a + b + c + d + e + f + g);

    runAndWait("TestTaskFactoryMethods.testPar7Dsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 7);
  }

  @Test
  public void testPar8Dsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6),
        Task.value(7), Task.value(8))
        .map("test", (a, b, c, d, e, f, g, h) -> a + b + c + d + e + f + g + h);

    runAndWait("TestTaskFactoryMethods.testPar8Dsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 8);
  }

  @Test
  public void testPar9Dsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6),
        Task.value(7), Task.value(8), Task.value(9))
          .map("test", (a, b, c, d, e, f, g, h, i) -> a + b + c + d + e + f + g + h + i);

    runAndWait("TestTaskFactoryMethods.testPar9Dsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9);

    assertEquals(countTasks(task.getTrace()), 2 + 1 + 9);
  }

  @Test
  public void testPar2FlatMap() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2)).flatMap((a, b) -> Task.value(a + b));

    runAndWait("TestTaskFactoryMethods.testPar2FlatMap", task);
    assertEquals((int)task.get(), 1 + 2);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 2);
  }

  @Test
  public void testPar3FlatMap() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3)).flatMap((a, b, c) -> Task.value(a + b + c));

    runAndWait("TestTaskFactoryMethods.testPar3FlatMap", task);
    assertEquals((int)task.get(), 1 + 2 + 3);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 3);
  }

  @Test
  public void testPar4FlatMap() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4)).flatMap((a, b, c, d) -> Task.value(a + b + c + d));

    runAndWait("TestTaskFactoryMethods.testPar4FlatMap", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 4);
  }

  @Test
  public void testPar5FlatMap() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5))
        .flatMap((a, b, c, d, e) -> Task.value(a + b + c + d + e));

    runAndWait("TestTaskFactoryMethods.testPar5FlatMap", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 5);
  }

  @Test
  public void testPar6FlatMap() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6))
        .flatMap((a, b, c, d, e, f) -> Task.value(a + b + c + d + e + f));

    runAndWait("TestTaskFactoryMethods.testPar6FlatMap", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 6);
  }

  @Test
  public void testPar7FlatMap() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7))
        .flatMap((a, b, c, d, e, f, g) -> Task.value(a + b + c + d + e + f + g));

    runAndWait("TestTaskFactoryMethods.testPar7FlatMap", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 7);
  }

  @Test
  public void testPar8FlatMap() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7), Task.value(8))
        .flatMap((a, b, c, d, e, f, g, h) -> Task.value(a + b + c + d + e + f + g + h));

    runAndWait("TestTaskFactoryMethods.testPar8FlatMap", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 8);
  }

  @Test
  public void testPar9FlatMap() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7), Task.value(8), Task.value(9))
        .flatMap((a, b, c, d, e, f, g, h, i) -> Task.value(a + b + c + d + e + f + g + h + i));

    runAndWait("TestTaskFactoryMethods.testPar9FlatMap", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 9);
  }

  @Test
  public void testPar2FlatMapDsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2)).flatMap("test", (a, b) -> Task.value(a + b));

    runAndWait("TestTaskFactoryMethods.testPar2FlatMapDsc", task);
    assertEquals((int)task.get(), 1 + 2);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 2);
  }

  @Test
  public void testPar3FlatMapDsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3)).flatMap("test", (a, b, c) -> Task.value(a + b + c));

    runAndWait("TestTaskFactoryMethods.testPar3FlatMapDsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 3);
  }

  @Test
  public void testPar4FlatMapDsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4)).flatMap("test", (a, b, c, d) -> Task.value(a + b + c + d));

    runAndWait("TestTaskFactoryMethods.testPar4FlatMapDsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 4);
  }

  @Test
  public void testPar5FlatMapDsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5))
        .flatMap("test", (a, b, c, d, e) -> Task.value(a + b + c + d + e));

    runAndWait("TestTaskFactoryMethods.testPar5FlatMapDsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 5);
  }

  @Test
  public void testPar6FlatMapDsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6))
        .flatMap("test", (a, b, c, d, e, f) -> Task.value(a + b + c + d + e + f));

    runAndWait("TestTaskFactoryMethods.testPar6FlatMapDsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 6);
  }

  @Test
  public void testPar7FlatMapDsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7))
        .flatMap("test", (a, b, c, d, e, f, g) -> Task.value(a + b + c + d + e + f + g));

    runAndWait("TestTaskFactoryMethods.testPar7FlatMapDsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 7);
  }

  @Test
  public void testPar8FlatMapDsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7), Task.value(8))
        .flatMap("test", (a, b, c, d, e, f, g, h) -> Task.value(a + b + c + d + e + f + g + h));

    runAndWait("TestTaskFactoryMethods.testPar8FlatMapDsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 8);
  }

  @Test
  public void testPar9FlatMapDsc() {
    Task<Integer> task = Task.par(Task.value(1), Task.value(2), Task.value(3), Task.value(4), Task.value(5), Task.value(6), Task.value(7), Task.value(8), Task.value(9))
        .flatMap("test", (a, b, c, d, e, f, g, h, i) -> Task.value(a + b + c + d + e + f + g + h + i));

    runAndWait("TestTaskFactoryMethods.testPar9FlatMapDsc", task);
    assertEquals((int)task.get(), 1 + 2 + 3 + 4 + 5 + 6 + 7 + 8 + 9);

    assertEquals(countTasks(task.getTrace()), 2 + 3 + 9);
  }
}
