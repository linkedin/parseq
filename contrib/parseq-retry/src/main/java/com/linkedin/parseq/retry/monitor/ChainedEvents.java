package com.linkedin.parseq.retry.monitor;

import com.linkedin.parseq.function.Try;


/**
 * A monitor that will always pass invocations on to the two specified monitors.
 *
 * @param <T> Type of a task result, used for strongly typed processing of outcomes.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public class ChainedEvents<T> implements EventMonitor<T> {
  protected final EventMonitor<T> _first;
  protected final EventMonitor<T> _second;

  /**
   * A monitor that will always pass invocations on to the two specified monitors.
   *
   * @param first The first event monitor to pass all invocations to.
   * @param second The second event monitor to pass all invocations to.
   */
  public ChainedEvents(EventMonitor<T> first, EventMonitor<T> second) {
    _first = first;
    _second = second;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void retrying(String name, Try<T> outcome, int attempts, long backoffTime, boolean isSilent) {
    try {
      _first.retrying(name, outcome, attempts, backoffTime, isSilent);
    } catch (Throwable error) {
      try {
        _second.retrying(name, outcome, attempts, backoffTime, isSilent);
      } catch (Throwable nestedError) {
        // ignore exception from the second monitor and rethrow first monitor's exception instead
      }
      throw error;
    }
    _second.retrying(name, outcome, attempts, backoffTime, isSilent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void interrupted(String name, Try<T> outcome, int attempts) {
    try {
      _first.interrupted(name, outcome, attempts);
    } catch (Throwable error) {
      try {
        _second.interrupted(name, outcome, attempts);
      } catch (Throwable nestedError) {
        // ignore exception from the second monitor and rethrow first monitor's exception instead
      }
      throw error;
    }
    _second.interrupted(name, outcome, attempts);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void aborted(String name, Try<T> outcome, int attempts) {
    try {
      _first.aborted(name, outcome, attempts);
    } catch (Throwable error) {
      try {
        _second.aborted(name, outcome, attempts);
      } catch (Throwable nestedError) {
        // ignore exception from the second monitor and rethrow first monitor's exception instead
      }
      throw error;
    }
    _second.aborted(name, outcome, attempts);
  }
}
