package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Consumer1;
import com.linkedin.parseq.function.Consumer6;
import com.linkedin.parseq.function.Function6;
import com.linkedin.parseq.function.Tuple6;

public interface Tuple6Task<T1, T2, T3, T4, T5, T6> extends Task<Tuple6<T1, T2, T3, T4, T5, T6>> {

  default <R> Task<R> map(final Function6<T1, T2, T3, T4, T5, T6, R> f) {
    return map(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6()));
  }

  default <R> Task<R> map(final String desc, final Function6<T1, T2, T3, T4, T5, T6, R> f) {
    return map(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6()));
  }

  default <R> Task<R> flatMap(final Function6<T1, T2, T3, T4, T5, T6, Task<R>> f) {
    return flatMap(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6()));
  }

  default <R> Task<R> flatMap(final String desc, final Function6<T1, T2, T3, T4, T5, T6, Task<R>> f) {
    return flatMap(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6()));
  }

  default Tuple6Task<T1, T2, T3, T4, T5, T6> andThen(final Consumer6<T1, T2, T3, T4, T5, T6> consumer) {
    return cast(andThen(tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6())));
  }

  default Tuple6Task<T1, T2, T3, T4, T5, T6> andThen(final String desc, final Consumer6<T1, T2, T3, T4, T5, T6> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple6Task<T1, T2, T3, T4, T5, T6> recover(final Function1<Throwable, Tuple6<T1, T2, T3, T4, T5, T6>> f) {
    return cast(Task.super.recover(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple6Task<T1, T2, T3, T4, T5, T6> recover(final String desc, final Function1<Throwable, Tuple6<T1, T2, T3, T4, T5, T6>> f) {
    return cast(Task.super.recover(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple6Task<T1, T2, T3, T4, T5, T6> recoverWith(final Function1<Throwable, Task<Tuple6<T1, T2, T3, T4, T5, T6>>> f) {
    return cast(Task.super.recoverWith(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple6Task<T1, T2, T3, T4, T5, T6> recoverWith(final String desc, final Function1<Throwable, Task<Tuple6<T1, T2, T3, T4, T5, T6>>> f) {
    return cast(Task.super.recoverWith(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple6Task<T1, T2, T3, T4, T5, T6> onFailure(final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple6Task<T1, T2, T3, T4, T5, T6> onFailure(final String desc, final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(desc, consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple6Task<T1, T2, T3, T4, T5, T6> shareable() {
    return cast(Task.super.shareable());
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple6Task<T1, T2, T3, T4, T5, T6> withTimeout(final long time, final TimeUnit unit) {
    return cast(Task.super.withTimeout(time, unit));
  };

  default Tuple6Task<T1, T2, T3, T4, T5, T6> withSideEffect(Function6<T1, T2, T3, T4, T5, T6, Task<?>> func) {
    return cast(Task.super.withSideEffect(tuple -> func.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6())));
  }

  default Tuple6Task<T1, T2, T3, T4, T5, T6> withSideEffect(final String desc, Function6<T1, T2, T3, T4, T5, T6, Task<?>> func) {
    return cast(Task.super.withSideEffect(desc, tuple -> func.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6())));
  }

  public static <T1, T2, T3, T4, T5, T6> Tuple6Task<T1, T2, T3, T4, T5, T6> cast(final Task<Tuple6<T1, T2, T3, T4, T5, T6>> task) {
    return new Tuple6TaskDelegate<>(task);
  }

}
