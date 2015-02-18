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

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * A Promise, like a {@link java.util.concurrent.Future}, represents the result
 * of an asynchronous computation. However, Promises are designed to work in
 * an entirely asynchronous work flow. Except where specifically
 * mentioned, none of the methods in the Promise will block the current thread
 * while waiting for a response.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public interface Promise<P>
{
  /**
   * If the promise's value is set, then this method returns the value. If the
   * promise has an error, the error is thrown, wrapped in a
   * {@link PromiseException}. If the promise has not yet been resolved (i.e.
   * it has no value and no error) then this method raises a
   * {@link PromiseUnresolvedException}.
   *
   * @return the value of the promise if it is available
   * @throws PromiseException if the promise has an error
   * @throws PromiseUnresolvedException if the promise has not yet been resolved
   */
  P get() throws PromiseException;

  /**
   * Returns the error in this promise, if there is one. If this promise
   * contains a value, then this method will return {@code null}. If this
   * promise has no value and no error (i.e. it is unresolved) then a
   * {@link PromiseUnresolvedException} is thrown.
   *
   * @return the error contained in this promise, or {@code null} if the
   *         promise contains a value.
   * @throws PromiseUnresolvedException if the promise has not yet been resolved
   */
  Throwable getError() throws PromiseUnresolvedException;

  /**
   * Gets the value in this promise or, if the promise contains an error,
   * returns the given default value. This is one mechanism for gracefully
   * handling promise failure.
   * <p/>
   * If the promise has not been resolved then this method will throw
   * {@link PromiseUnresolvedException}.
   *
   * @param defaultValue the default value to return if this promise contains an
   *                     error.
   * @return the value in this promise or the default value if this promise
   *         contains an error.
   * @throws PromiseUnresolvedException if the promise has not yet been resolved
   */
  P getOrDefault(P defaultValue) throws PromiseUnresolvedException;

  /**
   * Blocks the current thread for an unbounded amount of time until the promise
   * has be resolved.
   * <p/>
   * For asynchronous workflows, use {@link #addListener(PromiseListener)},
   * which will notify the user when the promise is resolved instead of blocking
   * the current thread.
   *
   * @throws InterruptedException if the current thread is interrupted
   */
  void await() throws InterruptedException;

  /**
   * Blocks the current thread for up to the specified amount of time or until
   * the promise has been resolved. If the promise was resolved before the
   * specified time has expired, this method returns {@code true}.
   * <p/>
   * For asynchronous workflows, use {@link #addListener(PromiseListener)},
   * which will notify the user when the promise is resolved instead of blocking
   * the current thread.
   *
   * @return {@code true} if the promise is resolved within the specified time
   * @param time the amount of time to wait
   * @param unit the units for the wait time
   * @throws InterruptedException if the current thread is interrupted
   */
  boolean await(long time, TimeUnit unit) throws InterruptedException;

  /**
   * Adds a listener to this promise that will be notified when the promise is
   * resolved. If the promise has already been resolved then the listener is
   * notified immediately.
   *
   * @param listener the listener to add to this promise
   */
  void addListener(PromiseListener<P> listener);

  /**
   * Returns {@code true} if this promise is resolved. A promise is resolved
   * when it has a value or an error.
   *
   * @return {@code true} if this promise has been resolved.
   */
  boolean isDone();

  /**
   * Returns {@code true} if the promise has an error. Errors can retrieved
   * using {@link #getError()}.
   *
   * @return {@code true} if the promise has en error.
   */
  boolean isFailed();

  /**
   * When this Promise is done, either through a Throwable or a value,
   * call the provided Consumer of this Promise.
   *
   * @param consumer the Consumer to be called when this Promise is done.
   */
  default void onResolve(final Consumer<Promise<P>> consumer)
  {
    addListener(new PromiseListener<P>() {
      @Override
      public void onResolved(final Promise<P> promise) {
        consumer.accept(promise);
      }
    });
  }

  /**
   * When this Promise is successfully done through a value,
   * call the provided Consumer of that value.
   *
   * @param consumer the Consumer to be called when this Promise is successfully done.
   */
  default void onSuccess(final Consumer<P> consumer)
  {
    addListener(new PromiseListener<P>() {
      @Override
      public void onResolved(final Promise<P> promise) {
        if (!promise.isFailed()) {
          consumer.accept(promise.get());
        }
      }
    });
  }

  /**
   * When this Promise is done through a Throwable,
   * call the provided Consumer of that Throwable.
   *
   * @param consumer the Consumer to be called when this Promise is successfully done.
   */
  default void onFailure(final Consumer<Throwable> consumer)
  {
    addListener(new PromiseListener<P>() {
      @Override
      public void onResolved(final Promise<P> promise) {
        if (promise.isFailed()) {
          consumer.accept(promise.getError());
        }
      }
    });
  }

}
