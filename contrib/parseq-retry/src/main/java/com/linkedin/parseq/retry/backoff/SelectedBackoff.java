package com.linkedin.parseq.retry.backoff;

import java.util.function.Function;


/**
 * A policy that delegates to another policy that is selected based on the most recent error.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class SelectedBackoff implements BackoffPolicy {
  protected final Function<Throwable, BackoffPolicy> _policyFunction;

  /**
   * A policy that delegates to another policy that is selected based on the most recent error.
   *
   * @param policyFunction The function that maps from errors to backoff policies.
   */
  public SelectedBackoff(Function<Throwable, BackoffPolicy> policyFunction) {
    _policyFunction = policyFunction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long nextBackoff(int attempts, Throwable error) {
    return _policyFunction.apply(error).nextBackoff(attempts, error);
  }
}
