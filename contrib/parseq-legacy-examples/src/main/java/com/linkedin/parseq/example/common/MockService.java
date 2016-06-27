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

package com.linkedin.parseq.example.common;

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class MockService<RES>
{
  private final ScheduledExecutorService _scheduler;

  public MockService(ScheduledExecutorService scheduler)
  {
    _scheduler = scheduler;
  }

  public Promise<RES> call(final MockRequest<RES> request)
  {
    final SettablePromise<RES> promise = Promises.settable();
    _scheduler.schedule(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          promise.done(request.getResult());
        }
        catch (Exception e)
        {
          promise.fail(e);
        }
      }
    }, request.getLatency(), TimeUnit.MILLISECONDS);
    return promise;
  }
}
