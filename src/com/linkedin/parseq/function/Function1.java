package com.linkedin.parseq.function;

import java.util.Objects;


@FunctionalInterface
public interface Function1<T1, R> {

  R apply(T1 t1) throws Exception;

  default <V> Function1<T1, V> andThen(Function1<? super R, ? extends V> f) {
    Objects.requireNonNull(f);
    return (T1 t1) -> f.apply(apply(t1));
  }

  /**
   * Returns a function that always returns its input argument.
   *
   * @param <T> the type of the input and output objects to the function
   * @return a function that always returns its input argument
   */
  static <T> Function1<T, T> identity() {
    return t -> t;
  }

}
