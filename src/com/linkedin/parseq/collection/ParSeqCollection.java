package com.linkedin.parseq.collection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Consumer1;
import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Function2;

import java.util.function.Predicate;

import com.linkedin.parseq.collection.async.AsyncCollection;
import com.linkedin.parseq.collection.async.Subscriber;
import com.linkedin.parseq.Task;


/**
 * TODO proposals
 * mapTask -> mapWith
 * + filterWith
 * + flatten
 * within -> takeWithin
 */

public interface ParSeqCollection<T> {

  //transformations

  public <A> ParSeqCollection<A> map(final Function1<T, A> f);

  public ParSeqCollection<T> forEach(final Consumer1<T> consumer);

  public ParSeqCollection<T> filter(final Predicate<T> predicate);

  public ParSeqCollection<T> take(final int n);

  public ParSeqCollection<T> distinct();

  public ParSeqCollection<T> sorted(Comparator<? super T> comparator);

  public ParSeqCollection<T> takeWhile(final Predicate<T> predicate);

  public ParSeqCollection<T> drop(final int n);

  public ParSeqCollection<T> dropWhile(final Predicate<T> predicate);

  public ParSeqCollection<T> within(final long time, final TimeUnit unit);

  public <A> ParSeqCollection<A> mapTask(final Function1<T, Task<A>> f);

  public <A> ParSeqCollection<A> flatMap(final Function1<T, ParSeqCollection<A>> f);

  public <K> ParSeqCollection<GroupedParSeqCollection<K, T>> groupBy(final Function1<T, K> classifier);

  //operations

  public <Z> Task<Z> fold(final Z zero, final Function2<Z, T, Z> op);

  public Task<T> first();

  public Task<T> last();

  public Task<T> max(Comparator<? super T> comparator);

  public Task<T> min(Comparator<? super T> comparator);

  public Task<List<T>> toList();

  public Task<T> reduce(final Function2<T, T, T> op);

  public Task<T> find(final Predicate<T> predicate);

  public Task<Integer> count();

  public Task<?> task();

  //streaming

  public Task<?> subscribe(Subscriber<T> subscriber);

  //------------------- static factory methods -------------------

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
