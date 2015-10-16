package com.linkedin.restli.client;

import com.linkedin.data.template.RecordTemplate;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.parseq.batching.Group;
import com.linkedin.restli.client.ParSeqRestClient.RestRequestBatchKey;

interface RequestGroup extends Group {

  public static RequestGroup fromRequest(final Request<?> request) {
    switch (request.getMethod()) {
      case GET:
        return new GetRequestGroup(request);
      default:
        return new SingletonRequestGroup(ParSeqRestClient.generateTaskName(request));
    }
  }

  abstract <RT extends RecordTemplate> void executeBatch(RestClient restClient, Batch<RestRequestBatchKey, Response<Object>> batch);

  abstract <RT extends RecordTemplate> void executeSingleton(RestClient restClient, RestRequestBatchKey key, BatchEntry<Response<Object>> entry);

}