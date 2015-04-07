package com.linkedin.parseq;

import com.linkedin.parseq.function.Function1;

import com.linkedin.parseq.function.Consumer5;
import com.linkedin.parseq.function.Function5;
import com.linkedin.parseq.function.Tuple5;

public interface Tuple5Task<T1, T2, T3, T4, T5> extends Task<Tuple5<T1, T2, T3, T4, T5>> {

  default <R> Task<R> map(final Function5<T1, T2, T3, T4, T5, R> f) {
    return map(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5()));
  }

  default <R> Task<R> map(final String desc, final Function5<T1, T2, T3, T4, T5, R> f) {
    return map(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5()));
  }

  default <R> Task<R> flatMap(final Function5<T1, T2, T3, T4, T5, Task<R>> f) {
    return flatMap(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5()));
  }

  default <R> Task<R> flatMap(final String desc, final Function5<T1, T2, T3, T4, T5, Task<R>> f) {
    return flatMap(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5()));
  }

  default Tuple5Task<T1, T2, T3, T4, T5> andThen(final Consumer5<T1, T2, T3, T4, T5> consumer) {
    return cast(andThen(tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5())));
  }

  default Tuple5Task<T1, T2, T3, T4, T5> andThen(final String desc, final Consumer5<T1, T2, T3, T4, T5> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple5Task<T1, T2, T3, T4, T5> recover(final Function1<Throwable, Tuple5<T1, T2, T3, T4, T5>> f) {
    return cast(Task.super.recover(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple5Task<T1, T2, T3, T4, T5> recover(final String desc, final Function1<Throwable, Tuple5<T1, T2, T3, T4, T5>> f) {
    return cast(Task.super.recover(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple5Task<T1, T2, T3, T4, T5> recoverWith(final Function1<Throwable, Task<Tuple5<T1, T2, T3, T4, T5>>> f) {
    return cast(Task.super.recoverWith(f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple5Task<T1, T2, T3, T4, T5> recoverWith(final String desc, final Function1<Throwable, Task<Tuple5<T1, T2, T3, T4, T5>>> f) {
    return cast(Task.super.recoverWith(desc, f));
  }

  public static <T1, T2, T3, T4, T5> Tuple5Task<T1, T2, T3, T4, T5> cast(final Task<Tuple5<T1, T2, T3, T4, T5>> task) {
    return new Tuple5TaskDelegate<>(task);
  }

}
