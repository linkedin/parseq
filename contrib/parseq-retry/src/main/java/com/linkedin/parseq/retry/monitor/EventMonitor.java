package com.linkedin.parseq.retry.monitor;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.logging.Logger;


/**
 * A monitor that is notified of events that occur while a retry operation is in progress.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public interface EventMonitor {
  /**
   * Called when an operation has failed with a non-fatal error and will be retried.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param error The error of the most recent retry attempt.
   * @param attempts The number of attempts that have been made so far.
   * @param backoffTime The amount of time that will pass before another attempt is made.
   * @param isSilent True if the exception was classified as silent.
   */
  void retrying(String name, Throwable error, int attempts, long backoffTime, boolean isSilent);

  /**
   * Called when an operation has failed with a fatal error and will not be retried.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param error The error of the most recent retry attempt.
   * @param attempts The number of attempts that were made.
   */
  void interrupted(String name, Throwable error, int attempts);

  /**
   * Called when an operation has failed too many times and will not be retried.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param error The error of the most recent retry attempt.
   * @param attempts The number of attempts that were made.
   */
  void aborted(String name, Throwable error, int attempts);

  /**
   * Formats a message for a retrying event.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param error The error of the most recent retry attempt.
   * @param attempts The number of attempts that have been made so far.
   * @param backoffTime The amount of time that will pass before another attempt is made.
   */
  default String formatRetrying(String name, Throwable error, int attempts, long backoffTime) {
    return String.format("Attempt %s of %s failed: %s\nRetrying after %s millis.\n", attempts, name, formatError(error), backoffTime);
  }

  /**
   * Formats a message for an interrupted event.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param error The error of the most recent retry attempt.
   * @param attempts The number of attempts that were made.
   */
  default String formatInterrupted(String name, Throwable error, int attempts) {
    return String.format("Attempt %s of %s interrupted: %s\n", attempts, name, formatError(error));
  }

  /**
   * Formats a message for an aborted event.
   *
   * @param name The name of the operation that failed if one was provided.
   * @param error The error of the most recent retry attempt.
   * @param attempts The number of attempts that were made.
   */
  default String formatAborted(String name, Throwable error, int attempts) {
    return String.format("Too many exceptions after attempt %s of %s, aborting: %s\n", attempts, name, formatError(error));
  }

  /**
   * Converts the provided error to a message describing the error.
   *
   * @param error The error of the most recent retry attempt.
   */
  default String formatError(Throwable error) {
    String message = error.getMessage();
    return error.getClass().getName() + (message != null && !message.isEmpty() ? ": " + message : "");
  }

  /**
   * A monitor that ignores all events.
   */
  static EventMonitor ignore() {
    return new IgnoreEvents();
  }

  /**
   * An event monitor that prints information about retry events to a stream.
   *
   * @param printStream The stream that this event monitor prints to.
   * @param Type of a task result, used for strongly typed processing of errors.
   */
  static EventMonitor printWithStream(PrintStream printStream) {
    return new PrintEventsWithStream(printStream);
  }

  /**
   * An event monitor that prints information about retry events to a writer.
   *
   * @param printWriter The writer that this event monitor prints to.
   */
  static EventMonitor printWithWriter(PrintWriter printWriter) {
    return new PrintEventsWithWriter(printWriter);
  }

  /**
   * An event monitor that formats and logs events using the `java.util.logging` framework.
   *
   * @param logger The logger that this event monitor submits to.
   */
  static EventMonitor logWithJava(Logger logger) {
    return new LogEventsWithJava(logger);
  }

  /**
   * An event monitor that formats and logs events using Slf4j.
   *
   * @param logger The logger that this event monitor submits to.
   */
  static EventMonitor logWithSlf4j(org.slf4j.Logger logger) {
    return new LogEventsWithSlf4j(logger);
  }

  /**
   * A monitor that will always pass invocations on to the two specified monitors.
   *
   * @param first The first event monitor to pass all invocations to.
   * @param second The second event monitor to pass all invocations to.
   */
  static EventMonitor chained(EventMonitor first, EventMonitor second) {
    return new ChainedEvents(first, second);
  }
}
