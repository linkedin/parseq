package com.linkedin.parseq.collection;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

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

  public <A> ParSeqCollection<A> map(final Function<T, A> f);

  public ParSeqCollection<T> forEach(final Consumer<T> consumer);

  public ParSeqCollection<T> filter(final Predicate<T> predicate);

  public ParSeqCollection<T> take(final int n);

  public ParSeqCollection<T> distinct();

  public ParSeqCollection<T> sorted(Comparator<? super T> comparator);

  public ParSeqCollection<T> takeWhile(final Predicate<T> predicate);

  public ParSeqCollection<T> drop(final int n);

  public ParSeqCollection<T> dropWhile(final Predicate<T> predicate);

  public ParSeqCollection<T> within(final long time, final TimeUnit unit);

  public <A> ParSeqCollection<A> mapTask(final Function<T, Task<A>> f);

  public <A> ParSeqCollection<A> flatMap(final Function<T, ParSeqCollection<A>> f);

  public <K> ParSeqCollection<GroupedParSeqCollection<K, T>> groupBy(final Function<T, K> classifier);

  //operations

  public <Z> Task<Z> fold(final Z zero, final BiFunction<Z, T, Z> op);

  public Task<T> first();

  public Task<T> last();

  public Task<T> max(Comparator<? super T> comparator);

  public Task<T> min(Comparator<? super T> comparator);

  public Task<List<T>> toList();

  public Task<T> reduce(final BiFunction<T, T, T> op);

  public Task<T> find(final Predicate<T> predicate);

  public Task<Integer> count();

  public Task<?> task();

  //streaming

  public Task<?> subscribe(Subscriber<T> subscriber);

}
