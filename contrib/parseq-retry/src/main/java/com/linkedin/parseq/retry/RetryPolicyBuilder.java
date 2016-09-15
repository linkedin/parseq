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
public final class RetryPolicyBuilder<T> implements RetryPolicy<T> {
  /** A name of this policy. It is used to configure parseq tasks. */
  private String _name;

  /** The strategy for determining when to abort a retry operation. */
  private TerminationPolicy _terminationPolicy;

  /** The strategy used to calculate delays between retries. */
  private BackoffPolicy<T> _backoffPolicy;

  /** The monitor that is notified of retry events. */
  private EventMonitor<T> _eventMonitor;

  /** The classifier for results returned during retry operations. */
  private Function<T, ResultClassification> _resultClassifier;

  /** The classifier for errors raised during retry operations. */
  private Function<Throwable, ErrorClassification> _errorClassifier;

  private RetryPolicyBuilder(String name, TerminationPolicy terminationPolicy, BackoffPolicy<T> backoffPolicy, EventMonitor<T> eventMonitor,
      Function<T, ResultClassification> resultClassifier, Function<Throwable, ErrorClassification> errorClassifier)
  {
    _name = name;
    _terminationPolicy = terminationPolicy;
    _backoffPolicy = backoffPolicy;
    _eventMonitor = eventMonitor;
    _resultClassifier = resultClassifier;
    _errorClassifier = errorClassifier;
  }

  /**
   * A policy builder that enables customizable retries for arbitrary parseq tasks.
   *
   * @param name A name of this policy. It is used to configure parseq tasks.
   * @param terminationPolicy The strategy for determining when to abort a retry operation.
   * @param backoffPolicy The strategy used to calculate delays between retries.
   * @param eventMonitor The monitor that is notified of retry events.
   * @param resultClassifier The classifier for results returned during retry operations.
   * @param errorClassifier The classifier for errors raised during retry operations.
   */
  static public <U> RetryPolicyBuilder<U> create(String name, TerminationPolicy terminationPolicy, BackoffPolicy<U> backoffPolicy, EventMonitor<U> eventMonitor,
      Function<U, ResultClassification> resultClassifier, Function<Throwable, ErrorClassification> errorClassifier) {
    return new RetryPolicyBuilder<>(name, terminationPolicy, backoffPolicy, eventMonitor, resultClassifier, errorClassifier);
  }

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
  public RetryPolicyBuilder<T> setName(String name) {
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
  public RetryPolicyBuilder<T> setTerminationPolicy(TerminationPolicy terminationPolicy) {
    _terminationPolicy = terminationPolicy;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public BackoffPolicy<T> getBackoffPolicy() {
    return _backoffPolicy;
  }

  /**
   * Set a strategy used to calculate delays between retries.
   */
  public RetryPolicyBuilder<T> setBackoffPolicy(BackoffPolicy<T> backoffPolicy) {
    _backoffPolicy = backoffPolicy;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EventMonitor<T> getEventMonitor() {
    return _eventMonitor;
  }

  /**
   * Set a monitor that is notified of retry events.
   */
  public RetryPolicyBuilder<T> setEventMonitor(EventMonitor<T> eventMonitor) {
    _eventMonitor = eventMonitor;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Function<T, ResultClassification> getResultClassifier() {
    return _resultClassifier;
  }

  /**
   * Set a classifier for results returned during retry operations.
   */
  public RetryPolicyBuilder<T> setResultClassifier(Function<T, ResultClassification> resultClassifier) {
    _resultClassifier = resultClassifier;
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
  public RetryPolicyBuilder<T> setErrorClassifier(Function<Throwable, ErrorClassification> errorClassifier) {
    _errorClassifier = errorClassifier;
    return this;
  }
}
