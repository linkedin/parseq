package com.linkedin.parseq.retry;

import com.linkedin.parseq.retry.backoff.BackoffPolicy;
import com.linkedin.parseq.retry.monitor.EventMonitor;
import com.linkedin.parseq.retry.termination.RequireEither;
import com.linkedin.parseq.retry.termination.TerminationPolicy;

import java.util.function.Function;


/**
 * A policy that enables customizable retries for arbitrary parseq tasks.
 *
 * @param <T> Type of a task result, used for strongly typed processing of outcomes.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class RetryPolicy<T> extends AbstractRetryPolicy<T> {
  /**
   * A policy that enables customizable retries for arbitrary parseq tasks.
   *
   * @param name A name of this policy. It is used to configure parseq tasks.
   * @param terminationPolicy The strategy for determining when to abort a retry operation.
   * @param backoffPolicy The strategy used to calculate delays between retries.
   * @param eventMonitor The monitor that is notified of retry events.
   * @param resultClassifier The classifier for results returned during retry operations.
   * @param errorClassifier The classifier for errors raised during retry operations.
   */
  public RetryPolicy(String name, TerminationPolicy terminationPolicy, BackoffPolicy<T> backoffPolicy, EventMonitor<T> eventMonitor,
      Function<T, ResultClassification> resultClassifier, Function<Throwable, ErrorClassification> errorClassifier)
  {
    super(name, terminationPolicy, backoffPolicy, eventMonitor, resultClassifier, errorClassifier);
  }

  /**
   * Retry policy with configurable number of attempts. It doesn't have any delays between retries and doesn't log any events.
   *
   * @param attempts Total number of attempts (the number of retries will be that minus 1).
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  public static <U> RetryPolicy<U> attempts(int attempts) {
    return new RetryPolicy<>("RetryPolicy.attempts", TerminationPolicy.limitAttempts(attempts), BackoffPolicy.constant(0),
        EventMonitor.ignore(), error -> ResultClassification.ACCEPTABLE, ErrorClassification.DEFAULT);
  }

  /**
   * Retry policy with limited total duration of the encompassing task (including unlimited retries).
   * It doesn't have any delays between retries and doesn't log any events.
   *
   * @param duration Total duration of the task. This includes both the original request and all potential retries.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  public static <U> RetryPolicy<U> duration(long duration) {
    return new RetryPolicy<>("RetryPolicy.duration", TerminationPolicy.limitDuration(duration), BackoffPolicy.constant(0),
        EventMonitor.ignore(), error -> ResultClassification.ACCEPTABLE, ErrorClassification.DEFAULT);
  }

  /**
   * Retry policy with configurable number of retries and limited total duration of the encompassing task.
   * It doesn't have any delays between retries and doesn't log any events.
   *
   * @param attempts Total number of attempts (the number of retries will be that minus 1).
   * @param duration Total duration of the task. This includes both the original request and all potential retries.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  public static <U> RetryPolicy<U> attemptsAndDuration(int attempts, long duration) {
    TerminationPolicy terminationPolicy = new RequireEither(TerminationPolicy.limitAttempts(attempts), TerminationPolicy.limitDuration(duration));
    return new RetryPolicy<>("RetryPolicy.attemptsAndDuration", terminationPolicy, BackoffPolicy.constant(0),
        EventMonitor.ignore(), error -> ResultClassification.ACCEPTABLE, ErrorClassification.DEFAULT);
  }

  /**
   * Set a name of this policy. It is used to configure parseq tasks.
   */
  public RetryPolicy<T> setName(String name) {
    _name = name;
    return this;
  }

  /**
   * Set a strategy for determining when to abort a retry operation.
   */
  public RetryPolicy<T> setTerminationPolicy(TerminationPolicy terminationPolicy) {
    _terminationPolicy = terminationPolicy;
    return this;
  }

  /**
   * Set a strategy used to calculate delays between retries.
   */
  public RetryPolicy<T> setBackoffPolicy(BackoffPolicy<T> backoffPolicy) {
    _backoffPolicy = backoffPolicy;
    return this;
  }

  /**
   * Set a monitor that is notified of retry events.
   */
  public RetryPolicy<T> setEventMonitor(EventMonitor<T> eventMonitor) {
    _eventMonitor = eventMonitor;
    return this;
  }

  /**
   * Set a classifier for results returned during retry operations.
   */
  public RetryPolicy<T> setResultClassifier(Function<T, ResultClassification> resultClassifier) {
    _resultClassifier = resultClassifier;
    return this;
  }

  /**
   * Set a classifier for errors raised during retry operations.
   */
  public RetryPolicy<T> setErrorClassifier(Function<Throwable, ErrorClassification> errorClassifier) {
    _errorClassifier = errorClassifier;
    return this;
  }
}
