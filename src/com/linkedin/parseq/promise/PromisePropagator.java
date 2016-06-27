package com.linkedin.parseq.promise;

import java.util.function.BiConsumer;


public interface PromisePropagator<S, T> extends BiConsumer<Promise<S>, Settable<T>> {

  default <R> PromisePropagator<S, R> compose(final PromisePropagator<T, R> propagator) {
    final PromisePropagator<S, T> that = this;
    return (src, dst) -> {
      that.accept(src, new Settable<T>() {
        @Override
        public void done(T value) throws PromiseResolvedException {
          propagator.accept(Promises.value(value), dst);
        }

        @Override
        public void fail(Throwable error) throws PromiseResolvedException {
          propagator.accept(Promises.error(error), dst);
        }
      });
    };
  }

}
