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

/**
 * Propagates the result from one promise to another.
 *
 * @author Chris Pettitt
 */
/* package private */ class PropagateResultListener<T, S extends T> implements PromiseListener<S>
{
  private final Promise<S> _source;
  private final SettablePromise<? super T> _dest;

  PropagateResultListener(final Promise<S> source, final SettablePromise<? super T> dest)
  {
    _source = source;
    _dest = dest;
  }

  @Override
  public void onResolved(Promise resolvedPromise)
  {
    if (_source.isFailed())
    {
      _dest.fail(_source.getError());
    }
    else
    {
      _dest.done(_source.get());
    }
  }
}
