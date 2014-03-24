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

package com.linkedin.parseq.promise;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.testng.AssertJUnit.*;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TestSettablePromise
{
  private ScheduledExecutorService _scheduler;

  @BeforeMethod
  public void setUp() throws Exception
  {
    _scheduler = Executors.newSingleThreadScheduledExecutor();
  }

  @AfterMethod
  public void tearDown() throws Exception
  {
    _scheduler.shutdownNow();
    _scheduler = null;
  }

  @Test
  public void testPromiseInitialState()
  {
    assertFalse(Promises.settable().isDone());
    assertFalse(Promises.settable().isFailed());
  }

  @Test
  public void testSetAndGet()
  {
    final SettablePromise<String> promise = Promises.settable();
    promise.done("done!");
    assertEquals("done!", promise.get());
    assertTrue(promise.isDone());
  }

  @Test
  public void testGetWithoutSet()
  {
    try
    {
      Promises.settable().get();
      fail("Should have thrown PromiseUnresolvedException");
    }
    catch (PromiseUnresolvedException e)
    {
      // Expected case
    }
  }

  @Test
  public void testGetOrDefaultWithSet()
  {
    final SettablePromise<String> promise = Promises.settable();
    promise.done("done!");
    assertEquals("done!", promise.getOrDefault("default"));
  }

  @Test
  public void testGetOrDefaultWithSetError()
  {
    final SettablePromise<String> promise = Promises.settable();
    promise.fail(new Exception());
    assertEquals("default", promise.getOrDefault("default"));
  }

  @Test
  public void testSetTwice()
  {
    final SettablePromise<String> promise = Promises.settable();
    promise.done("done!");

    try
    {
      promise.done("no, really done!");
      fail("Should have thrown an Exception");
    }
    catch (PromiseResolvedException e)
    {
      // Expected case
      assertEquals("done!", promise.get());
    }
  }

  @Test
  public void testSetErrorTwice()
  {
    final SettablePromise<String> promise = Promises.settable();
    final Exception exception = new Exception();
    promise.fail(exception);

    try
    {
      promise.fail(new Exception());
      fail("Should have thrown an Exception");
    }
    catch (PromiseResolvedException e)
    {
      // Expected case
      assertEquals(exception, promise.getError());
    }
  }

  @Test
  public void testSetThenSetError()
  {
    final SettablePromise<String> promise = Promises.settable();
    promise.done("done!");

    try
    {
      promise.fail(new Exception());
      fail("Should have thrown an Exception");
    }
    catch (PromiseResolvedException e)
    {
      // Expected case
      assertEquals("done!", promise.get());
    }
  }

  @Test
  public void testSetErrorAndGet()
  {
    final SettablePromise<String> promise = Promises.settable();
    final Exception error = new Exception();
    promise.fail(error);

    try
    {
      promise.get();
      fail("Should have thrown an exception");
    }
    catch (PromiseException e)
    {
      assertEquals(error, e.getCause());
    }
  }

  @Test
  public void testSetErrorAndGetError()
  {
    final SettablePromise<String> promise = Promises.settable();
    final Exception error = new Exception();
    promise.fail(error);
    assertEquals(error, promise.getError());
    assertTrue(promise.isDone());
    assertTrue(promise.isFailed());
  }

  @Test
  public void testSetAndGetError()
  {
    final SettablePromise<String> promise = Promises.settable();
    promise.done("done!");
    assertNull(promise.getError());
  }

  @Test
  public void testGetErrorWithoutSet()
  {
    try
    {
      Promises.settable().getError();
      fail("Should have thrown PromiseUnresolvedException");
    }
    catch (PromiseUnresolvedException e)
    {
      // Expected case
    }
  }

  @Test
  public void testTimedAwaitWithoutSet() throws InterruptedException
  {
    assertFalse(Promises.settable().await(50, TimeUnit.MILLISECONDS));
  }

  @Test
  public void testAwaitWithSet() throws InterruptedException
  {
    final SettablePromise<String> promise = Promises.settable();
    _scheduler.schedule(new Runnable()
    {
      @Override
      public void run()
      {
        promise.done("done!");
      }
    }, 10, TimeUnit.MILLISECONDS);

    assertTrue(promise.await(5, TimeUnit.SECONDS));
    assertEquals("done!", promise.get());
  }

  @Test
  public void testAwaitWithSetError() throws InterruptedException
  {
    final SettablePromise<String> promise = Promises.settable();
    final Exception error = new Exception();
    _scheduler.schedule(new Runnable()
    {
      @Override
      public void run()
      {
        promise.fail(error);
      }
    }, 10, TimeUnit.MILLISECONDS);

    assertTrue(promise.await(5, TimeUnit.SECONDS));
    assertEquals(error, promise.getError());
  }

  @Test
  public void testListenerWithSet() throws InterruptedException
  {
    final SettablePromise<String> promise = Promises.settable();

    final AtomicReference<String> resultRef = new AtomicReference<String>();
    final CountDownLatch latch = new CountDownLatch(1);
    promise.addListener(new PromiseListener<String>()
    {
      @Override
      public void onResolved(Promise<String> resolvedPromise)
      {
        resultRef.set(resolvedPromise.get());
        latch.countDown();
      }
    });

    _scheduler.schedule(new Runnable()
    {
      @Override
      public void run()
      {
        promise.done("done!");
      }
    }, 10, TimeUnit.MILLISECONDS);

    latch.await();
    assertEquals("done!", resultRef.get());
  }

  @Test
  public void testListenerWithSetError() throws InterruptedException
  {
    final SettablePromise<String> promise = Promises.settable();

    final Exception error = new Exception();
    final AtomicReference<Throwable> resultRef = new AtomicReference<Throwable>();
    final CountDownLatch latch = new CountDownLatch(1);
    promise.addListener(new PromiseListener<String>()
    {
      @Override
      public void onResolved(Promise<String> resolvedPromise)
      {
        resultRef.set(resolvedPromise.getError());
        latch.countDown();
      }
    });

    _scheduler.schedule(new Runnable()
    {
      @Override
      public void run()
      {
        promise.fail(error);
      }
    }, 10, TimeUnit.MILLISECONDS);

    latch.await();
    assertEquals(error, resultRef.get());
  }

  @Test
  public void testListenerAfterSet() throws InterruptedException
  {
    final SettablePromise<String> promise = Promises.settable();
    promise.done("done!");

    final AtomicReference<String> resultRef = new AtomicReference<String>();
    final CountDownLatch latch = new CountDownLatch(1);
    promise.addListener(new PromiseListener<String>()
    {
      @Override
      public void onResolved(Promise<String> resolvedPromise)
      {
        resultRef.set(resolvedPromise.get());
        latch.countDown();
      }
    });

    latch.await();
    assertEquals("done!", resultRef.get());
  }

  @Test
  public void testListenerThrowsException() throws InterruptedException
  {
    // This test ensures that we properly resolve the promise even when
    // one of its listeners throws an Error.
    final SettablePromise<String> promise = Promises.settable();
    promise.addListener(new PromiseListener<String>()
    {
      @Override
      public void onResolved(Promise<String> promise)
      {
        throw new RuntimeException();
      }
    });

    promise.done("Done!");
    assertTrue(promise.await(5, TimeUnit.SECONDS));
    assertEquals("Done!", promise.get());
  }

  @Test
  public void testListenerThrowsError() throws InterruptedException
  {
    // This test ensures that we properly resolve the promise even when
    // one of its listeners throws an Error.
    final SettablePromise<String> promise = Promises.settable();
    promise.addListener(new PromiseListener<String>()
    {
      @Override
      public void onResolved(Promise<String> promise)
      {
        throw new Error();
      }
    });

    promise.done("Done!");
    assertTrue(promise.await(5, TimeUnit.SECONDS));
    assertEquals("Done!", promise.get());
  }

  @Test
  public void testListenerThrowsErrorAfterPromiseDone() throws InterruptedException
  {
    // This test ensures that we catch and do not rethrow errors from listeners
    // even if they are added after the promise is done.
    final SettablePromise<String> promise = Promises.settable();
    promise.done("Done!");
    assertTrue(promise.await(5, TimeUnit.SECONDS));

    // This should not throw
    promise.addListener(new PromiseListener<String>()
    {
      @Override
      public void onResolved(Promise<String> promise)
      {
        throw new Error();
      }
    });
  }
}
