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

package com.linkedin.parseq;

import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;

import java.util.concurrent.Callable;


/**
 * A {@link Task} that will run a {@link Callable} and will set the task's value
 * to the value returned from the callable.
 * <p>
 * Use {@link Tasks#callable(String, java.util.concurrent.Callable)} to create
 * instances of this class.
 *
 * @deprecated  As of 2.0.0, replaced by {@link Task#callable(String, Callable) Task.callable}.
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @see Task#callable(String, Callable) Task.callable
 */
@Deprecated
public class CallableTask<T> extends BaseTask<T> {
  private final ThrowableCallable<? extends T> _callable;

  public CallableTask(final String name, final Callable<? extends T> callable) {
    this(name, adaptCallable(callable));
  }

  public CallableTask(final String name, final ThrowableCallable<? extends T> callable) {
    super(name);
    ArgumentUtil.requireNotNull(callable, "callable");
    _callable = callable;
  }

  @Override
  protected Promise<? extends T> run(final Context context) throws Throwable {
    return Promises.value(_callable.call());
  }

  private static <T> ThrowableCallable<T> adaptCallable(final Callable<? extends T> callable) {
    return new ThrowableCallable<T>() {
      @Override
      public T call() throws Throwable {
        return callable.call();
      }
    };
  }
}
