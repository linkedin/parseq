package com.linkedin.parseq.collection.transducer;

public interface Foldable<Z, T, V> {
  V fold(String name, Z zero, Reducer<Z, T> reducer);
}
