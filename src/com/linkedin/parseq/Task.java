/*
 * Copyright 2012 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.parseq;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import com.linkedin.parseq.function.Consumer1;
import com.linkedin.parseq.function.Failure;
import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromisePropagator;
import com.linkedin.parseq.promise.PromiseTransformer;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.TraceBuilder;

/**
 * A task represents a deferred execution that also contains its resulting
 * value. In addition, tasks include tracing information that can be
 * used with various trace printers.
 * <p/>
 * Tasks should be run using an {@link Engine}. They should not be run directly.
 * <p/>
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public interface Task<T> extends Promise<T>, Cancellable
{

  //------------------- interface definition -------------------

  /**
   * Returns the name of this task.
   *
   * @return the name of this task
   */
  public String getName();

  /**
   * Returns the priority for this task.
   *
   * @return the priority for this task.
   */
  int getPriority();

  /**
   * Overrides the priority for this task. Higher priority tasks will be
   * executed before lower priority tasks in the same context. In most cases,
   * the default priority is sufficient.
   * <p/>
   * The default priority is 0. Use {@code priority < 0} to make a task
   * lower priority and {@code priority > 0} to make a task higher
   * priority.
   * <p/>
   * If the task has already started execution the priority cannot be
   * changed.
   *
   * @param priority the new priority for the task.
   * @return {@code true} if the priority was set; otherwise {@code false}.
   * @throws IllegalArgumentException if the priority is out of range
   * @see Priority
   */
  boolean setPriority(int priority);

  /**
   * Allows adding {@code String} representation of value computed by this task to trace.
   * When this task is finished successfully, value will be converted to String using given
   * serializer and it will be included in this task's trace.
   * <p/>
   * Failures are automatically included in a trace.
   * @param serializer serialized used for converting result of this task
   * to String that will be included in this task's trace.
   */
  void traceValue(Function<T, String> serializer);

  /**
   * Attempts to run the task with the given context. This method is
   * reserved for use by {@link Engine} and {@link Context}.
   *
   * @param context the context to use while running this step
   * @param taskLogger the logger used for task events
   * @param parent the parent of this task
   * @param predecessors that lead to the execution of this task
   */
  void contextRun(Context context, Task<?> parent, Collection<Task<?>> predecessors);

  void wrapContextRun(ContextRunWrapper<T> wrapper);

  /**
   * Returns the ShallowTrace for this task. The ShallowTrace will be
   * a point-in-time snapshot and may change over time until the task is
   * completed.
   *
   * @return the ShallowTrace related to this task
   */
  ShallowTrace getShallowTrace();

  /**
   * Returns the Trace for this task. The Trace will be a point-in-time snapshot
   * and may change over time until the task is completed.
   *
   * @return the Trace related to this task
   */
  Trace getTrace();

  /**
   * Unique identifier of the task.
   * @return unique identifier of the task.
   */
  Long getId();

  ShallowTraceBuilder getShallowTraceBuilder();

  TraceBuilder getTraceBuilder();

  //------------------- default methods -------------------

  default <R> Task<R> apply(final String desc, final PromisePropagator<T, R> propagator) {
    return FusionTask.fuse(desc, this, propagator, new AtomicReference<>(), Optional.empty());
  }

  /**
   * Creates a new task by applying a function to the successful result of this task.
   * Returned task will complete with value calculated by a function.
   * <pre><code>
   * Task{@code <String>} hello = Task.value("Hello World");
   *
   * // this task will complete with value 11
   * Task{@code <Integer>} length = hello.map(s -> s.length());
   * </code></pre>
   *
   * If this task is completed with an exception then the new task will also complete
   * with that exception.
   * <pre><code>
   *  Task{@code <String>} failing = Task.callable("hello", () -> {
   *    return "Hello World".substring(100);
   *  });
   *
   *  // this task will fail with java.lang.StringIndexOutOfBoundsException
   *  Task{@code <Integer>} length = failing.map("length", s -> s.length());
   * </code></pre>
   * @param <R> return type of function <code>func</code>
   * @param desc description of a mapping function, it will show up in a trace
   * @param func function to be applied to successful result of this task.
   * @return a new task which will apply given function on result of successful completion of this task
   */
  default <R> Task<R> map(final String desc, final Function1<? super T,? extends R> func) {
    ArgumentUtil.requireNotNull(func, "function");
    return apply(desc, new PromiseTransformer<T, R>(func));
  }

  /**
   * Equivalent to {@code map("map", func)}.
   * @see #map(String, Function)
   */
  default <R> Task<R> map(final Function1<? super T,? extends R> func) {
    return map("map", func);
  }

  /**
   * Creates a new task by applying a function to the successful result of this task and
   * returns the result of a function as the new task.
   * Returned task will complete with value calculated by a task returned by the function.
   * <pre><code>
   *  Task{@code <URI>} url = Task.value("uri", URI.create("http://linkedin.com"));
   *
   *  // this task will complete with contents of a LinkedIn homepage
   *  // assuming fetch(u) fetches contents given by a URI
   *  Task{@code <String>} homepage = url.flatMap("fetch", u -> fetch(u));
   * </code></pre>
   *
   * If this task is completed with an exception then the new task will also contain
   * that exception.
   * <pre><code>
   *  Task{@code <URI>} url = Task.callable("uri", () -> URI.create("not a URI"));
   *
   *  // this task will fail with java.lang.IllegalArgumentException
   *  Task{@code <String>} homepage = url.flatMap("fetch", u -> fetch(u));
   * </code></pre>
   * @param <R> return type of function <code>func</code>
   * @param desc description of a mapping function, it will show up in a trace
   * @param func function to be applied to successful result of this task which returns new task
   * to be executed
   * @return a new task which will apply given function on result of successful completion of this task
   * to get instance of a task which will be executed next
   */
  default <R> Task<R> flatMap(final String desc, final Function1<? super T, Task<R>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    return flatten(desc, map("flatMap", func));
  }

  /**
   * Equivalent to {@code flatMap("flatMap", func)}.
   * @see #flatMap(String, Function)
   */
  default <R> Task<R> flatMap(final Function1<? super T, Task<R>> func) {
    return flatMap("flatMap", func);
  }

  /**
   * Creates a new task that will run another task as a side effect once the primary task
   * completes successfully. The properties of side effect task are:
   * <ul>
   * <li>The side effect task will not be run if the primary task fails or
   * is canceled.</li>
   * <li>The side effect does not affect returned task. It means that
   * failure of side effect task is not propagated to returned task.</li>
   * <li>The returned task is marked done once this task completes, even if
   * the side effect has not been run yet.</li>
   * </ul>
   * The side effect task is useful in situations where operation (side effect) should continue to run
   * in the background but it's execution is not required for the main computation. An example might
   * be updating cache once data has been retrieved from the main source.
   * <pre><code>
   *  Task{@code <Long>} id = Task.value(1223L);
   *
   *  // this task will be completed as soon as user name is fetched
   *  // by fetch() method and will not fail even if updateMemcache() fails
   *  Task{@code <String>} userName = id.flatMap("fetch", u -> fetch(u))
   *      .withSideEffect("update memcache", u -> updateMemcache(u));
   * </code></pre>
   * @param desc description of a function, it will show up in a trace
   * @param func function to be applied on result of successful completion of this task
   * to get side effect task
   * @return a new task that will run side effect task specified by given function upon succesful
   * completion of this task
   */
  default Task<T> withSideEffect(final String desc, final Function1<? super T, Task<?>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    final Task<T> that = this;
    return async(desc, context -> {
      final Task<?> sideEffectWrapper = async(desc, ctx -> {
        Task<?> sideEffect = func.apply(that.get());
        ctx.run(sideEffect);
        return sideEffect;
      }, true);
      context.after(that).runSideEffect(sideEffectWrapper);
      context.run(that);
      return that;
    }, true);
  }

  /**
   * Equivalent to {@code withSideEffect("withSideEffect", func)}.
   * @see #withSideEffect(String, Function)
   */
  default Task<T> withSideEffect(final Function1<? super T, Task<?>> func) {
    return withSideEffect("withSideEffect", func);
  }

  /**
   * Creates a new task which applies a consumer to the result of this task
   * and completes with a result of this task. It is used
   * in situations where consumer needs to be called after successful
   * completion of this task.
   * <pre><code>
   *  Task{@code <String>} hello = Task.value("Hello World");
   *
   *  // this task will print "Hello World"
   *  Task{@code <String>} sayHello = hello.andThen(System.out::println);
   * </code></pre>
   *
   * If this task fails then consumer will not be called and failure
   * will be propagated to task returned by this method.
   * <pre><code>
   *  Task{@code <String>} failing = Task.callable("hello", () {@code ->} {
   *    return "Hello World".substring(100);
   *  });
   *
   *  // this task will fail with java.lang.StringIndexOutOfBoundsException
   *  Task{@code <String>} sayHello = failing.andThen(System.out::println);
   * </code></pre>
   *
   * @param desc description of a consumer, it will show up in a trace
   * @param consumer consumer of a value returned by this task
   * @return a new task which will complete with result of this task
   */
  default Task<T> andThen(final String desc, final Consumer1<? super T> consumer) {
    ArgumentUtil.requireNotNull(consumer, "consumer");
    return apply(desc,
        new PromiseTransformer<T, T>(t -> {
          consumer.accept(t);
          return t;
        }));
  }

  /**
   * Equivalent to {@code andThen("andThen", consumer)}.
   * @see #andThen(String, Consumer1)
   */
  default Task<T> andThen(final Consumer1<? super T> consumer) {
    return andThen("andThen", consumer);
  }

  /**
   * Creates a new task which runs given task after successful
   * completion of this task and completes with a result of
   * that task. Task passed in as a parameter will run only
   * if this task completes successfully.
   * If this task fails then task passed in as a parameter will
   * not be scheduled for execution and failure
   * will be propagated to task returned by this method.
   * <pre><code>
   *  // task which processes payment
   *  Task{@code <PaymentStatus>} processPayment = processPayment(...);
   *
   *  // task which ships product
   *  Task{@code <ShipmentInfo>} shipProduct = shipProduct(...);
   *
   *  // this task will ship product only if payment was
   *  // successfully processed
   *  Task{@code <ShipmentInfo>} shipAfterPayment =
   *      processPayment.andThen("shipProductAterPayment", shipProduct);
   * </code></pre>
   *
   * @param desc description of a task, it will show up in a trace
   * @param task task which will be executed after successful completion of this task
   * @return a new task which will run given task after successful completion of this task
   */
  default <R> Task<R> andThen(final String desc, final Task<R> task) {
    ArgumentUtil.requireNotNull(task, "task");
    final Task<T> that = this;
    return async(desc, context -> {
      final SettablePromise<R> result = Promises.settable();
      context.after(that).run(task);
      Promises.propagateResult(task, result);
      context.run(that);
      return result;
    }, true);
  }

  /**
   * Equivalent to {@code andThen("andThen", task)}.
   * @see #andThen(String, Task)
   */
  default <R> Task<R> andThen(final Task<R> task) {
    return andThen("andThen", task);
  }

  /**
   * Creates a new task that will handle failure of this task.
   * Early completion due to cancellation is not considered to be a failure.
   * If this task completes successfully, then recovery function is not called.
   * <pre><code>
   *
   * // this method return task which asynchronously retrieves Person by id
   * Task{@code<Person>} fetchPerson(Long id) {
   * (...)
   * }
   *
   * // this task will fetch Person object and transform it into {@code"<first name> <last name>"}
   * // if fetching Person failed then form {@code"Member <id>"} will be return
   * Task{@code <String>} userName = fetchPerson(id)
   *      .map(p {@code ->} p.getFirstName() + " " + p.getLastName())
   *      .recover(e {@code ->} "Member " + id);
   * </code></pre>
   *
   * @param desc description of a recovery function, it will show up in a trace
   * @param func recovery function which can complete task with a value depending on
   *        failure of this task
   * @return a new task which can recover from failure of this task
   */
  default Task<T> recover(final String desc, final Function1<Throwable, T> func) {
    ArgumentUtil.requireNotNull(func, "function");
    return apply(desc,  (src, dst) -> {
      if (src.isFailed() && !(src.getError() instanceof EarlyFinishException)) {
        try {
          dst.done(func.apply(src.getError()));
        } catch (Throwable t) {
          dst.fail(t);
        }
      } else {
        dst.done(src.get());
      }
    });
  }

  /**
   * Equivalent to {@code recover("recover", func)}.
   * @see #recover(String, Function)
   */
  default Task<T> recover(final Function1<Throwable, T> func) {
    return recover("recover", func);
  }

  /**
   * Creates a new task which applies a consumer to the exception this
   * task may fail with. It is used in situations where consumer needs
   * to be called after failure of this task. Result of task returned by
   * this method will be exactly the same as result of this task.
   * <pre><code>
   *  Task{@code <String>} failing = Task.callable("hello", () {@code ->} {
   *    return "Hello World".substring(100);
   *  });
   *
   *  // this task will print out java.lang.StringIndexOutOfBoundsException
   *  // and complete with it
   *  Task{@code <String>} sayHello = failing.onFailure(System.out::println);
   * </code></pre>
   *
   * If this task completes successfully then consumer will not be called.
   * <pre><code>
   *  Task{@code <String>} hello = Task.value("Hello World");
   *
   *  // this task will print "Hello World"
   *  Task{@code <String>} sayHello = hello.onFailure(System.out::println);
   * </code></pre>
   *
   * Note that exceptions thrown by a consumer will be ignored.
   * <p/>
   * @param desc description of a consumer, it will show up in a trace
   * @param consumer consumer of an exception this task failed with
   * @return a new task which will complete with result of this task
   */
  default Task<T> onFailure(final String desc, final Consumer1<Throwable> consumer) {
    ArgumentUtil.requireNotNull(consumer, "function");
    return apply(desc,  (src, dst) -> {
      if (src.isFailed() && !(src.getError() instanceof EarlyFinishException)) {
        try {
          consumer.accept(src.getError());
        } catch (Exception e) {
          //exceptions thrown by consumer are ignored
        } finally {
          dst.fail(src.getError());
        }
      } else {
        dst.done(src.get());
      }
    });
  }

  /**
   * Equivalent to {@code onError("onError", consumer)}.
   * @see #onFailure(String, Consumer1)
   */
  default Task<T> onFailure(final Consumer1<Throwable> consumer) {
    return onFailure("onError", consumer);
  }

  /**
   * This method transforms {@code Task<T>} into {@code Task<Try<T>>}.
   * It allows explicit handling of failures by returning potential exceptions as a result of
   * task execution. Task returned by this method will always complete succesfully.
   * If this task completes successfully then return task will be
   * completed with result value wrapped with {@link Success}.
   * <pre><code>
   *  Task{@code <String>} hello = Task.value("Hello World");
   *
   *  // this task will complete with Success("Hello World")
   *  Task{@code <Try<String>>} helloTry = hello.withTry("try");
   * </code></pre>
   *
   * If this task is completed with an exception then the returned task will be
   * completed with an exception wrapped with {@link Failure}.
   * <pre><code>
   *  Task{@code <String>} failing = Task.callable("hello", () -> {
   *      return "Hello World".substring(100);
   *  });
   *
   *  // this task will complete successfully with Failure(java.lang.StringIndexOutOfBoundsException)
   *  Task{@code <Try<String>>} failingTry = failing.withTry("try");
   * </code></pre>
   * Note that all failures are automatically propagated and usually it is enough to use
   * {@link #recover(String, Function) recover}, {@link #recoverWith(String, Function) recoverWith}
   * or {@link #fallBackTo(String, Function) fallBackTo}.
   * <p/>
   * @return
   * @see Try
   * @see #recover(String, Function) recover
   * @see #recoverWith(String, Function) recoverWith
   * @see #fallBackTo(String, Function) fallBackTo
   */
  default Task<Try<T>> withTry(final String desc) {
    return apply(desc,  (src, dst) -> {
      if (src.isFailed()) {
        dst.done(Failure.of(src.getError()));
      } else {
        dst.done(Success.of(src.get()));
      }
    });
  }

  /**
   * Equivalent to {@code withTry("withTry")}.
   * @see #withTry(String)
   */
  default Task<Try<T>> withTry() {
    return withTry("withTry");
  }

  /**
   * Creates a new task that will handle failure of this task.
   * Early completion due to cancellation is not considered to be a failure.
   * If this task completes successfully, then recovery function is not called.
   * <pre><code>
   *
   * // this method return task which asynchronously retrieves Person by id from cache
   * Task{@code<Person>} fetchFromCache(Long id) {
   * (...)
   * }
   *
   * // this method return task which asynchronously retrieves Person by id from DB
   * Task{@code<Person>} fetchFromDB(Long id) {
   * (...)
   * }
   *
   * // this task will fetch Person object and transform it into {@code"<first name> <last name>"}
   * // it will first try to fetch from cache and when it fails for any reason it will
   * // attempt to fetch from DB
   * Task{@code <String>} userName = fetchFromCache(id)
   *      .recoverWith(e {@code ->} fetchFromDB(id))
   *      .map(p {@code ->} p.getFirstName() + " " + p.getLastName());
   * </code></pre>
   *
   * If recovery task fails then returned task is completed with that failure.
   * <p/>
   *
   * @param desc description of a recovery function, it will show up in a trace
   * @param func recovery function provides task which will be used to recover from
   * failure of this task
   * @return a new task which can recover from failure of this task
   */
  default Task<T> recoverWith(final String desc, final Function1<Throwable, Task<T>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    final Task<T> that = this;
    return async(desc, context -> {
      final SettablePromise<T> result = Promises.settable();
      final Task<T> recovery = async(desc, ctx -> {
        if (that.isFailed() && !(that.getError() instanceof EarlyFinishException)) {
          try {
            Task<T> r = func.apply(that.getError());
            Promises.propagateResult(r, result);
            ctx.run(r);
          } catch (Throwable t) {
            result.fail(t);
          }
        } else {
          result.done(that.get());
        }
        return result;
      }, true);
      context.after(that).run(recovery);
      context.run(that);
      return result;
    }, true);
  }

  /**
   * Equivalent to {@code recoverWith("recoverWith", func)}.
   * @see #recoverWith(String, Function)
   */
  default Task<T> recoverWith(final Function1<Throwable, Task<T>> func) {
    return recoverWith("recoverWith", func);
  }

  /**
   * @param time the time to wait before timing out
   * @param unit the units for the time
   * @param <T> the value type for the task
   * @return the new task with a timeout
   */
  //TODO change withTimeout to return new task
  default Task<T> withTimeout(final long time, final TimeUnit unit)
  {
    wrapContextRun(new TimeoutContextRunWrapper<T>(time, unit, Exceptions.TIMEOUT_EXCEPTION));
    return this;
  }

  /**
   * Converts {@code Task<Task<R>>} into {@code Task<R>}.
   * @param desc description that will show up in a trace
   * @param task task to be flattened
   * @return flattened task
   */
  public static <R> Task<R> flatten(final String desc, final Task<Task<R>> task) {
    return async(desc, context -> {
      final SettablePromise<R> result = Promises.settable();
      context.after(task).run(() -> {
        try {
          Task<R> t = task.get();
          Promises.propagateResult(t, result);
          return Optional.of(t);
        } catch (Throwable t) {
          result.fail(t);
          return Optional.empty();
        }
      });
      context.run(task);
      return result;
     }, true);
  }

  /**
   * Equivalent to {@code flatten("flatten", task)}.
   * @see #flatMap(String, Function)
   */

  public static <R> Task<R> flatten(final Task<Task<R>> task) {
    return flatten("flatten", task);
  }

  //------------------- static factory methods -------------------

  /**
   * Creates a new task that have a value of type {@code Void}. Because the
   * returned task returns no value, it is typically used to produce side effects.
   * It is not appropriate for long running or blocking actions. If action is
   * long running or blocking use {@link #blocking(String, ThrowableCallable, Executor) blocking} method.
   *
   * <pre><code>
   * // this task will print "Hello" on standard output
   * Task{@code <Void>} task = Task.action("greeting", () -> System.out.println("Hello"));
   * </code></pre>
   *
   * Returned task will fail if {@code Runnable} passed in as a parameter throws
   * an exception.
   * <pre><code>
   * // this task will fail with java.lang.ArithmeticException
   * Task{@code <Void>} task = Task.action("division", () -> System.out.println(2 / 0));
   * </code></pre>
   * <p/>
   * @param name a name that describes the action
   * @param action the action that will be executed when the task is run
   * @return the new task that will execute the action
   */
  public static Task<Void> action(final String name, final Runnable action)
  {
    ArgumentUtil.requireNotNull(action, "action");
    return async(name, () -> {
      action.run();
      return Promises.VOID;
    }, false);
  }

  /**
   * Equivalent to {@code action("action", runnable)}.
   * @see #action(String, Runnable)
   */
  public static Task<Void> action(final Runnable runnable)
  {
    return action("action", runnable);
  }

  public static <T> Task<T> value(final String name, final T value) {
    return callable(name, () -> value);
  }

  public static <T> Task<T> value(final T value) {
    return callable("value", () -> value);
  }

  public static <T> Task<T> failure(final String name, final Throwable failure) {
    return FusionTask.create(name, Promises.value(null), (src, dst) -> {
      dst.fail(failure);
    });
  }

  public static <T> Task<T> failure(final Throwable failure) {
    return failure("failure", failure);
  }

  /**
   * Creates a new task that's value will be set to the value returned
   * from the supplied callable. This task is useful when doing basic
   * computation that does not require asynchrony. It is not appropriate for
   * long running or blocking callables. If callable is long running or blocking
   * use {@link #blocking(String, ThrowableCallable, Executor) blocking} method.
   *
   * <pre><code>
   * // this task will complete with {@code String} representing current time
   * Task{@code <String>} task = Task.callable("current time", () -> new Date().toString());
   * </code></pre>
   *
   * Returned task will fail if callable passed in as a parameter throws
   * an exception.
   * <pre><code>
   * // this task will fail with java.lang.ArithmeticException
   * Task{@code <Integer>} task = Task.callable("division", () -> 2 / 0);
   * </code></pre>
   * <p/>
   * @param name a name that describes the task, it will show up in a trace
   * @param callable the callable to execute when this task is run
   * @param <T> the type of the return value for this task
   * @return the new task that will invoke the callable and complete with it's result
   */
  public static <T> Task<T> callable(final String name, final ThrowableCallable<? extends T> callable) {
    return FusionTask.create(name, Promises.value(null), (src, dst) -> {
      try {
        dst.done(callable.call());
      } catch (Throwable t) {
        dst.fail(t);
      }
    });
  }


  //TODO refactor to use Callable instead
  /**
   * Equivalent to {@code callable("callable", callable)}.
   * @see #callable(String, ThrowableCallable)
   */
  public static <T> Task<T> callable(final ThrowableCallable<? extends T> callable) {
    return callable("callable", callable);
  }

  /**
   * Creates a new task from a callable that returns a {@link Promise}.
   * This method is mainly used to integrate ParSeq with 3rd party
   * asynchronous libraries. It should not be used in order to compose
   * or manipulate existing tasks. The following example shows how to integrate
   * <a href="https://github.com/AsyncHttpClient">AsyncHttpClient</a> with ParSeq.
   *
   * <pre><code>
   *  // Creates a task that asynchronouslyexecutes given HTTP request
   *  // and will complete with HTTP response. It uses asyncHttpRequest()
   *  // method as a lambda of shape: ThrowableCallable{@code <Promise<Response>>}.
   *  Task{@code <Response>} httpTask(final Request request) {
   *    return Task.async(() -> asyncHttpRequest(request), false);
   *  }
   *
   *  // This method uses HTTP_CLIENT to make asynchronous
   *  // request and returns a Promise that will be resolved once
   *  // the HTTP request is completed.
   *  Promise{@code <Response>} asyncHttpRequest(final Request request) {
   *
   *    // Create a settable promise. We'll use this to signal completion of this
   *    // task once the response is received from the HTTP client.
   *    final SettablePromise{@code <Response>} promise = Promises.settable();
   *
   *    // Send the request and register a callback with the client that will
   *    // set the response on our promise.
   *    HTTP_CLIENT.prepareRequest(request).execute(new AsyncCompletionHandler{@code <Response>}() {
   *
   *      {@code @Override}
   *      public Response onCompleted(final Response response) throws Exception {
   *        // At this point the HTTP client has given us the HTTP response
   *        // asynchronously. We set the response value on our promise to indicate
   *        // that the task is complete.
   *        promise.done(response);
   *        return response;
   *      }
   *
   *      {@code @Override}
   *      public void onThrowable(final Throwable t) {
   *        // If there was an error then we should set it on the promise.
   *        promise.fail(t);
   *      }
   *    });
   *
   *    // Return the promise. It may or may not be
   *    // resolved by the time we return this promise.
   *    return promise;
   *  }
   * </code></pre>
   *
   * This method is not appropriate for long running or blocking callables.
   * If callable is long running or blocking use
   * {@link #blocking(String, ThrowableCallable, Executor) blocking} method.
   * <p/>
   *
   * @param <T> the type of the return value for this task
   * @param name a name that describes the task, it will show up in a trace
   * @param callable a callable to execute when this task is run, it must return
   * a {@code Promise<T>}
   * @param systemHidden flag that specifies whether trace of this task will have
   * a system-hidden flag set
   * @return a new task that will invoke the callable and complete with result
   * returned by a {@code Promise} returned by it
   * @see Promise
   */
  public static <T> Task<T> async(final String name, final ThrowableCallable<Promise<? extends T>> callable,
      final boolean systemHidden) {
    return async(name, context -> {
      try {
        return callable.call();
      } catch (Throwable e) {
        return Promises.error(e);
      }
    }, systemHidden);
  }

  /**
   * Equivalent to {@code async("async", callable, systemHidden)}.
   * @see #async(String, ThrowableCallable, boolean)
   */
  public static <T> Task<T> async(final ThrowableCallable<Promise<? extends T>> callable, final boolean systemHidden) {
    return async("async", callable, systemHidden);
  }

  /**
   * Creates a new task from a callable that returns a {@link Promise}.
   * This method is mainly used to build functionality that has not been provided
   * by ParSeq API. It gives access to {@link Context} which allows scheduling
   * tasks in current plan. This method almost never should be necessary. If you
   * feel the need to use this method, please contact ParSeq team to help us
   * improve our API.
   *
   * @param <T> the type of the return value for this task
   * @param name a name that describes the task, it will show up in a trace
   * @param func a function to execute when this task is run, it must return
   * a {@code Promise<T>}
   * @param systemHidden flag that specifies whether trace of this task will have
   * a system-hidden flag set
   * @return a new task that will invoke the function and complete with result
   * returned by a {@code Promise} returned by it
   * @see Context
   * @see Promise
   */
  public static <T> Task<T> async(final String name, final Function1<Context, Promise<? extends T>> func,
      final boolean systemHidden) {
    ArgumentUtil.requireNotNull(func, "function");
    return new BaseTask<T>(name) {

      @Override
      protected Promise<? extends T> run(Context context) throws Throwable {
        return func.apply(context);
      }

      @Override
      public ShallowTrace getShallowTrace() {
        _shallowTraceBuilder.setSystemHidden(systemHidden);
        return super.getShallowTrace();
      }
    };
  }

  /**
   * Equivalent to {@code async("async", func, systemHidden)}.
   * @see #async(String, Function1, boolean)
   */
  public static <T> Task<T> async(final Function1<Context, Promise<? extends T>> func, final boolean systemHidden) {
    return async("async", func, systemHidden);
  }

  /**
   * This method provides a way to create an asynchronous task from
   * a blocking or long running callables like JDBC requests.
   * Unlike with tasks created with all other methods a callable passed
   * as a parameter is not executed on ParSeq's thread but instead it is
   * executed on specified {@link Executor}. It means that callable
   * does not get any special memory consistency guarantees and should not
   * attempt to use shared state.
   *
   * @param <T> the type of the return value for this task
   * @param name a name that describes the task, it will show up in a trace
   * @param callable a callable that will provide result
   * @param executor {@code Executor} that will be used to run the callable
   * @return a new task that will submit the callable to given executor and complete
   * with result returned by that callable
   */
  public static <T> Task<T> blocking(final String name, final ThrowableCallable<? extends T> callable, final Executor executor) {
    ArgumentUtil.requireNotNull(callable, "callable");
    return async(name, () -> {
      final SettablePromise<T> promise = Promises.settable();
      executor.execute(() -> {
        try {
          promise.done(callable.call());
        } catch (Throwable t) {
          promise.fail(t);
        }
      });
      return promise;
    }, false);
  }

  /**
   * Equivalent to {@code blocking("blocking", callable, executor)}.
   * @see #blocking(String, ThrowableCallable, Executor)
   */
  public static <T> Task<T> blocking(final ThrowableCallable<? extends T> callable, final Executor executor) {
    return blocking("blocking", callable, executor);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <pre><code>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </code></pre>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks.
   * <p/>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2> Tuple2Task<T1, T2> par(final Task<T1> task1,
                                                final Task<T2> task2) {
    return new Par2Task<T1, T2>("par2", task1, task2);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <pre><code>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </code></pre>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks.
   * <p/>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3> Tuple3Task<T1, T2, T3> par(final Task<T1> task1,
                                                        final Task<T2> task2,
                                                        final Task<T3> task3) {
    return new Par3Task<T1, T2, T3>("par3", task1, task2, task3);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <pre><code>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </code></pre>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks.
   * <p/>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4> Tuple4Task<T1, T2, T3, T4> par(final Task<T1> task1,
                                                                final Task<T2> task2,
                                                                final Task<T3> task3,
                                                                final Task<T4> task4) {
    return new Par4Task<T1, T2, T3, T4>("par4", task1, task2, task3, task4);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <pre><code>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </code></pre>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks.
   * <p/>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4, T5> Tuple5Task<T1, T2, T3, T4, T5> par(final Task<T1> task1,
                                                                        final Task<T2> task2,
                                                                        final Task<T3> task3,
                                                                        final Task<T4> task4,
                                                                        final Task<T5> task5) {
    return new Par5Task<T1, T2, T3, T4, T5>("par5", task1, task2, task3, task4, task5);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <pre><code>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </code></pre>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks.
   * <p/>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4, T5, T6> Tuple6Task<T1, T2, T3, T4, T5, T6> par(final Task<T1> task1,
                                                                                final Task<T2> task2,
                                                                                final Task<T3> task3,
                                                                                final Task<T4> task4,
                                                                                final Task<T5> task5,
                                                                                final Task<T6> task6) {
    return new Par6Task<T1, T2, T3, T4, T5, T6>("par6", task1, task2, task3, task4, task5, task6);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <pre><code>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </code></pre>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks.
   * <p/>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4, T5, T6, T7> Tuple7Task<T1, T2, T3, T4, T5, T6, T7> par(final Task<T1> task1,
                                                                                        final Task<T2> task2,
                                                                                        final Task<T3> task3,
                                                                                        final Task<T4> task4,
                                                                                        final Task<T5> task5,
                                                                                        final Task<T6> task6,
                                                                                        final Task<T7> task7) {
    return new Par7Task<T1, T2, T3, T4, T5, T6, T7>("par7", task1, task2, task3, task4, task5, task6, task7);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <pre><code>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </code></pre>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks.
   * <p/>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> par(final Task<T1> task1,
                                                                                                final Task<T2> task2,
                                                                                                final Task<T3> task3,
                                                                                                final Task<T4> task4,
                                                                                                final Task<T5> task5,
                                                                                                final Task<T6> task6,
                                                                                                final Task<T7> task7,
                                                                                                final Task<T8> task8) {
    return new Par8Task<T1, T2, T3, T4, T5, T6, T7, T8>("par8", task1, task2, task3, task4, task5, task6, task7, task8);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <pre><code>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </code></pre>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks.
   * <p/>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> par(final Task<T1> task1,
                                                                                                        final Task<T2> task2,
                                                                                                        final Task<T3> task3,
                                                                                                        final Task<T4> task4,
                                                                                                        final Task<T5> task5,
                                                                                                        final Task<T6> task6,
                                                                                                        final Task<T7> task7,
                                                                                                        final Task<T8> task8,
                                                                                                        final Task<T9> task9) {
    return new Par9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9>("par9", task1, task2, task3, task4, task5, task6, task7, task8, task9);
  }

}
