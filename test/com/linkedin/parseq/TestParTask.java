package com.linkedin.parseq;

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.linkedin.parseq.Tasks.par;
import static com.linkedin.parseq.Task.value;
import static org.testng.Assert.assertFalse;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;


/**
 * @author Chris Pettitt
 */
public class TestParTask extends BaseEngineTest {
  @Test
  public void testIterableParWithEmptyList() {
    try {
      par(Collections.<Task<?>> emptyList());
      fail("Should have thrown IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      // Expected case
    }
  }

  @Test
  public void testIterableParWithSingletonList() throws InterruptedException {
    final String valueStr = "value";
    final Task<String> task = value("value", valueStr);
    final Task<List<String>> par = par(Collections.singleton(task));

    runAndWait("TestParTask.testIterableParWithSingletonList", par);

    assertEquals(1, par.get().size());
    assertEquals(valueStr, par.get().get(0));
    assertEquals(valueStr, task.get());
  }

  @Test
  public void testIterableSeqWithMultipleElements() throws InterruptedException {
    final int iters = 500;

    final Task<?>[] tasks = new BaseTask<?>[iters];
    final AtomicInteger counter = new AtomicInteger(0);
    for (int i = 0; i < iters; i++) {
      tasks[i] = Task.action("task-" + i, () -> {
        // Note: We intentionally do not use CAS. We guarantee that
        // the run method of Tasks are never executed in parallel.
        final int currentCount = counter.get();
        counter.set(currentCount + 1);
      } );
    }

    final ParTask<?> par = par(Arrays.asList(tasks));

    runAndWait("TestParTask.testIterableSeqWithMultipleElements", par);

    assertEquals(500, par.getSuccessful().size());
    assertEquals(500, par.getTasks().size());
    assertEquals(500, par.get().size());
    assertEquals(500, counter.get());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testAsyncTasksInPar() throws InterruptedException {
    // Tasks cannot have their run methods invoked at the same time, however
    // asynchronous tasks are allowed to execute concurrently outside of their
    // run methods. This test verifies that two asynchronous tasks are not
    // serialized such that one must complete before the other.

    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<Integer> promise2 = Promises.settable();

    // Used to ensure that both tasks have been run
    final CountDownLatch cdl = new CountDownLatch(2);

    final Task<String> task1 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        cdl.countDown();
        return promise1;
      }
    };

    final Task<Integer> task2 = new BaseTask<Integer>() {
      @Override
      public Promise<Integer> run(final Context context) throws Exception {
        cdl.countDown();
        return promise2;
      }
    };

    final ParTask<Object> par = Tasks.<Object> par(task1, task2);
    getEngine().run(par);

    assertTrue("Both tasks did not run within the timeout", cdl.await(100, TimeUnit.MILLISECONDS));

    // If execution was serialized then it would have hung after executing
    // task 1, because task 1's promise was not set.

    promise1.done("test");
    promise2.done(10);

    // We add a promise listener to the promise returned from a task to finish
    // tasks asynchronously. Since we can only wait on the start of tasks above
    // we should wait for the completion of tasks here before checking their
    // values.

    assertTrue("Par task did not finish in a reasonable amount of time", par.await(100, TimeUnit.MILLISECONDS));

    assertEquals(2, par.getTasks().size());
    assertEquals("test", par.getTasks().get(0).get());
    assertEquals(10, par.getTasks().get(1).get());
    assertEquals(2, par.getSuccessful().size());
    assertEquals("test", par.getSuccessful().get(0));
    assertEquals(10, par.getSuccessful().get(1));
  }

