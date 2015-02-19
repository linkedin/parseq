package com.linkedin.parseq.function;


@FunctionalInterface
public interface Consumer3<T1, T2, T3> {

    void accept(T1 t1, T2 t2, T3 t3);

}
