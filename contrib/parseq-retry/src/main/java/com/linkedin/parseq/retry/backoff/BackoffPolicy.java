package com.linkedin.parseq.retry.backoff;

import java.util.function.Function;


/**
 * A strategy for computing a sequence of wait durations for use between retry attempts.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public interface BackoffPolicy {
  /**
   * Computes the next backoff duration using the specified number of attempts and the error that caused the
   * operation to consider another attempt.
   *
   * @param attempts The number of attempts that have been made so far.
   * @param error The error that caused the operation to consider another attempt.
   *
   * @return The computed backoff duration in milliseconds.
   */
  long nextBackoff(int attempts, Throwable error);

  /**
   * A policy that doesn't do any backoff, so the retry happens immediately.
   */
  static BackoffPolicy noBackoff() {
    return (attempts, error) -> 0;
  }

  /**
   * A policy that uses the same backoff after every retry.
   *
   * @param backoff The backoff used for every retry (in milliseconds).
   */
  static BackoffPolicy constant(final long backoff) {
    return new ConstantBackoff(backoff);
  }

  /**
   * A policy that increases the backoff duration by the same amount after every retry.
   *
   * @param backoff The backoff used for the first retry as well as the base for all subsequent attempts (in milliseconds).
   */
  static BackoffPolicy linear(final long backoff) {
    return new LinearBackoff(backoff);
  }

  /**
   * A policy that doubles the backoff duration after every attempt.
   *
   * @param backoff The backoff used for the first retry as well as the base for all subsequent attempts (in milliseconds).
   */
  static BackoffPolicy exponential(final long backoff) {
    return new ExponentialBackoff(backoff);
  }

  /**
   * A policy that increases the initial backoff duration by repeatedly multiplying by an approximation of the golden
   * ratio (8 / 5, the sixth and fifth fibonacci numbers) (in milliseconds).
   *
   * @param backoff The backoff used for the first retry as well as the base for all subsequent attempts.
   */
  static BackoffPolicy fibonacci(final long backoff) {
    return new FibonacciBackoff(backoff);
  }

  /**
   * A policy that randomizes the result of another policy by adding a random duration in the specified range (in milliseconds).
   *
   * @param policy The base policy to randomize the result of.
   * @param minRange The minimum range of values that may be used to modify the result of the base policy.
   * @param maxRange The maximum range of values that may be used to modify the result of the base policy.
   */
  static BackoffPolicy randomized(BackoffPolicy policy, long minRange, long maxRange) {
    return new RandomizedBackoff(policy, minRange, maxRange);
  }

  /**
   * A policy that delegates to another policy that is selected based on the most recent error.
   *
   * @param policyFunction The function that maps from errors to backoff policies.
   */
  static BackoffPolicy selected(Function<Throwable, BackoffPolicy> policyFunction) {
    return new SelectedBackoff(policyFunction);
  }
}
