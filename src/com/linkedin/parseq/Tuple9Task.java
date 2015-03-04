package com.linkedin.parseq;

import java.util.function.Function;

import com.linkedin.parseq.function.Consumer9;
import com.linkedin.parseq.function.Function9;
import com.linkedin.parseq.function.Tuple9;

public interface Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> extends Task<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> {
  
  default <R> Task<R> map(final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> f) {
    return map(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8(), tuple._9()));
  }

  default <R> Task<R> map(final String desc, final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> f) {
    return map(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8(), tuple._9()));
  }
  
  default <R> Task<R> flatMap(final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Task<R>> f) {
    return flatMap(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8(), tuple._9()));
  }

  default <R> Task<R> flatMap(final String desc, final Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Task<R>> f) {
    return flatMap(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8(), tuple._9()));
  }
  
  default Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> andThen(final Consumer9<T1, T2, T3, T4, T5, T6, T7, T8, T9> consumer) {
    return cast(andThen(tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8(), tuple._9())));
  }
  
  default Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> andThen(final String desc, final Consumer9<T1, T2, T3, T4, T5, T6, T7, T8, T9> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5(), tuple._6(), tuple._7(), tuple._8(), tuple._9())));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> recover(final Function<Throwable, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> f) {
    return cast(recover(f));
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> recover(final String desc, final Function<Throwable, Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> f) {
    return cast(recover(desc, f));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> recoverWith(final Function<Throwable, Task<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>> f) {
    return cast(recoverWith(f));
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  default Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> recoverWith(final String desc, final Function<Throwable, Task<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>>> f) {
    return cast(recoverWith(desc, f));
  }
  
  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> cast(final Task<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> task) {
    return new Tuple9TaskDelegate<>(task);
  }
  
}
