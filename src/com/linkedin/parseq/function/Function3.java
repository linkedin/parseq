package com.linkedin.parseq.function;

import java.util.Objects;
import com.linkedin.parseq.function.Function1;

@FunctionalInterface
public interface Function3<T1, T2, T3, R> {

    R apply(T1 t1, T2 t2, T3 t3) throws Exception;

    default <V> Function3<T1, T2, T3, V> andThen(Function1<? super R, ? extends V> f) {
        Objects.requireNonNull(f);
        return (T1 t1, T2 t2, T3 t3) ->
          f.apply(apply(t1, t2, t3));
    }

}
