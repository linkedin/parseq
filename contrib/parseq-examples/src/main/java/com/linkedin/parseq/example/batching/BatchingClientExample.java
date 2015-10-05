/* $Id$ */
package com.linkedin.parseq.example.batching;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchingStrategy;
import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class BatchingClientExample extends AbstractExample {

  public static class ExampleBatchingStrategy extends BatchingStrategy<Long, String> {

    @Override
    public void executeBatch(Batch<Long, String> batch) {
      System.out.println("batch: " + batch);
      batch.foreach((key, promise) -> promise.done("value for id + " + key));
    }

  }


  final ExampleBatchingStrategy batchingStrategy = new ExampleBatchingStrategy();

  Task<String> batchableTask(final Long id) {
    return batchingStrategy.batchable("fetch id: " + id, id);
  }

  @Override
  protected void customizeEngine(com.linkedin.parseq.EngineBuilder engineBuilder) {
    BatchingSupport batchingSupport = new BatchingSupport();
    batchingSupport.registerStrategy(batchingStrategy);
    engineBuilder.setPlanActivityListener(batchingSupport);
  };

  public static void main(String[] args) throws Exception {
    new BatchingClientExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final Task<String> a = batchableTask(1L).flatMap(x -> Task.par(batchableTask(21L), batchableTask(22L))).flatMap(x -> batchableTask(3L));
    final Task<String> b = batchableTask(4L).flatMap(x -> Task.par(batchableTask(51L), batchableTask(52L))).flatMap(x -> batchableTask(6L));
    final Task<String> c = batchableTask(7L).flatMap(x -> Task.par(batchableTask(81L), batchableTask(82L))).flatMap(x -> batchableTask(9L));

    final Task<?> parFetch = Task.par(a, b, c);
    engine.run(parFetch);

    parFetch.await();

    ExampleUtil.printTracingResults(parFetch);
  }
}
