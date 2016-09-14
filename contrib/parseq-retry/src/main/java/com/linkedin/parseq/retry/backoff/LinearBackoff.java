package com.linkedin.parseq.retry.backoff;

import com.linkedin.parseq.function.Try;


/**
 * A policy that increases the backoff duration by the same amount after every retry.
 *
 * @param <T> Type of a task result, not used in this policy.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class LinearBackoff<T> implements BackoffPolicy<T> {
  protected final long _backoff;

  /**
   * A policy that increases the backoff duration by the same amount after every retry.
   *
   * @param backoff The backoff used for the first retry as well as the base for all subsequent attempts.
   */
  public LinearBackoff(final long backoff) {
    _backoff = backoff;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long nextBackoff(int attempts, Try<T> outcome) {
    return attempts * _backoff;
  }
}
