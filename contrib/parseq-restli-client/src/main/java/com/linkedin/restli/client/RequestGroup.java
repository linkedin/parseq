package com.linkedin.restli.client;

import com.linkedin.data.template.RecordTemplate;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.restli.client.config.ResourceConfig;
import com.linkedin.restli.common.ResourceMethod;

interface RequestGroup {

  public static RequestGroup fromRequest(final Request<?> request, boolean dryRun, int maxBatchSize) {
    switch (request.getMethod()) {
      case GET:
        return new GetRequestGroup(request, dryRun, maxBatchSize);
      case BATCH_GET:
        return new GetRequestGroup(request, dryRun, maxBatchSize);
      default:
        throw new IllegalArgumentException("Can't create RequestGroup for request method: " + request.getMethod() +
            ", batching for this method must be disabled");
    }
  }

  public static boolean isBatchable(final Request<?> request, ResourceConfig resourceConfig) {
    return (request.getMethod().equals(ResourceMethod.GET) || request.getMethod().equals(ResourceMethod.BATCH_GET)) &&
        resourceConfig.getBatchingConfig(request.getMethod()).isBatchingEnabled();
  }

  <RT extends RecordTemplate> void executeBatch(RestClient restClient, Batch<RestRequestBatchKey, Response<Object>> batch);

  <K, V> String getBatchName(Batch<K, V> batch);

  String getBaseUriTemplate();

  boolean isDryRun();

  int getMaxBatchSize();

}