package com.linkedin.parseq.retry.monitor;

import com.linkedin.parseq.function.Try;

import java.util.Optional;
import java.util.function.Function;


/**
 * Base type for event monitors that submit log entries for retry events.
 *
 * @param <T> Type of a task result, used for strongly typed processing of outcomes.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public abstract class LogEvents<T, U> implements EventMonitor<T> {
  /** The action that is performed by default when a retrying event is received (empty means don't log). */
  protected final Optional<U> _retryingAction;

  /** The action that is performed by default when an interrupted event is received (empty means don't log). */
  protected final Optional<U> _interruptedAction;

  /** The action that is performed by default when an aborted event is received (empty means don't log). */
  protected final Optional<U> _abortedAction;

  /** The strategy used to select an action to perform for a retrying event, defaulting to `retryingAction`. */
  protected final Function<Try<T>, Optional<U>> _retryingActionSelector;

  /** The strategy used to select an action to perform for an interrupted event, defaulting to `interruptedAction`. */
  protected final Function<Try<T>, Optional<U>> _interruptedActionSelector;

  /** The strategy used to select an action to perform for an aborted event, defaulting to `abortedAction`. */
  protected final Function<Try<T>, Optional<U>> _abortedActionSelector;

  /**
   * Base type for event monitors that submit log entries for retry events.
   *
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   * @param retryingActionSelector The strategy used to select an action to perform for a retrying event, defaulting to `retryingAction`.
   * @param interruptedActionSelector The strategy used to select an action to perform for an interrupted event, defaulting to `interruptedAction`.
   * @param abortedActionSelector The strategy used to select an action to perform for an aborted event, defaulting to `abortedAction`.
   */
  public LogEvents(Optional<U> retryingAction, Optional<U> interruptedAction, Optional<U> abortedAction,
      Function<Try<T>, Optional<U>> retryingActionSelector, Function<Try<T>, Optional<U>> interruptedActionSelector, Function<Try<T>, Optional<U>> abortedActionSelector) {
    _retryingAction = retryingAction;
    _interruptedAction = interruptedAction;
    _abortedAction = abortedAction;
    _retryingActionSelector = retryingActionSelector;
    _interruptedActionSelector = interruptedActionSelector;
    _abortedActionSelector = abortedActionSelector;
  }

  /**
   * Base type for event monitors that submit log entries for retry events.
   *
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   */
  public LogEvents(Optional<U> retryingAction, Optional<U> interruptedAction, Optional<U> abortedAction) {
    this(retryingAction, interruptedAction, abortedAction,
        outcome -> retryingAction, outcome -> interruptedAction, outcome -> abortedAction);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrying(String name, Try<T> outcome, int attempts, long backoffTime, boolean isSilent) {
    if (!isSilent) {
      _retryingActionSelector.apply(outcome).ifPresent(level -> {
        if (isLoggable(level)) {
          log(level, formatRetrying(name, outcome, attempts, backoffTime), outcome);
        }
      });
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void interrupted(String name, Try<T> outcome, int attempts) {
    _interruptedActionSelector.apply(outcome).ifPresent(level -> {
      if (isLoggable(level)) {
        log(level, formatInterrupted(name, outcome, attempts), outcome);
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void aborted(String name, Try<T> outcome, int attempts) {
    _abortedActionSelector.apply(outcome).ifPresent(level -> {
      if (isLoggable(level)) {
        log(level, formatAborted(name, outcome, attempts), outcome);
      }
    });
  }

  /**
   * Returns true if the specified level is currently loggable by the underlying logger.
   *
   * @param level Logging level
   */
  protected abstract boolean isLoggable(U level);

  /**
   * Logs information about an event to the underlying logger.
   *
   * @param level Logging level
   * @param message A message to log
   * @param outcome Task outcome (either result or a Throwable)
   */
  protected abstract void log(U level, String message, Try<T> outcome);
}
