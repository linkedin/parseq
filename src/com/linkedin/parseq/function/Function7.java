package com.linkedin.parseq.function;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface Function7<T1, T2, T3, T4, T5, T6, T7, R> {

    R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);

    default <V> Function7<T1, T2, T3, T4, T5, T6, T7, V> map(Function<? super R, ? extends V> f) {
        Objects.requireNonNull(f);
        return (T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) ->
          f.apply(apply(t1, t2, t3, t4, t5, t6, t7));
    }

}
