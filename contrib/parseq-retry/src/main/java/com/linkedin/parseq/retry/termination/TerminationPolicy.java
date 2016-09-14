package com.linkedin.parseq.retry.termination;


/**
 * Strategy for determining when to abort a retry operation.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public interface TerminationPolicy {
  /**
   * Returns true if the retry operation with the specified properties should terminate.
   *
   * @param attempts The number of attempts that have been made so far.
   * @param nextAttemptAt The duration between when the retry operation began and when the next attempt will occur.
   */
  boolean shouldTerminate(int attempts, long nextAttemptAt);

  /**
   * A termination policy that limits the number of attempts made.
   *
   * @param maxAttempts The maximum number of attempts that can be performed.
   */
  static TerminationPolicy limitAttempts(int maxAttempts) {
    return new LimitAttempts(maxAttempts);
  }

  /**
   * A termination policy that limits the amount of time spent retrying.
   *
   * @param maxDuration The maximum duration that a retry operation should not exceed.
   */
  static TerminationPolicy limitDuration(long maxDuration) {
    return new LimitDuration(maxDuration);
  }

  /**
   * A termination policy that signals for termination after both of the specified policies terminate.
   *
   * @param first The first of the two policies that must signal for termination.
   * @param second The second of the two policies that must signal for termination.
   */
  static TerminationPolicy requireBoth(TerminationPolicy first, TerminationPolicy second) {
    return new RequireBoth(first, second);
  }

  /**
   * A termination policy that signals for termination after either of the specified policies terminate.
   *
   * @param first The first of the two policies that may signal for termination.
   * @param second The second of the two policies that may signal for termination.
   */
  static TerminationPolicy requireEither(TerminationPolicy first, TerminationPolicy second) {
    return new RequireEither(first, second);
  }

  /**
   * A termination policy that always signals for termination.
   */
  static TerminationPolicy alwaysTerminate() {
    return new AlwaysTerminate();
  }

  /**
   * A termination policy that never signals for termination.
   */
  static TerminationPolicy neverTerminate() {
    return new NeverTerminate();
  }
}
