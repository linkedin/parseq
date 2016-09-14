package com.linkedin.parseq.retry;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.Priority;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.function.Failure;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * A parseq task wrapper that supports arbitrary retry policies.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class RetriableTask<T> extends BaseTask<T> {
  /** A name of the task that needs to be retried. */
  protected final String _name;

  /** A task generator function. It will receive a zero-based attempt number as a parameter. */
  protected final Function<Integer, Task<T>> _taskFunction;

  /** Retry policy which will control this task's behavior. */
  protected final AbstractRetryPolicy<T> _policy;

  /** Start time for the very first attempt. */
  protected long _startedAt;

  /**
   * A parseq task wrapper that supports arbitrary retry policies.
   *
   * @param name A name of the task that needs to be retried.
   * @param taskFunction A task generator function. It will receive a zero-based attempt number as a parameter.
   * @param policy Retry policy that will control this task's behavior.
   */
  public RetriableTask(String name, Function<Integer, Task<T>> taskFunction, AbstractRetryPolicy<T> policy)
  {
    ArgumentUtil.requireNotNull(taskFunction, "taskFunction");
    ArgumentUtil.requireNotNull(policy, "policy");

    _name = name;
    _taskFunction = taskFunction;
    _policy = policy;
  }

  /**
   * A helper for creating task wrapper with associated retry policy.
   *
   * @param name A name of the task that needs to be retried.
   * @param policy Retry policy that will control this task's behavior.
   * @param taskSupplier A task generator function.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  public static <U> RetriableTask<U> withRetryPolicy(String name, AbstractRetryPolicy<U> policy, Supplier<Task<U>> taskSupplier) {
    return new RetriableTask<>(name, attempt -> taskSupplier.get(), policy);
  }

  /**
   * A helper for creating task wrapper with associated retry policy.
   *
   * @param name A name of the task that needs to be retried.
   * @param policy Retry policy that will control this task's behavior.
   * @param taskFunction A task generator function. It will receive a zero-based attempt number as a parameter.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  public static <U> RetriableTask<U> withRetryPolicy(String name, AbstractRetryPolicy<U> policy, Function<Integer, Task<U>> taskFunction) {
    return new RetriableTask<>(name, taskFunction, policy);
  }

  /** Create a wrapped task with associated recovery task that will retry if necessary. */
  private Task<T> wrap(int attempt) {
    Task<T> task = _taskFunction.apply(attempt);

    return Task.async(_policy.getName(), context -> {
      final SettablePromise<T> result = Promises.settable();

      final Task<T> recovery = Task.async("recovery", recoveryContext -> {
        final SettablePromise<T> recoveryResult = Promises.settable();

        if (task.isFailed()) {
          // Failed task will cause retry to be scheduled.
          ErrorClassification errorClassification = _policy.getErrorClassifier().apply(task.getError());
          retry(attempt + 1, Failure.of(task.getError()), errorClassification, recoveryContext, recoveryResult);
        } else {
          // Successful task might still need to be retried if its result is not acceptable according to the policy.
          T taskResult = task.get();
          ResultClassification resultClassification = _policy.getResultClassifier().apply(taskResult);
          if (resultClassification == ResultClassification.UNACCEPTABLE) {
            // Unacceptable result -> should retry.
            retry(attempt + 1, Success.of(taskResult), resultClassification.getStatus(), recoveryContext, recoveryResult);
          } else {
            // Acceptable result -> we are done.
            recoveryResult.done(taskResult);
          }
        }

        return recoveryResult;
      });

      // Recovery task should run immediately after the original task to process its outcome.
      recovery.setPriority(Priority.MAX_PRIORITY);
      recovery.getShallowTraceBuilder().setSystemHidden(true);
      Promises.propagateResult(recovery, result);
      context.after(task).run(recovery);
      context.run(task);

      return result;
    });
  }

  /** Invoke event monitors and schedule a retry if policy allows. */
  private void retry(int attempt, Try<T> outcome, ErrorClassification errorClassification, Context recoveryContext, SettablePromise<T> recoveryResult) {
    long backoffTime = _policy.getBackoffPolicy().nextBackoff(attempt, outcome);

    if (errorClassification.isFatal()) {
      // For fatal errors there are no retries.
      _policy.getEventMonitor().interrupted(_name, outcome, attempt);
      recoveryResult.fail(outcome.isFailed() ? outcome.getError() : new RetryFailureException("Retry aborted because of unacceptable task result: " + outcome.get()));
    } else if (_policy.getTerminationPolicy().shouldTerminate(attempt, System.currentTimeMillis() - _startedAt + backoffTime)) {
      // Retry policy commands that no more retries should be done.
      _policy.getEventMonitor().aborted(_name, outcome, attempt);
      recoveryResult.fail(outcome.isFailed() ? outcome.getError() : new RetryFailureException("Retry aborted because of unacceptable task result: " + outcome.get()));
    } else {
      // Schedule a new retry task after a computed backoff timeout.
      _policy.getEventMonitor().retrying(_name, outcome, attempt, backoffTime, errorClassification.isSilent());
      Task<T> retryTask = wrap(attempt);
      Promises.propagateResult(retryTask, recoveryResult);
      recoveryContext.createTimer(backoffTime, TimeUnit.MILLISECONDS, retryTask);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Promise<? extends T> run(Context context) {
    _startedAt = System.currentTimeMillis();
    Task<T> task = wrap(0);
    context.run(task);
    return task;
  }
}
