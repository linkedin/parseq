package com.linkedin.parseq.batching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import com.linkedin.parseq.promise.PromiseResolvedException;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.ShallowTraceBuilder;

public class BatchImpl<K, T> implements Batch<K, T> {

  private final Map<K, BatchEntry<T>> _map;

  private BatchImpl(Map<K, BatchEntry<T>> map) {
    _map = map;
  }

  @Override
  public void done(K key, T value) throws PromiseResolvedException {
    _map.get(key).getPromise().done(value);
  }

  @Override
  public void fail(K key, Throwable error) throws PromiseResolvedException {
    _map.get(key).getPromise().fail(error);
  }

  @Override
  public void failAll(Throwable error) throws PromiseResolvedException {
    PromiseResolvedException exception = null;
    for (BatchEntry<T> entry: _map.values()) {
      try {
        entry.getPromise().fail(error);
      } catch (PromiseResolvedException e) {
        exception = e;
      }
    }
    if (exception != null) {
      throw exception;
    }
  }

  @Override
  public Set<K> keys() {
    return _map.keySet();
  }

  @Override
  public int size() {
    return _map.size();
  }

  @Override
  public void foreach(final BiConsumer<K, SettablePromise<T>> consumer) {
    _map.forEach((key, entry) -> consumer.accept(key, entry.getPromise()));
  }

  @Override
  public String toString() {
    return "BatchImpl [entries=" + _map + "]";
  }

  @Override
  public boolean failAllRemaining(Throwable error) {
    try {
      failAll(error);
      return true;
    } catch (PromiseResolvedException e) {
      return false;
    }
  }

  public static class BatchEntry<T> {

    private final SettablePromise<T> _promise;
    private final List<ShallowTraceBuilder> _shallowTraceBuilders = new ArrayList<>();

    public BatchEntry(ShallowTraceBuilder shallowTraceBuilder, SettablePromise<T> promise) {
      _promise = promise;
      _shallowTraceBuilders.add(shallowTraceBuilder);
    }

    public SettablePromise<T> getPromise() {
      return _promise;
    }

    public List<ShallowTraceBuilder> getShallowTraceBuilders() {
      return _shallowTraceBuilders;
    }

    public void addShallowTraceBuilder(final ShallowTraceBuilder shallowTraceBuilder) {
      _shallowTraceBuilders.add(shallowTraceBuilder);
    }

    public void addShallowTraceBuilders(final List<ShallowTraceBuilder> shallowTraceBuilders) {
      _shallowTraceBuilders.addAll(shallowTraceBuilders);
    }

  }

  static class BatchBuilder<K, T> {

    private final Map<K, BatchEntry<T>> _map = new HashMap<>();
    private Batch<K, T> _batch = null;

    public BatchBuilder<K, T> add(K key, BatchEntry<T> entry) {
      if (_batch != null) {
        throw new IllegalStateException("BatchBuilder has already been used to build a batch");
      }
      //deduplication
      BatchEntry<T> duplicate = _map.get(key);
      if (duplicate != null) {
        Promises.propagateResult(duplicate.getPromise(), entry.getPromise());
        duplicate.addShallowTraceBuilders(entry.getShallowTraceBuilders());
      } else {
        _map.put(key, entry);
      }
      return this;
    }

    public BatchBuilder<K, T> add(K key, ShallowTraceBuilder traceBuilder, SettablePromise<T> promise) {
      return add(key, new BatchEntry<>(traceBuilder, promise));
    }


    public Batch<K, T> build() {
      if (_batch == null) {
        _batch = new BatchImpl<>(_map);
      }
      return _batch;
    }

    public int size() {
      return _map.size();
    }
  }

  @Override
  public Collection<BatchEntry<T>> values() {
    return _map.values();
  }

  @Override
  public Set<Entry<K, BatchEntry<T>>> entires() {
    return _map.entrySet();
  }

}
