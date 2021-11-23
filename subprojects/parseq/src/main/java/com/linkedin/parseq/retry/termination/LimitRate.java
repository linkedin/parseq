package com.linkedin.parseq.retry.termination;

/**
 * A termination policy that limits the rate of retries.
 *
 * @author Mohamed Isoukrane (isoukrane.mohamed@gmail.com)
 */
public class LimitRate implements TerminationPolicy {
  protected final RateLimiter _rateLimiter;

  /**
   * A termination policy that limits the rate of retries.
   *
   * @param rateLimiter The rate limiter implementation that will rate the retries qps.
   */
  public LimitRate(RateLimiter rateLimiter) {
    _rateLimiter = rateLimiter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldTerminate(int attempts, long nextAttemptAt) {
    return !_rateLimiter.tryAcquire();
  }
}
