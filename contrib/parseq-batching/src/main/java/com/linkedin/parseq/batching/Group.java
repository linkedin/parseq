package com.linkedin.parseq.batching;

public interface Group {
  default String getName(Batch batch) {
    return "batch(" + batch.size() + ")";
  }
}
