/* $Id$ */
package com.linkedin.parseq.example.simple;

import static com.linkedin.parseq.example.common.ExampleUtil.callService;
import static com.linkedin.parseq.example.common.ExampleUtil.printTracingResults;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.MockService;
import com.linkedin.parseq.example.common.SimpleMockRequest;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class BranchSkippedExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new BranchSkippedExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final MockService<Integer> serviceX = getService();
    final Task<Integer> fetchX = fetchX(serviceX, 420);

    final Task<Integer> bigX = fetchX.flatMap("make x >= 42", x -> {
      if (x < 42) {
        final int toAdd = 42 - x;
        return add(x, toAdd);
      } else {
        return Task.value("x", x);
      }
    } );

    engine.run(bigX);

    bigX.await();

    System.out.println("Resulting value: " + bigX.get());

    printTracingResults(bigX);
  }

  private static Task<Integer> add(final int x, final int toAdd) {
    return Task.callable("add " + toAdd, () -> x + toAdd);
  }

  private Task<Integer> fetchX(final MockService<Integer> serviceX, final int x) {
    return callService("fetch x (x := " + x + ")", serviceX, new SimpleMockRequest<Integer>(10, x), x);
  }
}
