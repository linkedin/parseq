package com.linkedin.parseq.retry.backoff;

import com.linkedin.parseq.internal.ArgumentUtil;

import java.util.concurrent.ThreadLocalRandom;


/**
 * A policy that randomizes the result of another policy by adding a random duration in the specified range.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class RandomizedBackoff implements BackoffPolicy {
  protected final BackoffPolicy _policy;
  protected final long _minRange;
  protected final long _maxRange;

  /**
   * A policy that randomizes the result of another policy by adding a random duration in the specified range.
   *
   * @param policy The base policy to randomize the result of.
   * @param minRange The minimum range of values that may be used to modify the result of the base policy.
   * @param maxRange The maximum range of values that may be used to modify the result of the base policy.
   */
  public RandomizedBackoff(BackoffPolicy policy, long minRange, long maxRange) {
    ArgumentUtil.requireNotNull(policy, "policy");
    if (maxRange <= minRange) {
      throw new IllegalArgumentException(String.format("minRange %s must be strictly less than maxRange %s", minRange, maxRange));
    }

    _policy = policy;
    _minRange = minRange;
    _maxRange = maxRange;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long nextBackoff(int attempts, Throwable error) {
    return _policy.nextBackoff(attempts, error) + _minRange + Math.round((_maxRange - _minRange) * ThreadLocalRandom.current().nextDouble());
  }
}
