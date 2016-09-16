package com.linkedin.parseq.retry.monitor;


/**
 * A monitor that will always pass invocations on to the two specified monitors.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class ChainedEvents implements EventMonitor {
  protected final EventMonitor _first;
  protected final EventMonitor _second;

  /**
   * A monitor that will always pass invocations on to the two specified monitors.
   *
   * @param first The first event monitor to pass all invocations to.
   * @param second The second event monitor to pass all invocations to.
   */
  public ChainedEvents(EventMonitor first, EventMonitor second) {
    _first = first;
    _second = second;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrying(String name, Throwable error, int attempts, long backoffTime, boolean isSilent) {
    try {
      _first.retrying(name, error, attempts, backoffTime, isSilent);
    } catch (Throwable firstError) {
      try {
        _second.retrying(name, firstError, attempts, backoffTime, isSilent);
      } catch (Throwable secondError) {
        // ignore exception from the second monitor and rethrow first monitor's exception instead
      }
      throw firstError;
    }
    _second.retrying(name, error, attempts, backoffTime, isSilent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void interrupted(String name, Throwable error, int attempts) {
    try {
      _first.interrupted(name, error, attempts);
    } catch (Throwable firstError) {
      try {
        _second.interrupted(name, firstError, attempts);
      } catch (Throwable secondError) {
        // ignore exception from the second monitor and rethrow first monitor's exception instead
      }
      throw firstError;
    }
    _second.interrupted(name, error, attempts);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void aborted(String name, Throwable error, int attempts) {
    try {
      _first.aborted(name, error, attempts);
    } catch (Throwable firstError) {
      try {
        _second.aborted(name, firstError, attempts);
      } catch (Throwable secondError) {
        // ignore exception from the second monitor and rethrow first monitor's exception instead
      }
      throw firstError;
    }
    _second.aborted(name, error, attempts);
  }
}
