package com.linkedin.parseq.retry.monitor;

import com.linkedin.parseq.function.Try;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.logging.Logger;


/**
 * A monitor that is notified of events that occur while a retry operation is in progress.
 *
 * @param <T> Type of a task result, used for strongly typed processing of outcomes.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public interface EventMonitor<T> {
  /**
   * Called when an operation has failed with a non-fatal error and will be retried.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param outcome The outcome of the most recent retry attempt.
   * @param attempts The number of attempts that have been made so far.
   * @param backoffTime The amount of time that will pass before another attempt is made.
   * @param isSilent True if the exception was classified as silent.
   */
  void retrying(String name, Try<T> outcome, int attempts, long backoffTime, boolean isSilent);

  /**
   * Called when an operation has failed with a fatal error and will not be retried.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param outcome The outcome of the most recent retry attempt.
   * @param attempts The number of attempts that were made.
   */
  void interrupted(String name, Try<T> outcome, int attempts);

  /**
   * Called when an operation has failed too many times and will not be retried.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param outcome The outcome of the most recent retry attempt.
   * @param attempts The number of attempts that were made.
   */
  void aborted(String name, Try<T> outcome, int attempts);

  /**
   * Formats a message for a retrying event.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param outcome The outcome of the most recent retry attempt.
   * @param attempts The number of attempts that have been made so far.
   * @param backoffTime The amount of time that will pass before another attempt is made.
   */
  default String formatRetrying(String name, Try<T> outcome, int attempts, long backoffTime) {
    return String.format("Attempt %s of %s failed: %s\nRetrying after %s millis.\n", attempts, name, formatOutcome(outcome), backoffTime);
  }

  /**
   * Formats a message for an interrupted event.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param outcome The outcome of the most recent retry attempt.
   * @param attempts The number of attempts that were made.
   */
  default String formatInterrupted(String name, Try<T> outcome, int attempts) {
    return String.format("Attempt %s of %s interrupted: %s\n", attempts, name, formatOutcome(outcome));
  }

  /**
   * Formats a message for an aborted event.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param outcome The outcome of the most recent retry attempt.
   * @param attempts The number of attempts that were made.
   */
  default String formatAborted(String name, Try<T> outcome, int attempts) {
    return String.format("Too many exceptions after attempt %s of %s, aborting: %s\n", attempts, name, formatOutcome(outcome));
  }

  /**
   * Converts the provided outcome to a message describing the outcome.
   *
   * @param outcome The outcome of the most recent retry attempt.
   */
  default String formatOutcome(Try<T> outcome) {
    if (outcome.isFailed()) {
      Throwable error = outcome.getError();
      String message = error.getMessage();
      return error.getClass().getName() + (message != null && !message.isEmpty() ? ": " + message : "");
    } else {
      return outcome.get() == null ? "null" : outcome.get().toString();
    }
  }

  /**
   * A monitor that ignores all events.
   *
   * @param <U> Type of a task result, not used in this policy.
   */
  static <U> EventMonitor<U> ignore() {
    return new IgnoreEvents<>();
  }

  /**
   * An event monitor that prints information about retry events to a stream.
   *
   * @param printStream The stream that this event monitor prints to.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  static <U> EventMonitor<U> printWithStream(PrintStream printStream) {
    return new PrintEventsWithStream<>(printStream);
  }

  /**
   * An event monitor that prints information about retry events to a writer.
   *
   * @param printWriter The writer that this event monitor prints to.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  static <U> EventMonitor<U> printWithWriter(PrintWriter printWriter) {
    return new PrintEventsWithWriter<>(printWriter);
  }

  /**
   * An event monitor that formats and logs events using the `java.util.logging` framework.
   *
   * @param logger The logger that this event monitor submits to.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  static <U> EventMonitor<U> logWithJava(Logger logger) {
    return new LogEventsWithJava<>(logger);
  }

  /**
   * An event monitor that formats and logs events using Slf4j.
   *
   * @param logger The logger that this event monitor submits to.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  static <U> EventMonitor<U> logWithSlf4j(org.slf4j.Logger logger) {
    return new LogEventsWithSlf4j<>(logger);
  }

  /**
   * A monitor that will always pass invocations on to the two specified monitors.
   *
   * @param first The first event monitor to pass all invocations to.
   * @param second The second event monitor to pass all invocations to.
   * @param <U> Type of a task result, used for strongly typed processing of outcomes.
   */
  static <U> EventMonitor<U> chained(EventMonitor<U> first, EventMonitor<U> second) {
    return new ChainedEvents<>(first, second);
  }
}
