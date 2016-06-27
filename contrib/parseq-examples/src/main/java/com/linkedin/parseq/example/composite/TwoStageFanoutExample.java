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

import static com.linkedin.parseq.example.common.ExampleUtil.fetchUrl;
import static com.linkedin.parseq.example.common.ExampleUtil.printTracingResults;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.MockService;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TwoStageFanoutExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new TwoStageFanoutExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {

    final MockService<String> httpClient = getService();

    Task<String> stage1 = Task.par(fetchAndMap("http://www.bing.com", httpClient),
                                   fetchAndMap("http://www.yahoo.com", httpClient))
                                 .map((a, b) -> a + b);

    Task<String> stage2 = Task.par(fetchAndMap("http://www.google.com", httpClient),
                                   fetchAndMap("https://duckduckgo.com", httpClient))
                                 .map((a, b) -> a + b);

    Task<String> plan = stage1.flatMap("combine", s1 -> stage2.map(s2 -> s1 + s2));

    engine.run(plan);

    plan.await();
    printTracingResults(plan);
  }

  private Task<String> fetchAndMap(final String url, final MockService<String> httpClient) {
    return fetchUrl(httpClient, url).map(s -> String.format("%10s => %s\n", url, s));
  }

}
