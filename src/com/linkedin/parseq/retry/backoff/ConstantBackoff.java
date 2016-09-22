package com.linkedin.parseq.retry.backoff;


/**
 * A policy that uses the same backoff after every retry.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class ConstantBackoff implements BackoffPolicy {
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
  public long nextBackoff(int attempts, Throwable error) {
    return _backoff;
  }
}
