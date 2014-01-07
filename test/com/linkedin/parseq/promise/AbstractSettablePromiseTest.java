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
public abstract class AbstractSettablePromiseTest
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
  public void testSetAndGet()
  {
    final Promise<String> promise = createPromise();
    setPromiseValue(promise, "done!");
    assertEquals("done!", promise.get());
    assertTrue(promise.isDone());
  }

  @Test
  public void testGetWithoutSet()
  {
    final Promise<String> promise = createPromise();
    try
    {
      promise.get();
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
    final Promise<String> promise = createPromise();
    setPromiseValue(promise, "done!");
    assertEquals("done!", promise.getOrDefault("default"));
  }

  @Test
  public void testGetOrDefaultWithSetError()
  {
    final Promise<String> promise = createPromise();
    setPromiseError(promise, new Exception());
    assertEquals("default", promise.getOrDefault("default"));
  }

  @Test
  public void testSetTwice()
  {
    final Promise<String> promise = createPromise();
    setPromiseValue(promise, "done!");

    try
    {
      setPromiseValue(promise, "no, really done!");
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
    final Promise<String> promise = createPromise();
    final Exception exception = new Exception();
    setPromiseError(promise, exception);

    try
    {
      setPromiseError(promise, new Exception());
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
    final Promise<String> promise = createPromise();
    setPromiseValue(promise, "done!");

    try
    {
      setPromiseError(promise, new Exception());
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
    final Promise<String> promise = createPromise();
    final Exception error = new Exception();
    setPromiseError(promise, error);

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
    final Promise<String> promise = createPromise();
    final Exception error = new Exception();
    setPromiseError(promise, error);
    assertEquals(error, promise.getError());
    assertTrue(promise.isDone());
    assertTrue(promise.isFailed());
  }

  @Test
  public void testSetAndGetError()
  {
    final Promise<String> promise = createPromise();
    setPromiseValue(promise, "done!");
    assertNull(promise.getError());
  }

  @Test
  public void testGetErrorWithoutSet()
  {
    final Promise<String> promise = createPromise();
    try
    {
      promise.getError();
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
    final Promise<String> promise = createPromise();
    assertFalse(promise.await(50, TimeUnit.MILLISECONDS));
  }

  @Test
  public void testAwaitWithSet() throws InterruptedException
  {
    final Promise<String> promise = createPromise();
    _scheduler.schedule(new Runnable()
    {
      @Override
      public void run()
      {
        setPromiseValue(promise, "done!");
      }
    }, 10, TimeUnit.MILLISECONDS);

    promise.await(5, TimeUnit.SECONDS);
    assertEquals("done!", promise.get());
  }

  @Test
  public void testAwaitWithSetError() throws InterruptedException
  {
    final Promise<String> promise = createPromise();
    final Exception error = new Exception();
    _scheduler.schedule(new Runnable()
    {
      @Override
      public void run()
      {
        setPromiseError(promise, error);
      }
    }, 10, TimeUnit.MILLISECONDS);

    promise.await(5, TimeUnit.SECONDS);
    assertEquals(error, promise.getError());
  }

  @Test
  public void testListenerWithSet() throws InterruptedException
  {
    final Promise<String> promise = createPromise();

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
        setPromiseValue(promise, "done!");
      }
    }, 10, TimeUnit.MILLISECONDS);

    latch.await(5, TimeUnit.SECONDS);
    assertEquals("done!", resultRef.get());
  }

  @Test
  public void testListenerWithSetError() throws InterruptedException
  {
    final Promise<String> promise = createPromise();

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
        setPromiseError(promise, error);
      }
    }, 10, TimeUnit.MILLISECONDS);

    latch.await(5, TimeUnit.SECONDS);
    assertEquals(error, resultRef.get());
  }

  @Test
  public void testListenerAfterSet() throws InterruptedException
  {
    final Promise<String> promise = createPromise();
    setPromiseValue(promise, "done!");

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

    latch.await(5, TimeUnit.SECONDS);
    assertEquals("done!", resultRef.get());
  }

  protected abstract <T> Promise<T> createPromise();

  protected abstract <T> void setPromiseValue(Promise<T> promise, T value);

  protected abstract <T> void setPromiseError(Promise<T> promise, Throwable error);
}
