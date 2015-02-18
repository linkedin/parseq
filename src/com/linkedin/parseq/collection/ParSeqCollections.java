package com.linkedin.parseq.collection;

import java.util.Arrays;

import com.linkedin.parseq.collection.async.AsyncCollection;
import com.linkedin.parseq.Task;

public class ParSeqCollections {

  private ParSeqCollections() {}

  public static <T> ParSeqCollection<T> fromTasks(final Iterable<Task<T>> tasks) {
    return AsyncCollection.fromTasks(tasks);
  }

  @SafeVarargs
  public static <T> ParSeqCollection<T> fromTasks(final Task<T>... tasks) {
    return AsyncCollection.fromTasks(Arrays.asList(tasks));
  }

  public static <T> ParSeqCollection<T> fromValues(final Iterable<T> input) {
    return AsyncCollection.fromValues(input);
  }

  @SafeVarargs
  public static <T> ParSeqCollection<T> fromValues(final T... input) {
    return AsyncCollection.fromValues(Arrays.asList(input));
  }

}
