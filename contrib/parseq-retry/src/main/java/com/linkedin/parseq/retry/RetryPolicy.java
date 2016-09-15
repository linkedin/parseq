package com.linkedin.parseq.retry;

import com.linkedin.parseq.retry.backoff.BackoffPolicy;
import com.linkedin.parseq.retry.monitor.EventMonitor;
import com.linkedin.parseq.retry.termination.RequireEither;
import com.linkedin.parseq.retry.termination.TerminationPolicy;

import java.util.function.Function;


/**
 * An interface for policies that enable customizable retries for arbitrary parseq tasks.
 *
 * @param <T> Type of a task result, used for strongly typed processing of outcomes.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public interface RetryPolicy<T> {
  /**
   * @return The name of this policy. It is used to configure parseq tasks.
   */
  String getName();

  /**
   * @return The strategy for determining when to abort a retry operation.
   */
  TerminationPolicy getTerminationPolicy();

  /**
   * @return The strategy used to calculate delays between retries.
   */
  BackoffPolicy<T> getBackoffPolicy();

  /**
   * @return The monitor that is notified of retry events.
   */
  EventMonitor<T> getEventMonitor();

  /**
   * @return The classifier for results returned during retry operations.
   */
  Function<T, ResultClassification> getResultClassifier();

  /**
   * @return The classifier for errors raised during retry operations.
   */
  Function<Throwable, ErrorClassification> getErrorClassifier();

  /**
   * Retry policy with configurable number of attempts. It doesn't have any delays between retries and doesn't log any events.
   *
   * @param attempts Total number of attempts (the number of retries will be that minus 1).
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  static <U> RetryPolicyBuilder<U> attempts(int attempts) {
    return RetryPolicyBuilder.create("RetryPolicy.attempts", TerminationPolicy.limitAttempts(attempts), BackoffPolicy.constant(0),
        EventMonitor.ignore(), error -> ResultClassification.ACCEPTABLE, ErrorClassification.DEFAULT);
  }

  /**
   * Retry policy with limited total duration of the encompassing task (including unlimited retries).
   * It doesn't have any delays between retries and doesn't log any events.
   *
   * @param duration Total duration of the task. This includes both the original request and all potential retries.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  static <U> RetryPolicyBuilder<U> duration(long duration) {
    return RetryPolicyBuilder.create("RetryPolicy.duration", TerminationPolicy.limitDuration(duration), BackoffPolicy.constant(0),
        EventMonitor.ignore(), error -> ResultClassification.ACCEPTABLE, ErrorClassification.DEFAULT);
  }

  /**
   * Retry policy with configurable number of retries and limited total duration of the encompassing task.
   * It doesn't have any delays between retries and doesn't log any events.
   *
   * @param attempts Total number of attempts (the number of retries will be that minus 1).
   * @param duration Total duration of the task. This includes both the original request and all potential retries.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  static <U> RetryPolicyBuilder<U> attemptsAndDuration(int attempts, long duration) {
    TerminationPolicy terminationPolicy = new RequireEither(TerminationPolicy.limitAttempts(attempts), TerminationPolicy.limitDuration(duration));
    return RetryPolicyBuilder.create("RetryPolicy.attemptsAndDuration", terminationPolicy, BackoffPolicy.constant(0),
        EventMonitor.ignore(), error -> ResultClassification.ACCEPTABLE, ErrorClassification.DEFAULT);
  }
}
