package com.linkedin.parseq.internal;

import java.util.concurrent.TimeUnit;


/**
 * Rate limiter that can apply throttling on plan class.
 *
 * @author Min Chen (mnchen@linkedin.com)
 */
public interface PlanBasedRateLimiter {
  /**
   * Try acquire a rate limit permit for the given plan class.
   * @param planClass plan class name.
   * @return true if a permit is granted, false otherwise.
   */
  boolean tryAcquire(String planClass);

  /**
   * Try acquire a rate limit permit for the given plan class within the specified
   * time window.
   * @param planClass plan class name.
   * @param timeout timeout window
   * @param unit timeout unit.
   * @return true if a permit is granted within the time window, false otherwise.
   * @throws InterruptedException
   */
  boolean tryAcquire(String planClass, long timeout, TimeUnit unit) throws InterruptedException;

  /**
   * Acquire a rate limit permit, blocking until the permit is granted.
   * @param planClass plan class name.
   * @throws InterruptedException
   */
  void acquire(String planClass) throws InterruptedException;

  /**
   * Release a rate limit permit for the given plan class.
   * @param planClass plan class name
   */
  void release(String planClass);
}
