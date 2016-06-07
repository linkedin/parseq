package com.linkedin.parseq.batching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.promise.PromiseException;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.PromiseResolvedException;
import com.linkedin.parseq.promise.PromiseUnresolvedException;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.ShallowTraceBuilder;

public class BatchImpl<K, T> implements Batch<K, T> {

  private final Map<K, BatchEntry<T>> _map;
  private final int _batchSize;

  private BatchImpl(Map<K, BatchEntry<T>> map, int batchSize) {
    _map = map;
    _batchSize = batchSize;
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
  public void foreach(final BiConsumer<K, SettablePromise<T>> consumer) {
    _map.forEach((key, entry) -> consumer.accept(key, entry.getPromise()));
  }

  @Override
  public String toString() {
    return "BatchImpl [entries=" + _map + "]";
  }

  public static class BatchPromise<T> implements SettablePromise<T> {
    private final SettablePromise<T> _internal = Promises.settable();
    private final SettablePromise<T> _external = Promises.settable();

    @Override
    public T get() throws PromiseException {
      return _internal.get();
    }
    @Override
    public Throwable getError() throws PromiseUnresolvedException {
      return _internal.getError();
    }
    @Override
    public T getOrDefault(T defaultValue) throws PromiseUnresolvedException {
      return _internal.getOrDefault(defaultValue);
    }
    @Override
    public void await() throws InterruptedException {
      _internal.await();
    }
    @Override
    public boolean await(long time, TimeUnit unit) throws InterruptedException {
      return _internal.await(time, unit);
    }
    @Override
    public void addListener(PromiseListener<T> listener) {
      _external.addListener(listener);
    }
    @Override
    public boolean isDone() {
      return _internal.isDone();
    }
    @Override
    public boolean isFailed() {
      return _internal.isFailed();
    }
    @Override
    public void done(T value) throws PromiseResolvedException {
      _internal.done(value);
    }
    @Override
    public void fail(Throwable error) throws PromiseResolvedException {
      _internal.fail(error);
    }
    public void trigger() {
      Promises.propagateResult(_internal, _external);
    }
    public SettablePromise<T> getInternal() {
      return _internal;
    }
  }

  public static class BatchEntry<T> {

    private final BatchPromise<T> _promise;
    private final List<ShallowTraceBuilder> _shallowTraceBuilders = new ArrayList<>();
    private final long _creationTimeNano = System.nanoTime();

    public BatchEntry(ShallowTraceBuilder shallowTraceBuilder, BatchPromise<T> promise) {
      _promise = promise;
      _shallowTraceBuilders.add(shallowTraceBuilder);
    }

    public BatchPromise<T> getPromise() {
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
    private int _batchSize = 0;

    public BatchBuilder(int maxSize, BatchAggregationTimeMetric batchAggregationTimeMetric) {
      ArgumentUtil.requirePositive(maxSize, "max batch size");
      _maxSize = maxSize;
      _batchAggregationTimeMetric = batchAggregationTimeMetric;
    }

    private static final boolean safeToAddWithoutOverflow(int left, int right) {
      if (right > 0 ? left > Integer.MAX_VALUE - right
                    : left < Integer.MIN_VALUE - right) {
        return false;
      }
      return true;
    }

    /**
     * Adds a batch entry, returns true if adding was successful. Returns false if adding
     * was not successful. Adding will be successful if builder is currently empty or
     * the batch size after adding the entry not exceed max batch size.
     * Caller must check result of this operation.
     */
    boolean add(K key, BatchEntry<T> entry, int size) {
      if (_batch != null) {
        throw new IllegalStateException("BatchBuilder has already been used to build a batch");
      }
      if (_batchSize == 0 || (safeToAddWithoutOverflow(_batchSize, size) && _batchSize + size <= _maxSize)) {
        //de-duplication
        BatchEntry<T> duplicate = _map.get(key);
        if (duplicate != null) {
          Promises.propagateResult(duplicate.getPromise().getInternal(), entry.getPromise());
          duplicate.getPromise().addListener(p -> entry.getPromise().trigger());
          duplicate.addShallowTraceBuilders(entry.getShallowTraceBuilders());
        } else {
          _map.put(key, entry);
        }
        //this will not overflow
        _batchSize += size;
        return true;
      } else {
        return false;
      }
    }

    /**
     * Adds a batch entry, returns true if adding was successful. Returns false if adding
     * was not successful.
     * Caller must check result of this operation.
     */
    boolean add(K key, ShallowTraceBuilder traceBuilder, BatchPromise<T> promise, int size) {
      return add(key, new BatchEntry<>(traceBuilder, promise), size);
    }

    public boolean isFull() {
      return _batchSize >= _maxSize;
    }

    public Batch<K, T> build() {
      if (_batch == null) {
        final long _currentTimeNano = System.nanoTime();
        _map.values().forEach(entry -> {
          final long time = _currentTimeNano - entry._creationTimeNano;
          _batchAggregationTimeMetric.record(time > 0 ? time : 0);
        });
        _batch = new BatchImpl<>(_map, _batchSize);
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

  @Override
  public int keysSize() {
    return _map.size();
  }

  @Override
  public int batchSize() {
    return _batchSize;
  }

}
