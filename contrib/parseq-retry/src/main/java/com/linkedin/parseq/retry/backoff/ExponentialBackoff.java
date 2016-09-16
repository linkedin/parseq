package com.linkedin.parseq.retry.backoff;


/**
 * A policy that doubles the backoff duration after every attempt.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class ExponentialBackoff implements BackoffPolicy {
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
  public long nextBackoff(int attempts, Throwable error) {
    return (1L << attempts - 1) * _backoff;
  }
}
