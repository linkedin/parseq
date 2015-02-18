package com.linkedin.parseq.promise;

public interface Settable<P> {
  /**
   * Sets the value for this promise to the given value.
   *
   * @param value the value to set on this promise.
   * @throws PromiseResolvedException if the promise already has a value set.
   */
  void done(P value) throws PromiseResolvedException;

  /**
   * Sets an error on this promise.
   *
   * @param error the error to set on this promise
   * @throws PromiseResolvedException if the promise already has a value set.
   */
  void fail(Throwable error) throws PromiseResolvedException;

}
