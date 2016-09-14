package com.linkedin.parseq.retry.termination;


/**
 * A termination policy that always signals for termination.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class AlwaysTerminate implements TerminationPolicy {
  /**
   * A termination policy that always signals for termination.
   */
  public AlwaysTerminate() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldTerminate(int attempts, long nextAttemptAt) {
    return true;
  }
}
