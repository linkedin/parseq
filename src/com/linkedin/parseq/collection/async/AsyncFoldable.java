package com.linkedin.parseq.collection.async;

import java.util.Optional;

import com.linkedin.parseq.collection.transducer.Foldable;
import com.linkedin.parseq.collection.transducer.Reducer;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.TaskOrValue;


public class AsyncFoldable<Z, T> implements Foldable<Z, T, Task<Z>> {

  private final Publisher<TaskOrValue<T>> _input;
  private final Optional<Task<?>> _predecessor;

  public AsyncFoldable(Publisher<TaskOrValue<T>> input, Optional<Task<?>> predecessor) {
    _input = input;
    _predecessor = predecessor;
  }

  @Override
  public Task<Z> fold(final String name, final Z zero, final Reducer<Z, T> reducer) {
    return new AsyncFoldTask<Z, T>(name, _input, zero, reducer, _predecessor);
  }

}
