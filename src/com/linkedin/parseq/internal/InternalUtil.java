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

package com.linkedin.parseq.internal;

import com.linkedin.parseq.promise.CountDownPromiseListener;
import com.linkedin.parseq.promise.FastFailCountDownPromiseListener;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.Task;


/**
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class InternalUtil {
  private InternalUtil() {
  }

  /**
   * Invokes the {@code listener} once all given {@code promises} have been resolved.
   *
   * @param listener the listener to invoke when the promises have finished.
   * @param promises the set of promises that must be resolved
   *
   * @see com.linkedin.parseq.promise.PromiseListener
   */
  public static void after(final PromiseListener<?> listener, final Promise<?>... promises) {
    final SettablePromise<Object> afterPromise = Promises.settable();
    final PromiseListener<Object> countDownListener =
        new CountDownPromiseListener<Object>(promises.length, afterPromise, null);

    for (Promise<?> promise : promises) {
      unwildcardPromise(promise).addListener(countDownListener);
    }

    afterPromise.addListener(unwildcardListener(listener));
  }

  /**
   * Invokes the {@code listener} once all given {@code promises} have been resolved
   * or any of promises has been resolved with failure.
   *
   * @param listener the listener to invoke when the promises have finished.
   * @param promises the set of promises that must be resolved
   *
   * @see com.linkedin.parseq.promise.PromiseListener
   */
  public static void fastFailAfter(final PromiseListener<?> listener, final Promise<?>... promises) {
    final SettablePromise<Object> afterPromise = Promises.settable();
    final PromiseListener<Object> countDownListener =
        new FastFailCountDownPromiseListener<Object>(promises.length, afterPromise, null);

    for (Promise<?> promise : promises) {
      unwildcardPromise(promise).addListener(countDownListener);
    }

    afterPromise.addListener(unwildcardListener(listener));
  }

  @SuppressWarnings("unchecked")
  public static Task<Object> unwildcardTask(Task<?> task) {
    return (Task<Object>) task;
  }

  @SuppressWarnings("unchecked")
  public static Promise<Object> unwildcardPromise(Promise<?> promise) {
    return (Promise<Object>) promise;
  }

  @SuppressWarnings("unchecked")
  public static PromiseListener<Object> unwildcardListener(PromiseListener<?> listener) {
    return (PromiseListener<Object>) listener;
  }
}
