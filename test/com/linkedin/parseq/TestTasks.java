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

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.linkedin.parseq.Tasks.action;
import static com.linkedin.parseq.Tasks.par;
import static com.linkedin.parseq.Tasks.seq;
import static com.linkedin.parseq.Tasks.callable;
import static com.linkedin.parseq.TestUtil.value;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class TestTasks extends BaseEngineTest
{
  @Test
  public void testTaskThatThrows() throws InterruptedException
  {
    final Exception error = new Exception();

    final Task<?> task = new BaseTask<Object>()
    {
      @Override
      protected Promise<Object> run(final Context context) throws Exception
      {
        throw error;
      }
    };

    getEngine().run(task);

    task.await();

    assertTrue(task.isFailed());
    assertEquals(error, task.getError());
  }

  @Test
  public void testEmptySeq()
  {
    try
    {
      seq();
      fail("Should have thrown IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }

  @Test
  public void testSingletonSeq() throws InterruptedException
  {
    final String value = "value";
    final Task<String> seq = seq(value("value", value));

    getEngine().run(seq);

    seq.await();

    assertEquals("value", seq.get());
  }

  @Test
  public void testMultiSeqIterable() throws InterruptedException
  {
    final String value = "value";
    final String value1 = "value1";
    List<Task<?>> taskList = new ArrayList<Task<?>>();
    taskList.add(value(value));
    taskList.add(value(value1));

    final Task<String> seq = seq(taskList);

    getEngine().run(seq);
    seq.await();
    assertEquals(value, taskList.get(0).get());
    assertEquals(value1, seq.get());
  }

  @Test
  public void testSeveralTasksInSeq() throws InterruptedException
  {
    final int iters = 500;

    final Task<?>[] tasks = new Task<?>[iters];
    for (int i = 0; i < iters; i++)
    {
      final int finalI = i;
      tasks[i] = callable("task-" + i, new Callable<Integer>()
      {
        @Override
        public Integer call() throws Exception
        {
          if (finalI == 0)
          {
            return 1;
          } else
          {
            final int prevValue = (Integer) tasks[finalI - 1].get();
            if (prevValue != finalI)
            {
              throw new IllegalStateException("Expected: " + finalI + ". Got: " + prevValue);
            }
            return prevValue + 1;
          }
        }
      });
    }

    final Task<Integer> seq = seq(tasks);
    getEngine().run(seq);

    seq.await();

    assertEquals(iters, (int)seq.get());
  }

  @Test
  public void testEmptyPar()
  {
    try
    {
      par();
      fail("Should have thrown IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }

  @Test
  public void testSingletonPar() throws InterruptedException
  {
    final String value = "value";
    final Task<String> task = value("value", value);
    final ParTask<?> par = par(task);

    getEngine().run(par);

    par.await();
    assertEquals(1, par.get().size());
    assertEquals(value, par.get().get(0));
    assertEquals("value", task.get());
  }

  @Test
  public void testSeveralTasksInPar() throws InterruptedException
  {
    final int iters = 500;

    final Task<?>[] tasks = new BaseTask<?>[iters];
    final AtomicInteger counter = new AtomicInteger(0);
    for (int i = 0; i < iters; i++)
    {
      tasks[i] = action("task-" + i, new Runnable()
      {
        @Override
        public void run()
        {
          // Note: We intentionally do not use CAS. We guarantee that
          // the run method of Tasks are never executed in parallel.
          final int currentCount = counter.get();
          counter.set(currentCount + 1);
        }
      });
    }

    final ParTask<?> par = par(tasks);
    getEngine().run(par);

    par.await();

    assertEquals(500, par.getSuccessful().size());
    assertEquals(500, par.getTasks().size());
    assertEquals(500, par.get().size());
    assertEquals(500, counter.get());
  }

  @Test
  public void testAsyncTasksInPar() throws InterruptedException
  {
    // Tasks cannot have their run methods invoked at the same time, however
    // asynchronous tasks are allowed to execute concurrently outside of their
    // run methods. This test verifies that two asynchronous tasks are not
    // serialized such that one must complete before the other.

    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<Integer> promise2 = Promises.settable();

    // Used to ensure that both tasks have been run
    final CountDownLatch cdl = new CountDownLatch(2);

    final Task<?> task1 = new BaseTask<Object>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        cdl.countDown();
        return promise1;
      }
    };

    final Task<?> task2 = new BaseTask<Object>()
    {
      @Override
      public Promise<Integer> run(final Context context) throws Exception
      {
        cdl.countDown();
        return promise2;
      }
    };

    final ParTask<?> par = par(task1, task2);
    getEngine().run(par);

    assertTrue("Both tasks did not run within the timeout",
               cdl.await(100, TimeUnit.MILLISECONDS));

    promise1.done("test");
    promise2.done(10);
    assertEquals(2, par.getTasks().size());
    assertEquals("test", par.getTasks().get(0).get());
    assertEquals(10, par.getTasks().get(1).get());
    assertEquals(2, par.getSuccessful().size());
    assertEquals("test", par.getSuccessful().get(0));
    assertEquals(10, par.getSuccessful().get(1));

    assertTrue("Par task did not finish in a reasonable amount of time",
               par.await(100, TimeUnit.MILLISECONDS));
  }

  @Test
  public void testAsyncTasksInParWithType() throws InterruptedException
  {
    // Tasks cannot have their run methods invoked at the same time, however
    // asynchronous tasks are allowed to execute concurrently outside of their
    // run methods. This test verifies that two asynchronous tasks are not
    // serialized such that one must complete before the other.

    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<String> promise2 = Promises.settable();
    final SettablePromise<String> promise3 = Promises.settable();

    // Used to ensure that both tasks have been run
    final CountDownLatch cdl = new CountDownLatch(3);

    final Task<String> task1 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        cdl.countDown();
        return promise1;
      }
    };

    final Task<String> task2 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        cdl.countDown();
        return promise2;
      }
    };

    final Task<String> task3 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
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

    assertTrue("Both tasks did not run within the timeout",
            cdl.await(100, TimeUnit.MILLISECONDS));

    promise1.done("test1");
    promise2.done("test2");
    promise3.done(null);

    assertTrue("Par task did not finish in a reasonable amount of time",
            par.await(100, TimeUnit.MILLISECONDS));

    par.await();
    List<String> result = par.get();
    assertEquals(3, par.getTasks().size());
    assertEquals("Count should only be 3.", 3, result.size());
    assertEquals("Missing task1 in result.", "test1", result.get(0));
    assertEquals("Missing task2 in result.", "test2", result.get(1));
    assertEquals("Missing task3 in result.", null, result.get(2));
    assertEquals(par.get(), par.getSuccessful());
  }


  @Test
  public void testFailTaskInPar() throws InterruptedException
  {

    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<String> promise2 = Promises.settable();

    final Task<String> task1 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        return promise1;
      }
    };
    final Task<String> task2 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
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
    par.await();
    if (!par.isFailed())
    {
      fail("par should have failed.");
    }
    List<String> successful = par.getSuccessful();
    List<Task<String>> tasks = par.getTasks();


    assertEquals("Should have a size 2 for exceptions", 2, ((MultiException)par.getError()).getCauses().size());
    assertEquals(0, successful.size());
    assertEquals(2, tasks.size());
    assertEquals(true, tasks.get(0).isFailed());
    assertEquals(true, tasks.get(1).isFailed());
  }

  @Test
  public void testFailAndPassTaskInPar() throws InterruptedException
  {

    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<String> promise2 = Promises.settable();
    final SettablePromise<String> promise3 = Promises.settable();

    final Task<String> task1 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        return promise1;
      }
    };
    final Task<String> task2 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        return promise2;
      }
    };

    final Task<String> task3 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
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
    par.await();
    if (!par.isFailed())
    {
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
  public void testAllEarlyFinishTaskInPar() throws InterruptedException
  {
    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<String> promise2 = Promises.settable();
    final SettablePromise<String> promise3 = Promises.settable();

    final Task<String> task1 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        return promise1;
      }
    };
    final Task<String> task2 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        return promise2;
      }
    };

    final Task<String> task3 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
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
    promise2.fail(new EarlyFinishException());
    promise3.fail(new EarlyFinishException());
    par.await();

    if (!par.isFailed())
    {
      fail("par should have failed.");
    }
    List<String> successful = par.getSuccessful();
    List<Task<String>> tasks = par.getTasks();
    assertEquals("Should be early finish", true, par.getError() instanceof  EarlyFinishException);
    assertEquals(1, successful.size());
    assertEquals("done1", successful.get(0));
    assertEquals(3, tasks.size());
    assertEquals(false, tasks.get(0).isFailed());
    assertEquals(true, tasks.get(1).isFailed());
    assertEquals(true, tasks.get(2).isFailed());
  }

  @Test
  public void testOneEarlyFinishTaskInPar() throws InterruptedException
  {

    final SettablePromise<String> promise1 = Promises.settable();
    final SettablePromise<String> promise2 = Promises.settable();
    final SettablePromise<String> promise3 = Promises.settable();

    final Task<String> task1 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        return promise1;
      }
    };
    final Task<String> task2 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        return promise2;
      }
    };

    final Task<String> task3 = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
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
    promise2.fail(new EarlyFinishException());
    promise3.fail(new Exception());
    par.await();

    if (!par.isFailed())
    {
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

  @Test
  public void testTimeoutTaskWithTimeout() throws InterruptedException
  {
    // This task will not complete on its own, which allows us to test the timeout
    final Task<String> task = new BaseTask<String>("task")
    {
      @Override
      protected Promise<? extends String> run(
          final Context context) throws Exception
      {
        return Promises.settable();
      }
    };

    final Task<String> timeoutTask = Tasks.timeoutWithError(200, TimeUnit.MILLISECONDS, task);

    getEngine().run(timeoutTask);

    timeoutTask.await();

    assertTrue(timeoutTask.isFailed());
    assertTrue(timeoutTask.getError() instanceof TimeoutException);

    task.await();

    // The original task should also be failed - this time with an early finish
    // exception.
    assertTrue(task.isFailed());
    assertTrue(task.getError() instanceof EarlyFinishException);
  }

  @Test
  public void testTimeoutTaskWithoutTimeout() throws InterruptedException
  {
    final String value = "value";
    final Task<String> task = Tasks.callable("task", new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        return value;
      }
    });

    final Task<String> timeoutTask = Tasks.timeoutWithError(200, TimeUnit.MILLISECONDS, task);

    getEngine().run(timeoutTask);

    timeoutTask.await();

    assertEquals(value, task.get());

    // The wrapping task should get the same value as the wrapped task
    assertEquals(value, timeoutTask.get());
  }

  @Test
  public void testTimeoutTaskWithError() throws InterruptedException
  {
    final Exception error = new Exception();
    final Task<String> task = Tasks.callable("task", new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        throw error;
      }
    });

    final Task<String> timeoutTask = Tasks.timeoutWithError(2000, TimeUnit.MILLISECONDS, task);

    getEngine().run(timeoutTask);

    // We should complete with an error almost immediately
    assertTrue(timeoutTask.await(100, TimeUnit.MILLISECONDS));

    assertTrue(timeoutTask.isFailed());
    assertEquals(error, timeoutTask.getError());
  }

  @Test
  public void testSetPriorityBelowMinValue()
  {
    try
    {
      TestUtil.noop().setPriority(Priority.MIN_PRIORITY - 1);
      fail("Should have thrown IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }

  @Test
  public void testSetPriorityAboveMaxValue()
  {
    try
    {
      TestUtil.noop().setPriority(Priority.MAX_PRIORITY + 1);
      fail("Should have thrown IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }
}

