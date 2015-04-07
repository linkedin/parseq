package com.linkedin.parseq.promise;

import com.linkedin.parseq.function.Function1;


public class PromiseTransformer<S, T> implements PromisePropagator<S, T> {

  private final Function1<? super S, ? extends T> _transform;

  public PromiseTransformer(Function1<? super S, ? extends T> transform) {
    _transform = transform;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public <R> PromisePropagator<S, R> compose(final PromisePropagator<T, R> propagator) {
    if (propagator instanceof PromiseTransformer) {
      return doCompose((PromiseTransformer) propagator);
    } else {
      return PromisePropagator.super.compose(propagator);
    }
  }

  private <R> PromiseTransformer<S, R> doCompose(final PromiseTransformer<T, R> propagator) {
    return new PromiseTransformer<S, R>(_transform.andThen(propagator._transform));
  }

  @Override
  public void accept(Promise<S> src, Settable<T> dst) {
    if (src.isFailed()) {
      dst.fail(src.getError());
    } else {
      try {
        dst.done(_transform.apply(src.get()));
      } catch (Throwable t) {
        dst.fail(t);
      }
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static final PromiseTransformer IDENTITY = new PromiseTransformer(Function1.identity());

  @SuppressWarnings("unchecked")
  public static <R> PromiseTransformer<R, R> identity() {
    return (PromiseTransformer<R, R>) IDENTITY;
  }
}
