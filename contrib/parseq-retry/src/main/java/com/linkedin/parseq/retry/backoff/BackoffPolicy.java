package com.linkedin.parseq.retry.backoff;

import com.linkedin.parseq.function.Try;

import java.util.function.Function;


/**
 * A strategy for computing a sequence of wait durations for use between retry attempts.
 *
 * @param <T> Type of a task result, used mostly for {@link com.linkedin.parseq.retry.backoff.SelectedBackoff} policy.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public interface BackoffPolicy<T> {
  /**
   * Computes the next backoff duration using the specified number of attempts and the outcome that caused the
   * operation to consider another attempt.
   *
   * @param attempts The number of attempts that have been made so far.
   * @param outcome The outcome that caused the operation to consider another attempt.
   *
   * @return The computed backoff duration in milliseconds.
   */
  long nextBackoff(int attempts, Try<T> outcome);

  /**
   * A policy that uses the same backoff after every retry.
   *
   * @param backoff The backoff used for every retry (in milliseconds).
   * @param <U> Type of a task result, not used in this policy.
   */
  static <U> BackoffPolicy<U> constant(final long backoff) {
    return new ConstantBackoff<>(backoff);
  }

  /**
   * A policy that increases the backoff duration by the same amount after every retry.
   *
   * @param backoff The backoff used for the first retry as well as the base for all subsequent attempts (in milliseconds).
   * @param <U> Type of a task result, not used in this policy.
   */
  static <U> BackoffPolicy<U> linear(final long backoff) {
    return new LinearBackoff<>(backoff);
  }

  /**
   * A policy that doubles the backoff duration after every attempt.
   *
   * @param backoff The backoff used for the first retry as well as the base for all subsequent attempts (in milliseconds).
   * @param <U> Type of a task result, not used in this policy.
   */
  static <U> BackoffPolicy<U> exponential(final long backoff) {
    return new ExponentialBackoff<>(backoff);
  }

  /**
   * A policy that increases the initial backoff duration by repeatedly multiplying by an approximation of the golden
   * ratio (8 / 5, the sixth and fifth fibonacci numbers) (in milliseconds).
   *
   * @param backoff The backoff used for the first retry as well as the base for all subsequent attempts.
   * @param <U> Type of a task result, not used in this policy.
   */
  static <U> BackoffPolicy<U> fibonacci(final long backoff) {
    return new FibonacciBackoff<>(backoff);
  }

  /**
   * A policy that randomizes the result of another policy by adding a random duration in the specified range (in milliseconds).
   *
   * @param policy The base policy to randomize the result of.
   * @param minRange The minimum range of values that may be used to modify the result of the base policy.
   * @param maxRange The maximum range of values that may be used to modify the result of the base policy.
   * @param <U> Type of a task result, same as in a base policy.
   */
  static <U> BackoffPolicy<U> randomized(BackoffPolicy<U> policy, long minRange, long maxRange) {
    return new RandomizedBackoff<>(policy, minRange, maxRange);
  }

  /**
   * A policy that delegates to another policy that is selected based on the most recently evaluated outcome.
   *
   * @param policyFunction The function that maps from outcomes to backoff policies.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  static <U> BackoffPolicy<U> selected(Function<Try<U>, BackoffPolicy<U>> policyFunction) {
    return new SelectedBackoff<>(policyFunction);
  }
}
