package com.linkedin.parseq.retry.termination;


/**
 * A termination policy that limits the number of attempts made.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class LimitAttempts implements TerminationPolicy {
  protected final int _maxAttempts;

  /**
   * A termination policy that limits the number of attempts made.
   *
   * @param maxAttempts The maximum number of attempts that can be performed.
   */
  public LimitAttempts(int maxAttempts) {
    _maxAttempts = maxAttempts;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldTerminate(int attempts, long nextAttemptAt) {
    return attempts >= _maxAttempts;
  }
}
