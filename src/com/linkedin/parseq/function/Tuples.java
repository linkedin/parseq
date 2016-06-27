package com.linkedin.parseq.function;

public class Tuples {
  private Tuples() {}

  public static <T1, T2> Tuple2<T1, T2> tuple(final T1 t1, final T2 t2) {
    return new Tuple2<T1, T2>(t1, t2);
  }

  public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple(final T1 t1, final T2 t2, final T3 t3) {
    return new Tuple3<T1, T2, T3>(t1, t2, t3);
  }

  public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> tuple(final T1 t1, final T2 t2, final T3 t3, final T4 t4) {
    return new Tuple4<T1, T2, T3, T4>(t1, t2, t3, t4);
  }

  public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> tuple(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5) {
    return new Tuple5<T1, T2, T3, T4, T5>(t1, t2, t3, t4, t5);
  }

  public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> tuple(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6) {
    return new Tuple6<T1, T2, T3, T4, T5, T6>(t1, t2, t3, t4, t5, t6);
  }

  public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7) {
    return new Tuple7<T1, T2, T3, T4, T5, T6, T7>(t1, t2, t3, t4, t5, t6, t7);
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> tuple(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7, final T8 t8) {
    return new Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(t1, t2, t3, t4, t5, t6, t7, t8);
  }

  public static <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> tuple(final T1 t1, final T2 t2, final T3 t3, final T4 t4, final T5 t5, final T6 t6, final T7 t7, final T8 t8, final T9 t9) {
    return new Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(t1, t2, t3, t4, t5, t6, t7, t8, t9);
  }

}
