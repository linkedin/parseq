package com.linkedin.parseq.retry.monitor;

import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;


/**
 * An event monitor that formats and logs events using Slf4j.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class LogEventsWithSlf4j extends LogEvents<Slf4jLevel> {
  public static final Optional<Slf4jLevel> DEFAULT_RETRYING_ACTION = Optional.of(Slf4jLevel.INFO);
  public static final Optional<Slf4jLevel> DEFAULT_INTERRUPTED_ACTION = Optional.of(Slf4jLevel.WARNING);
  public static final Optional<Slf4jLevel> DEFAULT_ABORTED_ACTION = Optional.of(Slf4jLevel.ERROR);

  protected final Logger _logger;

  /**
   * An event monitor that formats and logs events using Slf4j.
   *
   * @param logger The logger that this event monitor submits to.
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   * @param retryingActionSelector The strategy used to select an action to perform for a retrying event, defaulting to `retryingAction`.
   * @param interruptedActionSelector The strategy used to select an action to perform for an interrupted event, defaulting to `interruptedAction`.
   * @param abortedActionSelector The strategy used to select an action to perform for an aborted event, defaulting to `abortedAction`.
   */
  public LogEventsWithSlf4j(Logger logger, Optional<Slf4jLevel> retryingAction, Optional<Slf4jLevel> interruptedAction, Optional<Slf4jLevel> abortedAction,
      Function<Throwable, Optional<Slf4jLevel>> retryingActionSelector, Function<Throwable, Optional<Slf4jLevel>> interruptedActionSelector, Function<Throwable, Optional<Slf4jLevel>> abortedActionSelector) {
    super(retryingAction, interruptedAction, abortedAction, retryingActionSelector, interruptedActionSelector, abortedActionSelector);
    _logger = logger;
  }

  /**
   * An event monitor that formats and logs events using Slf4j.
   *
   * @param logger The logger that this event monitor submits to.
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   */
  public LogEventsWithSlf4j(Logger logger, Optional<Slf4jLevel> retryingAction, Optional<Slf4jLevel> interruptedAction, Optional<Slf4jLevel> abortedAction) {
    super(retryingAction, interruptedAction, abortedAction);
    _logger = logger;
  }

  /**
   * An event monitor that formats and logs events using Slf4j.
   *
   * @param logger The logger that this event monitor submits to.
   */
  public LogEventsWithSlf4j(Logger logger) {
    super(DEFAULT_RETRYING_ACTION, DEFAULT_INTERRUPTED_ACTION, DEFAULT_ABORTED_ACTION);
    _logger = logger;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isLoggable(Slf4jLevel level) {
    switch (level) {
      case ERROR: return _logger.isErrorEnabled();
      case WARNING: return _logger.isWarnEnabled();
      case INFO: return _logger.isInfoEnabled();
      case DEBUG: return _logger.isDebugEnabled();
      case TRACE: return _logger.isTraceEnabled();
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void log(Slf4jLevel level, String message, Throwable error) {
    switch (level) {
      case ERROR: _logger.error(message, error);
      case WARNING: _logger.warn(message, error);
      case INFO: _logger.info(message, error);
      case DEBUG: _logger.debug(message, error);
      case TRACE: _logger.trace(message, error);
    }
  }
}
