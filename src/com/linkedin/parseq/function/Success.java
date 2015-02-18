package com.linkedin.parseq.function;

import java.util.Objects;

public class Success<T> implements Try<T>{

  private final T _value;

  private Success(T value) {
    this._value = value;
  }

  @Override
  public T get() {
    return _value;
  }

  @Override
  public boolean isFailed() {
    return false;
  }

  @Override
  public Throwable getError() {
    return null;
  }

  @Override
  public Result result() {
    return Result.success;
  }

  public static <R> Try<R> of(R value) {
    return new Success<R>(value);
  }

  @Override
  public int hashCode() {
      return Objects.hash(_value);
  }

  @Override
  public boolean equals(Object other) {
      if(other instanceof Success) {
          Success<?> that = (Success<?>) other;
          return Objects.equals(this._value, that._value);
      } else {
          return false;
      }
  }

  @Override
  public String toString() {
      return "success(" + _value + ")";
  }

}
