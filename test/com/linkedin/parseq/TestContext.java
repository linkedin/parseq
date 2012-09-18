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

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.linkedin.parseq.Tasks.action;
import static com.linkedin.parseq.Tasks.callable;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TestContext extends BaseEngineTest
{
  @Test
  public void testCallContextOutsideOfTask() throws InterruptedException
  {
    // Used to capture the context from a task
    final AtomicReference<Context> contextRef = new AtomicReference<Context>();

    // Used to indicate when the context reference has been captured
    final CountDownLatch contextSetLatch = new CountDownLatch(1);

    // Used to prevent our task from completing until after we've tried to
    // use it's context
    final CountDownLatch stopTaskLatch = new CountDownLatch(1);

    // Task that we use to capture the context
    final Task<?> task = new BaseTask<Object>()
    {
      @Override
      protected Promise<Object> run(final Context context) throws Exception
      {
        contextRef.set(context);
        contextSetLatch.countDown();

        try
        {
          stopTaskLatch.await();
        }
        catch (InterruptedException e)
        {
          // Ignore
        }
        return Promises.value(null);
      }
    };

    getEngine().run(task);

    contextSetLatch.await();

    final Context context = contextRef.get();

    TestUtil.assertThrows(IllegalStateException.class, new ThrowingRunnable()
    {
      @Override
      public void run() throws Exception
      {
        context.run(TestUtil.noop());
      }
    });

    TestUtil.assertThrows(IllegalStateException.class, new ThrowingRunnable()
    {
      @Override
      public void run() throws Exception
      {
        context.createTimer(1, TimeUnit.SECONDS, TestUtil.noop());
      }
    });

    TestUtil.assertThrows(IllegalStateException.class, new ThrowingRunnable()
    {
      @Override
      public void run() throws Exception
      {
        context.after(TestUtil.noop());
      }
    });

    stopTaskLatch.countDown();
  }

  @Test
  public void testCreateTimer() throws InterruptedException
  {
    final String value = "done";

    final SettablePromise<String> promise = Promises.settable();
    final Task<?> timerTask = action("timerTask", new Runnable()
    {
      @Override
      public void run()
      {
        promise.done(value);
      }
    });

    final Task<String> task = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        context.createTimer(50, TimeUnit.MILLISECONDS, timerTask);
        return promise;
      }
    };

    getEngine().run(task);

    assertTrue(task.await(100, TimeUnit.MILLISECONDS));
    assertEquals(value, task.get());
  }

  @Test
  public void testRun() throws InterruptedException
  {
    final String value = "done";

    final Task<String> innerTask = callable("innerTask", new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        return value;
      }
    });

    final Task<String> task = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        context.run(innerTask);
        return innerTask;
      }
    };

    getEngine().run(task);

    assertTrue(task.await(100, TimeUnit.MILLISECONDS));
    assertEquals(value, task.get());
  }

  @Test
  public void testAfter() throws InterruptedException
  {
    final String predecessorValue = "predecessor done";
    final String successorValue = "successor done";

    final AtomicReference<String> predecessorValueRef = new AtomicReference<String>();

    final Task<String> predecessorTask = callable("predecessorTask", new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        return predecessorValue;
      }
    });

    final Task<String> successorTask = callable("successorTask", new Callable<String>()
    {
      @Override
      public String call() throws Exception
      {
        predecessorValueRef.set(predecessorTask.get());
        return successorValue;
      }
    });

    final Task<String> task = new BaseTask<String>()
    {
      @Override
      public Promise<String> run(final Context context) throws Exception
      {
        context.run(predecessorTask);
        context.after(predecessorTask).run(successorTask);
        return successorTask;
      }
    };

    getEngine().run(task);

    assertTrue(task.await(100, TimeUnit.MILLISECONDS));
    assertEquals(successorValue, successorTask.get());
    assertEquals(predecessorValue, predecessorValueRef.get());
  }

  @Test
  public void testPrioritizedTasks() throws InterruptedException
  {
    final Task<Queue<Integer>> task = new BaseTask<Queue<Integer>>()
    {
      @Override
      public Promise<Queue<Integer>> run(final Context context) throws Exception
      {
        final SettablePromise<Queue<Integer>> promise = Promises.settable();
        final Queue<Integer> queue = new LinkedList<Integer>();

        final Task<?> t1 = enqueueTask(queue, 1);
        t1.setPriority(-5);

        final Task<?> t2 = enqueueTask(queue, 2);
        t2.setPriority(10);

        final Task<?> t3 = enqueueTask(queue, 3);
        t3.setPriority(0);

        context.run(t1, t2, t3);
        context.after(t1, t2, t3).run(Tasks.action("done", new Runnable()
        {
          @Override
          public void run()
          {
            promise.done(queue);
          }
        }));

        return promise;
      }
    };

    getEngine().run(task);

    task.await();

    final Queue<Integer> ints = task.get();
    assertEquals((Integer)2, ints.poll());
    assertEquals((Integer)3, ints.poll());
    assertEquals((Integer)1, ints.poll());
    assertTrue(ints.isEmpty());
  }

  private static <T> Task<?> enqueueTask(final Queue<T> queue, final T value)
  {
    return Tasks.action("enqueue", new Runnable()
    {
      @Override
      public void run()
      {
        queue.add(value);
      }
    });
  }
}
