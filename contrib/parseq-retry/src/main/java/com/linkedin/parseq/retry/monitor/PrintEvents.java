package com.linkedin.parseq.retry.monitor;

import com.linkedin.parseq.function.Try;

import java.util.function.Function;


/**
 * Base type for event monitors that print information about retry events as text.
 *
 * @param <T> Type of a task result, used for strongly typed processing of outcomes.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public abstract class PrintEvents<T> implements EventMonitor<T> {
  protected static final PrintAction DEFAULT_RETRYING_ACTION = PrintAction.MESSAGE;
  protected static final PrintAction DEFAULT_INTERRUPTED_ACTION = PrintAction.MESSAGE_AND_STACK_TRACE;
  protected static final PrintAction DEFAULT_ABORTED_ACTION = PrintAction.MESSAGE_AND_STACK_TRACE;

  /** The action that is performed by default when a retrying event is received. */
  protected final PrintAction _retryingAction;

  /** The action that is performed by default when an interrupted event is received. */
  protected final PrintAction _interruptedAction;

  /** The action that is performed by default when an aborted event is received. */
  protected final PrintAction _abortedAction;

  /** The strategy used to select an action to perform for a retrying event, defaulting to `retryingAction`. */
  protected final Function<Try<T>, PrintAction> _retryingActionSelector;

  /** The strategy used to select an action to perform for an interrupted event, defaulting to `interruptedAction`. */
  protected final Function<Try<T>, PrintAction> _interruptedActionSelector;

  /** The strategy used to select an action to perform for an aborted event, defaulting to `abortedAction`. */
  protected final Function<Try<T>, PrintAction> _abortedActionSelector;

  /**
   * Base type for event monitors that print information about retry events as text.
   *
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   * @param retryingActionSelector The strategy used to select an action to perform for a retrying event, defaulting to `retryingAction`.
   * @param interruptedActionSelector The strategy used to select an action to perform for an interrupted event, defaulting to `interruptedAction`.
   * @param abortedActionSelector The strategy used to select an action to perform for an aborted event, defaulting to `abortedAction`.
   */
  public PrintEvents(PrintAction retryingAction, PrintAction interruptedAction, PrintAction abortedAction,
      Function<Try<T>, PrintAction> retryingActionSelector, Function<Try<T>, PrintAction> interruptedActionSelector, Function<Try<T>, PrintAction> abortedActionSelector) {
    _retryingAction = retryingAction;
    _interruptedAction = interruptedAction;
    _abortedAction = abortedAction;
    _retryingActionSelector = retryingActionSelector;
    _interruptedActionSelector = interruptedActionSelector;
    _abortedActionSelector = abortedActionSelector;
  }

  /**
   * Base type for event monitors that print information about retry events as text.
   *
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   */
  public PrintEvents(PrintAction retryingAction, PrintAction interruptedAction, PrintAction abortedAction) {
    this(retryingAction, interruptedAction, abortedAction,
        outcome -> retryingAction, outcome -> interruptedAction, outcome -> abortedAction);
  }

  /**
   * Base type for event monitors that print information about retry events as text.
   */
  public PrintEvents() {
    this(DEFAULT_RETRYING_ACTION, DEFAULT_INTERRUPTED_ACTION, DEFAULT_ABORTED_ACTION);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrying(String name, Try<T> outcome, int attempts, long backoffTime, boolean isSilent) {
    if (!isSilent) {
      PrintAction action = _retryingActionSelector.apply(outcome);
      if (action != PrintAction.NOTHING) {
        printEvent(formatRetrying(name, outcome, attempts, backoffTime), outcome, action == PrintAction.MESSAGE_AND_STACK_TRACE);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void interrupted(String name, Try<T> outcome, int attempts) {
    PrintAction action = _interruptedActionSelector.apply(outcome);
    if (action != PrintAction.NOTHING) {
      printEvent(formatInterrupted(name, outcome, attempts), outcome, action == PrintAction.MESSAGE_AND_STACK_TRACE);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void aborted(String name, Try<T> outcome, int attempts) {
    PrintAction action = _abortedActionSelector.apply(outcome);
    if (action != PrintAction.NOTHING) {
      printEvent(formatAborted(name, outcome, attempts), outcome, action == PrintAction.MESSAGE_AND_STACK_TRACE);
    }
  }

  /**
   * Utility method that handles locking on the target object when printing both a message and a stack trace.
   *
   * @param message A message to print.
   * @param outcome
   * @param printStackTrace
   */
  protected void printEvent(String message, Try<T> outcome, boolean printStackTrace) {
    if (outcome.isFailed() && printStackTrace) {
      printMessageAndStackTrace(message, outcome.getError());
    } else {
      printMessage(message);
    }
  }

  /**
   * Prints a message the to underlying target object.
   *
   * @param message A message to print.
   */
  protected abstract void printMessage(String message);

  /**
   * Prints a stack trace to the underlying target object.
   *
   * @param message A message to print.
   * @param error Error from a failed task.
   */
  protected abstract void printMessageAndStackTrace(String message, Throwable error);
}
