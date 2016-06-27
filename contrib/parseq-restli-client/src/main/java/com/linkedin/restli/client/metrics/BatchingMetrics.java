/*
 * Copyright 2016 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.restli.client.metrics;

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
