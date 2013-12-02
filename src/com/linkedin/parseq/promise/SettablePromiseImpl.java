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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
/* package private */ class SettablePromiseImpl<T> implements SettablePromise<T>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SettablePromiseImpl.class);

  private final CountDownLatch _awaitLatch = new CountDownLatch(1);

  private final Lock _lock = new ReentrantLock();
  private List<PromiseListener<T>> _listeners = new ArrayList<PromiseListener<T>>();
  private volatile T _value;
  private volatile Throwable _error;
  private volatile boolean _done;

  @Override
  public void done(final T value) throws PromiseResolvedException
  {
    doFinish(value, null);
  }

  @Override
  public void fail(final Throwable error) throws PromiseResolvedException
  {
    doFinish(null, error);
  }

  @Override
  public T get() throws PromiseException
  {
    ensureDone();
    if (_error != null)
    {
      throw new PromiseException(_error);
    }
    return _value;
  }

  @Override
  public Throwable getError() throws PromiseUnresolvedException
  {
    ensureDone();
    return _error;
  }

  @Override
  public T getOrDefault(final T defaultValue) throws PromiseUnresolvedException
  {
    ensureDone();
    if (_error != null)
    {
      return defaultValue;
    }
    return _value;
  }

  @Override
  public void await() throws InterruptedException
  {
    _awaitLatch.await();
  }

  @Override
  public boolean await(final long time, final TimeUnit unit) throws InterruptedException
  {
    return _awaitLatch.await(time, unit);
  }

  @Override
  public void addListener(final PromiseListener<T> listener)
  {
    _lock.lock();
    try
    {
      if (!isDone())
      {
        _listeners.add(listener);
        return;
      }
    }
    finally
    {
      _lock.unlock();
    }

    notifyListener(listener);
  }

  @Override
  public boolean isDone()
  {
    return _done;
  }

  @Override
  public boolean isFailed()
  {
    return isDone() && _error != null;
  }

  private void doFinish(T value, Throwable error) throws PromiseResolvedException
  {
    final List<PromiseListener<T>> listeners;

    _lock.lock();
    try
    {
      ensureNotDone();
      _value = value;
      _error = error;
      listeners = _listeners;
      _listeners = null;
      _done = true;
    }
    finally
    {
      _lock.unlock();
    }

    for (int i = listeners.size() - 1; i >= 0; i--)
    {
      notifyListener(listeners.get(i));
    }

    _awaitLatch.countDown();
  }

  private void notifyListener(final PromiseListener<T> listener)
  {
    try
    {
      listener.onResolved(this);
    }
    catch (Exception e)
    {
      LOGGER.warn("An exception was thrown by listener: " + listener.getClass(), e);
    }
  }

  private void ensureNotDone() throws PromiseResolvedException
  {
    if (isDone())
    {
      throw new PromiseResolvedException("Promise has already been satisfied");
    }
  }

  private void ensureDone() throws PromiseUnresolvedException
  {
    if (!isDone())
    {
      throw new PromiseUnresolvedException("Promise has not yet been satisfied");
    }
  }
}
