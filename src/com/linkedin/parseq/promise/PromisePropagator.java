package com.linkedin.parseq.promise;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

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

  default <R> PromisePropagator<S, R> map(final Function<? super T, ? extends R> f) {
    final PromisePropagator<S, T> that = this;
    return (src, dst) -> {
      that.accept(src, new Settable<T>() {
        @Override
        public void done(T value) throws PromiseResolvedException {
          try {
            dst.done(f.apply(value));
          } catch (Throwable t) {
            dst.fail(t);
          }
        }
        @Override
        public void fail(Throwable error) throws PromiseResolvedException {
          dst.fail(error);
        }
      });
    };
  }

  default PromisePropagator<S, T> andThen(final Consumer<? super T> consumer) {
    return map(x -> {
      consumer.accept(x);
      return x;
    });
  }

}
