/* $Id$ */
package com.linkedin.parseq.example.legacy.simple;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.MockService;
import com.linkedin.parseq.example.common.SimpleMockRequest;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;

import java.util.concurrent.Callable;

import static com.linkedin.parseq.Tasks.seq;
import static com.linkedin.parseq.Tasks.callable;
import static com.linkedin.parseq.example.common.ExampleUtil.callService;
import static com.linkedin.parseq.example.common.ExampleUtil.printTracingResults;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class BranchExecutedExample extends AbstractExample {
  public static void main(String[] args) throws Exception {
    new BranchExecutedExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final MockService<Integer> serviceX = getService();

    final Task<Integer> fetchX = fetchX(serviceX, 24);
    final Task<Integer> enlargeX = new BaseTask<Integer>("make x >= 42") {
      @Override
      protected Promise<? extends Integer> run(final Context context) throws Exception {
        final int x = fetchX.get();
        if (x < 42) {
          final int toAdd = 42 - x;
          final Task<Integer> addTo42 = add(x, toAdd);
          context.run(addTo42);
          return addTo42;
        }
        return Promises.value(x);
      }
    };

    final Task<Integer> bigX = seq(fetchX, enlargeX);
    engine.run(bigX);

    bigX.await();

    System.out.println("Resulting value: " + bigX.get());

    printTracingResults(bigX);
  }

  private static Task<Integer> add(final int x, final int toAdd) {
    return callable("add " + toAdd, new Callable<Integer>() {
      @Override
      public Integer call() {
        return x + toAdd;
      }
    });
  }

  private Task<Integer> fetchX(final MockService<Integer> serviceX, final int x) {
    return callService("fetch x (x := " + x + ")", serviceX, new SimpleMockRequest<Integer>(10, x));
  }
}
