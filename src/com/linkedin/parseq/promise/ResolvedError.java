package com.linkedin.parseq.promise;

import java.util.concurrent.TimeUnit;

public class ResolvedError<T> implements Promise<T> {

  private final Throwable _error;

  public ResolvedError(Throwable error) {
    _error = error;
  }

  @Override
  public T get() throws PromiseException {
    throw new PromiseException(_error);
  }

  @Override
  public Throwable getError() throws PromiseUnresolvedException {
    return _error;
  }

  @Override
  public T getOrDefault(T defaultValue) throws PromiseUnresolvedException {
    return defaultValue;
  }

  @Override
  public void await() throws InterruptedException {
  }

  @Override
  public boolean await(long time, TimeUnit unit) throws InterruptedException {
    return true;
  }

  @Override
  public void addListener(PromiseListener<T> listener) {
    listener.onResolved(this);
  }

  @Override
  public boolean isDone() {
    return true;
  }

  @Override
  public boolean isFailed() {
    return true;
  }

}
