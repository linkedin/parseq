package com.linkedin.parseq.retry.monitor;

import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An event monitor that formats and logs events using the `java.util.logging` framework.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class LogEventsWithJava extends LogEvents<Level> {
  public static final Optional<Level> DEFAULT_RETRYING_ACTION = Optional.of(Level.INFO);
  public static final Optional<Level> DEFAULT_INTERRUPTED_ACTION = Optional.of(Level.WARNING);
  public static final Optional<Level> DEFAULT_ABORTED_ACTION = Optional.of(Level.SEVERE);

  protected final Logger _logger;

  /**
   * An event monitor that formats and logs events using the `java.util.logging` framework.
   *
   * @param logger The logger that this event monitor submits to.
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   * @param retryingActionSelector The strategy used to select an action to perform for a retrying event, defaulting to `retryingAction`.
   * @param interruptedActionSelector The strategy used to select an action to perform for an interrupted event, defaulting to `interruptedAction`.
   * @param abortedActionSelector The strategy used to select an action to perform for an aborted event, defaulting to `abortedAction`.
   */
  public LogEventsWithJava(Logger logger, Optional<Level> retryingAction, Optional<Level> interruptedAction, Optional<Level> abortedAction,
      Function<Throwable, Optional<Level>> retryingActionSelector, Function<Throwable, Optional<Level>> interruptedActionSelector, Function<Throwable, Optional<Level>> abortedActionSelector) {
    super(retryingAction, interruptedAction, abortedAction, retryingActionSelector, interruptedActionSelector, abortedActionSelector);
    _logger = logger;
  }

  /**
   * An event monitor that formats and logs events using the `java.util.logging` framework.
   *
   * @param logger The logger that this event monitor submits to.
   * @param retryingAction The action that is performed by default when a retrying event is received.
   * @param interruptedAction The action that is performed by default when an interrupted event is received.
   * @param abortedAction The action that is performed by default when an aborted event is received.
   */
  public LogEventsWithJava(Logger logger, Optional<Level> retryingAction, Optional<Level> interruptedAction, Optional<Level> abortedAction) {
    super(retryingAction, interruptedAction, abortedAction);
    _logger = logger;
  }

  /**
   * An event monitor that formats and logs events using the `java.util.logging` framework.
   *
   * @param logger The logger that this event monitor submits to.
   */
  public LogEventsWithJava(Logger logger) {
    super(DEFAULT_RETRYING_ACTION, DEFAULT_INTERRUPTED_ACTION, DEFAULT_ABORTED_ACTION);
    _logger = logger;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isLoggable(Level level) {
    return _logger.isLoggable(level);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void log(Level level, String message, Throwable error) {
    _logger.log(level, message, error);
  }
}
