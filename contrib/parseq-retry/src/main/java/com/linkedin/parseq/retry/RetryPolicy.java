package com.linkedin.parseq.retry;

import com.linkedin.parseq.retry.backoff.BackoffPolicy;
import com.linkedin.parseq.retry.monitor.EventMonitor;
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
   * Simple retry policy with configurable number of retries. It doesn't have any delays between retries and doesn't log any events.
   *
   * @param attempts Total number of attempts (the number of retries will be that minus 1).
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  public static <U> AbstractRetryPolicy<U> simple(int attempts) {
    return new RetryPolicy<>("simple retry", TerminationPolicy.limitAttempts(attempts), BackoffPolicy.constant(0),
        EventMonitor.ignore(), error -> ResultClassification.ACCEPTABLE, ErrorClassification.DEFAULT);
  }
}
