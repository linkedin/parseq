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

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class provides a wrapper to allow synchronous tasks to be treated as asynchronous tasks.
 * This is intended to allow ParSeq to get parallel execution on things like JDBC requests.
 * It does nothing to prevent threading problems between these Tasks.
 * That should be handled by the synchronous task authors.
 *
 * @author Walter Fender (wfender@linkedin.com)
 */
public class AsyncCallableTask<R> extends BaseTask<R>
{
  private static final String CALLABLE_SERVICE_EXECUTOR = "_CallableServiceExecutor_";

  private final Callable<R> _syncJob;

  public static void register(EngineBuilder builder, Executor executor)
  {
     builder.setEngineProperty(CALLABLE_SERVICE_EXECUTOR, executor);
  }

  public static void register(EngineBuilder builder, int size)
  {
     builder.setEngineProperty(CALLABLE_SERVICE_EXECUTOR, Executors.newFixedThreadPool(size));
  }

  public AsyncCallableTask(Callable<R> syncJob)
  {
    _syncJob = syncJob;
  }

  @Override
  protected Promise<R> run(final Context context) throws Exception
  {
    Executor executor = (Executor)context.getEngineProperty(CALLABLE_SERVICE_EXECUTOR);
    if (executor == null)
    {
      throw new Exception("Could not wrap the relevant callable sync job");
    }

    final SettablePromise<R> promise = Promises.settable();
    executor.execute(new Runnable()
      {
        @Override
        public void run()
        {
          try
          {
            promise.done(_syncJob.call());
          }
          catch (Throwable t)
          {
            promise.fail(t);
          }
        }
      });
    return promise;
  }
}
