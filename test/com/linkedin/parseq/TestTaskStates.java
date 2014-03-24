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

import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseException;
import com.linkedin.parseq.promise.PromiseUnresolvedException;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.linkedin.parseq.Tasks.callable;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TestTaskStates
{
  private static Task<?> NO_PARENT = null;
  private static Collection<Task<?>> NO_PREDECESSORS = Collections.emptySet();

  @Test
  public void testInit()
  {
    final Task<?> task = TestUtil.noop();
    assertInitOrScheduled(task);
    assertEquals(0, task.getPriority());
  }

  @Test
  public void testSetPriorityAfterInit()
  {
    final int newPriority = 5;
    final Task<?> task = TestUtil.noop();

    assertTrue(task.setPriority(newPriority));
    assertEquals(newPriority, task.getPriority());
    assertInitOrScheduled(task);
  }

  @Test
  public void testCancelAfterInit()
  {
    final Task<?> task = TestUtil.noop();
    final Exception reason = new Exception();

    assertTrue(task.cancel(reason));
    assertFailed(task, reason);
  }

  @Test
  public void testCancelAfterCancel()
  {
    final Task<?> task = TestUtil.noop();
    final Exception reason1 = new Exception();
    final Exception reason2 = new Exception();

    assertTrue(task.cancel(reason1));
    assertFalse(task.cancel(reason2));

    assertFailed(task, reason1);
  }

  @Test
  public void testRun() throws InterruptedException
  {
    final String result = "result";
    final Task<String> task = callable("task", new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        return result;
      }
    });

    runTask(task);

    assertTrue(task.await(5, TimeUnit.SECONDS));

    assertDone(task, result);
  }

  @Test
  public void testRunAfterRun() throws InterruptedException
  {
    final AtomicInteger runCount = new AtomicInteger();
    final SettablePromise<Void> promise = Promises.settable();
    final Task<Void> task = new BaseTask<Void>()
    {
      @Override
      protected Promise<? extends Void> run(final Context context) throws Exception
      {
        runCount.getAndIncrement();
        return promise;
      }
    };

    runTask(task);

    // Run it again
    runTask(task);

    // We'll give each task some time to hit run
    Thread.sleep(50);

    assertEquals(1, runCount.get());

    promise.done(null);

    // Again give the other task some time to enter run
    Thread.sleep(50);

    assertEquals(1, runCount.get());
  }

  @Test
  public void testRunWithSyncError() throws InterruptedException
  {
    final Exception exception = new Exception();
    final Task<String> task = callable("task", new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        throw exception;
      }
    });

    runTask(task);

    assertTrue(task.await(5, TimeUnit.SECONDS));

    assertFailed(task, exception);
  }

  @Test
  public void testRunWithAsyncError() throws InterruptedException
  {
    final Exception exception = new Exception();
    final Task<String> task = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        final SettablePromise<String> promise = Promises.settable();
        promise.fail(exception);
        return promise;
      }
    };

    runTask(task);

    assertTrue(task.await(5, TimeUnit.SECONDS));

    assertFailed(task, exception);
  }

  @Test
  public void testSetPriorityAfterRun() throws InterruptedException
  {
    final String result = "result";
    final Task<String> task = Tasks.callable("task", new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        return result;
      }
    });

    runTask(task);

    assertTrue(task.await(5, TimeUnit.SECONDS));

    assertFalse(task.setPriority(5));
    assertEquals(0, task.getPriority());

    assertDone(task, result);
  }

  @Test
  public void testCancelAfterRun() throws InterruptedException
  {
    final String result = "result";

    // Used to wait for the task to start running
    final CountDownLatch startLatch = new CountDownLatch(1);

    // Used to finish the sync task
    final CountDownLatch finishLatch = new CountDownLatch(1);

    final Task<String> task = callable("task", new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        startLatch.countDown();
        finishLatch.await();
        return result;
      }
    });

    final Thread thread = new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        runTask(task);
      }
    });
    thread.setDaemon(true);
    thread.start();

    startLatch.await();

    assertFalse(task.cancel(new Exception()));

    assertRunOrPending(task);

    finishLatch.countDown();

    assertTrue(task.await(5, TimeUnit.SECONDS));

    assertDone(task, result);
  }

  @Test
  public void testCancelAfterPending() throws InterruptedException
  {
    final SettablePromise<String> promise = Promises.settable();

    final Task<String> task = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        return promise;
      }
    };

    final Thread thread = new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        runTask(task);
      }
    });
    thread.setDaemon(true);
    thread.start();

    thread.join();

    final Exception reason = new Exception();
    assertTrue(task.cancel(reason));

    assertTrue(task.await(5, TimeUnit.SECONDS));

    assertFailed(task, reason);
  }

  @Test
  public void testCancelAfterDone() throws InterruptedException
  {
    final String result = "result";
    final Task<String> task = callable("task", new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        return result;
      }
    });

    runTask(task);

    assertTrue(task.await(5, TimeUnit.SECONDS));

    assertDone(task, result);

    assertFalse(task.cancel(new Exception()));

    assertDone(task, result);
  }

  private void runTask(final Task<?> task)
  {
    task.contextRun(new NullContext(), new NullTaskLog(), NO_PARENT, NO_PREDECESSORS);
  }

  private void assertInitOrScheduled(final Task<?> task)
  {
    assertFalse(task.isDone());
    assertFalse(task.isFailed());
    assertNull(task.getShallowTrace().getStartNanos());
    assertNull(task.getShallowTrace().getEndNanos());

    try
    {
      task.get();
      fail("Should have thrown PromiseUnresolvedException");
    }
    catch (PromiseUnresolvedException e)
    {
      // Expected case
    }

    try
    {
      task.getError();
      fail("Should have thrown PromiseUnresolvedException");
    }
    catch (PromiseUnresolvedException e)
    {
      // Expected case
    }
  }

  private void assertRunOrPending(final Task<?> task)
  {
    assertFalse(task.isDone());
    assertFalse(task.isFailed());
    assertTrue(task.getShallowTrace().getStartNanos() > 0);
    assertNull(task.getShallowTrace().getEndNanos());

    try
    {
      task.get();
      fail("Should have thrown PromiseUnresolvedException");
    }
    catch (PromiseUnresolvedException e)
    {
      // Expected case
    }

    try
    {
      task.getError();
      fail("Should have thrown PromiseUnresolvedException");
    }
    catch (PromiseUnresolvedException e)
    {
      // Expected case
    }
  }

  private <T> void assertDone(final Task<T> task, final T expectedValue)
  {
    assertTrue(task.isDone());
    assertFalse(task.isFailed());
    assertEquals(expectedValue, task.get());
    assertNull(task.getError());
    assertTrue(task.getShallowTrace().getStartNanos() > 0);
    assertTrue(task.getShallowTrace().getStartNanos() <= task.getShallowTrace().getEndNanos());
  }

  private void assertFailed(final Task<?> task, Exception exception)
  {

    assertTrue(task.isDone());
    assertTrue(task.isFailed());
    assertEquals(exception, task.getError());
    assertTrue(task.getShallowTrace().getStartNanos() > 0);
    assertTrue(task.getShallowTrace().getStartNanos() <= task.getShallowTrace().getEndNanos());

    try
    {
      task.get();
      fail("Should have thwon PromiseException");
    }
    catch (PromiseException e)
    {
      assertEquals(exception, e.getCause());
    }
  }

  private static class NullContext implements Context
  {

    @Override
    public Cancellable createTimer(final long time, final TimeUnit unit,
                                   final Task<?> task)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public void run(final Task<?>... tasks)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public After after(final Promise<?>... promises)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object getEngineProperty(String key)
    {
      throw new UnsupportedOperationException();
    }
  }

  private static class NullTaskLog extends TaskLogger
  {
    public NullTaskLog()
    {
      super(null, null, null, null);
    }

    @Override
    public void logTaskStart(final Task<?> task)
    {
    }

    @Override
    public void logTaskEnd(final Task<?> task)
    {
    }
  }
}
