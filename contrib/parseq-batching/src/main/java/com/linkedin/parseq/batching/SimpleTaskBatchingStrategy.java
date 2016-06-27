package com.linkedin.parseq.batching;

import java.util.Map;
import java.util.Set;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.SimpleBatchingStrategy.Group;
import com.linkedin.parseq.function.Try;
import static com.linkedin.parseq.batching.SimpleBatchingStrategy.ALL;

/**
 * A simple {@link TaskBatchingStrategy} that groups all keys into one batch.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 * @param <K> Type of a Key
 * @param <T> Type of a Value
  */
public abstract class SimpleTaskBatchingStrategy<K, T> extends TaskBatchingStrategy<SimpleBatchingStrategy.Group, K, T> {

  @Override
  final public Group classify(K key) {
    return ALL;
  }

  @Override
  public Task<Map<K, Try<T>>> taskForBatch(Group group, Set<K> keys) {
    return taskForBatch(keys);
  }

  @Override
  final public String getBatchName(Group group, Set<K> keys) {
    return getBatchName(keys);
  }

  /**
   * Overriding this method allows providing custom name for a batch. Name will appear in the
   * ParSeq trace as a description of the task that executes the batch.
   * @param batch set of keys belonging to the batch that needs to be described
   * @return name for the batch
   */
  public String getBatchName(Set<K> batch) {
    return super.getBatchName(ALL, batch);
  }

  /**
   * This method will be called for every batch. It returns a map that for every key contains
   * either a success with a value or a failure. If returned map does not contain results for
   * some keys the tasks for which results are missing will fail.
   * @param keys set of keys belonging to the batch
   * @return A map that for every key contains either a success with a value or a failure.
   * If returned map does not contain results for some keys the tasks for which results are missing will fail.
   */
  public abstract Task<Map<K, Try<T>>> taskForBatch(Set<K> keys);

}
