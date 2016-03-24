package com.linkedin.restli.client;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

import com.linkedin.parseq.batching.BatchSizeMetric;

public class BatchingMetrics {
  private final ConcurrentMap<String, BatchSizeMetric> batchSizePerEndpoint = new ConcurrentHashMap<>();

  private final ConcurrentLinkedQueue<BiConsumer<String, BatchSizeMetric>> _metricsConsumers =
      new ConcurrentLinkedQueue<>();

  public void recordBatchSize(String endpoint, int batchSize) {
    final BatchSizeMetric metric = batchSizePerEndpoint.computeIfAbsent(endpoint, k -> {
      final BatchSizeMetric newMetric = new BatchSizeMetric();
      _metricsConsumers.forEach(consumer -> consumer.accept(k, newMetric));
      return newMetric;
    });
    metric.record(batchSize);
  }

  public ConcurrentMap<String, BatchSizeMetric> getBatchSizeMetrics() {
    return batchSizePerEndpoint;
  }

  public void addNewEndpointMetricConsumer(BiConsumer<String, BatchSizeMetric> consumer) {
    _metricsConsumers.add(consumer);
  }

}
