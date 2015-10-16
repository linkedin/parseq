package com.linkedin.parseq.batching;

public interface Group {
  default String getName() {
    return "batchTask";
  }
}
