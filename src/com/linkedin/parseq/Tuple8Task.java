package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Consumer1;
import com.linkedin.parseq.function.Consumer8;
import com.linkedin.parseq.function.Function8;
import com.linkedin.parseq.function.Tuple8;

public interface Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> extends Task<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> {

  default <R> Task<R> map(final Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> f) {
    return map(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8()));
  }

  default <R> Task<R> map(final String desc, final Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> f) {
    return map(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8()));
  }

  default <R> Task<R> flatMap(final Function8<T1, T2, T3, T4, T5, T6, T7, T8, Task<R>> f) {
    return flatMap(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8()));
  }

  default <R> Task<R> flatMap(final String desc, final Function8<T1, T2, T3, T4, T5, T6, T7, T8, Task<R>> f) {
    return flatMap(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8()));
  }

  default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> andThen(final Consumer8<T1, T2, T3, T4, T5, T6, T7, T8> consumer) {
    return cast(andThen(tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8())));
  }

  default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> andThen(final String desc, final Consumer8<T1, T2, T3, T4, T5, T6, T7, T8> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> recover(final Function1<Throwable, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> f) {
    return cast(Task.super.recover(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> recover(final String desc, final Function1<Throwable, Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> f) {
    return cast(Task.super.recover(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> recoverWith(final Function1<Throwable, Task<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>> f) {
    return cast(Task.super.recoverWith(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> recoverWith(final String desc, final Function1<Throwable, Task<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>>> f) {
    return cast(Task.super.recoverWith(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> onFailure(final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> onFailure(final String desc, final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(desc, consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> shareable() {
    return cast(Task.super.shareable());
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> withTimeout(final long time, final TimeUnit unit) {
    return cast(Task.super.withTimeout(time, unit));
  };

  default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> withSideEffect(Function8<T1, T2, T3, T4, T5, T6, T7, T8, Task<?>> func) {
    return cast(Task.super.withSideEffect(tuple -> func.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8())));
  }

  default Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> withSideEffect(final String desc, Function8<T1, T2, T3, T4, T5, T6, T7, T8, Task<?>> func) {
    return cast(Task.super.withSideEffect(desc, tuple -> func.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8())));
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> cast(final Task<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> task) {
    return new Tuple8TaskDelegate<>(task);
  }

}
