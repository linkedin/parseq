package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Consumer1;
import com.linkedin.parseq.function.Consumer4;
import com.linkedin.parseq.function.Function4;
import com.linkedin.parseq.function.Tuple4;

public interface Tuple4Task<T1, T2, T3, T4> extends Task<Tuple4<T1, T2, T3, T4>> {

  default <R> Task<R> map(final Function4<T1, T2, T3, T4, R> f) {
    return map(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4()));
  }

  default <R> Task<R> map(final String desc, final Function4<T1, T2, T3, T4, R> f) {
    return map(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4()));
  }

  default <R> Task<R> flatMap(final Function4<T1, T2, T3, T4, Task<R>> f) {
    return flatMap(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4()));
  }

  default <R> Task<R> flatMap(final String desc, final Function4<T1, T2, T3, T4, Task<R>> f) {
    return flatMap(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4()));
  }

  default Tuple4Task<T1, T2, T3, T4> andThen(final Consumer4<T1, T2, T3, T4> consumer) {
    return cast(andThen(tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4())));
  }

  default Tuple4Task<T1, T2, T3, T4> andThen(final String desc, final Consumer4<T1, T2, T3, T4> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple4Task<T1, T2, T3, T4> recover(final Function1<Throwable, Tuple4<T1, T2, T3, T4>> f) {
    return cast(Task.super.recover(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple4Task<T1, T2, T3, T4> recover(final String desc, final Function1<Throwable, Tuple4<T1, T2, T3, T4>> f) {
    return cast(Task.super.recover(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple4Task<T1, T2, T3, T4> recoverWith(final Function1<Throwable, Task<Tuple4<T1, T2, T3, T4>>> f) {
    return cast(Task.super.recoverWith(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple4Task<T1, T2, T3, T4> recoverWith(final String desc, final Function1<Throwable, Task<Tuple4<T1, T2, T3, T4>>> f) {
    return cast(Task.super.recoverWith(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple4Task<T1, T2, T3, T4> onFailure(final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple4Task<T1, T2, T3, T4> onFailure(final String desc, final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(desc, consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple4Task<T1, T2, T3, T4> shareable() {
    return cast(Task.super.shareable());
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple4Task<T1, T2, T3, T4> withTimeout(final long time, final TimeUnit unit) {
    return cast(Task.super.withTimeout(time, unit));
  };

  default Tuple4Task<T1, T2, T3, T4> withSideEffect(Function4<T1, T2, T3, T4, Task<?>> func) {
    return cast(Task.super.withSideEffect(tuple -> func.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4())));
  }

  default Tuple4Task<T1, T2, T3, T4> withSideEffect(final String desc, Function4<T1, T2, T3, T4, Task<?>> func) {
    return cast(Task.super.withSideEffect(desc, tuple -> func.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4())));
  }

  public static <T1, T2, T3, T4> Tuple4Task<T1, T2, T3, T4> cast(final Task<Tuple4<T1, T2, T3, T4>> task) {
    return new Tuple4TaskDelegate<>(task);
  }

}
