package com.linkedin.parseq.batching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.function.Try;

public class RecordingTaskStrategy<G, K, T> extends TaskBatchingStrategy<G, K, T> {

  final List<K> _classifiedKeys = new ArrayList<>();
  final List<Set<K>> _batches = new ArrayList<>();
  final List<Set<K>> _singletons = new ArrayList<>();

  final Function<K, Try<T>> _completer;
  final Function<K, G> _classifier;

  public RecordingTaskStrategy(Function<K, Try<T>> completer,Function<K, G> classifier) {
    _completer = completer;
    _classifier = classifier;
  }

  @Override
  public G classify(K key) {
    _classifiedKeys.add(key);
    return _classifier.apply(key);
  }

  public List<K> getClassifiedKeys() {
    return _classifiedKeys;
  }

  public List<Set<K>> getExecutedBatches() {
    return _batches;
  }

  public List<Set<K>> getExecutedSingletons() {
    return _singletons;
  }

  @Override
  public Task<Map<K, Try<T>>> taskForBatch(final G group, final Set<K> keys) {
    return Task.callable("taskForBatch", () -> {
      if (keys.size() == 1) {
        _singletons.add(keys);
      } else {
        _batches.add(keys);
      }
      return keys.stream().collect(Collectors.toMap(Function.identity(), _completer));
    });
  }


}