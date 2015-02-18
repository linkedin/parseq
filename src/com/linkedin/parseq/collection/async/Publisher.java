package com.linkedin.parseq.collection.async;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Function;


public interface Publisher<T> {

  void subscribe(Subscriber<? super T> subscriber);

  public static <A> Publisher<A> flatten(Publisher<? extends Publisher<? extends A>> publisher) {
    return publisher.flatMap(p -> p);
  }

  default <A> Publisher<A> flatMap(Function<? super T, ? extends Publisher<? extends A>> f) {
      final Publisher<T> that = this;
      return new Publisher<A>() {
        private Subscriber<? super A> _subscriberOfFlatMappedA = null;
        private final Queue<Publisher<? extends A>> _publishers = new ArrayDeque<Publisher<? extends A>>();
        private final Map<Publisher<? extends A>, Subscription> _subscriptions = new HashMap<Publisher<? extends A>, Subscription>();
        boolean _sourceDone = false;

        @Override
        public void subscribe(final Subscriber<? super A> subscriberOfFlatMappedA) {
          _subscriberOfFlatMappedA = subscriberOfFlatMappedA;
          that.subscribe(new Subscriber<T>() {

            /**
             * Subscribe to received publishers
             */
            private void subscribe(final Publisher<? extends A> publisher) {
              publisher.subscribe(new Subscriber<A>() {

                @Override
                public void onComplete() {
                  _publishers.remove(publisher);
                  _subscriptions.remove(publisher);
                  if (_sourceDone) {
                    _publishers.clear();
                    _subscriptions.clear();
                    subscriberOfFlatMappedA.onComplete();
                  }
                }

                @Override
                public void onError(Throwable cause) {
                  _publishers.remove(publisher);
                  _subscriptions.remove(publisher);
                  subscriberOfFlatMappedA.onError(cause);
                  cancelOtherPublishers();
                }

                private void cancelOtherPublishers() {
                  for (Map.Entry<Publisher<? extends A>, Subscription> entry: _subscriptions.entrySet()) {
                    if (!entry.getKey().equals(publisher)) {
                      entry.getValue().cancel();
                    }
                  }
                }

                @Override
                public void onNext(A element) {
                  subscriberOfFlatMappedA.onNext(element);
                }

                @Override
                public void onSubscribe(Subscription subscription) {
                  _subscriptions.put(publisher, subscription);
                }
              });
            }

            @Override
            public void onComplete() {
              // source publisher has finished
              _sourceDone = true;
              if (_publishers.size() == 0) {
                _publishers.clear();
                _subscriptions.clear();
                subscriberOfFlatMappedA.onComplete();
              }
            }

            private void cancelAllSubscriptions() {
              for (Subscription s: _subscriptions.values()) {
                s.cancel();
              }
            }

            @Override
            public void onError(Throwable cause) {
              // source publisher has failed
              _sourceDone = true;
              cancelAllSubscriptions();
              _publishers.clear();
              _subscriptions.clear();
              subscriberOfFlatMappedA.onError(cause);
            }

            @Override
            public void onNext(T element) {
              Publisher<? extends A> publisher = f.apply(element);
              _publishers.add(publisher);
              subscribe(publisher);
            }

            @Override
            public void onSubscribe(final Subscription subscription) {
              _subscriberOfFlatMappedA.onSubscribe(() -> {
                subscription.cancel();
                cancelAllSubscriptions();
                _publishers.clear();
                _subscriptions.clear();
              });

            }
          });
        }

      };
  }
}