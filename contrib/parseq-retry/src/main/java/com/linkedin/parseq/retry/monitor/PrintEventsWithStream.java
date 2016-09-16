package com.linkedin.parseq.retry.monitor;

import java.io.PrintStream;
import java.util.function.Function;


/**
 * An event monitor that prints information about retry events to a stream.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class PrintEventsWithStream extends PrintEvents {
  protected final PrintStream _printStream;

  /**
   * An event monitor that prints information about retry events to a stream.
   *
   * @param printStream The stream that this event monitor prints to.
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   * @param retryingActionSelector The strategy used to select an action to perform for a retrying event, defaulting to `retryingAction`.
   * @param interruptedActionSelector The strategy used to select an action to perform for an interrupted event, defaulting to `interruptedAction`.
   * @param abortedActionSelector The strategy used to select an action to perform for an aborted event, defaulting to `abortedAction`.
   */
  public PrintEventsWithStream(PrintStream printStream, PrintAction retryingAction, PrintAction interruptedAction, PrintAction abortedAction,
      Function<Throwable, PrintAction> retryingActionSelector, Function<Throwable, PrintAction> interruptedActionSelector, Function<Throwable, PrintAction> abortedActionSelector) {
    super(retryingAction, interruptedAction, abortedAction, retryingActionSelector, interruptedActionSelector, abortedActionSelector);
    _printStream = printStream;
  }

  /**
   * An event monitor that prints information about retry events to a stream.
   *
   * @param printStream The stream that this event monitor prints to.
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   */
  public PrintEventsWithStream(PrintStream printStream, PrintAction retryingAction, PrintAction interruptedAction, PrintAction abortedAction) {
    super(retryingAction, interruptedAction, abortedAction);
    _printStream = printStream;
  }

  /**
   * An event monitor that prints information about retry events to a stream.
   *
   * @param printStream The stream that this event monitor prints to.
   */
  public PrintEventsWithStream(PrintStream printStream) {
    super();
    _printStream = printStream;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void printMessage(String message) {
    _printStream.print(message);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void printMessageAndStackTrace(String message, Throwable error) {
    _printStream.print(message);
    error.printStackTrace(_printStream);
  }
}
