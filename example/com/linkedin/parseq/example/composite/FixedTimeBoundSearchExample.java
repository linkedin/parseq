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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.collection.ParSeqCollections;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.MockService;
import com.linkedin.parseq.example.common.SimpleMockRequest;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class FixedTimeBoundSearchExample extends AbstractExample
{
  // How long it takes to get a response for each request
  private static final List<Long> REQUEST_LATENCIES = Arrays.asList(175L, 67L, 30L, 20L, 177L, 350L);

  public static void main(String[] args) throws Exception
  {
    new FixedTimeBoundSearchExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception
  {
    final MockService<Integer> service = getService();

    AtomicInteger idx = new AtomicInteger();
    Task<List<Integer>> example =
        ParSeqCollections.fromValues(REQUEST_LATENCIES)
          .mapTask(requestLatency -> callService("subSearch[" + idx.get() + "]",
                                      service,
                                      new SimpleMockRequest<Integer>(requestLatency, idx.getAndIncrement())))
          .toList()
          .within(200, TimeUnit.MILLISECONDS);

    System.out.printf("This com.linkedin.asm.example will issue %d parallel requests\n", REQUEST_LATENCIES.size());
    System.out.println();
    for (int i = 0; i < REQUEST_LATENCIES.size(); i++)
    {
      System.out.printf("Request %d will take %3dms to complete\n", i, REQUEST_LATENCIES.get(i));
    }

    System.out.println();
    System.out.println("Latency rule:");
    System.out.println("--------------");
    System.out.println("Finish after 200ms");

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
}
