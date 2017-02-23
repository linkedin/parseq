package com.linkedin.parseq;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PoissonEventsArrival implements EventsArrival {

  private final Random _rand = new Random(System.nanoTime());
  private final double _nanosToNextEventMean;

  public PoissonEventsArrival(double events, TimeUnit perUnit) {
    _nanosToNextEventMean = perUnit.toNanos(1) / events;
  }

  @Override
  public long nanosToNextEvent() {
    //rand is uniformly distributed form 0.0d inclusive up to 1.0d exclusive
    double rand = _rand.nextDouble();
    return (long)(-_nanosToNextEventMean * Math.log(1 - rand));
  }

  @Override
  public String toString() {
    return "PoissonEventsArrival [nanosToNextEventMean=" + _nanosToNextEventMean + "]";
  }


}
