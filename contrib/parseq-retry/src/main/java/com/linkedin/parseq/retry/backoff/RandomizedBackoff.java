package com.linkedin.parseq.retry.backoff;

import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.internal.ArgumentUtil;

import java.util.Random;


/**
 * A policy that randomizes the result of another policy by adding a random duration in the specified range.
 *
 * @param <T> Type of a task result, same as in a base policy.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class RandomizedBackoff<T> implements BackoffPolicy<T> {
  protected final Random _random = new Random();
  protected final BackoffPolicy<T> _policy;
  protected final long _minRange;
  protected final long _maxRange;

  /**
   * A policy that randomizes the result of another policy by adding a random duration in the specified range.
   *
   * @param policy The base policy to randomize the result of.
   * @param minRange The minimum range of values that may be used to modify the result of the base policy.
   * @param maxRange The maximum range of values that may be used to modify the result of the base policy.
   */
  public RandomizedBackoff(BackoffPolicy<T> policy, long minRange, long maxRange) {
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
  public long nextBackoff(int attempts, Try<T> outcome) {
    return _policy.nextBackoff(attempts, outcome) + _minRange + Math.round((_maxRange - _minRange) * _random.nextDouble());
  }
}
