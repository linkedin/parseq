package com.linkedin.parseq;

import java.util.function.Function;

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
  default Tuple6Task<T1, T2, T3, T4, T5, T6> recover(final Function<Throwable, Tuple6<T1, T2, T3, T4, T5, T6>> f) {
    return cast(recover(f));
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple6Task<T1, T2, T3, T4, T5, T6> recover(final String desc, final Function<Throwable, Tuple6<T1, T2, T3, T4, T5, T6>> f) {
    return cast(recover(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple6Task<T1, T2, T3, T4, T5, T6> recoverWith(final Function<Throwable, Task<Tuple6<T1, T2, T3, T4, T5, T6>>> f) {
    return cast(recoverWith(f));
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple6Task<T1, T2, T3, T4, T5, T6> recoverWith(final String desc, final Function<Throwable, Task<Tuple6<T1, T2, T3, T4, T5, T6>>> f) {
    return cast(recoverWith(desc, f));
  }
  
  public static <T1, T2, T3, T4, T5, T6> Tuple6Task<T1, T2, T3, T4, T5, T6> cast(final Task<Tuple6<T1, T2, T3, T4, T5, T6>> task) {
    return new Tuple6TaskDelegate<>(task);
  }
  
}
