package com.linkedin.parseq.retry.backoff;

import com.linkedin.parseq.function.Try;

import java.util.function.Function;


/**
 * A policy that delegates to another policy that is selected based on the most recently evaluated outcome.
 *
 * @param <T> Type of a task result, used for strongly typed processing of outcomes.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class SelectedBackoff<T> implements BackoffPolicy<T> {
  protected final Function<Try<T>, BackoffPolicy<T>> _policyFunction;

  /**
   * A policy that delegates to another policy that is selected based on the most recently evaluated outcome.
   *
   * @param policyFunction The function that maps from outcomes to backoff policies.
   */
  public SelectedBackoff(Function<Try<T>, BackoffPolicy<T>> policyFunction) {
    _policyFunction = policyFunction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long nextBackoff(int attempts, Try<T> outcome) {
    return _policyFunction.apply(outcome).nextBackoff(attempts, outcome);
  }
}
