package com.linkedin.parseq.promise;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResolvedValue<T> implements Promise<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResolvedValue.class);

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
    try {
      listener.onResolved(this);
    } catch (Throwable e) {
      LOGGER.warn("An exception was thrown by listener: " + listener.getClass(), e);
    }
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
