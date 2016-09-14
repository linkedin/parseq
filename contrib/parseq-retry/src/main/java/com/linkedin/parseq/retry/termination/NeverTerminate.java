package com.linkedin.parseq.retry.termination;


/**
 * A termination policy that never signals for termination.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class NeverTerminate implements TerminationPolicy {
  /**
   * A termination policy that never signals for termination.
   */
  public NeverTerminate() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldTerminate(int attempts, long nextAttemptAt) {
    return false;
  }
}
