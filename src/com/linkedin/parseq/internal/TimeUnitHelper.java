package com.linkedin.parseq.internal;

import java.util.concurrent.TimeUnit;


public class TimeUnitHelper {

  private static final String[] names = new String[] { "ns", "us", "ms", "s", "min", "h", "d" };

  public static String toString(TimeUnit unit) {
    return names[unit.ordinal()];
  }
}
