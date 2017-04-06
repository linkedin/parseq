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
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.function.Action;
import com.linkedin.parseq.function.Consumer1;
import com.linkedin.parseq.function.Failure;
import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.internal.TimeUnitHelper;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromisePropagator;
import com.linkedin.parseq.promise.PromiseTransformer;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.retry.RetriableTask;
import com.linkedin.parseq.retry.RetryPolicy;
import com.linkedin.parseq.retry.RetryPolicyBuilder;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.TraceBuilder;


/**
 * A task represents a deferred execution that also contains its resulting
 * value. In addition, tasks include tracing information that can be
 * used with various trace printers.
 * <p>
 * Tasks should be run using an {@link Engine}. They should not be run directly.
 * <p>
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public interface Task<T> extends Promise<T>, Cancellable {
  static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

  static final TaskDescriptor _taskDescriptor = TaskDescriptorFactory.getTaskDescriptor();

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
   * <p>
   * The default priority is 0. Use {@code priority < 0} to make a task
   * lower priority and {@code priority > 0} to make a task higher
   * priority.
   * <p>
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
   * <p>
   * Failures are automatically included in a trace.
   * @param serializer serialized used for converting result of this task
   * to String that will be included in this task's trace.
   */
  void setTraceValueSerializer(Function<T, String> serializer);

  /**
   * Attempts to run the task with the given context. This method is
   * reserved for use by {@link Engine} and {@link Context}.
   *
   * @param context the context to use while running this step
   * @param parent the parent of this task
   * @param predecessors that lead to the execution of this task
   */
  void contextRun(Context context, Task<?> parent, Collection<Task<?>> predecessors);

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
    return FusionTask.create(desc, this, propagator);
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
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/map-1.png" height="90" width="296"/>
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
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/map-2.png" height="90" width="296"/>
   *
   * @param <R> return type of function <code>func</code>
   * @param desc description of a mapping function, it will show up in a trace
   * @param func function to be applied to successful result of this task.
   * @return a new task which will apply given function on result of successful completion of this task
   */
  default <R> Task<R> map(final String desc, final Function1<? super T, ? extends R> func) {
    ArgumentUtil.requireNotNull(func, "function");
    return apply(desc, new PromiseTransformer<T, R>(func));
  }

  /**
   * Equivalent to {@code map("map", func)}.
   * @see #map(String, Function1)
   */
  default <R> Task<R> map(final Function1<? super T, ? extends R> func) {
    return map("map: " + _taskDescriptor.getDescription(func.getClass().getName()), func);
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
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/flatMap-1.png" height="90" width="462"/>
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
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/flatMap-2.png" height="90" width="296"/>
   * @param <R> return type of function <code>func</code>
   * @param desc description of a mapping function, it will show up in a trace
   * @param func function to be applied to successful result of this task which returns new task
   * to be executed
   * @return a new task which will apply given function on result of successful completion of this task
   * to get instance of a task which will be executed next
   */
  default <R> Task<R> flatMap(final String desc, final Function1<? super T, Task<R>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    final Function1<? super T, Task<R>> flatMapFunc = x -> {
      Task<R> t = func.apply(x);
      if (t == null) {
        throw new RuntimeException(desc + " returned null");
      } else {
        return t;
      }
    };
    final Task<Task<R>> nested = map(desc, flatMapFunc);
    nested.getShallowTraceBuilder().setSystemHidden(true);
    return flatten(desc, nested);
  }

  /**
   * Equivalent to {@code flatMap("flatMap", func)}.
   * @see #flatMap(String, Function1)
   */
  default <R> Task<R> flatMap(final Function1<? super T, Task<R>> func) {
    return flatMap("flatMap: " + _taskDescriptor.getDescription(func.getClass().getName()), func);
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
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/withSideEffect-1.png" height="120" width="868"/>
   *
   * @param desc description of a side effect, it will show up in a trace
   * @param func function to be applied on result of successful completion of this task
   * to get side effect task
   * @return a new task that will run side effect task specified by given function upon successful
   * completion of this task
   */
  default Task<T> withSideEffect(final String desc, final Function1<? super T, Task<?>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    final Task<T> that = this;
    Task<T> withSideEffectTask = async("withSideEffect", context -> {
      final Task<T> sideEffectWrapper = async(desc, ctx -> {
        SettablePromise<T> promise = Promises.settable();
        if (!that.isFailed()) {
          Task<?> sideEffect = func.apply(that.get());
          //TODO test that none of changed methods hang
          //TODO add javadoc about returning null
          if (sideEffect == null) {
            throw new RuntimeException(desc + " returned null");
          } else {
            ctx.runSideEffect(sideEffect);
          }
        }
        Promises.propagateResult(that, promise);
        return promise;
      });

      context.after(that).run(sideEffectWrapper);
      context.run(that);
      return sideEffectWrapper;
    });

    withSideEffectTask.getShallowTraceBuilder().setTaskType(TaskType.WITH_SIDE_EFFECT.getName());
    return withSideEffectTask;
  }

  /**
   * Equivalent to {@code withSideEffect("sideEffect", func)}.
   * @see #withSideEffect(String, Function1)
   */
  default Task<T> withSideEffect(final Function1<? super T, Task<?>> func) {
    return withSideEffect("sideEffect: " + _taskDescriptor.getDescription(func.getClass().getName()), func);
  }

  /**
   * Creates a new task that can be safely shared within a plan or between multiple
   * plans. Cancellation of returned task will not cause cancellation of the original task.
   * <p>
   * Sharing tasks within a plan or among different plans is generally not safe because task can
   * be cancelled if it's parent has been resolved. Imagine situation where <code>fetch</code>
   * task that fetches data from a remote server is shared among few plans. If one of those
   * plans times out then all started tasks that belong to it will be automatically cancelled.
   * This means that <code>fetch</code> may also be cancelled and this can affect other plans that
   * are still running. Similar situation can happen even within one plan if task is used multiple
   * times.
   * <p>
   * In example below <code>google</code> task has timeout 10ms what causes entire plan to fail and as a consequence
   * all tasks that belong to it that have been started - in this case <code>bing</code> task. This may
   * be problematic if <code>bing</code> task is used somewhere else.
   * <blockquote><pre>
   * final Task{@code <Response>} google = HttpClient.get("http://google.com").task();
   * final Task{@code <Response>} bing = HttpClient.get("http://bing.com").task();
   *
   * // this task will fail because google task will timeout after 10ms
   * // as a consequence bing task will be cancelled
   * final Task<?> both = Task.par(google.withTimeout(10, TimeUnit.MILLISECONDS), bing);
   * </pre></blockquote>
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/shareable-1.png" height="250" width="608"/>
   * <p>
   * <code>shareable</code> method solves above problem. Task returned by <code>shareable()</code> can be
   * can be cancelled without affecting original task.
   *<p>
   * <blockquote><pre>
   * final Task{@code <Response>} google = HttpClient.get("http://google.com").task();
   * final Task{@code <Response>} bing = HttpClient.get("http://bing.com").task();
   *
   * // this task will fail because wrapped google task will timeout after 10ms
   * // notice however that original googel and bing tasks were not cancelled
   *   final Task<?> both =
   *       Task.par(google.shareable().withTimeout(10, TimeUnit.MILLISECONDS), bing.shareable());
   * </pre></blockquote>
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/shareable-2.png" height="290" width="814"/>
   *
   * @return new task that can be safely shared within a plan or between multiple
   * plans. Cancellation of returned task will not cause cancellation of the original task.
   */
  default Task<T> shareable() {
    final Task<T> that = this;
    Task<T> shareableTask = async("shareable", context -> {
      final SettablePromise<T> result = Promises.settable();
      context.runSideEffect(that);
      Promises.propagateResult(that, result);
      return result;
    });
    shareableTask.getShallowTraceBuilder().setTaskType(TaskType.SHAREABLE.getName());
    return shareableTask;
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
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/andThen-1.png" height="90" width="296"/>
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
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/andThen-2.png" height="90" width="296"/>
   *
   * @param desc description of a consumer, it will show up in a trace
   * @param consumer consumer of a value returned by this task
   * @return a new task which will complete with result of this task
   */
  default Task<T> andThen(final String desc, final Consumer1<? super T> consumer) {
    ArgumentUtil.requireNotNull(consumer, "consumer");
    return apply(desc, new PromiseTransformer<T, T>(t -> {
      consumer.accept(t);
      return t;
    } ));
  }

  /**
   * Equivalent to {@code andThen("andThen", consumer)}.
   * @see #andThen(String, Consumer1)
   */
  default Task<T> andThen(final Consumer1<? super T> consumer) {
    return andThen("andThen: " + _taskDescriptor.getDescription(consumer.getClass().getName()), consumer);
  }

  /**
   * Creates a new task which runs given task after
   * completion of this task and completes with a result of
   * that task. Task passed in as a parameter will run even if
   * this task fails. Notice that task passed in as a parameter
   * does not depend on an actual result of this task.
   * <blockquote><pre>
   *  // task that processes payment
   *  Task{@code <PaymentStatus>} processPayment = processPayment(...);
   *
   *  // task that ships product
   *  Task{@code <ShipmentInfo>} shipProduct = shipProduct(...);
   *
   *  Task{@code <ShipmentInfo>} shipAfterPayment =
   *      processPayment.andThen("shipProductAterPayment", shipProduct);
   * </pre></blockquote>
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/andThen-3.png" height="90" width="462"/>
   *
   * @param <R> return type of the <code>task</code>
   * @param desc description of a task, it will show up in a trace
   * @param task task which will be executed after completion of this task
   * @return a new task which will run given task after completion of this task
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
    });
  }

  /**
   * Equivalent to {@code andThen("andThen", task)}.
   * @see #andThen(String, Task)
   */
  default <R> Task<R> andThen(final Task<R> task) {
    return andThen("andThen: " + task.getName(), task);
  }

  /**
   * Creates a new task that will handle failure of this task.
   * Early completion due to cancellation is not considered to be a failure.
   * If this task completes successfully, then recovery function is not called.
   * <blockquote><pre>
   *
   * // this method return task which asynchronously retrieves Person by id
   * Task{@code <Person>} fetchPerson(Long id) {
   * (...)
   * }
   *
   * // this task will fetch Person object and transform it into {@code "<first name> <last name>"}
   * // if fetching Person failed then form {@code "Member <id>"} will be return
   * Task{@code <String>} userName = fetchPerson(id)
   *      .map("toSignature", p {@code ->} p.getFirstName() + " " + p.getLastName())
   *      .recover(e {@code ->} "Member " + id);
   * </pre></blockquote>
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/recover-1.png" height="90" width="462"/>
   * <p>
   * Note that task cancellation is not considered to be a failure.
   * If this task has been cancelled then task returned by this method will also
   * be cancelled and recovery function will not be applied.
   *
   * @param desc description of a recovery function, it will show up in a trace
   * @param func recovery function which can complete task with a value depending on
   *        failure of this task
   * @return a new task which can recover from failure of this task
   */
  default Task<T> recover(final String desc, final Function1<Throwable, T> func) {
    ArgumentUtil.requireNotNull(func, "function");
    return apply(desc, (src, dst) -> {
      if (src.isFailed()) {
        if (!(Exceptions.isCancellation(src.getError()))) {
          try {
            dst.done(func.apply(src.getError()));
          } catch (Throwable t) {
            dst.fail(t);
          }
        } else {
          dst.fail(src.getError());
        }
      } else {
        dst.done(src.get());
      }
    } );
  }

  /**
   * Equivalent to {@code recover("recover", func)}.
   * @see #recover(String, Function1)
   */
  default Task<T> recover(final Function1<Throwable, T> func) {
    return recover("recover: " + _taskDescriptor.getDescription(func.getClass().getName()), func);
  }

  /**
   * Creates a new task which applies a consumer to the exception this
   * task may fail with. It is used in situations where consumer needs
   * to be called after failure of this task. Result of task returned by
   * this method will be exactly the same as result of this task.
   * <blockquote><pre>
   *  Task{@code <String>} failing = Task.callable("greeting", () {@code ->} {
   *    return "Hello World".substring(100);
   *  });
   *
   *  // this task will print out java.lang.StringIndexOutOfBoundsException
   *  // and complete with that exception as a reason for failure
   *  Task{@code <String>} sayHello = failing.onFailure("printFailure", System.out::println);
   * </pre></blockquote>
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/onFailure-1.png" height="90" width="296"/>
   * <p>
   * If this task completes successfully then consumer will not be called.
   * <blockquote><pre>
   *  Task{@code <String>} hello = Task.value("greeting", "Hello World");
   *
   *  // this task will return "Hello World"
   *  Task{@code <String>} sayHello = hello.onFailure(System.out::println);
   * </pre></blockquote>
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/onFailure-2.png" height="90" width="296"/>
   * <p>
   * Exceptions thrown by a consumer will be ignored.
   * <p>
   * Note that task cancellation is not considered to be a failure.
   * If this task has been cancelled then task returned by this method will also
   * be cancelled and consumer will not be called.
   *
   * @param desc description of a consumer, it will show up in a trace
   * @param consumer consumer of an exception this task failed with
   * @return a new task which will complete with result of this task
   */
  default Task<T> onFailure(final String desc, final Consumer1<Throwable> consumer) {
    ArgumentUtil.requireNotNull(consumer, "consumer");
    return apply(desc, (src, dst) -> {
      if (src.isFailed()) {
        if (!(Exceptions.isCancellation(src.getError()))) {
          try {
            consumer.accept(src.getError());
          } catch (Exception e) {
            //exceptions thrown by consumer are logged and ignored
            LOGGER.error("Exception thrown by onFailure consumer: ", e);
          } finally {
            dst.fail(src.getError());
          }
        } else {
          dst.fail(src.getError());
        }
      } else {
        dst.done(src.get());
      }
    } );
  }

  /**
   * Equivalent to {@code onFailure("onFailure", consumer)}.
   * @see #onFailure(String, Consumer1)
   */
  default Task<T> onFailure(final Consumer1<Throwable> consumer) {
    return onFailure("onFailure: " + _taskDescriptor.getDescription(consumer.getClass().getName()), consumer);
  }

  /**
   * This method transforms {@code Task<T>} into {@code Task<Try<T>>}.
   * It allows explicit handling of failures by returning potential exceptions as a result of
   * task execution. Task returned by this method will always complete successfully
   * unless it has been cancelled.
   * If this task completes successfully then return task will be
   * completed with result value wrapped with {@link Success}.
   * <blockquote><pre>
   *  Task{@code <String>} hello = Task.value("greeting", "Hello World");
   *
   *  // this task will complete with Success("Hello World")
   *  Task{@code <Try<String>>} helloTry = hello.toTry("try");
   * </pre></blockquote>
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/toTry-1.png" height="90" width="296"/>
   * <p>
   * If this task is completed with an exception then the returned task will be
   * completed with an exception wrapped with {@link Failure}.
   * <blockquote><pre>
   *  Task{@code <String>} failing = Task.callable("greeting", () {@code ->} {
   *      return "Hello World".substring(100);
   *  });
   *
   *  // this task will complete successfully with Failure(java.lang.StringIndexOutOfBoundsException)
   *  Task{@code <Try<String>>} failingTry = failing.toTry("try");
   * </pre></blockquote>
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/toTry-2.png" height="90" width="296"/>
   * <p>
   * All failures are automatically propagated and it is usually enough to use
   * {@link #recover(String, Function1) recover} or {@link #recoverWith(String, Function1) recoverWith}.
   * <p>
   * Note that task cancellation is not considered to be a failure.
   * If this task has been cancelled then task returned by this method will also
   * be cancelled.
   *
   * @param desc description of a consumer, it will show up in a trace
   * @return a new task that will complete successfully with the result of this task
   * @see Try
   * @see #recover(String, Function1) recover
   * @see #recoverWith(String, Function1) recoverWith
   * @see CancellationException
   */
  default Task<Try<T>> toTry(final String desc) {
    return apply(desc, (src, dst) -> {
      final Try<T> tryT = Promises.toTry(src);
      if (tryT.isFailed() && Exceptions.isCancellation(tryT.getError())) {
        dst.fail(src.getError());
      } else {
        dst.done(Promises.toTry(src));
      }
    } );
  }

  /**
   * Equivalent to {@code toTry("toTry")}.
   * @see #toTry(String)
   */
  default Task<Try<T>> toTry() {
    return toTry("toTry");
  }

  /**
   * Creates a new task that applies a transformation to the result of this
   * task. This method allows handling both successful completion and failure
   * at the same time.
   * <blockquote><pre>
   * Task{@code <Integer>} num = ...
   *
   * // this task will complete with either complete successfully
   * // with String representation of num or fail with  MyLibException
   * Task{@code <String>} text = num.transform("toString", t {@code ->} {
   *   if (t.isFailed()) {
   *     return Failure.of(new MyLibException(t.getError()));
   *   } else {
   *     return Success.of(String.valueOf(t.get()));
   *   }
   * });
   * </pre></blockquote>
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/transform-1.png" height="90" width="296"/>
   * <p>
   * Note that task cancellation is not considered to be a failure.
   * If this task has been cancelled then task returned by this method will also
   * be cancelled and transformation will not be applied.
   *
   * @param <R> type parameter of function <code>func</code> return <code>Try</code>
   * @param desc description of a consumer, it will show up in a trace
   * @param func a transformation to be applied to the result of this task
   * @return a new task that will apply a transformation to the result of this task
   * @see Try
   */
  default <R> Task<R> transform(final String desc, final Function1<Try<T>, Try<R>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    return apply(desc, (src, dst) -> {
      final Try<T> tryT = Promises.toTry(src);
      if (tryT.isFailed() && Exceptions.isCancellation(tryT.getError())) {
        dst.fail(src.getError());
      } else {
        try {
          final Try<R> tryR = func.apply(tryT);
          if (tryR.isFailed()) {
            dst.fail(tryR.getError());
          } else {
            dst.done(tryR.get());
          }
        } catch (Exception e) {
          dst.fail(e);
        }
      }
    } );
  }

  /**
   * Equivalent to {@code transform("transform", func)}.
   * @see #transform(String, Function1)
   */
  default <R> Task<R> transform(final Function1<Try<T>, Try<R>> func) {
    return transform("transform: " + _taskDescriptor.getDescription(func.getClass().getName()), func);
  }

  /**
   * Creates a new task that will handle failure of this task.
   * Early completion due to cancellation is not considered to be a failure.
   * If this task completes successfully, then recovery function is not called.
   * <blockquote><pre>
   *
   * // this method return task which asynchronously retrieves Person by id from cache
   * Task{@code <Person>} fetchFromCache(Long id) {
   * (...)
   * }
   *
   * // this method return task which asynchronously retrieves Person by id from DB
   * Task{@code <Person>} fetchFromDB(Long id) {
   * (...)
   * }
   *
   * // this task will try to fetch Person from cache and
   * // if it fails for any reason it will attempt to fetch from DB
   * Task{@code <Person>} user = fetchFromCache(id).recoverWith(e {@code ->} fetchFromDB(id));
   * </pre></blockquote>
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/recoverWith-1.png" height="90" width="462"/>
   * <p>
   * If recovery task fails then returned task is completed with that failure.
   * <p>
   * Note that task cancellation is not considered to be a failure.
   * If this task has been cancelled then task returned by this method will also
   * be cancelled and recovery function will not be applied.
   *
   * @param desc description of a recovery function, it will show up in a trace
   * @param func recovery function provides task which will be used to recover from
   * failure of this task
   * @return a new task which can recover from failure of this task
   */
  default Task<T> recoverWith(final String desc, final Function1<Throwable, Task<T>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    final Task<T> that = this;
    Task<T> recoverWithTask = async(desc, context -> {
      final SettablePromise<T> result = Promises.settable();
      final Task<T> recovery = async("recovery", ctx -> {
        final SettablePromise<T> recoveryResult = Promises.settable();
        if (that.isFailed()) {
          if (!(Exceptions.isCancellation(that.getError()))) {
            try {
              Task<T> r = func.apply(that.getError());
              if (r == null) {
                throw new RuntimeException(desc + " returned null");
              }
              Promises.propagateResult(r, recoveryResult);
              ctx.run(r);
            } catch (Throwable t) {
              recoveryResult.fail(t);
            }
          } else {
            recoveryResult.fail(that.getError());
          }
        } else {
          recoveryResult.done(that.get());
        }
        return recoveryResult;
      });
      recovery.getShallowTraceBuilder().setSystemHidden(true);
      recovery.getShallowTraceBuilder().setTaskType(TaskType.RECOVER.getName());
      Promises.propagateResult(recovery, result);
      context.after(that).run(recovery);
      context.run(that);
      return result;
    });
    recoverWithTask.getShallowTraceBuilder().setTaskType(TaskType.WITH_RECOVER.getName());
    return recoverWithTask;
  }

  /**
   * Equivalent to {@code recoverWith("recoverWith", func)}.
   * @see #recoverWith(String, Function1)
   */
  default Task<T> recoverWith(final Function1<Throwable, Task<T>> func) {
    return recoverWith("recoverWith: " + _taskDescriptor.getDescription(func.getClass().getName()), func);
  }

  /**
   * Equivalent to {@code withTimeout(null, time, unit)}.
   * @see #withTimeout(String, long, TimeUnit)
   */
  default Task<T> withTimeout(final long time, final TimeUnit unit) {
    return withTimeout(null, time, unit);
  }

  /**
   * Creates a new task that has a timeout associated with it. If this task finishes
   * before the timeout occurs then returned task will be completed with the value of this task.
   * If this task does not complete in the given time then returned task will
   * fail with a {@link TimeoutException} as a reason of failure and this task will be cancelled.
   * <blockquote><pre>
   * final Task<Response> google = HttpClient.get("http://google.com").task()
   *     .withTimeout(10, TimeUnit.MILLISECONDS);
   * </pre></blockquote>
   * <img src="https://raw.githubusercontent.com/linkedin/parseq/master/src/com/linkedin/parseq/doc-files/withTimeout-1.png" height="150" width="318"/>
   *
   * @param desc description of a timeout. There is no need to put timeout value here because it will be automatically
   * included. Full description of a timeout will be: {@code "withTimeout " + time + " " + TimeUnitHelper.toString(unit) +
   * (desc != null ? " " + desc : "")}. It is a good idea to put information that will help understand why the timeout
   * was specified e.g. if timeout was specified by a configuration, the configuration parameter name would be a useful
   * information
   * @param time the time to wait before timing out
   * @param unit the units for the time
   * @return the new task with a timeout
   */
  default Task<T> withTimeout(final String desc, final long time, final TimeUnit unit) {
    final Task<T> that = this;
    final String taskName = "withTimeout " + time + TimeUnitHelper.toString(unit) +
        (desc != null ? " " + desc : "");
    Task<T> withTimeout = async(taskName, ctx -> {
      final AtomicBoolean committed = new AtomicBoolean();
      final SettablePromise<T> result = Promises.settable();
      final Task<?> timeoutTask = Task.action("timeout", () -> {
        if (committed.compareAndSet(false, true)) {
          result.fail(Exceptions.timeoutException(taskName));
        }
      } );
      //timeout tasks should run as early as possible
      timeoutTask.setPriority(Priority.MAX_PRIORITY);
      timeoutTask.getShallowTraceBuilder().setTaskType(TaskType.TIMEOUT.getName());
      ctx.createTimer(time, unit, timeoutTask);
      that.addListener(p -> {
        if (committed.compareAndSet(false, true)) {
          Promises.propagateResult(that, result);
        }
      } );
      //we want to schedule this task as soon as possible
      //because timeout timer has started ticking
      that.setPriority(Priority.MAX_PRIORITY);
      ctx.run(that);
      return result;
    });
    withTimeout.setPriority(getPriority());
    withTimeout.getShallowTraceBuilder().setTaskType(TaskType.WITH_TIMEOUT.getName());
    return withTimeout;
  }

  /**
   * Converts {@code Task<Task<R>>} into {@code Task<R>}.
   * @param <R> return type of nested <code>task</code>
   * @param desc description that will show up in a trace
   * @param task task to be flattened
   * @return flattened task
   */
  public static <R> Task<R> flatten(final String desc, final Task<Task<R>> task) {
    ArgumentUtil.requireNotNull(task, "task");
    Task<R> flattenTask = async(desc, context -> {
      final SettablePromise<R> result = Promises.settable();
      context.after(task).run(() -> {
        try {
          if (!task.isFailed()) {
            Task<R> t = task.get();
            if (t == null) {
              throw new RuntimeException(desc + " returned null");
            } else {
              Promises.propagateResult(t, result);
              return t;
            }
          } else {
            result.fail(task.getError());
          }
        } catch (Throwable t) {
          result.fail(t);
        }
        return null;
      } );
      context.run(task);
      return result;
    });
    flattenTask.getShallowTraceBuilder().setTaskType(TaskType.FLATTEN.getName());
    return flattenTask;
  }

  /**
   * Equivalent to {@code flatten("flatten", task)}.
   * @see #flatten(String, Task)
   */

  public static <R> Task<R> flatten(final Task<Task<R>> task) {
    return flatten("flatten", task);
  }

  //------------------- static factory methods -------------------

  /**
   * Creates a new task that have a value of type {@code Void}. Because the
   * returned task returns no value, it is typically used to produce side effects.
   * It is not appropriate for long running or blocking actions. If action is
   * long running or blocking use {@link #blocking(String, Callable, Executor) blocking} method.
   *
   * <blockquote><pre>
   * // this task will print "Hello" on standard output
   * Task{@code <Void>} task = Task.action("greeting", () {@code ->} System.out.println("Hello"));
   * </pre></blockquote>
   *
   * Returned task will fail if {@code Action} passed in as a parameter throws
   * an exception.
   * <blockquote><pre>
   * // this task will fail with java.lang.ArithmeticException
   * Task{@code <Void>} task = Task.action("division", () {@code ->} System.out.println(2 / 0));
   * </pre></blockquote>
   * <p>
   * @param desc a description the action, it will show up in a trace
   * @param action the action that will be executed when the task is run
   * @return the new task that will execute the action
   */
  public static Task<Void> action(final String desc, final Action action) {
    ArgumentUtil.requireNotNull(action, "action");
    return async(desc, () -> {
      action.run();
      return Promises.VOID;
    });
  }

  /**
   * Equivalent to {@code action("action", action)}.
   * @see #action(String, Action)
   */
  public static Task<Void> action(final Action action) {
    return action("action: " + _taskDescriptor.getDescription(action.getClass().getName()), action);
  }

  /**
   * Creates a new task that will be resolved with given value when it is
   * executed. Note that this task is not initially completed.
   *
   * @param <T> type of the value
   * @param desc a description of the value, it will show up in a trace
   * @param value a value the task will be resolved with
   * @return new task that will be resolved with given value when it is
   * executed
   */
  public static <T> Task<T> value(final String desc, final T value) {
    return FusionTask.create(desc, (src, dst) -> {
      dst.done(value);
    } );
  }

  /**
   * Equivalent to {@code value("value", value)}.
   * @see #value(String, Object)
   */
  public static <T> Task<T> value(final T value) {
    return value("value", value);
  }

  /**
   * Creates a new task that will be fail with given exception when it is
   * executed. Note that this task is not initially completed.
   *
   * @param <T> type parameter of the returned task
   * @param desc a description of the failure, it will show up in a trace
   * @param failure a failure the task will fail with
   * @return new task that will fail with given failure when it is
   * executed
   */
  public static <T> Task<T> failure(final String desc, final Throwable failure) {
    ArgumentUtil.requireNotNull(failure, "failure");
    return FusionTask.create(desc, (src, dst) -> {
      dst.fail(failure);
    } );
  }

  /**
   * Equivalent to {@code failure("failure", failure)}.
   * @see #failure(String, Throwable)
   */
  public static <T> Task<T> failure(final Throwable failure) {
    return failure("failure", failure);
  }

  /**
   * Creates a new task that's value will be set to the value returned
   * from the supplied callable. This task is useful when doing basic
   * computation that does not require asynchrony. It is not appropriate for
   * long running or blocking callables. If callable is long running or blocking
   * use {@link #blocking(String, Callable, Executor) blocking} method.
   *
   * <blockquote><pre>
   * // this task will complete with {@code String} representing current time
   * Task{@code <String>} task = Task.callable("current time", () {@code ->} new Date().toString());
   * </pre></blockquote>
   *
   * Returned task will fail if callable passed in as a parameter throws
   * an exception.
   * <blockquote><pre>
   * // this task will fail with java.lang.ArithmeticException
   * Task{@code <Integer>} task = Task.callable("division", () {@code ->} 2 / 0);
   * </pre></blockquote>
   * <p>
   * @param <T> the type of the return value for this task
   * @param name a name that describes the task, it will show up in a trace
   * @param callable the callable to execute when this task is run
   * @return the new task that will invoke the callable and complete with it's result
   */
  public static <T> Task<T> callable(final String name, final Callable<? extends T> callable) {
    ArgumentUtil.requireNotNull(callable, "callable");
    return FusionTask.create(name, (src, dst) -> {
      try {
        dst.done(callable.call());
      } catch (Throwable t) {
        dst.fail(t);
      }
    } );
  }

  /**
   * Equivalent to {@code callable("callable", callable)}.
   * @see #callable(String, Callable)
   */
  public static <T> Task<T> callable(final Callable<? extends T> callable) {
    return callable("callable: " + _taskDescriptor.getDescription(callable.getClass().getName()), callable);
  }

  /**
   * Creates a new task from a callable that returns a {@link Promise}.
   * This method is mainly used to integrate ParSeq with 3rd party
   * asynchronous libraries. It should not be used in order to compose
   * or manipulate existing tasks. The following example shows how to integrate
   * <a href="https://github.com/AsyncHttpClient">AsyncHttpClient</a> with ParSeq.
   *
   * <blockquote><pre>
   *  // Creates a task that asynchronouslyexecutes given HTTP request
   *  // and will complete with HTTP response. It uses asyncHttpRequest()
   *  // method as a lambda of shape: ThrowableCallable{@code <Promise<Response>>}.
   *  Task{@code <Response>} httpTask(final Request request) {
   *    return Task.async(() {@code ->} asyncHttpRequest(request), false);
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
   * </pre></blockquote>
   *
   * This method is not appropriate for long running or blocking callables.
   * If callable is long running or blocking use
   * {@link #blocking(String, Callable, Executor) blocking} method.
   * <p>
   *
   * @param <T> the type of the return value for this task
   * @param name a name that describes the task, it will show up in a trace
   * @param callable a callable to execute when this task is run, it must return
   * a {@code Promise<T>}
   * @return a new task that will invoke the callable and complete with result
   * returned by a {@code Promise} returned by it
   * @see Promise
   */
  public static <T> Task<T> async(final String name, final Callable<Promise<? extends T>> callable) {
    ArgumentUtil.requireNotNull(callable, "callable");
    return async(name, context -> {
      try {
        return callable.call();
      } catch (Throwable e) {
        return Promises.error(e);
      }
    });
  }

  /**
   * Equivalent to {@code async("async", callable)}.
   * @see #async(String, Callable)
   */
  public static <T> Task<T> async(final Callable<Promise<? extends T>> callable) {
    return async("async: " + _taskDescriptor.getDescription(callable.getClass().getName()), callable);
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
   * @return a new task that will invoke the function and complete with result
   * returned by a {@code Promise} returned by it
   * @see Context
   * @see Promise
   */
  public static <T> Task<T> async(final String name, final Function1<Context, Promise<? extends T>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    Task<T> task = new BaseTask<T>(name) {
      @Override
      protected Promise<? extends T> run(Context context) throws Throwable {
        return func.apply(context);
      }
    };

    return task;
  }

  /**
   * Equivalent to {@code async("async", func)}.
   * @see #async(String, Function1)
   */
  public static <T> Task<T> async(final Function1<Context, Promise<? extends T>> func) {
    return async("async: " + _taskDescriptor.getDescription(func.getClass().getName()), func);
  }

  /**
   * This method provides a way to create an asynchronous task from
   * a blocking or long running callables like JDBC requests.
   * Unlike with tasks created with all other methods a callable passed
   * as a parameter is not executed on ParSeq's thread but instead it is
   * executed on specified {@link Executor}. It means that callable
   * does not get any special memory consistency guarantees and should not
   * attempt to use shared state.
   * <p>
   * In order to avoid creating tasks that never complete the Executor passed in as a
   * parameter <b>must</b> signal execution rejection by throwing an exception.
   * <p>
   * In order to prevent blocking ParSeq threads the Executor passed in as a
   * parameter <b>must not</b> use {@link ThreadPoolExecutor.CallerRunsPolicy}
   * as a rejection policy.
   *
   * @param <T> the type of the return value for this task
   * @param name a name that describes the task, it will show up in a trace
   * @param callable a callable that will provide result
   * @param executor {@code Executor} that will be used to run the callable
   * @return a new task that will submit the callable to given executor and complete
   * with result returned by that callable
   */
  public static <T> Task<T> blocking(final String name, final Callable<? extends T> callable, final Executor executor) {
    ArgumentUtil.requireNotNull(callable, "callable");
    ArgumentUtil.requireNotNull(callable, "executor");
    Task<T> blockingTask = async(name, () -> {
      final SettablePromise<T> promise = Promises.settable();
      executor.execute(() -> {
        try {
          promise.done(callable.call());
        } catch (Throwable t) {
          promise.fail(t);
        }
      } );
      return promise;
    });
    blockingTask.getShallowTraceBuilder().setTaskType(TaskType.BLOCKING.getName());
    return blockingTask;
  }

  /**
   * Equivalent to {@code blocking("blocking", callable, executor)}.
   * @see #blocking(String, Callable, Executor)
   */
  public static <T> Task<T> blocking(final Callable<? extends T> callable, final Executor executor) {
    return blocking("blocking: " + _taskDescriptor.getDescription(callable.getClass().getName()), callable, executor);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <blockquote><pre>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </pre></blockquote>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks and other
   * tasks will be cancelled (if possible).
   * <p>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2> Tuple2Task<T1, T2> par(final Task<T1> task1, final Task<T2> task2) {
    ArgumentUtil.requireNotNull(task1, "task1");
    ArgumentUtil.requireNotNull(task2, "task2");
    return new Par2Task<T1, T2>("par2", task1, task2);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <blockquote><pre>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </pre></blockquote>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks and other
   * tasks will be cancelled (if possible).
   * <p>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3> Tuple3Task<T1, T2, T3> par(final Task<T1> task1, final Task<T2> task2,
      final Task<T3> task3) {
    ArgumentUtil.requireNotNull(task1, "task1");
    ArgumentUtil.requireNotNull(task2, "task2");
    ArgumentUtil.requireNotNull(task3, "task3");
    return new Par3Task<T1, T2, T3>("par3", task1, task2, task3);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <blockquote><pre>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </pre></blockquote>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks and other
   * tasks will be cancelled (if possible).
   * <p>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4> Tuple4Task<T1, T2, T3, T4> par(final Task<T1> task1, final Task<T2> task2,
      final Task<T3> task3, final Task<T4> task4) {
    ArgumentUtil.requireNotNull(task1, "task1");
    ArgumentUtil.requireNotNull(task2, "task2");
    ArgumentUtil.requireNotNull(task3, "task3");
    ArgumentUtil.requireNotNull(task4, "task4");
    return new Par4Task<T1, T2, T3, T4>("par4", task1, task2, task3, task4);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <blockquote><pre>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </pre></blockquote>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks and other
   * tasks will be cancelled (if possible).
   * <p>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4, T5> Tuple5Task<T1, T2, T3, T4, T5> par(final Task<T1> task1, final Task<T2> task2,
      final Task<T3> task3, final Task<T4> task4, final Task<T5> task5) {
    ArgumentUtil.requireNotNull(task1, "task1");
    ArgumentUtil.requireNotNull(task2, "task2");
    ArgumentUtil.requireNotNull(task3, "task3");
    ArgumentUtil.requireNotNull(task4, "task4");
    ArgumentUtil.requireNotNull(task5, "task5");
    return new Par5Task<T1, T2, T3, T4, T5>("par5", task1, task2, task3, task4, task5);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <blockquote><pre>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </pre></blockquote>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks and other
   * tasks will be cancelled (if possible).
   * <p>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4, T5, T6> Tuple6Task<T1, T2, T3, T4, T5, T6> par(final Task<T1> task1,
      final Task<T2> task2, final Task<T3> task3, final Task<T4> task4, final Task<T5> task5, final Task<T6> task6) {
    ArgumentUtil.requireNotNull(task1, "task1");
    ArgumentUtil.requireNotNull(task2, "task2");
    ArgumentUtil.requireNotNull(task3, "task3");
    ArgumentUtil.requireNotNull(task4, "task4");
    ArgumentUtil.requireNotNull(task5, "task5");
    ArgumentUtil.requireNotNull(task6, "task6");
    return new Par6Task<T1, T2, T3, T4, T5, T6>("par6", task1, task2, task3, task4, task5, task6);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <blockquote><pre>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </pre></blockquote>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks and other
   * tasks will be cancelled (if possible).
   * <p>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4, T5, T6, T7> Tuple7Task<T1, T2, T3, T4, T5, T6, T7> par(final Task<T1> task1,
      final Task<T2> task2, final Task<T3> task3, final Task<T4> task4, final Task<T5> task5, final Task<T6> task6,
      final Task<T7> task7) {
    ArgumentUtil.requireNotNull(task1, "task1");
    ArgumentUtil.requireNotNull(task2, "task2");
    ArgumentUtil.requireNotNull(task3, "task3");
    ArgumentUtil.requireNotNull(task4, "task4");
    ArgumentUtil.requireNotNull(task5, "task5");
    ArgumentUtil.requireNotNull(task6, "task6");
    ArgumentUtil.requireNotNull(task7, "task7");
    return new Par7Task<T1, T2, T3, T4, T5, T6, T7>("par7", task1, task2, task3, task4, task5, task6, task7);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <blockquote><pre>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </pre></blockquote>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks and other
   * tasks will be cancelled (if possible).
   * <p>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> par(final Task<T1> task1,
      final Task<T2> task2, final Task<T3> task3, final Task<T4> task4, final Task<T5> task5, final Task<T6> task6,
      final Task<T7> task7, final Task<T8> task8) {
    ArgumentUtil.requireNotNull(task1, "task1");
    ArgumentUtil.requireNotNull(task2, "task2");
    ArgumentUtil.requireNotNull(task3, "task3");
    ArgumentUtil.requireNotNull(task4, "task4");
    ArgumentUtil.requireNotNull(task5, "task5");
    ArgumentUtil.requireNotNull(task6, "task6");
    ArgumentUtil.requireNotNull(task7, "task7");
    ArgumentUtil.requireNotNull(task8, "task8");
    return new Par8Task<T1, T2, T3, T4, T5, T6, T7, T8>("par8", task1, task2, task3, task4, task5, task6, task7, task8);
  }

  /**
   * Creates a new task that will run given tasks in parallel. Returned task
   * will be resolved with results of all tasks as soon as all of them has
   * been completed successfully.
   *
   * <blockquote><pre>
   *  // this task will asynchronously fetch user and company in parallel
   *  // and create signature in a form {@code "<first name> <last name> working for <company>"}
   *  Task{@code <String>} signature =
   *      Task.par(fetchUser(userId), fetchCompany(companyId))
   *        .map((user, company) {@code ->}
   *          user.getFirstName() + user.getLastName() + " working for " + company.getName());
   * </pre></blockquote>
   *
   * If any of tasks passed in as a parameters fails then returned task will also fail immediately.
   * In this case returned task will be resolved with error from the first of failing tasks and other
   * tasks will be cancelled (if possible).
   * <p>
   * @return task that will run given tasks in parallel
   */
  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> par(
      final Task<T1> task1, final Task<T2> task2, final Task<T3> task3, final Task<T4> task4, final Task<T5> task5,
      final Task<T6> task6, final Task<T7> task7, final Task<T8> task8, final Task<T9> task9) {
    ArgumentUtil.requireNotNull(task1, "task1");
    ArgumentUtil.requireNotNull(task2, "task2");
    ArgumentUtil.requireNotNull(task3, "task3");
    ArgumentUtil.requireNotNull(task4, "task4");
    ArgumentUtil.requireNotNull(task5, "task5");
    ArgumentUtil.requireNotNull(task6, "task6");
    ArgumentUtil.requireNotNull(task7, "task7");
    ArgumentUtil.requireNotNull(task8, "task8");
    ArgumentUtil.requireNotNull(task9, "task9");
    return new Par9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9>("par9", task1, task2, task3, task4, task5, task6, task7,
        task8, task9);
  }

  /**
   * Creates a new task that will run each of the supplied tasks in parallel (e.g.
   * tasks[0] can be run at the same time as tasks[1]).
   * <p>
   * When all tasks complete successfully, you can use
   * {@link com.linkedin.parseq.ParTask#get()} to get a list of the results. If
   * at least one task failed, then this task will also be marked as failed. Use
   * {@link com.linkedin.parseq.ParTask#getTasks()} or
   * {@link com.linkedin.parseq.ParTask#getSuccessful()} to get results in this
   * case.
   * <p>
   * If the Iterable of tasks is empty, {@link com.linkedin.parseq.ParTask#get()}
   * will return an empty list.
   * <p>
   * Note that resulting task does not fast-fail e.g. if one of the tasks fail others
   * are not cancelled. This is different behavior than {@link Task#par(Task, Task)} where
   * resulting task fast-fails.
   *
   * @param tasks the tasks to run in parallel
   * @return The results of the tasks
   */
  public static <T> ParTask<T> par(final Iterable<? extends Task<? extends T>> tasks) {
    return tasks.iterator().hasNext()
        ? new ParTaskImpl<T>("par", tasks)
        : new ParTaskImpl<T>("par");
  }

  /**
   * Equivalent to {@code withRetryPolicy("operation", policy, taskSupplier)}.
   * @see #withRetryPolicy(String, RetryPolicy, Callable)
   */
  public static <T> Task<T> withRetryPolicy(RetryPolicy policy, Callable<Task<T>> taskSupplier) {
    return withRetryPolicy("operation", policy, taskSupplier);
  }

  /**
   * Equivalent to {@code withRetryPolicy("operation", policy, taskSupplier)}.
   * @see #withRetryPolicy(String, RetryPolicy, Callable)
   */
  public static <T> Task<T> withRetryPolicy(RetryPolicy policy, Function1<Integer, Task<T>> taskSupplier) {
    return withRetryPolicy("operation", policy, taskSupplier);
  }

  /**
   * Creates a new task that will run and potentially retry task returned
   * by a {@code taskSupplier}. Use {@link RetryPolicyBuilder} to create desired
   * retry policy.
   * <p>
   * NOTE: using tasks with retry can have significant performance implications. For example, HTTP request may
   * failed due to server overload and retrying request may prevent server from recovering. In this example
   * a better approach is the opposite: decrease number of requests to the server unit it is fully recovered.
   * Please make sure you have considered why the first task failed and why is it reasonable to expect retry task
   * to complete successfully. It is also highly recommended to specify reasonable backoff and termination conditions.
   *
   * @param name A name of the task that needs to be retried.
   * @param policy Retry policy that will control this task's retry behavior.
   * @param taskSupplier A task generator function.
   * @param <T> the type of the return value for this task
   */
  public static <T> Task<T> withRetryPolicy(String name, RetryPolicy policy, Callable<Task<T>> taskSupplier) {
    return withRetryPolicy(name, policy, attempt -> taskSupplier.call());
  }

  /**
   * Creates a new task that will run and potentially retry task returned
   * by a {@code taskSupplier}. Use {@link RetryPolicyBuilder} to create desired
   * retry policy.
   * <p>
   * NOTE: using tasks with retry can have significant performance implications. For example, HTTP request may
   * failed due to server overload and retrying request may prevent server from recovering. In this example
   * a better approach is the opposite: decrease number of requests to the server unit it is fully recovered.
   * Please make sure you have considered why the first task failed and why is it reasonable to expect retry task
   * to complete successfully. It is also highly recommended to specify reasonable backoff and termination conditions.
   *
   * @param name A name of the task that needs to be retried.
   * @param policy Retry policy that will control this task's retry behavior.
   * @param taskSupplier A task generator function. It will receive a zero-based attempt number as a parameter.
   * @param <T> the type of the return value for this task
   * @see RetryPolicyBuilder
   */
  public static <T> Task<T> withRetryPolicy(String name, RetryPolicy policy, Function1<Integer, Task<T>> taskSupplier) {
    return RetriableTask.withRetryPolicy(name, policy, taskSupplier);
  }

}
