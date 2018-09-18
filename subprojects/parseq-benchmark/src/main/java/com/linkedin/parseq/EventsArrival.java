package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;

/**
 * This interface represents an arrival process. It contains one method that returns number of nanoseconds
 * until next arrival.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public interface EventsArrival {

  /**
   * Returns number of nanoseconds until next arrival.
   * @return
   */
  long nanosToNextEvent();

  static EventsArrival fromName(final String name, final double events, final TimeUnit perUnit) {
    switch (name) {
      case "poisson":
        return new PoissonEventsArrival(events, perUnit);
      case "uniform":
        return new UniformEventsArrival(events, perUnit);
    }
    throw new IllegalArgumentException("unsupported events arrival type: " + name);
  }
}
