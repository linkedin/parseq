package com.linkedin.parseq.promise;

public interface Settable<P> {
  /**
   * Sets the value for this promise to the given value.
   * <p/>
   * The guarantee is that after this method is finished the promise
   * has been completed but it is not guaranteed that all listeners have been
   * called. Listeners can be notified shortly after this method returns.
   *
   * @param value the value to set on this promise.
   * @throws PromiseResolvedException if the promise already has a value set.
   */
  void done(P value) throws PromiseResolvedException;

  /**
   * Sets an error on this promise.
   * <p/>
   * The guarantee is that after this method is finished the promise
   * has been completed but it is not guaranteed that all listeners have been
   * called. Listeners can be notified shortly after this method returns.
   *
   * @param error the error to set on this promise
   * @throws PromiseResolvedException if the promise already has a value set.
   */
  void fail(Throwable error) throws PromiseResolvedException;

}
