package com.linkedin.parseq.retry.backoff;


/**
 * A policy that increases the backoff duration by the same amount after every retry.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class LinearBackoff implements BackoffPolicy {
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
  public long nextBackoff(int attempts, Throwable error) {
    return attempts * _backoff;
  }
}
