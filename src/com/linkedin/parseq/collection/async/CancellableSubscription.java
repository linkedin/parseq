package com.linkedin.parseq.collection.async;


public class CancellableSubscription implements Subscription {

  private boolean _cancelled = false;

  @Override
  public void cancel() {
    _cancelled = true;
  }

  public boolean isCancelled() {
    return _cancelled;
  }

}