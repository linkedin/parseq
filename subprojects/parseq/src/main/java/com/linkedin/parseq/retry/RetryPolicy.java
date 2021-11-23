package com.linkedin.parseq.retry;

import com.linkedin.parseq.retry.backoff.BackoffPolicy;
import com.linkedin.parseq.retry.termination.RateLimiter;
import com.linkedin.parseq.retry.termination.RequireAny;
import com.linkedin.parseq.retry.termination.RequireEither;
import com.linkedin.parseq.retry.termination.TerminationPolicy;

import java.util.function.Function;


/**
 * An interface for policies that enable customizable retries for arbitrary parseq tasks.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public interface RetryPolicy {
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
  BackoffPolicy getBackoffPolicy();

  /**
   * @return The classifier for errors raised during retry operations.
   */
  Function<Throwable, ErrorClassification> getErrorClassifier();

  /**
   * Retry policy with configurable number of attempts.
   *
   * @param attempts Total number of attempts (the number of retries will be that minus 1).
   * @param backoff The constant delay (in milliseconds) between retry attempts.
   */
  static RetryPolicy attempts(int attempts, long backoff) {
    return new RetryPolicyBuilder().
        setTerminationPolicy(TerminationPolicy.limitAttempts(attempts)).
        setBackoffPolicy(BackoffPolicy.constant(backoff)).
        build();
  }

  /**
   * Retry policy with limited total duration of the encompassing task (including unlimited retries).
   *
   * @param duration Total duration of the task. This includes both the original request and all potential retries.
   * @param backoff The constant delay (in milliseconds) between retry attempts.
   */
  static RetryPolicy duration(long duration, long backoff) {
    return new RetryPolicyBuilder().
        setTerminationPolicy(TerminationPolicy.limitDuration(duration)).
        setBackoffPolicy(BackoffPolicy.constant(backoff)).
        build();
  }

  /**
   * Retry policy with configurable number of retries and limited total duration of the encompassing task.
   *
   * @param attempts Total number of attempts (the number of retries will be that minus 1).
   * @param duration Total duration of the task. This includes both the original request and all potential retries.
   * @param backoff The constant delay (in milliseconds) between retry attempts.
   */
  static RetryPolicy attemptsAndDuration(int attempts, long duration, long backoff) {
    TerminationPolicy terminationPolicy = new RequireEither(TerminationPolicy.limitAttempts(attempts), TerminationPolicy.limitDuration(duration));
    return new RetryPolicyBuilder().
        setName("RetryPolicy.LimitAttemptsAndDuration").
        setTerminationPolicy(terminationPolicy).
        setBackoffPolicy(BackoffPolicy.constant(backoff)).
        build();
  }

  /**
   * Retry policy with configurable number of retries, limited total duration, and limited rate of the encompassing task.
   *
   * @param attempts Total number of attempts (the number of retries will be that minus 1).
   * @param duration Total duration of the task. This includes both the original request and all potential retries.
   * @param rateLimiter The limiter to control the rate of retries per second.
   * @param backoff The constant delay (in milliseconds) between retry attempts.
   */
  static RetryPolicy attemptsAndDurationAndRate(int attempts, long duration, RateLimiter rateLimiter, long backoff) {

    TerminationPolicy terminationPolicy = new RequireAny(
      TerminationPolicy.limitAttempts(attempts),
      TerminationPolicy.limitDuration(duration),
      TerminationPolicy.limitRate(rateLimiter)
    );

    return new RetryPolicyBuilder().
        setName("RetryPolicy.LimitAttemptsAndDurationAndRate").
        setTerminationPolicy(terminationPolicy).
        setBackoffPolicy(BackoffPolicy.constant(backoff)).
        build();
  }
}
