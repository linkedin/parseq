package com.linkedin.parseq.retry.monitor;

import java.util.Optional;
import java.util.function.Function;


/**
 * Base type for event monitors that submit log entries for retry events.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public abstract class LogEvents<U> implements EventMonitor {
  /** The action that is performed by default when a retrying event is received (empty means don't log). */
  protected final Optional<U> _retryingAction;

  /** The action that is performed by default when an interrupted event is received (empty means don't log). */
  protected final Optional<U> _interruptedAction;

  /** The action that is performed by default when an aborted event is received (empty means don't log). */
  protected final Optional<U> _abortedAction;

  /** The strategy used to select an action to perform for a retrying event, defaulting to `retryingAction`. */
  protected final Function<Throwable, Optional<U>> _retryingActionSelector;

  /** The strategy used to select an action to perform for an interrupted event, defaulting to `interruptedAction`. */
  protected final Function<Throwable, Optional<U>> _interruptedActionSelector;

  /** The strategy used to select an action to perform for an aborted event, defaulting to `abortedAction`. */
  protected final Function<Throwable, Optional<U>> _abortedActionSelector;

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
      Function<Throwable, Optional<U>> retryingActionSelector, Function<Throwable, Optional<U>> interruptedActionSelector, Function<Throwable, Optional<U>> abortedActionSelector) {
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
        error -> retryingAction, error -> interruptedAction, error -> abortedAction);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrying(String name, Throwable error, int attempts, long backoffTime, boolean isSilent) {
    if (!isSilent) {
      _retryingActionSelector.apply(error).ifPresent(level -> {
        if (isLoggable(level)) {
          log(level, formatRetrying(name, error, attempts, backoffTime), error);
        }
      });
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void interrupted(String name, Throwable error, int attempts) {
    _interruptedActionSelector.apply(error).ifPresent(level -> {
      if (isLoggable(level)) {
        log(level, formatInterrupted(name, error, attempts), error);
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void aborted(String name, Throwable error, int attempts) {
    _abortedActionSelector.apply(error).ifPresent(level -> {
      if (isLoggable(level)) {
        log(level, formatAborted(name, error, attempts), error);
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
   * @param error Task error (either result or a Throwable)
   */
  protected abstract void log(U level, String message, Throwable error);
}
