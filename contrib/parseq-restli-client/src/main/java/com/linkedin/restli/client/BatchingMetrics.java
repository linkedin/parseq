package com.linkedin.restli.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.linkedin.parseq.batching.BatchSizeMetric;

public class BatchingMetrics {
  private final ConcurrentMap<String, BatchSizeMetric> batchSizePerEndpoint = new ConcurrentHashMap<>();

  public void recordBatchSize(String endpoint, int batchSize) {
    BatchSizeMetric metric = batchSizePerEndpoint.computeIfAbsent(endpoint, k -> new BatchSizeMetric());
    metric.record(batchSize);
  }

  public ConcurrentMap<String, BatchSizeMetric> getBatchSizeMetrics() {
    return batchSizePerEndpoint;
  }
}
