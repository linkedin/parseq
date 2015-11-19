/* $Id$ */
package com.linkedin.parseq.example.batching;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.parseq.batching.BatchingStrategy;
import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class BatchingClientExample extends AbstractExample {


  static class KVStore {
    String get(Long key) {
      return String.valueOf(key);
    }
    Map<Long, String> batchGet(Collection<Long> keys) {
      return keys.stream().collect(Collectors.toMap(key -> key, key -> get(key)));
    }
  }

  public static class BatchingKVStoreClient extends BatchingStrategy<Integer, Long, String> {
    private final KVStore _store;

    public BatchingKVStoreClient(KVStore store) {
      _store = store;
    }

    @Override
    public void executeBatch(Integer group, Batch<Long, String> batch) {
      Map<Long, String> batchResult = _store.batchGet(batch.keys());
      batch.foreach((key, promise) -> promise.done(batchResult.get(key)));
    }

    @Override
    public void executeSingleton(Integer group, Long key, BatchEntry<String> entry) {
      entry.getPromise().done(_store.get(key));
    }

    @Override
    public Integer classify(Long entry) {
      return 0;
    }
  }

  final KVStore store = new KVStore();

  final BatchingKVStoreClient batchingStrategy = new BatchingKVStoreClient(store);

  Task<String> batchableTask(final Long id) {
    return batchingStrategy.batchable("fetch id: " + id, id);
  }

  Task<String> nonBatchableTask(final Long id) {
    return Task.callable("fetch id: " + id, () -> store.get(id));
  }

  Task<String> branch(final Function<Long, Task<String>> client, Long base) {
    return client.apply(base).flatMap(x -> Task.par(client.apply(base + 1), client.apply(base + 2))).flatMap(x -> client.apply(base + 3));
  }

  Task<?> plan(final Function<Long, Task<String>> client) {
    return Task.par(branch(client, 1L), branch(client, 5L), branch(client, 7L));
  }

  @Override
  protected void customizeEngine(com.linkedin.parseq.EngineBuilder engineBuilder) {
    BatchingSupport batchingSupport = new BatchingSupport();
    batchingSupport.registerStrategy(batchingStrategy);
    engineBuilder.setPlanDeactivationListener(batchingSupport);
  };

  public static void main(String[] args) throws Exception {
    new BatchingClientExample().runExample();
  }



  @Override
  protected void doRunExample(final Engine engine) throws Exception {

    final Task<?> nonBatchable = plan(this::nonBatchableTask);
    engine.run(nonBatchable);
    nonBatchable.await();
    System.out.println("not batched:");
    ExampleUtil.printTracingResults(nonBatchable);

    System.out.println("batched:");
    final Task<?> batchable = plan(this::batchableTask);
    engine.run(batchable);
    batchable.await();
    ExampleUtil.printTracingResults(batchable);

  }



}
