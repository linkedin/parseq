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
import static com.linkedin.parseq.function.Tuples.tuple;

import java.util.Arrays;
import java.util.List;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.collection.ParSeqCollection;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.function.Tuple2;


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

    //TODO early finish???

    List<String> first = Arrays.asList("http://www.bing.com", "http://www.yahoo.com");
    List<String> second = Arrays.asList("http://www.google.com", "https://duckduckgo.com/");

    Task<String> fanout = stage(first, new StringBuilder()).flatMap(resultBuilder -> stage(second, resultBuilder))
        .map(builder -> builder.toString());

    final Task<?> plan = fanout.andThen(System.out::println);

    engine.run(plan);

    plan.await();
    printTracingResults(plan);
  }

  private Task<StringBuilder> stage(final List<String> input, final StringBuilder resultBuilder) {
    return ParSeqCollection.fromValues(input)
        .mapTask(url -> (Task<Tuple2<String, String>>) fetchUrl(getService(), url).map(s -> tuple(url, s)))
        .fold(resultBuilder, (z, r) -> z.append(String.format("%10s => %s\n", r._1(), r._2())));
  }
}
