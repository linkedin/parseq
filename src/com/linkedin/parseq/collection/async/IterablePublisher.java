package com.linkedin.parseq.collection.async;

import java.util.function.Function;

import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.TaskOrValue;

public abstract class IterablePublisher<A, T> implements Publisher<TaskOrValue<T>> {

  private Subscriber<? super TaskOrValue<T>> _subscriber;
  private final Function<A, TaskOrValue<T>> _converter;
  CancellableSubscription _subscription;

  public IterablePublisher(Function<A, TaskOrValue<T>> converter) {
    ArgumentUtil.notNull(converter, "converter");
    _converter = converter;
  }

  abstract Iterable<A> getElements();

  @Override
  public void subscribe(final Subscriber<? super TaskOrValue<T>> subscriber) {
    ArgumentUtil.notNull(subscriber, "subscriber");
    _subscriber = subscriber;
  }

  public void run() {
    if (_subscriber != null) {
      _subscription = new CancellableSubscription();
      _subscriber.onSubscribe(_subscription);
      for (A e : getElements()) {
        if (!_subscription.isCancelled()) {
          _subscriber.onNext(_converter.apply(e));
        }
      }
    } else {
      throw new IllegalStateException("attempting to push elements to subscriber but it has not been set");
    }
  }

  public <P> void complete(Promise<P> p) {
    if (_subscriber != null) {
      if (!_subscription.isCancelled()) {
        if (!p.isFailed()) {
          _subscriber.onComplete();
        } else {
          _subscriber.onError(p.getError());
        }
      }
    }
  }
}
