package com.linkedin.parseq.function;

import java.util.NoSuchElementException;


/**
 * The {@code Try} interface represents result that may either be a
 * successful completion or a failure. It has two implementation: {@link Success}
 * and {@link Failure}. New instances of {@code Try} can be created by either
 * calling {@code Success.of(...)} of {@code Failure.of(...)}.
 *
 *
 * @see Success
 * @see Failure
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public interface Try<T> {

  public enum ResultType {
    success,
    failure
  }

  /**
   * Returns value associated with successful completion or
   * {@link NoSuchElementException} if result represents a failure.
   * @return value associated with successful completion
   * @throws NoSuchElementException if result is a failure
   */
  T get();

  boolean isFailed();

  /**
   * Returns {@code Throwable} associated with this failure or
   * null if result represents successful completion.
   * @return the error associated with this failure
   */
  Throwable getError();

  /**
   * Returns either {@link ResultType#success success} or {@link ResultType#failure failure}.
   * @return the type of the result
   */
  ResultType resultType();

}
