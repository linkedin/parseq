package com.linkedin.parseq.retry.backoff;

import com.linkedin.parseq.function.Try;


/**
 * A policy that increases the initial backoff duration by repeatedly multiplying by an approximation of the golden
 * ratio (8 / 5, the sixth and fifth fibonacci numbers).
 *
 * @param <T> Type of a task result, not used in this policy.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class FibonacciBackoff<T> implements BackoffPolicy<T> {
  protected final long _backoff;

  /**
   * A policy that increases the initial backoff duration by repeatedly multiplying by an approximation of the golden
   * ratio (8 / 5, the sixth and fifth fibonacci numbers).
   *
   * @param backoff The backoff used for the first retry as well as the base for all subsequent attempts.
   */
  public FibonacciBackoff(final long backoff) {
    _backoff = backoff;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long nextBackoff(int attempts, Try<T> outcome) {
    return Math.round(Math.pow(1.6, attempts - 1) * _backoff);
  }
}
