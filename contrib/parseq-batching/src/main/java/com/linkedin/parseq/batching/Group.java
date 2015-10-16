package com.linkedin.parseq.batching;

public interface Group {
  default <K, V> String getName(Batch<K, V> batch) {
    return "batch(" + batch.size() + ")";
  }
}
