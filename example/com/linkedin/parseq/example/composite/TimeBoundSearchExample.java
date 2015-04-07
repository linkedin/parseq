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

package com.linkedin.parseq.example.composite;

import static com.linkedin.parseq.example.common.ExampleUtil.callService;
import static com.linkedin.parseq.example.common.ExampleUtil.printTracingResults;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.MockService;
import com.linkedin.parseq.example.common.SimpleMockRequest;
import com.linkedin.parseq.function.Action;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TimeBoundSearchExample extends AbstractExample {
  // How long it takes to get a response for each request
  private static final long[] REQUEST_LATENCIES = new long[] { 175, 67, 30, 20, 177, 350 };

  // How long the engine will wait for index number of responses
  private static final long[] WAIT_TIMES = new long[] { 400, 300, 200, 100, 0 };

  public static void main(String[] args) throws Exception {
    new TimeBoundSearchExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final MockService<Integer> service = getService();

    final SearchTask example = new SearchTask(service);

    System.out.printf("This com.linkedin.asm.example will issue %d parallel requests\n", REQUEST_LATENCIES.length);
    System.out.println();
    for (int i = 0; i < REQUEST_LATENCIES.length; i++) {
      System.out.printf("Request %d will take %3dms to complete\n", i, REQUEST_LATENCIES[i]);
    }

    System.out.println();
    System.out.println("Latency rules:");
    System.out.println("--------------");
    for (int i = 0; i < WAIT_TIMES.length; i++) {
      System.out.printf("Finish if received %d responses after %3dms\n", i, WAIT_TIMES[i]);
    }

    System.out.println();
    System.out.println("Execution:");
    System.out.println("----------");
    final long startMillis = System.currentTimeMillis();
    engine.run(example);
    example.await();
    final long endMillis = System.currentTimeMillis();

    System.out.println("Responses: " + example.get());
    System.out.println("Execution time: " + (endMillis - startMillis) + "ms");

    printTracingResults(example);
  }

  private static class SearchTask extends BaseTask<List<Integer>> {
    private final MockService<Integer> _service;
    private final List<Integer> _responses = new ArrayList<Integer>();
    private final SettablePromise<List<Integer>> _result = Promises.settable();

    private long _startMillis;

    public SearchTask(final MockService<Integer> service) {
      super("search");
      _service = service;
    }

    @Override
    public Promise<List<Integer>> run(final Context ctx) {
      // Save the start time so we can determine when to finish
      _startMillis = System.currentTimeMillis();

      // Set up timeouts for responses
      long lastWaitTime = Integer.MAX_VALUE;
      for (final long waitTime : WAIT_TIMES) {
        if (waitTime < lastWaitTime && waitTime > 0) {
          ctx.createTimer(waitTime, TimeUnit.MILLISECONDS, checkDone());
          lastWaitTime = waitTime;
        }
      }

      // Issue requests
      for (int i = 0; i < REQUEST_LATENCIES.length; i++) {
        final long requestLatency = REQUEST_LATENCIES[i];
        final Task<Integer> callSvc =
            callService("subSearch[" + i + "]", _service, new SimpleMockRequest<Integer>(requestLatency, i));

        ctx.run(callSvc.andThen(addResponse(callSvc)).andThen(checkDone()));
      }

      return _result;
    }

    private Task<?> checkDone() {
      return Task.action("checkDone", new Action() {
        @Override
        public void run() {
          final int index = Math.min(WAIT_TIMES.length - 1, _responses.size());
          if (WAIT_TIMES[index] + _startMillis <= System.currentTimeMillis()) {
            _result.done(_responses);
          }
        }
      });
    }

    private Task<?> addResponse(final Promise<Integer> response) {
      return Task.action("addResponse", new Action() {
        @Override
        public void run() {
          _responses.add(response.get());
        }
      });
    }
  }
}
