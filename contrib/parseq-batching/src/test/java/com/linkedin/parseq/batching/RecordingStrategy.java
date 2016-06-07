package com.linkedin.parseq.batching;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.linkedin.parseq.promise.SettablePromise;

public class RecordingStrategy<G, K, T> extends BatchingStrategy<G, K, T> {

  final List<K> _classifiedKeys = new ArrayList<>();
  final List<Batch<K, T>> _executedBatches = new ArrayList<>();
  final List<Batch<K, T>> _executedSingletons = new ArrayList<>();

  final BiConsumer<K, SettablePromise<T>> _completer;
  final Function<K, G> _classifier;

  public RecordingStrategy(BiConsumer<K, SettablePromise<T>> completer,Function<K, G> classifier) {
    _completer = completer;
    _classifier = classifier;
  }

  @Override
  public void executeBatch(G group, Batch<K, T> batch) {
    if (batch.keysSize() == 1) {
      _executedSingletons.add(batch);
    } else {
      _executedBatches.add(batch);
    }
    batch.foreach(_completer);
  }

  @Override
  public G classify(K key) {
    _classifiedKeys.add(key);
    return _classifier.apply(key);
  }

  public List<K> getClassifiedKeys() {
    return _classifiedKeys;
  }

  public List<Batch<K, T>> getExecutedBatches() {
    return _executedBatches;
  }

  public List<Batch<K, T>> getExecutedSingletons() {
    return _executedSingletons;
  }

}