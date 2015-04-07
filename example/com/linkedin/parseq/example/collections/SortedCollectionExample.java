/* $Id$ */
package com.linkedin.parseq.example.collections;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.collection.ParSeqCollection;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class SortedCollectionExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new SortedCollectionExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    List<Integer> numbers = Arrays.asList(10, 20, 5, 8, 21, 14);

    Task<?> task =
        ParSeqCollection.fromValues(numbers).sorted(Comparator.naturalOrder()).toList().andThen(System.out::println);

    engine.run(task);

    task.await();

    ExampleUtil.printTracingResults(task);

  }
}
