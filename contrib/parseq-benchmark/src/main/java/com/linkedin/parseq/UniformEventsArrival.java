package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;

public class UniformEventsArrival implements EventsArrival {
  private final double _nanosToNextEvent;

  public UniformEventsArrival(double events, TimeUnit perUnit) {
    _nanosToNextEvent = perUnit.toNanos(1) / events;
  }

  @Override
  public long nanosToNextEvent() {
    return (long)_nanosToNextEvent;
  }

  @Override
  public String toString() {
    return "UniformEventsArrival [nanosToNextEvent=" + _nanosToNextEvent + "]";
  }
}
