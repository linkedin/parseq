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

import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TestDelegatingPromise
{
  @Test
  public void testGet()
  {
    final Promise<String> delegate = Promises.value("value");
    final Promise<String> promise = new DelegatingPromise<String>(delegate);
    assertEquals(delegate.get(), promise.get());
    assertEquals(delegate.isDone(), promise.isDone());
  }

  @Test
  public void testGetError()
  {
    final Promise<String> delegate = Promises.error(new Exception());
    final Promise<String> promise = new DelegatingPromise<String>(delegate);
    assertEquals(delegate.getError(), promise.getError());
    assertEquals(delegate.isDone(), promise.isDone());
    assertEquals(delegate.isFailed(), promise.isFailed());
  }

  @Test
  public void testGetOrDefaultWithError()
  {
    final Promise<String> delegate = Promises.error(new Exception());
    final Promise<String> promise = new DelegatingPromise<String>(delegate);
    assertEquals(delegate.getOrDefault("defaultValue"),
                 promise.getOrDefault("defaultValue"));
  }

  @Test
  public void testGetOrDefaultWithValue()
  {
    final Promise<String> delegate = Promises.value("value");
    final Promise<String> promise = new DelegatingPromise<String>(delegate);
    assertEquals(delegate.getOrDefault("defaulValue"),
                 promise.getOrDefault("defaultValue"));
  }

  @Test
  public void testAwait() throws InterruptedException
  {
    final SettablePromise<String> delegate = Promises.settable();
    final Promise<String> promise = new DelegatingPromise<String>(delegate);

    final String value = "value";
    delegate.done(value);

    assertTrue(promise.await(20, TimeUnit.MILLISECONDS));
    assertEquals(value, promise.get());
  }

  @Test
  public void testAddListener() throws InterruptedException
  {
    final SettablePromise<String> delegate = Promises.settable();
    final Promise<String> promise = new DelegatingPromise<String>(delegate);

    final String value = "value";
    final CountDownLatch cdl = new CountDownLatch(1);
    promise.addListener(new PromiseListener<String>()
    {
      @Override
      public void onResolved(Promise<String> resolvedPromise)
      {
        cdl.countDown();
      }
    });

    delegate.done(value);
    assertTrue(cdl.await(20, TimeUnit.MILLISECONDS));
    assertEquals(value, promise.get());
  }
}
