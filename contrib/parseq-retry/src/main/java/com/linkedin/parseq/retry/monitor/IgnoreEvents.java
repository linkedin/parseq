package com.linkedin.parseq.retry.monitor;

import com.linkedin.parseq.function.Try;


/**
 * A monitor that ignores all events.
 *
 * @param <T> Type of a task result, not used in this policy.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class IgnoreEvents<T> implements EventMonitor<T> {
  /**
   * {@inheritDoc}
   */
  @Override
  public void retrying(String name, Try<T> outcome, int attempts, long backoffTime, boolean isSilent) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void interrupted(String name, Try<T> outcome, int attempts) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void aborted(String name, Try<T> outcome, int attempts) {
  }
}
