package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Consumer1;
import com.linkedin.parseq.function.Consumer7;
import com.linkedin.parseq.function.Function7;
import com.linkedin.parseq.function.Tuple7;

public interface Tuple7Task<T1, T2, T3, T4, T5, T6, T7> extends Task<Tuple7<T1, T2, T3, T4, T5, T6, T7>> {

  default <R> Task<R> map(final Function7<T1, T2, T3, T4, T5, T6, T7, R> f) {
    return map(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7()));
  }

  default <R> Task<R> map(final String desc, final Function7<T1, T2, T3, T4, T5, T6, T7, R> f) {
    return map(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7()));
  }

  default <R> Task<R> flatMap(final Function7<T1, T2, T3, T4, T5, T6, T7, Task<R>> f) {
    return flatMap(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7()));
  }

  default <R> Task<R> flatMap(final String desc, final Function7<T1, T2, T3, T4, T5, T6, T7, Task<R>> f) {
    return flatMap(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7()));
  }

  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> andThen(final Consumer7<T1, T2, T3, T4, T5, T6, T7> consumer) {
    return cast(andThen(tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7())));
  }

  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> andThen(final String desc, final Consumer7<T1, T2, T3, T4, T5, T6, T7> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recover(final Function1<Throwable, Tuple7<T1, T2, T3, T4, T5, T6, T7>> f) {
    return cast(Task.super.recover(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recover(final String desc, final Function1<Throwable, Tuple7<T1, T2, T3, T4, T5, T6, T7>> f) {
    return cast(Task.super.recover(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recoverWith(final Function1<Throwable, Task<Tuple7<T1, T2, T3, T4, T5, T6, T7>>> f) {
    return cast(Task.super.recoverWith(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recoverWith(final String desc, final Function1<Throwable, Task<Tuple7<T1, T2, T3, T4, T5, T6, T7>>> f) {
    return cast(Task.super.recoverWith(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> onFailure(final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> onFailure(final String desc, final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(desc, consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> shareable() {
    return cast(Task.super.shareable());
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> withTimeout(final long time, final TimeUnit unit) {
    return cast(Task.super.withTimeout(time, unit));
  };

  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> withSideEffect(Function7<T1, T2, T3, T4, T5, T6, T7, Task<?>> func) {
    return cast(Task.super.withSideEffect(tuple -> func.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7())));
  }

  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> withSideEffect(final String desc, Function7<T1, T2, T3, T4, T5, T6, T7, Task<?>> func) {
    return cast(Task.super.withSideEffect(desc, tuple -> func.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7())));
  }

  public static <T1, T2, T3, T4, T5, T6, T7> Tuple7Task<T1, T2, T3, T4, T5, T6, T7> cast(final Task<Tuple7<T1, T2, T3, T4, T5, T6, T7>> task) {
    return new Tuple7TaskDelegate<>(task);
  }

}
