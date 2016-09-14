package com.linkedin.parseq.retry.backoff;

import com.linkedin.parseq.function.Try;


/**
 * A policy that uses the same backoff after every retry.
 *
 * @param <T> Type of a task result, not used in this policy.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class ConstantBackoff<T> implements BackoffPolicy<T> {
  protected final long _backoff;

  /**
   * A policy that uses the same backoff after every retry.
   *
   * @param backoff The backoff used for every retry.
   */
  public ConstantBackoff(final long backoff) {
    _backoff = backoff;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long nextBackoff(int attempts, Try<T> outcome) {
    return _backoff;
  }
}
