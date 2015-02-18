package com.linkedin.parseq.util;

public final class Integers {

  private Integers() {}

  public static void requireNonNegative(final int n) {
    if (n < 0) {
      throw new IllegalArgumentException("Argument must be a non negative integer numebr, but is: " + n);
    }
  }

  public static void requirePositive(final int n) {
    if (n <= 0) {
      throw new IllegalArgumentException("Argument must be a positive integer numebr, but is: " + n);
    }
  }
}
