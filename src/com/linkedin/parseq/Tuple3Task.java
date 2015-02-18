package com.linkedin.parseq;

import java.util.function.Function;

import com.linkedin.parseq.function.Consumer3;
import com.linkedin.parseq.function.Function3;
import com.linkedin.parseq.function.Tuple3;

public interface Tuple3Task<T1, T2, T3> extends Task<Tuple3<T1, T2, T3>> {
  
  default <R> Task<R> map(final Function3<T1, T2, T3, R> f) {
    return map(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3()));
  }

  default <R> Task<R> map(final String desc, final Function3<T1, T2, T3, R> f) {
    return map(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3()));
  }
  
  default <R> Task<R> flatMap(final Function3<T1, T2, T3, Task<R>> f) {
    return flatMap(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3()));
  }

  default <R> Task<R> flatMap(final String desc, final Function3<T1, T2, T3, Task<R>> f) {
    return flatMap(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3()));
  }
  
  default <R> Task<R> mapOrFlatMap(final Function3<T1, T2, T3, TaskOrValue<R>> f) {
    return mapOrFlatMap(tuple -> f.apply(tuple._1(), tuple._2(), tuple._3()));
  }

  default <R> Task<R> mapOrFlatMap(final String desc, final Function3<T1, T2, T3, TaskOrValue<R>> f) {
    return mapOrFlatMap(desc, tuple -> f.apply(tuple._1(), tuple._2(), tuple._3()));
  }

  default Tuple3Task<T1, T2, T3> andThen(final Consumer3<T1, T2, T3> consumer) {
    return cast(andThen(tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3())));
  }
  
  default Tuple3Task<T1, T2, T3> andThen(final String desc, final Consumer3<T1, T2, T3> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(tuple._1(), tuple._2(), tuple._3())));
  }

  @Override
  default Tuple3Task<T1, T2, T3> recover(final Function<Throwable, Tuple3<T1, T2, T3>> f) {
    return cast(recover(f));
  }
  
  @Override
  default Tuple3Task<T1, T2, T3> recover(final String desc, final Function<Throwable, Tuple3<T1, T2, T3>> f) {
    return cast(recover(desc, f));
  }

  @Override
  default Tuple3Task<T1, T2, T3> recoverWith(final Function<Throwable, Task<Tuple3<T1, T2, T3>>> f) {
    return cast(recoverWith(f));
  }
  
  @Override
  default Tuple3Task<T1, T2, T3> recoverWith(final String desc, final Function<Throwable, Task<Tuple3<T1, T2, T3>>> f) {
    return cast(recoverWith(desc, f));
  }
  
  @Override
  default Tuple3Task<T1, T2, T3> fallBackTo(final String desc, final Function<Throwable, Task<Tuple3<T1, T2, T3>>> f) {
    return cast(fallBackTo(desc, f));
  }
  
  @Override
  default Tuple3Task<T1, T2, T3> fallBackTo(final Function<Throwable, Task<Tuple3<T1, T2, T3>>> f) {
    return cast(fallBackTo(f));
  }
  
  public static <T1, T2, T3> Tuple3Task<T1, T2, T3> cast(final Task<Tuple3<T1, T2, T3>> task) {
    return new Tuple3TaskDelegate<>(task);
  }
  
}
