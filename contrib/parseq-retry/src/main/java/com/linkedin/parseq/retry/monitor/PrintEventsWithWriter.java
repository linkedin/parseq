package com.linkedin.parseq.retry.monitor;

import com.linkedin.parseq.function.Try;

import java.io.PrintWriter;
import java.util.function.Function;


/**
 * An event monitor that prints information about retry events to a writer.
 *
 * @param <T> Type of a task result, used for strongly typed processing of outcomes.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class PrintEventsWithWriter<T> extends PrintEvents<T> {
  protected final PrintWriter _printWriter;

  /**
   * An event monitor that prints information about retry events to a writer.
   *
   * @param printWriter The writer that this event monitor prints to.
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   * @param retryingActionSelector The strategy used to select an action to perform for a retrying event, defaulting to `retryingAction`.
   * @param interruptedActionSelector The strategy used to select an action to perform for an interrupted event, defaulting to `interruptedAction`.
   * @param abortedActionSelector The strategy used to select an action to perform for an aborted event, defaulting to `abortedAction`.
   */
  public PrintEventsWithWriter(PrintWriter printWriter, PrintAction retryingAction, PrintAction interruptedAction, PrintAction abortedAction,
      Function<Try<T>, PrintAction> retryingActionSelector, Function<Try<T>, PrintAction> interruptedActionSelector, Function<Try<T>, PrintAction> abortedActionSelector) {
    super(retryingAction, interruptedAction, abortedAction, retryingActionSelector, interruptedActionSelector, abortedActionSelector);
    _printWriter = printWriter;
  }

  /**
   * An event monitor that prints information about retry events to a writer.
   *
   * @param printWriter The writer that this event monitor prints to.
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   */
  public PrintEventsWithWriter(PrintWriter printWriter, PrintAction retryingAction, PrintAction interruptedAction, PrintAction abortedAction) {
    super(retryingAction, interruptedAction, abortedAction);
    _printWriter = printWriter;
  }

  /**
   * An event monitor that prints information about retry events to a writer.
   *
   * @param printWriter The writer that this event monitor prints to.
   */
  public PrintEventsWithWriter(PrintWriter printWriter) {
    super();
    _printWriter = printWriter;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void printMessage(String message) {
    _printWriter.print(message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void printMessageAndStackTrace(String message, Throwable error) {
    _printWriter.print(message);
    error.printStackTrace(_printWriter);
  }
}
