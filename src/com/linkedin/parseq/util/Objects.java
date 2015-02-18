package com.linkedin.parseq.util;

public class Objects {

  private Objects() {}

  public static void requireNonNull(Object o) {
    if (o == null) {
      throw new IllegalArgumentException("Argument must not be null");
    }
  }

}
