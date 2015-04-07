package com.linkedin.parseq;

import java.util.NoSuchElementException;

import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.internal.SystemHiddenTask;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;


/**
 * {@code TaskOrValue} represents either {@link Task} or a value.
 * This interface allows calling {@link #map(Function) map} and
 * {@link #flatMap(Function) flatMap} regardless of the underlying type.
 *
 * <p>
 * This interface is experimental and may be modified or removed in future
 * versions.
 * @author jodzga
 */
public interface TaskOrValue<T> {

  public boolean isTask();

  public T getValue();

  public Task<T> getTask();

  public <A> TaskOrValue<A> map(final Function1<T, A> func);

  public <A> TaskOrValue<A> mapTask(final Function1<T, Task<A>> f);

  public <A> TaskOrValue<A> flatMap(final Function1<T, TaskOrValue<A>> f);

  public static <A> TaskOrValue<A> task(Task<A> task) {
    ArgumentUtil.requireNotNull(task, "task");
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
    public <A> TaskOrValue<A> map(Function1<T, A> func) {
      return task(_task.map(func));
    }

    @Override
    public <A> TaskOrValue<A> mapTask(Function1<T, Task<A>> f) {
      return task(_task.flatMap(f));
    }

    @Override
    public <A> TaskOrValue<A> flatMap(Function1<T, TaskOrValue<A>> f) {
      return task(mapOrFlatMap(f));
    }

    /**
     * This method behaves as a {@link #map(String, Function) map} or {@link #flatMap(String, Function) flatMap}
     * depending on result of given function. If given function returns a value then this
     * method behaves like a <code>map</code>. If the function returns a Task then this method
     * behaves like a <code>flatMap</code>. Example:
     * <pre><code>
     * {@code
     *  // cache of user names
     *  Map{@code <Long, String>} userNamesCache = new HashMap{@code <>}();
     *
     *  Task{@code <Long>} idTask = Task.value(1223L);
     *
     *  Task{@code <String>} userName = idTask.mapOrFlatMap("get user name", id -> {
     *    if (userNamesCache.containsKey(id)) {
     *      return TaskOrValue.value(userNamesCache.get(id));
     *    } else {
     *      return TaskOrValue.task(fetch(id));
     *    }
     *  });
     * </code></pre>
     * @param desc description of a mapping function, it will show up in a trace
     * @param func function to be applied to successful result of this Task which returns
     * instance of a {@link TaskOrValue}.
     * @return a new Task which will apply given function on result of successful completion of this task
     * to get instance of a {@link TaskOrValue} and act as a <code>map</code> or <code>flatMap></code>
     * depending on it's value
     * @see #map(String, Function) map
     * @see #flatMap(String, Function) flatMap
     * @see {@link TaskOrValue}
     */
    private <R> Task<R> mapOrFlatMap(final Function1<T, TaskOrValue<R>> func) {
      return new SystemHiddenTask<R>("flatMap") {
        @Override
        protected Promise<R> run(Context context) throws Throwable {
          final SettablePromise<R> result = Promises.settable();
          context.after(_task).run(new SystemHiddenTask<R>("flatMap") {
            @Override
            protected Promise<R> run(Context context) throws Throwable {
              try {
                TaskOrValue<R> taskOrValueR = func.apply(_task.get());
                if (taskOrValueR.isTask()) {
                  Task<R> taskR = taskOrValueR.getTask();
                  Promises.propagateResult(taskR, result);
                  context.run(taskR);
                  return taskR;
                } else {
                  Promise<R> valueR = Promises.value(taskOrValueR.getValue());
                  Promises.propagateResult(valueR, result);
                  return valueR;
                }
              } catch (Throwable t) {
                result.fail(t);
                return Promises.error(t);
              }
            }
          });
          context.run(_task);
          return result;
        }
      };
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
    public <A> TaskOrValue<A> map(Function1<T, A> func) {
      try {
        return value(func.apply(getValue()));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public <A> TaskOrValue<A> mapTask(Function1<T, Task<A>> func) {
      try {
        return task(func.apply(getValue()));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public <A> TaskOrValue<A> flatMap(Function1<T, TaskOrValue<A>> func) {
      try {
        return func.apply(getValue());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

  }

}
