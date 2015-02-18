package com.linkedin.parseq.collection.transducer;

public interface Ref<T> {

  T refGet();

  void refSet(T t);

}
