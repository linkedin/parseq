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

package com.linkedin.parseq.example.composite.classifier.client.impl;

import com.linkedin.parseq.example.composite.classifier.client.Client;
import com.linkedin.parseq.example.composite.classifier.client.Request;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class ClientImpl implements Client
{
  private static final int LATENCY_MIN = 10;

  private final ScheduledExecutorService scheduler;
  private final Random random = new Random();

  public ClientImpl(final ScheduledExecutorService scheduler)
  {
    this.scheduler = scheduler;
  }

  @Override
  public <T> Promise<T> sendRequest(final Request<T> request)
  {
    final SettablePromise<T> promise = Promises.settable();
    final int mean = request.getLatencyMean();
    final int stddev = request.getLatencyStdDev();
    final int latency = Math.max(LATENCY_MIN, (int)(random.nextGaussian() * stddev + mean));
    scheduler.schedule(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          promise.done(request.getResponse());
        }
        catch (Exception e)
        {
          promise.fail(e);
        }
      }
    }, latency, TimeUnit.MILLISECONDS);
    return promise;
  }
}
