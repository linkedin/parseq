package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Consumer1;
import com.linkedin.parseq.function.Consumer7;
import com.linkedin.parseq.function.Function7;
import com.linkedin.parseq.function.Tuple7;

public interface Tuple7Task<T1, T2, T3, T4, T5, T6, T7> extends Task<Tuple7<T1, T2, T3, T4, T5, T6, T7>> {

  /**
   * Equivalent to {@code map("map", f)}.
   * @see #map(String, Function7)
   */
  default <R> Task<R> map(final Function7<T1, T2, T3, T4, T5, T6, T7, R> f) {
    return map("map: " + _taskDescriptor.getDescription(f.getClass().getName()), tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7()));
  }

  /**
   * Creates a new task by applying a function to the successful result of this task.
   * Returned task will complete with value calculated by a function.
   * <blockquote><pre>
   * Task{@code <String>} hello = Task.value("Hello World");
   *
   * // this task will complete with value 11
   * Task{@code <Integer>} length = hello.map("length", s {@code ->} s.length());
   * </pre></blockquote>
   * <img src="doc-files/map-1.png" type="image/svg+xml" height="90px"/>
   * <p>
   * If this task is completed with an exception then the new task will also complete
   * with that exception.
   * <blockquote><pre>
   *  Task{@code <String>} failing = Task.callable("hello", () {@code ->} {
   *    return "Hello World".substring(100);
   *  });
   *
   *  // this task will fail with java.lang.StringIndexOutOfBoundsException
   *  Task{@code <Integer>} length = failing.map("length", s {@code ->} s.length());
   * </pre></blockquote>
   * <img src="doc-files/map-2.png" type="image/svg+xml" height="90px"/>
   *
   * @param <R> return type of function <code>func</code>
   * @param desc description of a mapping function, it will show up in a trace
   * @param f function to be applied to successful result of this task.
   * @return a new task which will apply given function on result of successful completion of this task
   */
  default <R> Task<R> map(final String desc, final Function7<T1, T2, T3, T4, T5, T6, T7, R> f) {
    return map(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7()));
  }

  /**
   * Equivalent to {@code flatMap("flatMap", f)}.
   * @see #flatMap(String, Function7)
   */
  default <R> Task<R> flatMap(final Function7<T1, T2, T3, T4, T5, T6, T7, Task<R>> f) {
    return flatMap("flatMap: " + _taskDescriptor.getDescription(f.getClass().getName()), tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7()));
  }

  /**
   * Creates a new task by applying a function to the successful result of this task and
   * returns the result of a function as the new task.
   * Returned task will complete with value calculated by a task returned by the function.
   * <blockquote><pre>
   *  Task{@code <URI>} url = Task.value("uri", URI.create("http://linkedin.com"));
   *
   *  // this task will complete with contents of a LinkedIn homepage
   *  // assuming fetch(u) fetches contents given by a URI
   *  Task{@code <String>} homepage = url.flatMap("fetch", u {@code ->} fetch(u));
   * </pre></blockquote>
   * <img src="doc-files/flatMap-1.png" type="image/svg+xml" height="90px"/>
   * <p>
   *
   * If this task is completed with an exception then the new task will also contain
   * that exception.
   * <blockquote><pre>
   *  Task{@code <URI>} url = Task.callable("uri", () {@code ->} URI.create("not a URI"));
   *
   *  // this task will fail with java.lang.IllegalArgumentException
   *  Task{@code <String>} homepage = url.flatMap("fetch", u {@code ->} fetch(u));
   * </pre></blockquote>
   * <img src="doc-files/flatMap-2.png" type="image/svg+xml" height="90px"/>
   * @param <R> return type of function <code>func</code>
   * @param desc description of a mapping function, it will show up in a trace
   * @param f function to be applied to successful result of this task which returns new task
   * to be executed
   * @return a new task which will apply given function on result of successful completion of this task
   * to get instance of a task which will be executed next
   */
  default <R> Task<R> flatMap(final String desc, final Function7<T1, T2, T3, T4, T5, T6, T7, Task<R>> f) {
    return flatMap(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7()));
  }

  /**
   * Equivalent to {@code andThen("andThen", consumer)}.
   * @see #andThen(String, Consumer7)
   */
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> andThen(final Consumer7<T1, T2, T3, T4, T5, T6, T7> consumer) {
    return cast(andThen("andThen: " + _taskDescriptor.getDescription(consumer.getClass().getName()), tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7())));
  }

  /**
   * Creates a new task which applies a consumer to the result of this task
   * and completes with a result of this task. It is used
   * in situations where consumer needs to be called after successful
   * completion of this task.
   * <blockquote><pre>
   *  Task{@code <String>} hello = Task.value("greeting", "Hello World");
   *
   *  // this task will print "Hello World"
   *  Task{@code <String>} sayHello = hello.andThen("say", System.out::println);
   * </pre></blockquote>
   * <img src="doc-files/andThen-1.png" type="image/svg+xml" height="90px"/>
   * <p>
   * If this task fails then consumer will not be called and failure
   * will be propagated to task returned by this method.
   * <blockquote><pre>
   *  Task{@code <String>} failing = Task.callable("greeting", () {@code ->} {
   *    return "Hello World".substring(100);
   *  });
   *
   *  // this task will fail with java.lang.StringIndexOutOfBoundsException
   *  Task{@code <String>} sayHello = failing.andThen("say", System.out::println);
   * </pre></blockquote>
   * <img src="doc-files/andThen-2.png" type="image/svg+xml" height="90px"/>
   *
   * @param desc description of a consumer, it will show up in a trace
   * @param consumer consumer of a value returned by this task
   * @return a new task which will complete with result of this task
   */
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> andThen(final String desc, final Consumer7<T1, T2, T3, T4, T5, T6, T7> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recover(final Function1<Throwable, Tuple7<T1, T2, T3, T4, T5, T6, T7>> f) {
    return cast(Task.super.recover(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recover(final String desc, final Function1<Throwable, Tuple7<T1, T2, T3, T4, T5, T6, T7>> f) {
    return cast(Task.super.recover(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recoverWith(final Function1<Throwable, Task<Tuple7<T1, T2, T3, T4, T5, T6, T7>>> f) {
    return cast(Task.super.recoverWith(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recoverWith(final String desc, final Function1<Throwable, Task<Tuple7<T1, T2, T3, T4, T5, T6, T7>>> f) {
    return cast(Task.super.recoverWith(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> onFailure(final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> onFailure(final String desc, final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(desc, consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> shareable() {
    return cast(Task.super.shareable());
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> withTimeout(final long time, final TimeUnit unit) {
    return cast(Task.super.withTimeout(time, unit));
  };

  /**
   * Equivalent to {@code withSideEffect("sideEffect", func)}.
   * @see #withSideEffect(String, Function7)
   */
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> withSideEffect(Function7<T1, T2, T3, T4, T5, T6, T7, Task<?>> func) {
    return cast(Task.super.withSideEffect("sideEffect: " + _taskDescriptor.getDescription(func.getClass().getName()), tuple -> func.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7())));
  }

  /**
   * Creates a new task that will run another task as a side effect once the primary task
   * completes successfully. The properties of side effect task are:
   * <ul>
   * <li>The side effect task will not be run if the primary task has not run e.g. due to
   * failure or cancellation.</li>
   * <li>The side effect does not affect returned task. It means that
   * failure of side effect task is not propagated to returned task.</li>
   * <li>The returned task is marked done once this task completes, even if
   * the side effect has not been run yet.</li>
   * </ul>
   * The side effect task is useful in situations where operation (side effect) should continue to run
   * in the background but it's execution is not required for the main computation. An example might
   * be updating cache once data has been retrieved from the main source.
   * <blockquote><pre>
   *  Task{@code <Long>} id = Task.value("id", 1223L);
   *
   *  // this task will be completed as soon as user name is fetched
   *  // by fetch() method and will not fail even if updateMemcache() fails
   *  Task{@code <String>} userName = id.flatMap("fetch", u {@code ->} fetch(u))
   *      .withSideEffect("update memcache", u {@code ->} updateMemcache(u));
   * </pre></blockquote>
   * <img src="doc-files/withSideEffect-1.png" type="image/svg+xml" height="120px"/>
   *
   * @param desc description of a side effect, it will show up in a trace
   * @param func function to be applied on result of successful completion of this task
   * to get side effect task
   * @return a new task that will run side effect task specified by given function upon succesful
   * completion of this task
   */
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> withSideEffect(final String desc, Function7<T1, T2, T3, T4, T5, T6, T7, Task<?>> func) {
    return cast(Task.super.withSideEffect(desc, tuple -> func.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7())));
  }

  public static <T1, T2, T3, T4, T5, T6, T7> Tuple7Task<T1, T2, T3, T4, T5, T6, T7> cast(final Task<Tuple7<T1, T2, T3, T4, T5, T6, T7>> task) {
    return new Tuple7TaskDelegate<>(task);
  }

}
