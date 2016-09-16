package com.linkedin.parseq.retry;

import com.linkedin.parseq.retry.backoff.BackoffPolicy;
import com.linkedin.parseq.retry.monitor.EventMonitor;
import com.linkedin.parseq.retry.termination.TerminationPolicy;

import java.util.function.Function;


/**
 * A policy builder that enables customizable retries for arbitrary parseq tasks.
 *
 * @param <T> Type of a task result, used for strongly typed processing of outcomes.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public final class RetryPolicyBuilder<T> {
  private final RetryPolicyImpl<T> _retryPolicy = new RetryPolicyImpl<>();

  public RetryPolicy<T> build() {
    if (_retryPolicy.getTerminationPolicy() == null) {
      throw new IllegalArgumentException("Unable to build retry policy because termination policy is not specified");
    }
    if (_retryPolicy.getBackoffPolicy() == null) {
      _retryPolicy.setBackoffPolicy(BackoffPolicy.noBackoff());
    }
    if (_retryPolicy.getEventMonitor() == null) {
      _retryPolicy.setEventMonitor(EventMonitor.ignore());
    }
    if (_retryPolicy.getResultClassifier() == null) {
      _retryPolicy.setResultClassifier(error -> ResultClassification.ACCEPTABLE);
    }
    if (_retryPolicy.getErrorClassifier() == null) {
      _retryPolicy.setErrorClassifier(ErrorClassification.DEFAULT);
    }
    if (_retryPolicy.getName() == null) {
      StringBuilder policyName = new StringBuilder("RetryPolicy");
      String terminationPolicyName = _retryPolicy.getTerminationPolicy().getClass().getSimpleName();
      if (!terminationPolicyName.contains("$$")) {
        policyName.append(".");
        policyName.append(terminationPolicyName);
      }
      String backoffPolicyName = _retryPolicy.getBackoffPolicy().getClass().getSimpleName();
      if (!backoffPolicyName.contains("$$")) {
        policyName.append(".");
        policyName.append(backoffPolicyName);
      }
      _retryPolicy.setName(policyName.toString());
    }
    return _retryPolicy;
  }

  /**
   * Set a name of this policy. It is used to configure parseq tasks.
   */
  public RetryPolicyBuilder<T> setName(String name) {
    _retryPolicy.setName(name);
    return this;
  }

  /**
   * Set a strategy for determining when to abort a retry operation.
   */
  public RetryPolicyBuilder<T> setTerminationPolicy(TerminationPolicy terminationPolicy) {
    _retryPolicy.setTerminationPolicy(terminationPolicy);
    return this;
  }

  /**
   * Set a strategy used to calculate delays between retries.
   */
  public RetryPolicyBuilder<T> setBackoffPolicy(BackoffPolicy<T> backoffPolicy) {
    _retryPolicy.setBackoffPolicy(backoffPolicy);
    return this;
  }

  /**
   * Set a monitor that is notified of retry events.
   */
  public RetryPolicyBuilder<T> setEventMonitor(EventMonitor<T> eventMonitor) {
    _retryPolicy.setEventMonitor(eventMonitor);
    return this;
  }

  /**
   * Set a classifier for results returned during retry operations.
   */
  public RetryPolicyBuilder<T> setResultClassifier(Function<T, ResultClassification> resultClassifier) {
    _retryPolicy.setResultClassifier(resultClassifier);
    return this;
  }

  /**
   * Set a classifier for errors raised during retry operations.
   */
  public RetryPolicyBuilder<T> setErrorClassifier(Function<Throwable, ErrorClassification> errorClassifier) {
    _retryPolicy.setErrorClassifier(errorClassifier);
    return this;
  }
}
