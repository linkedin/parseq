package com.linkedin.parseq.retry.termination;

/**
 * A rate limiter that allows a certain number of retries per second.
 *
 * @author Mohamed Isoukrane (isoukrane.mohamed@gmail.com)
 */
public interface RateLimiter {

  /**
   * Acquires a permit to process a retry request if it can be acquired immediately without delay.
   * It needs ot be thread safe and nonblocking.
   */
  boolean tryAcquire();
}