  @Test
  public void testAsyncTasksInParWithType() throws InterruptedException {
    // Tasks cannot have their run methods invoked at the same time, however
    // asynchronous tasks are allowed to execute concurrently outside of their
    // run methods. This test verifies that two asynchronous tasks are not
    // serialized such that one must complete before the other.

    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<String> promise2 = Promises.settable();
    final SettablePromise<String> promise3 = Promises.settable();

    // Used to ensure that both tasks have been run
    final CountDownLatch cdl = new CountDownLatch(3);

    final Task<String> task1 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        cdl.countDown();
        return promise1;
      }
    };

    final Task<String> task2 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        cdl.countDown();
        return promise2;
      }
    };

    final Task<String> task3 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        cdl.countDown();
        return promise3;
      }
    };
    List<Task<String>> taskList = new ArrayList<Task<String>>();
    taskList.add(task1);
    taskList.add(task2);
    taskList.add(task3);
    final ParTask<String> par = par(taskList);
    getEngine().run(par);

    assertTrue("Both tasks did not run within the timeout", cdl.await(100, TimeUnit.MILLISECONDS));

    promise1.done("test1");
    promise2.done("test2");
    promise3.done(null);

    assertTrue("Par task did not finish in a reasonable amount of time", par.await(100, TimeUnit.MILLISECONDS));

    assertTrue(par.await(5, TimeUnit.SECONDS));
    List<String> result = par.get();
    assertEquals(3, par.getTasks().size());
    assertEquals("Count should only be 3.", 3, result.size());
    assertEquals("Missing task1 in result.", "test1", result.get(0));
    assertEquals("Missing task2 in result.", "test2", result.get(1));
    assertEquals("Missing task3 in result.", null, result.get(2));
    assertEquals(par.get(), par.getSuccessful());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testParWithGeneralType() throws InterruptedException {
    final Integer intVal = 123;
    final Double dblVal = 456.789;

    final Task<Integer> intTask = value("intTask", intVal);
    final Task<Double> dblTask = value("dblTask", dblVal);
    final ParTask<? extends Number> par = par(intTask, dblTask);

    runAndWait("TestParTask.testParWithGeneralType", par);

    assertEquals(2, par.get().size());
    assertEquals(intVal, par.get().get(0));
    assertEquals(dblVal, par.get().get(1));
  }

  @Test
  public void testTypedParWithGeneralType() throws InterruptedException {
    final Integer intVal = 123;
    final Double dblVal = 456.789;

    final Task<Integer> intTask = value("intTask", intVal);
    final Task<Double> dblTask = value("dblTask", dblVal);
    final List<Task<? extends Number>> numTasks = new ArrayList<Task<? extends Number>>();
    numTasks.add(intTask);
    numTasks.add(dblTask);

    final ParTask<? extends Number> par = par(numTasks);

    runAndWait("TestParTask.testTypedParWithGeneralType", par);

    assertEquals(2, par.get().size());
    assertEquals(intVal, par.get().get(0));
    assertEquals(dblVal, par.get().get(1));
  }

  @Test
  public void testFailTaskInPar() throws InterruptedException {

    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<String> promise2 = Promises.settable();

    final Task<String> task1 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise1;
      }
    };
    final Task<String> task2 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise2;
      }
    };
    List<Task<String>> taskList = new ArrayList<Task<String>>();
    taskList.add(task1);
    taskList.add(task2);

    final ParTask<String> par = par(taskList);
    getEngine().run(par);

    promise1.fail(new Exception());
    promise2.fail(new Exception());
    assertTrue(par.await(5, TimeUnit.SECONDS));
    if (!par.isFailed()) {
      fail("par should have failed.");
    }
    List<String> successful = par.getSuccessful();
    List<Task<String>> tasks = par.getTasks();

    assertEquals("Should have a size 2 for exceptions", 2, ((MultiException) par.getError()).getCauses().size());
    assertEquals(0, successful.size());
    assertEquals(2, tasks.size());
    assertEquals(true, tasks.get(0).isFailed());
    assertEquals(true, tasks.get(1).isFailed());
  }

  @Test
  public void testFailAndPassTaskInPar() throws InterruptedException {

    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<String> promise2 = Promises.settable();
    final SettablePromise<String> promise3 = Promises.settable();

    final Task<String> task1 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise1;
      }
    };
    final Task<String> task2 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise2;
      }
    };

    final Task<String> task3 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise3;
      }
    };
    List<Task<String>> taskList = new ArrayList<Task<String>>();
    taskList.add(task1);
    taskList.add(task2);
    taskList.add(task3);

    final ParTask<String> par = par(taskList);
    getEngine().run(par);

    promise1.done("done1");
    promise2.fail(new Exception());
    promise3.done("done3");
    assertTrue(par.await(5, TimeUnit.SECONDS));
    if (!par.isFailed()) {
      fail("par should have failed.");
    }

    List<String> successful = par.getSuccessful();
    List<Task<String>> tasks = par.getTasks();

    assertEquals("Should have a size 1 for exceptions", 1, ((MultiException) par.getError()).getCauses().size());
    assertEquals(2, successful.size());
    assertEquals("done1", successful.get(0));
    assertEquals("done3", successful.get(1));
    assertEquals(3, tasks.size());
    assertEquals(false, tasks.get(0).isFailed());
    assertEquals(true, tasks.get(1).isFailed());
    assertEquals(false, tasks.get(2).isFailed());
    assertEquals("done1", tasks.get(0).get());
    assertEquals("done3", tasks.get(2).get());
  }

  @Test
  public void testNoFastFailInPar() throws InterruptedException {

    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<String> promise2 = Promises.settable();

    final Task<String> task1 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise1;
      }
    };
    final Task<String> task2 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise2;
      }
    };

    List<Task<String>> taskList = new ArrayList<Task<String>>();
    taskList.add(task1);
    taskList.add(task2);

    final ParTask<String> par = par(taskList);
    getEngine().run(par);

    promise2.fail(new Exception()); //This failure does not complete tha par
    Thread.sleep(100);
    assertFalse(par.isDone());
    promise1.done("done1");
    assertTrue(par.await(5, TimeUnit.SECONDS));
    if (!par.isFailed()) {
      fail("par should have failed.");
    }

    List<String> successful = par.getSuccessful();
    List<Task<String>> tasks = par.getTasks();

    assertEquals("Should have a size 1 for exceptions", 1, ((MultiException) par.getError()).getCauses().size());
    assertEquals(1, successful.size());
    assertEquals("done1", successful.get(0));
    assertEquals(2, tasks.size());
    assertEquals(false, tasks.get(0).isFailed());
    assertEquals(true, tasks.get(1).isFailed());
    assertEquals("done1", tasks.get(0).get());
  }


  @Test
  public void testAllEarlyFinishTaskInPar() throws InterruptedException {
    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<String> promise2 = Promises.settable();
    final SettablePromise<String> promise3 = Promises.settable();

    final Task<String> task1 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise1;
      }
    };
    final Task<String> task2 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise2;
      }
    };

    final Task<String> task3 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise3;
      }
    };
    List<Task<String>> taskList = new ArrayList<Task<String>>();
    taskList.add(task1);
    taskList.add(task2);
    taskList.add(task3);

    final ParTask<String> par = par(taskList);
    getEngine().run(par);

    promise1.done("done1");
    promise2.fail(new CancellationException(Exceptions.EARLY_FINISH_EXCEPTION));
    promise3.fail(new CancellationException(Exceptions.EARLY_FINISH_EXCEPTION));
    assertTrue(par.await(5, TimeUnit.SECONDS));

    if (!par.isFailed()) {
      fail("par should have failed.");
    }
    List<String> successful = par.getSuccessful();
    List<Task<String>> tasks = par.getTasks();
    assertTrue("Should be early finish", Exceptions.isEarlyFinish(par.getError()));
    assertEquals(1, successful.size());
    assertEquals("done1", successful.get(0));
    assertEquals(3, tasks.size());
    assertEquals(false, tasks.get(0).isFailed());
    assertEquals(true, tasks.get(1).isFailed());
    assertEquals(true, tasks.get(2).isFailed());
  }

  @Test
  public void testOneEarlyFinishTaskInPar() throws InterruptedException {

    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<String> promise2 = Promises.settable();
    final SettablePromise<String> promise3 = Promises.settable();

    final Task<String> task1 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise1;
      }
    };
    final Task<String> task2 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise2;
      }
    };

    final Task<String> task3 = new BaseTask<String>() {
      @Override
      public Promise<String> run(final Context context) throws Exception {
        return promise3;
      }
    };
    List<Task<String>> taskList = new ArrayList<Task<String>>();
    taskList.add(task1);
    taskList.add(task2);
    taskList.add(task3);

    final ParTask<String> par = par(taskList);
    getEngine().run(par);

    promise1.done("done1");
    promise2.fail(new CancellationException(Exceptions.EARLY_FINISH_EXCEPTION));
    promise3.fail(new Exception());
    assertTrue(par.await(5, TimeUnit.SECONDS));

    if (!par.isFailed()) {
      fail("par should have failed.");
    }
    List<String> successful = par.getSuccessful();
    List<Task<String>> tasks = par.getTasks();

    assertEquals("Should have a size 2 for exceptions", 2, ((MultiException) par.getError()).getCauses().size());
    assertEquals(1, successful.size());
    assertEquals("done1", successful.get(0));
    assertEquals(3, tasks.size());
    assertEquals(false, tasks.get(0).isFailed());
    assertEquals(true, tasks.get(1).isFailed());
    assertEquals(true, tasks.get(2).isFailed());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testPar2() throws InterruptedException {
    final Task<List<Integer>> par = par(value(1), value(2));
    runAndWait("TestParTask.testPar2", par);
    assertEquals(Arrays.asList(1, 2), par.get());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testPar3() throws InterruptedException {
    final Task<List<Integer>> par = par(value(1), value(2), value(3));
    runAndWait("TestParTask.testPar3", par);
    assertEquals(Arrays.asList(1, 2, 3), par.get());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testPar4() throws InterruptedException {
    final Task<List<Integer>> par = par(value(1), value(2), value(3), value(4));
    runAndWait("TestParTask.testPar4", par);
    assertEquals(Arrays.asList(1, 2, 3, 4), par.get());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testPar5() throws InterruptedException {
    final Task<List<Integer>> par = par(value(1), value(2), value(3), value(4), value(5));
    runAndWait("TestParTask.testPar5", par);
    assertEquals(Arrays.asList(1, 2, 3, 4, 5), par.get());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testPar6() throws InterruptedException {
    final Task<List<Integer>> par = par(value(1), value(2), value(3), value(4), value(5), value(6));
    runAndWait("TestParTask.testPar6", par);
    assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), par.get());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testPar7() throws InterruptedException {
    final Task<List<Integer>> par = par(value(1), value(2), value(3), value(4), value(5), value(6), value(7));
    runAndWait("TestParTask.testPar7", par);
    assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7), par.get());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testPar8() throws InterruptedException {
    final Task<List<Integer>> par = par(value(1), value(2), value(3), value(4), value(5), value(6), value(7), value(8));
    runAndWait("TestParTask.testPar8", par);
    assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8), par.get());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testPar9() throws InterruptedException {
    final Task<List<Integer>> par =
        par(value(1), value(2), value(3), value(4), value(5), value(6), value(7), value(8), value(9));
    runAndWait("TestParTask.testPar9", par);
    assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), par.get());
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testPar10() throws InterruptedException {
    final Task<List<Integer>> par =
        par(value(1), value(2), value(3), value(4), value(5), value(6), value(7), value(8), value(9), value(10));
    runAndWait("TestParTask.testPar10", par);
    assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), par.get());
  }

  @Test
  public void testParIsNotSystemHidden() {
    final ParTask<String> par = par(Collections.singleton(value("value")));
    assertFalse(par.getShallowTrace().getSystemHidden());
  }
}
