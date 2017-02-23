package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;

public interface EventsArrival {
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
