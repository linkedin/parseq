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

import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;


/**
 * This class provides a wrapper to allow synchronous tasks to be treated as
 * asynchronous tasks. This can be used for tasks that are blocking and are not
 * CPU intensive, like JDBC requests. Unlike tasks created with
 * {@link com.linkedin.parseq.TasksHelper.Tasks#callable(String, java.util.concurrent.Callable)},
 * tasks wrapped in AsyncCallableTask do not get any special memory consistency
 * guarantees and should not attempt to use shared state. In others, they should
 * act as a stateless function.
 * <p>
 * To use this class with an engine, register an executor with engine using
 * {@link #register(EngineBuilder, java.util.concurrent.Executor)}
 *
 * @deprecated  As of 2.0.0, replaced by {@link Task#blocking(String, Callable, Executor) Task.blocking}.
 * @author Walter Fender (wfender@linkedin.com)
 */
@Deprecated
public class AsyncCallableTask<R> extends BaseTask<R> {
  static final String CALLABLE_SERVICE_EXECUTOR = "_CallableServiceExecutor_";

  private final Callable<R> _syncJob;

  public static void register(EngineBuilder builder, Executor executor) {
    builder.setEngineProperty(CALLABLE_SERVICE_EXECUTOR, executor);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#blocking(String, Callable, Executor) Task.blocking}.
   */
  @Deprecated
  public AsyncCallableTask(final Callable<R> syncJob) {
    this(null, syncJob);
  }

  /**
   * @deprecated  As of 2.0.0, replaced by {@link Task#blocking(String, Callable, Executor) Task.blocking}.
   */
  @Deprecated
  public AsyncCallableTask(final String name, final Callable<R> syncJob) {
    super(name);
    ArgumentUtil.requireNotNull(syncJob, "job");
    _syncJob = syncJob;
  }

  @Override
  protected Promise<R> run(final Context context) throws Exception {
    Executor executor = (Executor) context.getEngineProperty(CALLABLE_SERVICE_EXECUTOR);
    if (executor == null) {
      throw new IllegalStateException(
          "To use AsyncCallableTask you must first register an executor with the engine using AsyncCallableTask.register");
    }

    final SettablePromise<R> promise = Promises.settable();
    executor.execute(() -> {
      try {
        promise.done(_syncJob.call());
      } catch (Throwable t) {
        promise.fail(t);
      }
    } );
    return promise;
  }
}
