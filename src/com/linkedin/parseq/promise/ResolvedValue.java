package com.linkedin.parseq.promise;

import java.util.concurrent.TimeUnit;

public class ResolvedValue<T> implements Promise<T> {

  private final T _value;

  public ResolvedValue(T value) {
    _value = value;
  }

  @Override
  public T get() throws PromiseException {
    return _value;
  }

  @Override
  public Throwable getError() throws PromiseUnresolvedException {
    return null;
  }

  @Override
  public T getOrDefault(T defaultValue) throws PromiseUnresolvedException {
    return _value;
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
    return false;
  }

}
