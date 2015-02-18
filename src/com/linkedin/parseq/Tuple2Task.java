package com.linkedin.parseq;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.linkedin.parseq.function.Tuple2;

public interface Tuple2Task<T1, T2> extends Task<Tuple2<T1, T2>> {
  
  default <R> Task<R> map(final BiFunction<T1, T2, R> f) {
    return map(tuple -> f.apply(tuple._1(), tuple._2()));
  }

  default <R> Task<R> map(final String desc, final BiFunction<T1, T2, R> f) {
    return map(desc, tuple -> f.apply(tuple._1(), tuple._2()));
  }
  
  default <R> Task<R> flatMap(final BiFunction<T1, T2, Task<R>> f) {
    return flatMap(tuple -> f.apply(tuple._1(), tuple._2()));
  }

  default <R> Task<R> flatMap(final String desc, final BiFunction<T1, T2, Task<R>> f) {
    return flatMap(desc, tuple -> f.apply(tuple._1(), tuple._2()));
  }
  
  default <R> Task<R> mapOrFlatMap(final BiFunction<T1, T2, TaskOrValue<R>> f) {
    return mapOrFlatMap(tuple -> f.apply(tuple._1(), tuple._2()));
  }

  default <R> Task<R> mapOrFlatMap(final String desc, final BiFunction<T1, T2, TaskOrValue<R>> f) {
    return mapOrFlatMap(desc, tuple -> f.apply(tuple._1(), tuple._2()));
  }

  default Tuple2Task<T1, T2> andThen(final BiConsumer<T1, T2> consumer) {
    return cast(andThen(tuple -> consumer.accept(tuple._1(), tuple._2())));
  }
  
  default Tuple2Task<T1, T2> andThen(final String desc, final BiConsumer<T1, T2> consumer) {
    return cast(andThen(desc, tuple -> consumer.accept(tuple._1(), tuple._2())));
  }

  @Override
  default Tuple2Task<T1, T2> recover(final Function<Throwable, Tuple2<T1, T2>> f) {
    return cast(recover(f));
  }
  
  @Override
  default Tuple2Task<T1, T2> recover(final String desc, final Function<Throwable, Tuple2<T1, T2>> f) {
    return cast(recover(desc, f));
  }

  @Override
  default Tuple2Task<T1, T2> recoverWith(final Function<Throwable, Task<Tuple2<T1, T2>>> f) {
    return cast(recoverWith(f));
  }
  
  @Override
  default Tuple2Task<T1, T2> recoverWith(final String desc, final Function<Throwable, Task<Tuple2<T1, T2>>> f) {
    return cast(recoverWith(desc, f));
  }
  
  @Override
  default Tuple2Task<T1, T2> fallBackTo(final String desc, final Function<Throwable, Task<Tuple2<T1, T2>>> f) {
    return cast(fallBackTo(desc, f));
  }
  
  @Override
  default Tuple2Task<T1, T2> fallBackTo(final Function<Throwable, Task<Tuple2<T1, T2>>> f) {
    return cast(fallBackTo(f));
  }
  
  public static <T1, T2> Tuple2Task<T1, T2> cast(final Task<Tuple2<T1, T2>> task) {
    return new Tuple2TaskDelegate<>(task);
  }
  
}
