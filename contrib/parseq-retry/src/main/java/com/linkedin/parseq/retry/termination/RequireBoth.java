package com.linkedin.parseq.retry.termination;


/**
 * A termination policy that signals for termination after both of the specified policies terminate.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class RequireBoth implements TerminationPolicy {
  protected final TerminationPolicy _first;
  protected final TerminationPolicy _second;

  /**
   * A termination policy that signals for termination after both of the specified policies terminate.
   *
   * @param first The first of the two policies that must signal for termination.
   * @param second The second of the two policies that must signal for termination.
   */
  public RequireBoth(TerminationPolicy first, TerminationPolicy second) {
    _first = first;
    _second = second;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldTerminate(int attempts, long nextAttemptAt) {
    return _first.shouldTerminate(attempts, nextAttemptAt) && _second.shouldTerminate(attempts, nextAttemptAt);
  }
}
