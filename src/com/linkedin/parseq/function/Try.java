package com.linkedin.parseq.function;

public interface Try<T> {

  public enum Result {
    success,
    failure
  }

  T get();

  boolean isFailed();

  Throwable getError();

  Result result();

}
