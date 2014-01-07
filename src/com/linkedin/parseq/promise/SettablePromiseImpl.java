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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
/* package private */ class SettablePromiseImpl<T> implements SettablePromise<T>
{
  private static final Logger LOGGER = LoggerFactory.getLogger(SettablePromiseImpl.class);

  private final CountDownLatch _awaitLatch = new CountDownLatch(1);

  private final AtomicReference<Result<T>> _result = new AtomicReference<Result<T>>();
  private final ConcurrentLinkedQueue<PromiseListener<T>> _listeners = new ConcurrentLinkedQueue<PromiseListener<T>>();

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
    Result<T> result = getResult();
    if (result._error != null)
    {
      throw new PromiseException(result._error);
    }
    return result._value;
  }

  @Override
  public Throwable getError() throws PromiseUnresolvedException
  {
    return getResult()._error;
  }

  @Override
  public T getOrDefault(final T defaultValue) throws PromiseUnresolvedException
  {
    Result<T> result = getResult();
    if (result._error != null)
    {
      return defaultValue;
    }
    return result._value;
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
    if (isDone())
    {
      listener.onResolved(this);
      return;
    }

    _listeners.add(listener);

    if (isDone())
      purgeListeners();
  }

  @Override
  public boolean isDone()
  {
    return _result.get() != null;
  }

  @Override
  public boolean isFailed()
  {
    final Result voe = _result.get();
    return voe != null && voe._error != null;
  }

  private void doFinish(T value, Throwable error) throws PromiseResolvedException
  {
    if (!_result.compareAndSet(null, new Result<T>(value, error)))
    {
      throw new PromiseResolvedException("Promise has already been satisfied");
    }

    purgeListeners();

    _awaitLatch.countDown();
  }

  private void purgeListeners()
  {
    PromiseListener<T> listener;
    while((listener = _listeners.poll()) != null)
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
  }

  private Result<T> getResult() throws PromiseUnresolvedException
  {
    final Result<T> voe = _result.get();
    if (voe == null)
    {
      throw new PromiseUnresolvedException("Promise has not yet been satisfied");
    }
    return voe;
  }

  private static class Result<T>
  {
    private final T _value;
    private final Throwable _error;

    public Result(T value, Throwable error)
    {
      if (error != null)
      {
        _value = null;
        _error = error;
      }
      else
      {
        _value = value;
        _error = null;
      }
    }
  }
}
