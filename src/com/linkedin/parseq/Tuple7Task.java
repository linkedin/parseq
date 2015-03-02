package com.linkedin.parseq;

import java.util.function.Function;

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

  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recover(final Function<Throwable, Tuple7<T1, T2, T3, T4, T5, T6, T7>> f) {
    return cast(recover(f));
  }
  
  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recover(final String desc, final Function<Throwable, Tuple7<T1, T2, T3, T4, T5, T6, T7>> f) {
    return cast(recover(desc, f));
  }

  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recoverWith(final Function<Throwable, Task<Tuple7<T1, T2, T3, T4, T5, T6, T7>>> f) {
    return cast(recoverWith(f));
  }
  
  @Override
  default Tuple7Task<T1, T2, T3, T4, T5, T6, T7> recoverWith(final String desc, final Function<Throwable, Task<Tuple7<T1, T2, T3, T4, T5, T6, T7>>> f) {
    return cast(recoverWith(desc, f));
  }
  
  public static <T1, T2, T3, T4, T5, T6, T7> Tuple7Task<T1, T2, T3, T4, T5, T6, T7> cast(final Task<Tuple7<T1, T2, T3, T4, T5, T6, T7>> task) {
    return new Tuple7TaskDelegate<>(task);
  }
  
}
