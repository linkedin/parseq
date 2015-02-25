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
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.function.Failure;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromisePropagator;
import com.linkedin.parseq.promise.PromiseTransformer;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.Related;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;

/**
 * A task represents a deferred execution that also contains its resulting
 * value. In addition, tasks include some tracing information that can be
 * used with various trace printers.
 * <p/>
 * Tasks should generally be run using either an {@link Engine} or a
 * {@link Context}. They should not be run directly.
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
   * Attempts to run the task with the given context. This method is
   * reserved for use by {@link Engine} and {@link Context}.
   *
   * @param context the context to use while running this step
   * @param taskLogger the logger used for task events
   * @param parent the parent of this task
   * @param predecessors that lead to the execution of this task
   */
  void contextRun(Context context, TaskLogger taskLogger,
                  Task<?> parent, Collection<Task<?>> predecessors);

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
   * Returns the set of relationships of this task. The parent relationships are not included.
   *
   * @see com.linkedin.parseq.trace.Relationship the available relationships
   * @return the set of relationships of this task.
   */
  Set<Related<Task<?>>> getRelationships();

  //------------------- default methods -------------------

  default <R> Task<R> apply(final String desc, final PromisePropagator<T, R> propagator) {
    return FusionTask.fuse(desc, this, propagator);
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
  default <R> Task<R> map(final String desc, final Function<T, R> func) {
    ArgumentUtil.requireNotNull(func, "function");
    return apply(desc, new PromiseTransformer<T, R>(func));
  }

  /**
   * Equivalent to {@code map("map", func)}.
   * @see #map(String, Function)
   */
  default <R> Task<R> map(final Function<T, R> func) {
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
  default <R> Task<R> flatMap(final String desc, final Function<T, Task<R>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    final Task<T> that = this;
    return async(desc, context -> {
      final SettablePromise<R> result = Promises.settable();
      final Task<R> taskR = async(desc, ctx -> {
        try {
          Task<R> t = func.apply(that.get());
          Promises.propagateResult(t, result);
          ctx.run(t);
          return t;
        } catch (Throwable t) {
          result.fail(t);
          return Promises.error(t);
        }
      }, true);
      context.after(that).run(taskR);
      context.run(that);
      return result;
     }, true);
  }

  /**
   * Equivalent to {@code flatMap("flatMap", func)}.
   * @see #flatMap(String, Function)
   */
  default <R> Task<R> flatMap(final Function<T, Task<R>> func) {
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
  default Task<T> withSideEffect(final String desc, final Function<T, Task<?>> func) {
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
  default Task<T> withSideEffect(final Function<T, Task<?>> func) {
    return withSideEffect("withSideEffect", func);
  }

  /**
   * Equivalent to {@code withSideEffect(desc, t -> sideEffect)}.
   * @see #withSideEffect(String, Function)
   */
  default Task<T> withSideEffect(final String desc, final Task<?> sideEffect) {
    ArgumentUtil.requireNotNull(sideEffect, "sideEffect");
    final Task<T> that = this;
    return async(desc, context -> {
      context.after(that).runSideEffect(sideEffect);
      context.run(that);
      return that;
    }, true);
  }

  /**
   * Equivalent to {@code withSideEffect("withSideEffect", t -> sideEffect)}.
   * @see #withSideEffect(String, Function)
   */
  default Task<T> withSideEffect(final Task<?> sideEffect) {
    return withSideEffect("withSideEffect", sideEffect);
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
  default Task<T> andThen(final String desc, final Consumer<T> consumer) {
    ArgumentUtil.requireNotNull(consumer, "consumer");
    return apply(desc,
        new PromiseTransformer<T, T>(t -> {
          consumer.accept(t);
          return t;
        }));
  }

  /**
   * Equivalent to {@code andThen("andThen", consumer)}.
   * @see #andThen(String, Consumer)
   */
  default Task<T> andThen(final Consumer<T> consumer) {
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
  default Task<T> recover(final String desc, final Function<Throwable, T> func) {
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
  default Task<T> recover(final Function<Throwable, T> func) {
    return recover("recover", func);
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
   *  Task{@code <Try<String>>} helloTry = hello.withTry();
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
   *  Task{@code <Try<String>>} failingTry = failing.withTry();
   * </code></pre>
   * Note that all failures are automatically propagated and usually it is enough to use
   * {@link #recover(String, Function) recover}, {@link #recoverWith(String, Function) recoverWith}
   * or {@link #fallBackTo(String, Function) fallBackTo}.
   * <p>
   * @return
   * @see Try
   * @see #recover(String, Function) recover
   * @see #recoverWith(String, Function) recoverWith
   * @see #fallBackTo(String, Function) fallBackTo
   */
  default Task<Try<T>> withTry() {
    return map("map to Try", t -> Success.of(t))
             .recover("recover to Try", t -> Failure.of(t));
  }

  /**
   * Creates a new task that will handle any Throwable that this task might throw.
   * If this task completes successfully, then recovery function is not invoked.
   * Task returned by recovery function
   * will become a new result of this task. This means that if recovery function fails,
   * then result of this task will fail with a Throwable from recovery function.
   *
   * @param desc description of a recovery function, it will show up in a trace
   * @param func recovery function which can return task which will become a new result of
   * this task
   * @return a new task which can recover from Throwable thrown by this task or cancellation
   */
  default Task<T> recoverWith(final String desc, final Function<Throwable, Task<T>> func) {
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

  default Task<T> recoverWith(final Function<Throwable, Task<T>> func) {
    return recoverWith("recoverWith", func);
  }
  /**
   * Creates a new task that will handle any Throwable that this task might throw
   * or task cancellation. If this task completes successfully,
   * then fall-back function is not invoked. If task returned by fall-back function
   * completes successfully with a value, then that value becomes a result of this
   * task. If task returned by fall-back function fails with a Throwable or is cancelled,
   * then this task will fail with the original Throwable, not the one coming from
   * the fall-back function's task.
   *
   * @param desc description of a recovery function, it will show up in a trace
   * @param func recovery function which can return task which will become a new result of
   * this task
   * @return a new task which can recover from Throwable thrown by this task or cancellation
   */
  default Task<T> fallBackTo(final String desc, final Function<Throwable, Task<T>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    return recoverWith(desc, originalFailure -> {
      Task<T> fallBack = func.apply(originalFailure).apply("restoreFailure", (src, dst) -> {
        if (src.isFailed()) {
          dst.fail(originalFailure);
        } else {
          dst.done(src.get());
        }
      });
      return fallBack;
    });
  }

  default Task<T> fallBackTo(final Function<Throwable, Task<T>> func) {
    return fallBackTo("fallBackTo", func);
  }

  static class TimeoutContextRunWrapper<T> implements ContextRunWrapper<T> {

    protected final SettablePromise<T> _result = Promises.settable();
    protected final AtomicBoolean _committed = new AtomicBoolean();
    private final long _time;
    private final TimeUnit _unit;
    private final Exception _exception;

    public TimeoutContextRunWrapper(long time, TimeUnit unit, final Exception exception) {
      _time = time;
      _unit = unit;
      _exception = exception;
    }

    @Override
    public void before(Context context) {
      final Task<?> timeoutTask = action("timeoutTimer", () -> {
        if (_committed.compareAndSet(false, true)) {
          _result.fail(_exception);
        }
      });
      //timeout tasks should run as early as possible
      timeoutTask.setPriority(Priority.MAX_PRIORITY);
      context.createTimer(_time, _unit, timeoutTask);
    }

    @Override
    public Promise<T> after(Context context, Promise<T> promise) {
      promise.addListener(p -> {
        if (_committed.compareAndSet(false, true)) {
          Promises.propagateResult(promise, _result);
        }
      });
      return _result;
    }
  }

  /**
   * @param time the time to wait before timing out
   * @param unit the units for the time
   * @param <T> the value type for the task
   * @return the new task with a timeout
   */
  default Task<T> withTimeout(final long time, final TimeUnit unit)
  {
    wrapContextRun(new TimeoutContextRunWrapper<T>(time, unit, Exceptions.TIMEOUT_EXCEPTION));
    return this;
  }

  public static interface ContextRunWrapper<T> {

    void before(Context context);

    Promise<T> after(Context context, Promise<T> promise);

    default ContextRunWrapper<T> compose(final ContextRunWrapper<T> wrapper) {
      ContextRunWrapper<T> that = this;
      return new ContextRunWrapper<T>() {

        @Override
        public void before(Context context) {
          wrapper.before(context);
          that.before(context);
        }

        @Override
        public Promise<T> after(Context context, Promise<T> promise) {
          return wrapper.after(context, that.after(context, promise));
        }
      };
    }
  }

  //------------------- static factory methods -------------------

  /**
   * Creates a new {@link Task} that have a value of type Void. Because the
   * returned task returns no value, it is typically used to produce side-effects.
   *
   * @param name a name that describes the action
   * @param runnable the action that will be executed when the task is run
   * @return the new task
   */
  public static Task<Void> action(final String name, final Runnable action)
  {
    ArgumentUtil.requireNotNull(action, "action");
    return async(name, () -> {
      action.run();
      return Promises.VOID;
    }, false);
  }

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
    return async(name, () -> Promises.error(failure), false);
  }

  public static <T> Task<T> failure(final Throwable failure) {
    return failure("failure", failure);
  }

  public static <T> Task<T> callable(final String name, final Callable<? extends T> callable) {
    return async(name, () -> Promises.value(callable.call()), false);
  }

  public static <T> Task<T> callable(final Callable<? extends T> callable) {
    return callable("callable", callable);
  }

  public static <T> Task<T> async(final String name, final Callable<Promise<? extends T>> callable,
      final boolean systemHidden) {
    return async(name, context -> {
      try {
        return callable.call();
      } catch (Exception e) {
        return Promises.error(e);
      }
    }, systemHidden);
  }

  public static <T> Task<T> async(final String name, final Function<Context, Promise<? extends T>> func,
      final boolean systemHidden) {
    ArgumentUtil.requireNotNull(func, "function");
    return new BaseTask<T>(name) {
      @Override
      protected Promise<? extends T> run(Context context) throws Throwable {
        return func.apply(context);
      }

      @Override
      public ShallowTrace getShallowTrace() {
        return new ShallowTraceBuilder(super.getShallowTrace()).setSystemHidden(systemHidden).build();
      }
    };
  }

  public static <T> Task<T> async(final Function<Context, Promise<? extends T>> func, final boolean systemHidden) {
    return async("async", func, systemHidden);
  }

  public static <T> Task<T> async(final Callable<Promise<? extends T>> callable, final boolean systemHidden) {
    return async("async", callable, systemHidden);
  }

  public static <T> Task<T> blocking(final String name, final Callable<? extends T> callable, final Executor executor) {
    ArgumentUtil.requireNotNull(callable, "callable");
    return async(name, context -> {
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

  public static <T> Task<T> blocking(final Callable<? extends T> callable, final Executor executor) {
    return blocking("blocking", callable, executor);
  }

  public static <T1, T2> Tuple2Task<T1, T2> par(final Task<T1> task1,
                                                final Task<T2> task2) {
    return new Par2Task<T1, T2>("par2", task1, task2);
  }

  public static <T1, T2, T3> Tuple3Task<T1, T2, T3> par(final Task<T1> task1,
                                                        final Task<T2> task2,
                                                        final Task<T3> task3) {
    return new Par3Task<T1, T2, T3>("par3", task1, task2, task3);
  }

  public static <T1, T2, T3, T4> Tuple4Task<T1, T2, T3, T4> par(final Task<T1> task1,
                                                                final Task<T2> task2,
                                                                final Task<T3> task3,
                                                                final Task<T4> task4) {
    return new Par4Task<T1, T2, T3, T4>("par4", task1, task2, task3, task4);
  }

  public static <T1, T2, T3, T4, T5> Tuple5Task<T1, T2, T3, T4, T5> par(final Task<T1> task1,
                                                                        final Task<T2> task2,
                                                                        final Task<T3> task3,
                                                                        final Task<T4> task4,
                                                                        final Task<T5> task5) {
    return new Par5Task<T1, T2, T3, T4, T5>("par5", task1, task2, task3, task4, task5);
  }

  public static <T1, T2, T3, T4, T5, T6> Tuple6Task<T1, T2, T3, T4, T5, T6> par(final Task<T1> task1,
                                                                                final Task<T2> task2,
                                                                                final Task<T3> task3,
                                                                                final Task<T4> task4,
                                                                                final Task<T5> task5,
                                                                                final Task<T6> task6) {
    return new Par6Task<T1, T2, T3, T4, T5, T6>("par6", task1, task2, task3, task4, task5, task6);
  }

  public static <T1, T2, T3, T4, T5, T6, T7> Tuple7Task<T1, T2, T3, T4, T5, T6, T7> par(final Task<T1> task1,
                                                                                        final Task<T2> task2,
                                                                                        final Task<T3> task3,
                                                                                        final Task<T4> task4,
                                                                                        final Task<T5> task5,
                                                                                        final Task<T6> task6,
                                                                                        final Task<T7> task7) {
    return new Par7Task<T1, T2, T3, T4, T5, T6, T7>("par7", task1, task2, task3, task4, task5, task6, task7);
  }

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
