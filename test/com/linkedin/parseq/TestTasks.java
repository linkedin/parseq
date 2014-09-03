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
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.trace.codec.json.JsonTraceCodec;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static com.linkedin.parseq.TestUtil.value;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
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

    assertTrue(task.await(5, TimeUnit.SECONDS));

    assertTrue(task.isFailed());
    assertEquals(error, task.getError());
  }

  @Test
  public void testAwait() throws InterruptedException
  {
    final String value = "value";
    final Task<String> task = value("value", value);
    final AtomicReference<Boolean> resultRef = new AtomicReference<Boolean>(false);

    task.addListener(new PromiseListener<String>()
    {
      @Override
      public void onResolved(Promise<String> stringPromise)
      {
        try
        {
          Thread.sleep(100);
        } catch (InterruptedException e)
        {
          //ignore
        } finally
        {
          resultRef.set(true);
        }
      }
    });

    getEngine().run(task);
    assertTrue(task.await(5, TimeUnit.SECONDS));
    assertEquals(Boolean.TRUE, resultRef.get());
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

    assertTrue(timeoutTask.await(5, TimeUnit.SECONDS));

    assertTrue(timeoutTask.isFailed());
    assertTrue(timeoutTask.getError() instanceof TimeoutException);

    assertTrue(task.await(5, TimeUnit.SECONDS));

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

    assertTrue(timeoutTask.await(5, TimeUnit.SECONDS));

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
  public void testTimeoutTaskWithoutTimeoutWhenManyTasksAreOnQueeu() throws InterruptedException, IOException
  {
    final String value = "value";

    List<Task<String>> tasks = new ArrayList<Task<String>>();
    for (int i = 0; i < 50; i++) {
      Task<String> t = Tasks.callable("task", new Callable<String>() {
        @Override
        public String call() throws Exception {
          Thread.sleep(1); //this task is "busy" for 1 millisecond
          return value;
        }
      });
      tasks.add(Tasks.timeoutWithError(50, TimeUnit.MILLISECONDS, t));
    }

    final Task<?> timeoutTask = Tasks.par(tasks);

    getEngine().run(timeoutTask);

    assertTrue(timeoutTask.await(5, TimeUnit.SECONDS));

    System.out.println(new JsonTraceCodec().encode(timeoutTask.getTrace()));

    //tasks should not time out
    assertEquals(false, timeoutTask.isFailed());
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

  @Test
  public void testThrowableCallableNoError() throws InterruptedException
  {
    final Integer magic = 0x5f3759df;
    final ThrowableCallable<Integer> callable = new ThrowableCallable<Integer>()
    {
      @Override
      public Integer call() throws Throwable
      {
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

  @Test
  public void testThrowableCallableWithError() throws InterruptedException
  {
    final Throwable throwable = new Throwable();
    final ThrowableCallable<Integer> callable = new ThrowableCallable<Integer>()
    {
      @Override
      public Integer call() throws Throwable
      {
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

