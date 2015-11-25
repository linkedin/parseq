package com.linkedin.parseq.batching;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.parseq.function.Tuple2;
import com.linkedin.parseq.function.Tuples;
import com.linkedin.parseq.promise.SettablePromise;

public class RecordingStrategy<G, K, T> extends BatchingStrategy<G, K, T> {

  final List<K> _classifiedKeys = new ArrayList<>();
  final List<Batch<K, T>> _executedBatches = new ArrayList<>();
  final List<Tuple2<K, BatchEntry<T>>> _executedSingletons = new ArrayList<>();

  final BiConsumer<K, SettablePromise<T>> _completer;
  final Function<K, G> _classifier;

  public RecordingStrategy(BiConsumer<K, SettablePromise<T>> completer,Function<K, G> classifier) {
    _completer = completer;
    _classifier = classifier;
  }

  @Override
  public void executeBatch(G group, Batch<K, T> batch) {
    _executedBatches.add(batch);
    batch.foreach(_completer);
  }

  @Override
  public void executeSingleton(G group, K key, BatchEntry<T> entry) {
    _executedSingletons.add(Tuples.tuple(key, entry));
    _completer.accept(key, entry.getPromise());
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

  public List<Tuple2<K, BatchEntry<T>>> getExecutedSingletons() {
    return _executedSingletons;
  }

}