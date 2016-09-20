package com.linkedin.parseq.retry.termination;


/**
 * A termination policy that limits the amount of time spent retrying.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class LimitDuration implements TerminationPolicy {
  protected final long _maxDuration;

  /**
   * A termination policy that limits the amount of time spent retrying.
   *
   * @param maxDuration The maximum duration that a retry operation should not exceed.
   */
  public LimitDuration(long maxDuration) {
    _maxDuration = maxDuration;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldTerminate(int attempts, long nextAttemptAt) {
    return nextAttemptAt >= _maxDuration;
  }
}
