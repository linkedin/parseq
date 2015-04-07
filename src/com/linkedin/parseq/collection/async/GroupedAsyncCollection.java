package com.linkedin.parseq.collection.async;

import java.util.Optional;

import com.linkedin.parseq.collection.transducer.Transducer;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.TaskOrValue;


public class GroupedAsyncCollection<K, T, R> extends AsyncCollection<T, R> {

  private final K _key;

  public GroupedAsyncCollection(K key, Publisher<TaskOrValue<T>> source, Transducer<T, R> transducer,
      Optional<Task<?>> predecessor) {
    super(source, transducer, predecessor);
    _key = key;
  }

  public K getKey() {
    return _key;
  }

}
