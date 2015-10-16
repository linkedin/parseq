package com.linkedin.parseq.batching;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import com.linkedin.parseq.batching.BatchImpl.BatchBuilder;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.parseq.promise.PromiseResolvedException;
import com.linkedin.parseq.promise.SettablePromise;

public interface Batch<K, T> {

  void foreach(BiConsumer<K, SettablePromise<T>> consumer);

  Set<K> keys();

  int size();

  void done(K key, T value) throws PromiseResolvedException;

  void fail(K key, Throwable error) throws PromiseResolvedException;

  void failAll(Throwable error) throws PromiseResolvedException;

  boolean failAllRemaining(Throwable error);

  Collection<BatchEntry<T>> values();

  Set<Map.Entry<K, BatchEntry<T>>> entires();

  static <K, T> BatchBuilder<K, T> builder() {
    return new BatchBuilder<>();
  }
}
