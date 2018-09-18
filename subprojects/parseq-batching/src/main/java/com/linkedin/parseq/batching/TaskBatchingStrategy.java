package com.linkedin.parseq.batching;

import java.util.Map;
import java.util.Set;

import com.linkedin.parseq.Context;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.function.Try;

/**
 * This is a base class for a batching strategy that leverages existing Task-based API.
 * <p>
 * Example below shows how to build a ParSeq client for a key-value store that provides
 * transparent batching given existing Task-based API. Let's assume that we have an implementation
 * of the following key-value store interface:
 * <blockquote><pre>
 *  interface KVStore {
 *    Task{@code <String>} get(Long key);
 *    Task{@code <Map<Long, Try<String>>>} batchGet(Collection{@code <Long>} keys);
 *  }
 * </pre></blockquote>
 *
 * We can then implement a {@code TaskBatchingStrategy} in the following way (for the sake
 * of simplicity we assume that all keys can be grouped into one batch thus we implement
 * {@code SimpleTaskBatchingStrategy}):
 * <blockquote><pre>
 *  public static class BatchingKVStoreClient extends SimpleTaskBatchingStrategy{@code <Long, String>} {
 *    private final KVStore _store;
 *    public BatchingKVStoreClient(KVStore store) {
 *      _store = store;
 *    }
 *
 *    {@code @Override}
 *    public void executeBatch(Integer group, Batch{@code <Long, String>} batch) {
 *      Map{@code <Long, String>} batchResult = _store.batchGet(batch.keys());
 *      batch.foreach((key, promise) {@code ->} promise.done(batchResult.get(key)));
 *    }
 *
 *    {@code @Override}
 *    public {@code Task<Map<Long, Try<String>>>} taskForBatch(Set{@code <Long>} keys) {
 *      return _store.batchGet(keys);
 *    }
 *  }
 * </pre></blockquote>
 *
 * {@code taskForBatch} method returns a task that computes a map that for every key contains
 * either a success with a value or a failure. If returned map does not contain results for
 * some keys the tasks for which results are missing will fail.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 * @param <G> Type of a Group
 * @param <K> Type of a Key
 * @param <T> Type of a Value
 *
 * @see BatchingStrategy
 * @see SimpleTaskBatchingStrategy
 */
public abstract class TaskBatchingStrategy<G, K, T> extends BatchingStrategy<G, K, T> {

  @Override
  public final void executeBatch(G group, Batch<K, T> batch) {
    // This method should be unreachable because we also override executeBatchWithContext
    throw new IllegalStateException("This method should be unreachable");
  }

  @Override
  protected void executeBatchWithContext(final G group, final Batch<K, T> batch, final Context ctx) {
    Task<Map<K, Try<T>>> task = taskForBatch(group, batch.keys());

    Task<Map<K, Try<T>>> completing = task.andThen("completePromises", map -> {
      batch.foreach((key, promise) -> {
        Try<T> result = map.get(key);
        if (result != null) {
          if (result.isFailed()) {
            promise.fail(result.getError());
          } else {
            promise.done(result.get());
          }
        } else {
          promise.fail(new Exception("Result for key: " + key + " not found in batch response"));
        }
      });
    });
    completing.getShallowTraceBuilder().setSystemHidden(true);

    Task<Map<K, Try<T>>> withFailureHandling = completing.onFailure("handleFailures", t -> {
      batch.failAll(t);
    });
    withFailureHandling.getShallowTraceBuilder().setSystemHidden(true);

    ctx.run(withFailureHandling);
  }

  @Override
  final public String getBatchName(G group, Batch<K, T> batch) {
    return getBatchName(group, batch.keys());
  }

  /**
   * Overriding this method allows providing custom name for a batch. Name will appear in the
   * ParSeq trace as a description of the task that executes the batch.
   * @param keys set of keys belonging to the batch that needs to be described
   * @param group group to be described
   * @return name for the batch
   */
  public String getBatchName(G group, Set<K> keys) {
    return "batch(" + keys.size() + ")";
  }

  /**
   * This method will be called for every batch. It returns a map that for every key contains
   * either a success with a value or a failure. If returned map does not contain results for
   * some keys the tasks for which results are missing will fail.
   * @param group group that represents the batch
   * @param keys set of keys belonging to the batch
   * @return A map that for every key contains either a success with a value or a failure.
   * If returned map does not contain results for some keys the tasks for which results are missing will fail.
   */
  public abstract Task<Map<K, Try<T>>> taskForBatch(G group, Set<K> keys);

}
