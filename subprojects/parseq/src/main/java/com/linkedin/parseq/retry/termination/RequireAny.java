package com.linkedin.parseq.retry.termination;

/**
 * A termination policy that signals for termination after any of the specified policies terminate.
 *
 * @author Mohamed Isoukrane (isoukrane.mohamed@gmail.com)
 */
public class RequireAny implements TerminationPolicy {

  private final TerminationPolicy[] _policies;

  /**
   * A termination policy that signals for termination after any of the specified policies terminate.
   *
   * @param policies The list of policies that may signal for termination.
   */
  public RequireAny(TerminationPolicy... policies) {
    _policies = policies;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean shouldTerminate(int attempts, long nextAttemptAt) {
    for (TerminationPolicy policy : _policies) {
      if (policy.shouldTerminate(attempts, nextAttemptAt)) {
        return true;
      }
    }
    return false;
  }
}
