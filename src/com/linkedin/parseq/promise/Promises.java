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
 * This class provides a set of static helper methods that make it easier to
 * work with {@link Promise}s.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class Promises
{
  public static final Promise<Void> VOID = value(null);

  private Promises() {}

  /**
   * Creates a new promise that is already resolved with the given value.
   *
   * @param value the value for the new promise
   * @param <T> the type of the value for the promise
   * @return the promise
   */
  @SuppressWarnings("unchecked")
  public static <T> Promise<T> value(final T value)
  {
    if (value == null) {
      if (VOID == null) {
        return new ResolvedValue<T>(value);
      } else {
        return (Promise<T>) VOID;
      }
    }
    return new ResolvedValue<T>(value);
  }

  /**
   * Creates a new promise that is already resolved with the given error.
   *
   * @param error the error for the new promise
   * @param <T> the type of the value for the promise
   * @return the promise
   */
  public static <T> Promise<T> error(final Throwable error)
  {
    return new ResolvedError<T>(error);
  }

  /**
   * Returns a new promise that can have its value set at a later time.
   *
   * @param <T> the type of the value for the promise
   * @return the promise
   */
  public static <T> SettablePromise<T> settable()
  {
    return new SettablePromiseImpl<T>();
  }

  /**
   * Copies the value or error from the source promise to the destination
   * promise.
   *
   * @param source the source promise
   * @param dest the destination promise
   * @param <T> the value type for both promises
   */
  public static <T> void propagateResult(final Promise<T> source,
                                         final SettablePromise<T> dest)
  {
    source.addListener(new TransformingPromiseListener<T, T>(dest, PromiseTransformer.identity()));
  }
}
