package com.linkedin.parseq.retry;

import com.linkedin.parseq.Context;
import com.linkedin.parseq.Priority;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A parseq task wrapper that supports arbitrary retry policies.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public final class RetriableTask<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RetriableTask.class);

  /** A name of the task that needs to be retried. */
  private final String _name;

  /** A task generator function. It will receive a zero-based attempt number as a parameter. */
  private final Function<Integer, Task<T>> _taskFunction;

  /** Retry policy which will control this task's behavior. */
  private final RetryPolicy _policy;

  /** Start time for the very first attempt. */
  private long _startedAt;

  /**
   * A parseq task wrapper that supports arbitrary retry policies.
   *
   * @param name A name of the task that needs to be retried.
   * @param policy Retry policy that will control this task's behavior.
   * @param taskFunction A task generator function. It will receive a zero-based attempt number as a parameter.
   */
  private RetriableTask(String name, RetryPolicy policy, Function<Integer, Task<T>> taskFunction)
  {
    ArgumentUtil.requireNotNull(name, "name");
    ArgumentUtil.requireNotNull(policy, "policy");
    ArgumentUtil.requireNotNull(taskFunction, "taskFunction");

    _name = name;
    _policy = policy;
    _taskFunction = taskFunction;
  }

  /**
   * A helper for creating task wrapper with associated retry policy.
   *
   * @param policy Retry policy that will control this task's behavior.
   * @param taskSupplier A task generator function.
   * @param <U> Type of a task result.
   */
  public static <U> Task<U> withRetryPolicy(RetryPolicy policy, Supplier<Task<U>> taskSupplier) {
    return withRetryPolicy("operation", policy, attempt -> taskSupplier.get());
  }

  /**
   * A helper for creating task wrapper with associated retry policy.
   *
   * @param policy Retry policy that will control this task's behavior.
   * @param taskFunction A task generator function. It will receive a zero-based attempt number as a parameter.
   * @param <U> Type of a task result.
   */
  public static <U> Task<U> withRetryPolicy(RetryPolicy policy, Function<Integer, Task<U>> taskFunction) {
    return withRetryPolicy("operation", policy, taskFunction);
  }

  /**
   * A helper for creating task wrapper with associated retry policy.
   *
   * @param name A name of the task that needs to be retried.
   * @param policy Retry policy that will control this task's behavior.
   * @param taskSupplier A task generator function.
   * @param <U> Type of a task result.
   */
  public static <U> Task<U> withRetryPolicy(String name, RetryPolicy policy, Supplier<Task<U>> taskSupplier) {
    return withRetryPolicy(name, policy, attempt -> taskSupplier.get());
  }

  /**
   * A helper for creating task wrapper with associated retry policy.
   *
   * @param name A name of the task that needs to be retried.
   * @param policy Retry policy that will control this task's behavior.
   * @param taskFunction A task generator function. It will receive a zero-based attempt number as a parameter.
   * @param <U> Type of a task result.
   */
  public static <U> Task<U> withRetryPolicy(String name, RetryPolicy policy, Function<Integer, Task<U>> taskFunction) {
    RetriableTask<U> retriableTask = new RetriableTask<>(name, policy, taskFunction);
    return Task.async(name + " retriableTask", retriableTask::run);
  }

  /** Create a wrapped task with associated recovery task that will retry if necessary. */
  private Task<T> wrap(int attempt) {
    Task<T> task = _taskFunction.apply(attempt);

    return Task.async(_policy.getName() + ", attempt " + attempt, context -> {
      final SettablePromise<T> result = Promises.settable();

      final Task<T> recovery = Task.async(_name + " recovery", recoveryContext -> {
        final SettablePromise<T> recoveryResult = Promises.settable();

        if (task.isFailed()) {
          // Failed task will cause retry to be scheduled.
          ErrorClassification errorClassification = _policy.getErrorClassifier().apply(task.getError());
          retry(attempt + 1, task.getError(), errorClassification, recoveryContext, recoveryResult);
        } else {
          recoveryResult.done(task.get());
        }

        return recoveryResult;
      });

      // Recovery task should run immediately after the original task to process its error.
      recovery.setPriority(Priority.MAX_PRIORITY);
      recovery.getShallowTraceBuilder().setSystemHidden(true);
      Promises.propagateResult(recovery, result);
      context.after(task).run(recovery);
      context.run(task);

      return result;
    });
  }

  /** Invoke event monitors and schedule a retry if policy allows. */
  private void retry(int attempt, Throwable error, ErrorClassification errorClassification, Context recoveryContext, SettablePromise<T> recoveryResult) {
    long backoffTime = _policy.getBackoffPolicy().nextBackoff(attempt, error);

    if (errorClassification.isFatal()) {
      // For fatal errors there are no retries.
      LOGGER.debug(String.format("Attempt %s of %s interrupted: %s", attempt, _name, error.getMessage()));
      recoveryResult.fail(error);
    } else if (_policy.getTerminationPolicy().shouldTerminate(attempt, System.currentTimeMillis() - _startedAt + backoffTime)) {
      // Retry policy commands that no more retries should be done.
      LOGGER.debug(String.format("Too many exceptions after attempt %s of %s, aborting: %s", attempt, _name, error.getMessage()));
      recoveryResult.fail(error);
    } else {
      // Schedule a new retry task after a computed backoff timeout.
      LOGGER.debug(String.format("Attempt %s of %s failed and will be retried after %s millis: %s", attempt, _name, backoffTime, error.getMessage()));
      Task<T> retryTask = wrap(attempt);
      Promises.propagateResult(retryTask, recoveryResult);
      recoveryContext.createTimer(backoffTime, TimeUnit.MILLISECONDS, retryTask);
    }
  }

  /** Starts a retriable task */
  private Promise<? extends T> run(Context context) {
    _startedAt = System.currentTimeMillis();
    Task<T> task = wrap(0);
    context.run(task);
    return task;
  }
}
