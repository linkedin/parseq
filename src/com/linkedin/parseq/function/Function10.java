package com.linkedin.parseq.function;

import java.util.Objects;
import com.linkedin.parseq.function.Function1;

@FunctionalInterface
public interface Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> {

    R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10) throws Exception;

    default <V> Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, V> andThen(Function1<? super R, ? extends V> f) {
        Objects.requireNonNull(f);
        return (T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10) ->
          f.apply(apply(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10));
    }

}
