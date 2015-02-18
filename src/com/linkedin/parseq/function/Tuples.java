package com.linkedin.parseq.function;

public class Tuples {
  private Tuples() {}

  public static <T1, T2> Tuple2<T1, T2> tuple(T1 t1, T2 t2) {
    return new Tuple2<T1, T2>(t1, t2);
  }

  public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple(T1 t1, T2 t2, T3 t3) {
    return new Tuple3<T1, T2, T3>(t1, t2, t3);
  }
}
