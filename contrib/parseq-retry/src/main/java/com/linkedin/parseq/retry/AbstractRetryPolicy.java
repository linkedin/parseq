package com.linkedin.parseq.retry;

import com.linkedin.parseq.retry.backoff.BackoffPolicy;
import com.linkedin.parseq.retry.monitor.EventMonitor;
import com.linkedin.parseq.retry.termination.TerminationPolicy;

import java.util.function.Function;


/**
 * A base class for policies that enable customizable retries for arbitrary parseq tasks.
 *
 * @param <T> Type of a task result, used for strongly typed processing of outcomes.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public abstract class AbstractRetryPolicy<T> {
  /** A name of this policy. It is used to configure parseq tasks. */
  protected String _name;

  /** The strategy for determining when to abort a retry operation. */
  protected TerminationPolicy _terminationPolicy;

  /** The strategy used to calculate delays between retries. */
  protected BackoffPolicy<T> _backoffPolicy;

  /** The monitor that is notified of retry events. */
  protected EventMonitor<T> _eventMonitor;

  /** The classifier for results returned during retry operations. */
  protected Function<T, ResultClassification> _resultClassifier;

  /** The classifier for errors raised during retry operations. */
  protected Function<Throwable, ErrorClassification> _errorClassifier;

  /**
   * A base class for policies that enable customizable retries for arbitrary parseq tasks.
   *
   * @param name A name of this policy. It is used to configure parseq tasks.
   * @param terminationPolicy The strategy for determining when to abort a retry operation.
   * @param backoffPolicy The strategy used to calculate delays between retries.
   * @param eventMonitor The monitor that is notified of retry events.
   * @param resultClassifier The classifier for results returned during retry operations.
   * @param errorClassifier The classifier for errors raised during retry operations.
   */
  public AbstractRetryPolicy(String name, TerminationPolicy terminationPolicy, BackoffPolicy<T> backoffPolicy, EventMonitor<T> eventMonitor,
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
   * @return The name of this policy. It is used to configure parseq tasks.
   */
  public String getName() {
    return _name;
  }

  /**
   * Set a name of this policy. It is used to configure parseq tasks.
   */
  public AbstractRetryPolicy<T> setName(String name) {
    _name = name;
    return this;
  }

  /**
   * @return The strategy for determining when to abort a retry operation.
   */
  public TerminationPolicy getTerminationPolicy() {
    return _terminationPolicy;
  }

  /**
   * Set a strategy for determining when to abort a retry operation.
   */
  public AbstractRetryPolicy<T> setTerminationPolicy(TerminationPolicy terminationPolicy) {
    _terminationPolicy = terminationPolicy;
    return this;
  }

  /**
   * @return The strategy used to calculate delays between retries.
   */
  public BackoffPolicy<T> getBackoffPolicy() {
    return _backoffPolicy;
  }

  /**
   * Set a strategy used to calculate delays between retries.
   */
  public AbstractRetryPolicy<T> setBackoffPolicy(BackoffPolicy<T> backoffPolicy) {
    _backoffPolicy = backoffPolicy;
    return this;
  }

  /**
   * @return The monitor that is notified of retry events.
   */
  public EventMonitor<T> getEventMonitor() {
    return _eventMonitor;
  }

  /**
   * Set a monitor that is notified of retry events.
   */
  public AbstractRetryPolicy<T> setEventMonitor(EventMonitor<T> eventMonitor) {
    _eventMonitor = eventMonitor;
    return this;
  }

  /**
   * @return The classifier for results returned during retry operations.
   */
  public Function<T, ResultClassification> getResultClassifier() {
    return _resultClassifier;
  }

  /**
   * Set a classifier for results returned during retry operations.
   */
  public AbstractRetryPolicy<T> setResultClassifier(Function<T, ResultClassification> resultClassifier) {
    _resultClassifier = resultClassifier;
    return this;
  }

  /**
   * @return The classifier for errors raised during retry operations.
   */
  public Function<Throwable, ErrorClassification> getErrorClassifier() {
    return _errorClassifier;
  }

  /**
   * Set a classifier for errors raised during retry operations.
   */
  public AbstractRetryPolicy<T> setErrorClassifier(Function<Throwable, ErrorClassification> errorClassifier) {
    _errorClassifier = errorClassifier;
    return this;
  }
}
