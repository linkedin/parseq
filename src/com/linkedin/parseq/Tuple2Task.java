package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.function.Consumer1;
import com.linkedin.parseq.function.Consumer2;
import com.linkedin.parseq.function.Function2;
import com.linkedin.parseq.function.Tuple2;

public interface Tuple2Task<T1, T2> extends Task<Tuple2<T1, T2>> {

  default <R> Task<R> map(final Function2<T1, T2, R> f) {
    return map(tuple -> f.apply(tuple._1(), tuple._2()));
  }

  default <R> Task<R> map(final String desc, final Function2<T1, T2, R> f) {
    return map(desc, tuple -> f.apply(tuple._1(), tuple._2()));
  }

  default <R> Task<R> flatMap(final Function2<T1, T2, Task<R>> f) {
    return flatMap(tuple -> f.apply(tuple._1(), tuple._2()));
  }

  default <R> Task<R> flatMap(final String desc, final Function2<T1, T2, Task<R>> f) {
    return flatMap(desc, tuple -> f.apply(tuple._1(), tuple._2()));
  }

  default Tuple2Task<T1, T2> andThen(final Consumer2<T1, T2> consumer) {
    return cast(andThen(tuple -> consumer.accept(tuple._1(), tuple._2())));
  }

  default Tuple2Task<T1, T2> andThen(final String desc, final Consumer2<T1, T2> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(tuple._1(), tuple._2())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple2Task<T1, T2> recover(final Function1<Throwable, Tuple2<T1, T2>> f) {
    return cast(Task.super.recover(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple2Task<T1, T2> recover(final String desc, final Function1<Throwable, Tuple2<T1, T2>> f) {
    return cast(Task.super.recover(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple2Task<T1, T2> recoverWith(final Function1<Throwable, Task<Tuple2<T1, T2>>> f) {
    return cast(Task.super.recoverWith(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple2Task<T1, T2> recoverWith(final String desc, final Function1<Throwable, Task<Tuple2<T1, T2>>> f) {
    return cast(Task.super.recoverWith(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple2Task<T1, T2> onFailure(final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple2Task<T1, T2> onFailure(final String desc, final Consumer1<Throwable> consumer) {
    return cast(Task.super.onFailure(desc, consumer));
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple2Task<T1, T2> shareable() {
    return cast(Task.super.shareable());
  };

  /**
   * {@inheritDoc}
   */
  @Override
  public default Tuple2Task<T1, T2> withTimeout(final long time, final TimeUnit unit) {
    return cast(Task.super.withTimeout(time, unit));
  };

  default Tuple2Task<T1, T2> withSideEffect(Function2<T1, T2, Task<?>> func) {
    return cast(Task.super.withSideEffect(tuple -> func.apply(tuple._1(), tuple._2())));
  }

  default Tuple2Task<T1, T2> withSideEffect(final String desc, Function2<T1, T2, Task<?>> func) {
    return cast(Task.super.withSideEffect(desc, tuple -> func.apply(tuple._1(), tuple._2())));
  }

  public static <T1, T2> Tuple2Task<T1, T2> cast(final Task<Tuple2<T1, T2>> task) {
    return new Tuple2TaskDelegate<>(task);
  }

}
