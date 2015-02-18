package com.linkedin.parseq.collection;

public interface GroupedParSeqCollection<K, T> extends ParSeqCollection<T> {

  public K getKey();

}
