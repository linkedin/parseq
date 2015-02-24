package com.linkedin.parseq.function;

public interface Try<T> {

  public enum ResultType {
    success,
    failure
  }

  T get();

  boolean isFailed();

  Throwable getError();

  ResultType resultType();

}
