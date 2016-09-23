package com.linkedin.parseq.retry;

import com.linkedin.parseq.retry.backoff.BackoffPolicy;
import com.linkedin.parseq.retry.termination.TerminationPolicy;

import java.util.function.Function;


/**
 * A policy builder that enables customizable retries for arbitrary parseq tasks.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
final class RetryPolicyImpl implements RetryPolicy {
  /** A name of this policy. It is used to configure parseq tasks. */
  private String _name;

  /** The strategy for determining when to abort a retry operation. */
  private TerminationPolicy _terminationPolicy;

  /** The strategy used to calculate delays between retries. */
  private BackoffPolicy _backoffPolicy;

  /** The classifier for errors raised during retry operations. */
  private Function<Throwable, ErrorClassification> _errorClassifier;

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return _name;
  }

  /**
   * Set a name of this policy. It is used to configure parseq tasks.
   */
  RetryPolicyImpl setName(String name) {
    _name = name;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TerminationPolicy getTerminationPolicy() {
    return _terminationPolicy;
  }

  /**
   * Set a strategy for determining when to abort a retry operation.
   */
  RetryPolicyImpl setTerminationPolicy(TerminationPolicy terminationPolicy) {
    _terminationPolicy = terminationPolicy;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BackoffPolicy getBackoffPolicy() {
    return _backoffPolicy;
  }

  /**
   * Set a strategy used to calculate delays between retries.
   */
  RetryPolicyImpl setBackoffPolicy(BackoffPolicy backoffPolicy) {
    _backoffPolicy = backoffPolicy;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Function<Throwable, ErrorClassification> getErrorClassifier() {
    return _errorClassifier;
  }

  /**
   * Set a classifier for errors raised during retry operations.
   */
  RetryPolicyImpl setErrorClassifier(Function<Throwable, ErrorClassification> errorClassifier) {
    _errorClassifier = errorClassifier;
    return this;
  }
}
