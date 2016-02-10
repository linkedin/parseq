/* $Id$ */
package com.linkedin.parseq.example.batching;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.batching.SimpleTaskBasedBatchingStrategy;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TaskBasedBatchingClientExample extends AbstractExample {


  static class KVStore {
    Task<String> get(Long key) {
      return Task.callable("get key: " + key, () -> String.valueOf(key));
    }
    Task<Map<Long, Try<String>>> batchGet(Collection<Long> keys) {
      return Task.callable("batchGet",
          () -> keys.stream().collect(Collectors.toMap(Function.identity(), key -> Success.of(Long.toString(key)))));
    }
  }

  public static class BatchingKVStoreClient extends SimpleTaskBasedBatchingStrategy<Long, String> {
    private final KVStore _store;

    public BatchingKVStoreClient(KVStore store) {
      _store = store;
    }

    @Override
    public Task<Map<Long, Try<String>>> taskForBatch(Set<Long> keys) {
      return _store.batchGet(keys);
    }
  }

  final KVStore store = new KVStore();

  final BatchingKVStoreClient batchingStrategy = new BatchingKVStoreClient(store);

  Task<String> batchableTask(final Long id) {
    return batchingStrategy.batchable("fetch id: " + id, id);
  }

  Task<String> nonBatchableTask(final Long id) {
    return store.get(id);
  }

  Task<String> branch(final Function<Long, Task<String>> client, Long base) {
    return client.apply(base).flatMap("first", x -> Task.par(client.apply(base + 1), client.apply(base + 2))).flatMap("second", x -> client.apply(base + 3));
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
    new TaskBasedBatchingClientExample().runExample();
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
