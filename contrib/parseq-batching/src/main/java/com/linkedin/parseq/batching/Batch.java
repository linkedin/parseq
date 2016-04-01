package com.linkedin.parseq.batching;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import com.linkedin.parseq.batching.BatchImpl.BatchBuilder;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseResolvedException;
import com.linkedin.parseq.promise.SettablePromise;

/**
 * Batch represents a collection of keys related to each other in such a way that
 * it is more efficient to compute values for those keys in bulk than computing value
 * for each key individually.
 * This class contains methods helpful in implementing bulk operation that completes
 * Promises associated with keys.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 * @param <K> Type of a Key
 * @param <T> Type of a Value
 */
public interface Batch<K, T> {

  /**
   * Performs the given action for each element of the batch
   * until all elements have been processed or the action throws an
   * exception. Order in which elements are processed is unspecified.
   * Exceptions thrown by the action are relayed to the caller.
   * @param consumer action to be performed in each element of the batch
   */
  void foreach(BiConsumer<K, SettablePromise<T>> consumer);

  /**
   * Returns set of keys belonging to this batch.
   * @return set of keys belonging to this batch
   */
  Set<K> keys();

  /**
   * Returns size of this batch.
   * @return size of this batch.
   */
  int size();

  /**
   * Completes a {@link Promise} associated with given key with
   * a value.
   * Throws PromiseResolvedException if Promise associated with given key has already been resolved.
   * @param key key that identifies a Promise to be completed
   * @param value value to complete Promise with
   * @throws PromiseResolvedException if Promise associated with given key has already been resolved
   */
  void done(K key, T value) throws PromiseResolvedException;


  /**
   * Fails a {@link Promise} associated with given key with
   * an error.
   * Throws PromiseResolvedException if Promise associated with given key has already been resolved.
   * @param key key that identifies a Promise to be completed
   * @param error error to fail Promise with
   * @throws PromiseResolvedException if Promise associated with given key has already been resolved
   */
  void fail(K key, Throwable error) throws PromiseResolvedException;

  /**
   * Fails all promises belonging to this batch with given error.
   * If a promise belonging to this batch has already been completed then it is
   * ignored by this method.
   * This method guarantees that after it returns each Promise in this batch is either completed or failed.
   * @param error error that all promises belonging to this batch will be failed with
   * @return number of promises that were not failed because the promise has already been resolved; if this
   * method returns {@code 0} it means that it successfully failed all promises belonging to this batch with
   * specified error
   */
  int failAll(Throwable error);

  Collection<BatchEntry<T>> values();

  Set<Map.Entry<K, BatchEntry<T>>> entries();

  static <K, T> BatchBuilder<K, T> builder(int maxSize, BatchAggregationTimeMetric batchAggregationTimeMetric) {
    return new BatchBuilder<>(maxSize, batchAggregationTimeMetric);
  }
}
