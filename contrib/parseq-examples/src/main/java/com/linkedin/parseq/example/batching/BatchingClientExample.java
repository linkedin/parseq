/* $Id$ */
package com.linkedin.parseq.example.batching;

import java.util.List;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.BatchEntry;
import com.linkedin.parseq.batching.BatchingStrategy;
import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.promise.Promises;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class BatchingClientExample extends AbstractExample {

  public static class ExampleBatchingStrategy extends BatchingStrategy<Long, Long> {

    @Override
    public Long groupForKey(Long key) {
      //lets say all tasks can be grouped into one batch
      return 0L;
    }

    @Override
    public Task<?> taskForBatch(final Long group, final List<? extends BatchEntry<Long, Object>> batch) {
      return Task.action("batchExecute", () -> {
        System.out.println("batch: " + batch);
        batch.forEach(entry -> entry.getPromise().done("value for id + " + entry.getKey()));
      });
    }
  }

  final ExampleBatchingStrategy batchingStrategy = new ExampleBatchingStrategy();

  Task<String> batchableTask(final Long id) {
    return batchingStrategy.batchable("fetch id: " + id, () -> {
      return new BatchEntry<Long, String>(id, Promises.settable());
    });
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
