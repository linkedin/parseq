package com.linkedin.parseq.retry.monitor;


/**
 * A monitor that ignores all events.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class IgnoreEvents implements EventMonitor {
  /**
   * {@inheritDoc}
   */
  @Override
  public void retrying(String name, Throwable error, int attempts, long backoffTime, boolean isSilent) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void interrupted(String name, Throwable error, int attempts) {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void aborted(String name, Throwable error, int attempts) {
  }
}
