package com.linkedin.parseq.function;

import java.util.NoSuchElementException;
import java.util.Objects;

public class Failure<T> implements Try<T> {

  private final Throwable _error;

  private Failure(Throwable error) {
    _error = error;
  }

  @Override
  public T get() {
    throw (NoSuchElementException)new NoSuchElementException().initCause(_error);
  }

  @Override
  public boolean isFailed() {
    return true;
  }

  @Override
  public Throwable getError() {
    return _error;
  }

  @Override
  public Result result() {
    return Result.failure;
  }

  public static <R> Try<R> of(Throwable t) {
    return new Failure<R>(t);
  }

  @Override
  public int hashCode() {
      return Objects.hash(_error);
  }

  @Override
  public boolean equals(Object other) {
      if(other instanceof Failure) {
          Failure<?> that = (Failure<?>) other;
          return Objects.equals(this._error, that._error);
      } else {
          return false;
      }
  }

  @Override
  public String toString() {
      return "failure(" + _error + ")";
  }

}
