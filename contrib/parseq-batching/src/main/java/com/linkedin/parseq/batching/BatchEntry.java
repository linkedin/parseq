package com.linkedin.parseq.batching;

import com.linkedin.parseq.promise.SettablePromise;

public class BatchEntry<K, T> {

  private final K _key;
  private final SettablePromise<T> _promise;

  public BatchEntry(K key, SettablePromise<T> promise) {
    _key = key;
    _promise = promise;
  }

  public K getKey() {
    return _key;
  }

  public SettablePromise<T> getPromise() {
    return _promise;
  }

}
