/* $Id$ */
package com.linkedin.parseq.example.collections;

import java.util.Arrays;
import java.util.List;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.Tasks;
import com.linkedin.parseq.collection.ParSeqCollection;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class SumExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new SumExample().runExample();
  }

  static final List<Integer> numbers = Arrays.asList(1, 2, 3);

  static Task<Integer> toTask(final Integer i) {
    //    if (true) throw new RuntimeException();
    return Task.callable("number", () -> i);
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    Task<?> task = ParSeqCollection.fromValues(numbers).mapTask(SumExample::toTask).reduce((a, b) -> a + b)
        .map(sum -> "result: " + sum).recover(e -> "error").andThen(System.out::println);

    engine.run(task);

    task.await();

    ExampleUtil.printTracingResults(task);
  }
}
