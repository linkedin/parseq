package com.linkedin.restli.client;

import com.linkedin.data.template.RecordTemplate;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.restli.common.ResourceMethod;

interface RequestGroup {

  public static RequestGroup fromRequest(final Request<?> request) {
    switch (request.getMethod()) {
      case GET:
        return new GetRequestGroup(request);
      default:
        return new SingletonRequestGroup(ParSeqRestClient.generateTaskName(request));
    }
  }

  public static boolean isBatchable(final Request<?> request) {
    return request.getMethod().equals(ResourceMethod.GET);
  }

  <RT extends RecordTemplate> void executeBatch(RestClient restClient, Batch<RestRequestBatchKey, Response<Object>> batch);

  <K, V> String getBatchName(Batch<K, V> batch);

}