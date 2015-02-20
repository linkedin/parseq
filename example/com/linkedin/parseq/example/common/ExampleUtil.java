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

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.codec.json.JsonTraceCodec;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class ExampleUtil
{
  private static final Random RANDOM = new Random();
  private static final int DEFAULT_LATENCY_MEAN = 100;
  private static final int DEFAULT_LATENCY_STDDEV = 50;
  private static final int LATENCY_MIN = 10;

  private ExampleUtil() {}

  public static <RES> Task<RES> callService(final String name,
                                            final MockService<RES> service,
                                            final MockRequest<RES> request)
  {
    return new BaseTask<RES>(name) {
      @Override
      protected Promise<RES> run(final Context context) throws Exception
      {
        return service.call(request);
      }
    };
  }

  public static <T> Task<T> fetch(String name, final MockService<T> service, final int id, final Map<Integer, T> map) {
    final long mean = DEFAULT_LATENCY_MEAN;
    final long stddev = DEFAULT_LATENCY_STDDEV;
    final long latency = Math.max(LATENCY_MIN, (int)(RANDOM.nextGaussian() * stddev + mean));
    final MockRequest<T> request = (map.containsKey(id)) ?
        new SimpleMockRequest<T>(latency, map.get(id)) :
        new ErrorMockRequest<T>(latency, new Exception("404"));
    return callService("fetch" + name + "[id=" + id + "]", service, request);
  }

  public static Task<String> fetchUrl(final MockService<String> httpClient,
                                      final String url)
  {
    final long mean = DEFAULT_LATENCY_MEAN;
    final long stddev = DEFAULT_LATENCY_STDDEV;
    final long latency = Math.max(LATENCY_MIN, (int)(RANDOM.nextGaussian() * stddev + mean));
    return callService("fetch[url=" + url + "]", httpClient, new SimpleMockRequest<String>(latency, "HTTP response for " + url));
  }

  public static Task<String> fetchUrl(final MockService<String> httpClient, final String url, final long latency) {
    return callService("fetch[url=" + url + "]", httpClient, new SimpleMockRequest<String>(latency,
        "HTTP response for " + url));
  }

  public static Task<String> fetch404Url(final MockService<String> httpClient,
                                         final String url)
  {
    final long mean = DEFAULT_LATENCY_MEAN;
    final long stddev = DEFAULT_LATENCY_STDDEV;
    final long latency = Math.max(LATENCY_MIN, (int)(RANDOM.nextGaussian() * stddev + mean));
    return callService("fetch[url=" + url + "]", httpClient, new ErrorMockRequest<String>(latency, new Exception("404")));
  }

  public static void printTracingResults(final Task<?> task)
  {
    final Trace trace = task.getTrace();

    System.out.println();
    System.out.println();
    System.out.println("JSON Trace:");

    try
    {
      System.out.println(new JsonTraceCodec().encode(trace));
    }
    catch (IOException e)
    {
      System.err.println("Failed to encode JSON");
      e.printStackTrace();
    }
    System.out.println();
  }
}
