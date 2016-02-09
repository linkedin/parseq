package com.linkedin.parseq.batching;

import java.util.Map;
import java.util.Set;

import com.linkedin.parseq.Context;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.function.Try;

/**
 * This is a base class for a batching strategy that leverages existing Task-based API.
 *
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 * @param <G> Type of a Group
 * @param <K> Type of a Key
 * @param <T> Type of a Value
 *
 * @see BatchingStrategy
 * @see SimpleTaskBasedBatchingStrategy
 */
public abstract class TaskBasedBatchingStrategy<G, K, T> extends BatchingStrategy<G, K, T> {

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

  public abstract Task<Map<K, Try<T>>> taskForBatch(G group, Set<K> keys);

}
