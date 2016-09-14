package com.linkedin.parseq.retry.backoff;

import com.linkedin.parseq.function.Try;


/**
 * A policy that doubles the backoff duration after every attempt.
 *
 * @param <T> Type of a task result, not used in this policy.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class ExponentialBackoff<T> implements BackoffPolicy<T> {
  protected final long _backoff;

  /**
   * A policy that doubles the backoff duration after every attempt.
   *
   * @param backoff The backoff used for the first retry as well as the base for all subsequent attempts.
   */
  public ExponentialBackoff(final long backoff) {
    _backoff = backoff;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long nextBackoff(int attempts, Try<T> outcome) {
    return (1L << attempts - 1) * _backoff;
  }
}
