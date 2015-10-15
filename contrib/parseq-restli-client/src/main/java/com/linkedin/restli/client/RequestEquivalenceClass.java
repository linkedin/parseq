package com.linkedin.restli.client;

import com.linkedin.data.template.RecordTemplate;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.restli.client.ParSeqRestClient.RestRequestBatchKey;

interface RequestEquivalenceClass {

  public static RequestEquivalenceClass fromRequest(final Request<?> request) {
    switch (request.getMethod()) {
      case GET:
        return new GetRequestEquivalenceClass(request.getBaseUriTemplate(), request.getHeaders());
      default:
        return new UniqueRequestEquivalenceClass();
    }
  }

  abstract <RT extends RecordTemplate> void executeBatch(RestClient restClient, Batch<RestRequestBatchKey, Response<Object>> batch);

  abstract <RT extends RecordTemplate> void executeSingleton(RestClient restClient, RestRequestBatchKey key, BatchEntry<Response<Object>> entry);

}