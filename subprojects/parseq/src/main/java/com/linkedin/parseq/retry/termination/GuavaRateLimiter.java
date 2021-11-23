package com.linkedin.parseq.retry.termination;

/**
 * A RateLimiter implementation based on the guava RateLimiter.
 *
 * @author Mohamed Isoukrane (isoukrane.mohamed@gmail.com)
 */
public class GuavaRateLimiter implements RateLimiter {
  private final com.google.common.util.concurrent.RateLimiter _rateLimiter;

  public GuavaRateLimiter(double permitsPerSecond) {
    _rateLimiter = com.google.common.util.concurrent.RateLimiter.create(permitsPerSecond);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean tryAcquire() {
    return _rateLimiter.tryAcquire();
  }
}
