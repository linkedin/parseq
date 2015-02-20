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

package com.linkedin.parseq.example.legacy.composite;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.MockService;
import com.linkedin.parseq.promise.Promise;

import java.util.concurrent.Callable;

import static com.linkedin.parseq.Tasks.action;
import static com.linkedin.parseq.Tasks.callable;
import static com.linkedin.parseq.Tasks.par;
import static com.linkedin.parseq.Tasks.seq;
import static com.linkedin.parseq.example.common.ExampleUtil.fetchUrl;
import static com.linkedin.parseq.example.common.ExampleUtil.printTracingResults;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TwoStageFanoutExample extends AbstractExample
{
  public static void main(String[] args) throws Exception
  {
    new TwoStageFanoutExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception
  {
    final MockService<String> httpClient = getService();

    final FanoutTask fanout = new FanoutTask(httpClient);
    final Task<?> printResults = action("printResults", new Runnable()
    {
      @Override
      public void run()
      {
        System.out.println(fanout.get());
      }
    });

    final Task<?> plan = seq(fanout, printResults);
    engine.run(plan);

    plan.await();
    printTracingResults(plan);
  }

  private static class FanoutTask extends BaseTask<String>
  {
    private final MockService<String> _httpClient;
    private final StringBuilder _result = new StringBuilder();

    private FanoutTask(final MockService<String> httpClient)
    {
      super("TwoStageFanout");
      _httpClient = httpClient;
    }

    @Override
    public Promise<String> run(final Context ctx)
    {
      final Task<String> twoStage =
          seq(par(fetchAndLog("http://www.bing.com"),
                  fetchAndLog("http://www.yahoo.com")),
              par(fetchAndLog("http://www.google.com"),
                  fetchAndLog("https://duckduckgo.com/")),
              buildResult());
      ctx.run(twoStage);
      return twoStage;
    }

    private Task<String> buildResult()
    {
      return callable("buildResult", new Callable<String>()
      {
        @Override
        public String call()
        {
          return _result.toString();
        }
      });
    }

    private Task<?> fetchAndLog(final String url)
    {
      final Task<String> fetch = fetchUrl(_httpClient, url);
      final Task<?> logResult = logResult(url, fetch);
      return seq(fetch, logResult);
    }

    private Task<?> logResult(final String url, final Promise<String> promise)
    {
      return action("logResult[" + url + "]", new Runnable()
      {
        @Override
        public void run()
        {
          _result.append(String.format("%10s => %s\n", url, promise.get()));
        }
      });
    }
  }
}
