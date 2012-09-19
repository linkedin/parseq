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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sets a value on a {@link SettablePromise} after this listener has been
 * notified of a specified number of promise resolutions.
 *
 * @author Chris Pettitt
 */
public class CountDownPromiseListener<T> implements PromiseListener<T>
{
  private final AtomicInteger _counter;
  private final SettablePromise<T> _promise;
  private final T _value;

  public CountDownPromiseListener(final int count, final SettablePromise<T> promise, final T value)
  {
    _counter = new AtomicInteger(count);
    _promise = promise;
    _value = value;
  }

  @Override
  public void onResolved(Promise<T> resolvedPromise)
  {
    if (_counter.decrementAndGet() == 0)
    {
      _promise.done(_value);
    }
  }
}
