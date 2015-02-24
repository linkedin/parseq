package com.linkedin.parseq;

import java.util.NoSuchElementException;
import java.util.function.Function;

import com.linkedin.parseq.internal.ArgumentUtil;


/**
 * <code>TaskOrValue</code> represents either {@link Task} or a value.
 * This interface allows calling {@link #map(Function) map} and
 * {@link #flatMap(Function) flatMap} regardless of the underlying type.
 *
 * @author jodzga
 */
public interface TaskOrValue<T> {

  public boolean isTask();

  public T getValue();

  public Task<T> getTask();

  public <A> TaskOrValue<A> map(final Function<T, A> func);

  public <A> TaskOrValue<A> mapTask(final Function<T, Task<A>> f);

  public <A> TaskOrValue<A> flatMap(final Function<T, TaskOrValue<A>> f);

  public static <A> TaskOrValue<A> task(Task<A> task) {
    ArgumentUtil.requireNonNull(task);
    return new Tsk<>(task);
  }

  public static <A> TaskOrValue<A> value(A value) {
    return new Val<A>(value);
  }

  public static class Tsk<T> implements TaskOrValue<T> {

    private final Task<T> _task;

    public Tsk(Task<T> task) {
      _task = task;
    }

    @Override
    public boolean isTask() {
      return true;
    }

    @Override
    public T getValue() {
      throw new NoSuchElementException();
    }

    @Override
    public Task<T> getTask() {
      return _task;
    }

    @Override
    public <A> TaskOrValue<A> map(Function<T, A> func) {
      return task(_task.map(func));
    }

    @Override
    public <A> TaskOrValue<A> mapTask(Function<T, Task<A>> f) {
      return task(_task.flatMap(f));
    }

    @Override
    public <A> TaskOrValue<A> flatMap(Function<T, TaskOrValue<A>> f) {
      return task(_task.mapOrFlatMap(f));
    }

  }

  public static class Val<T> implements TaskOrValue<T> {

    private final T _value;

    public Val(T value) {
      _value = value;
    }

    @Override
    public boolean isTask() {
      return false;
    }

    @Override
    public T getValue() {
      return _value;
    }

    @Override
    public Task<T> getTask() {
      throw new NoSuchElementException();
    }

    @Override
    public <A> TaskOrValue<A> map(Function<T, A> func) {
      return value(func.apply(getValue()));
    }

    @Override
    public <A> TaskOrValue<A> mapTask(Function<T, Task<A>> func) {
      return task(func.apply(getValue()));
    }

    @Override
    public <A> TaskOrValue<A> flatMap(Function<T, TaskOrValue<A>> func) {
      return func.apply(getValue());
    }

  }

}
