package com.linkedin.parseq;

import java.util.function.Function;

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
  
  default <R> Task<R> mapOrFlatMap(final Function5<T1, T2, T3, T4, T5, TaskOrValue<R>> f) {
    return mapOrFlatMap(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5()));
  }

  default <R> Task<R> mapOrFlatMap(final String desc, final Function5<T1, T2, T3, T4, T5, TaskOrValue<R>> f) {
    return mapOrFlatMap(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5()));
  }

  default Tuple5Task<T1, T2, T3, T4, T5> andThen(final Consumer5<T1, T2, T3, T4, T5> consumer) {
    return cast(andThen(tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5())));
  }
  
  default Tuple5Task<T1, T2, T3, T4, T5> andThen(final String desc, final Consumer5<T1, T2, T3, T4, T5> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3(), tuple._4(), tuple._5())));
  }

  @Override
  default Tuple5Task<T1, T2, T3, T4, T5> recover(final Function<Throwable, Tuple5<T1, T2, T3, T4, T5>> f) {
    return cast(recover(f));
  }
  
  @Override
  default Tuple5Task<T1, T2, T3, T4, T5> recover(final String desc, final Function<Throwable, Tuple5<T1, T2, T3, T4, T5>> f) {
    return cast(recover(desc, f));
  }

  @Override
  default Tuple5Task<T1, T2, T3, T4, T5> recoverWith(final Function<Throwable, Task<Tuple5<T1, T2, T3, T4, T5>>> f) {
    return cast(recoverWith(f));
  }
  
  @Override
  default Tuple5Task<T1, T2, T3, T4, T5> recoverWith(final String desc, final Function<Throwable, Task<Tuple5<T1, T2, T3, T4, T5>>> f) {
    return cast(recoverWith(desc, f));
  }
  
  @Override
  default Tuple5Task<T1, T2, T3, T4, T5> fallBackTo(final String desc, final Function<Throwable, Task<Tuple5<T1, T2, T3, T4, T5>>> f) {
    return cast(fallBackTo(desc, f));
  }
  
  @Override
  default Tuple5Task<T1, T2, T3, T4, T5> fallBackTo(final Function<Throwable, Task<Tuple5<T1, T2, T3, T4, T5>>> f) {
    return cast(fallBackTo(f));
  }
  
  public static <T1, T2, T3, T4, T5> Tuple5Task<T1, T2, T3, T4, T5> cast(final Task<Tuple5<T1, T2, T3, T4, T5>> task) {
    return new Tuple5TaskDelegate<>(task);
  }
  
}
