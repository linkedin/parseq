package com.linkedin.parseq.collection.async;

@FunctionalInterface
public interface Subscription {
  void cancel();
}
