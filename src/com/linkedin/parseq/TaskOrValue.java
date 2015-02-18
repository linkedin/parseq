package com.linkedin.parseq;

import java.util.NoSuchElementException;
import java.util.function.Function;

import com.linkedin.parseq.util.Objects;


/**
 * consider folding TaskOrValue into Task i.e.
 * value is a special case of a task which has been resolved.
 *
 * @author jodzga
 *
 * @param <T>
 */
public class TaskOrValue<T> {

  private final Task<T> _task;
  private final T _value;

  private TaskOrValue(Task<T> task, T value) {
    _task = task;
    _value = value;
  }

  public boolean isTask() {
    return _task != null;
  }

  public T getValue() {
    if (isTask()) {
      throw new NoSuchElementException();
    } else {
      return _value;
    }
  }

  public Task<T> getTask() {
    if (isTask()) {
      return _task;
    } else {
      throw new NoSuchElementException();
    }
  }

  public static <A> TaskOrValue<A> task(Task<A> task) {
    Objects.requireNonNull(task);
    return new TaskOrValue<A>(task, null);
  }

  public static <T> TaskOrValue<T> value(T value) {
    return new TaskOrValue<T>(null, value);
  }

  public <A> TaskOrValue<A> map(final Function<T, A> f) {
    if (isTask()) {
      return task(getTask().map(f));
    } else {
      return value(f.apply(getValue()));
    }
  }

  public <A> TaskOrValue<A> mapTask(final Function<T, Task<A>> f) {
    if (isTask()) {
      return task(getTask().flatMap(f));
    } else {
      return task(f.apply(getValue()));
    }
  }

  public <A> TaskOrValue<A> flatMap(final Function<T, TaskOrValue<A>> f) {
    if (isTask()) {
      return task(getTask().mapOrFlatMap(f));
    } else {
      return f.apply(getValue());
    }
  }

//  private static <Z, A> Reducer<Z, A> lower(final Reducer<Task<Z>, TaskOrValue<A>> r) {
//    return null;
//  }
//
//  private static <Z, A> Reducer<Task<Z>, TaskOrValue<A>> lift(final Reducer<Z, A> r) {
//    return null;
//  }
//
//  public static <A, B> Transducer<TaskOrValue<A>, TaskOrValue<B>> lift(final Transducer<A, B> transducer) {
//    return ftovb -> lift(transducer.apply(lower(ftovb)));
//  }
}
