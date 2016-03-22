package com.linkedin.parseq.batching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;

import com.linkedin.parseq.internal.ArgumentUtil;
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
  public int failAll(Throwable error) {
    int alreadyResolved = 0;
    for (Entry<K, BatchEntry<T>> entry: _map.entrySet()) {
      try {
        entry.getValue().getPromise().fail(error);
      } catch (PromiseResolvedException e) {
        alreadyResolved++;
      }
    }
    return alreadyResolved;
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

  public static class BatchEntry<T> {

    private final SettablePromise<T> _promise;
    private final List<ShallowTraceBuilder> _shallowTraceBuilders = new ArrayList<>();
    private final long _creationTimeNano = System.nanoTime();

    public BatchEntry(ShallowTraceBuilder shallowTraceBuilder, SettablePromise<T> promise) {
      _promise = promise;
      _shallowTraceBuilders.add(shallowTraceBuilder);
    }

    public SettablePromise<T> getPromise() {
      return _promise;
    }

    List<ShallowTraceBuilder> getShallowTraceBuilders() {
      return _shallowTraceBuilders;
    }

    void addShallowTraceBuilder(final ShallowTraceBuilder shallowTraceBuilder) {
      _shallowTraceBuilders.add(shallowTraceBuilder);
    }

    void addShallowTraceBuilders(final List<ShallowTraceBuilder> shallowTraceBuilders) {
      _shallowTraceBuilders.addAll(shallowTraceBuilders);
    }
  }

  static class BatchBuilder<K, T> {

    private final Map<K, BatchEntry<T>> _map = new HashMap<>();
    private Batch<K, T> _batch = null;
    private final int _maxSize;
    private final BatchAggregationTimeMetric _batchAggregationTimeMetric;

    public BatchBuilder(int maxSize, BatchAggregationTimeMetric batchAggregationTimeMetric) {
      ArgumentUtil.requirePositive(maxSize, "max batch size");
      _maxSize = maxSize;
      _batchAggregationTimeMetric = batchAggregationTimeMetric;
    }

    BatchBuilder<K, T> add(K key, BatchEntry<T> entry) {
      if (_batch != null) {
        throw new IllegalStateException("BatchBuilder has already been used to build a batch");
      }
      if (isFull()) {
        throw new IllegalStateException("BatchBuilder is full, max size: " + _maxSize);
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

    BatchBuilder<K, T> add(K key, ShallowTraceBuilder traceBuilder, SettablePromise<T> promise) {
      return add(key, new BatchEntry<>(traceBuilder, promise));
    }

    public boolean isFull() {
      return _map.size() == _maxSize;
    }

    public Batch<K, T> build() {
      if (_batch == null) {
        final long _currentTimeNano = System.nanoTime();
        _map.values().forEach(entry -> {
          final long time = _currentTimeNano - entry._creationTimeNano;
          _batchAggregationTimeMetric.record(time > 0 ? time : 0);
        });
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
  public Set<Entry<K, BatchEntry<T>>> entries() {
    return _map.entrySet();
  }

}
