package com.linkedin.restli.client;

import com.linkedin.data.template.RecordTemplate;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.restli.client.ParSeqRestClient.PromiseCallbackAdapter;
import com.linkedin.restli.client.ParSeqRestClient.RestRequestBatchKey;

public class UniqueRequestEquivalenceClass implements RequestEquivalenceClass {

  @Override
  public <RT extends RecordTemplate> void executeSingleton(RestClient restClient, RestRequestBatchKey key, BatchEntry<Response<Object>> entry) {
    restClient.sendRequest(key.getRequest(), key.getRequestContext(),
        new PromiseCallbackAdapter<Object>(entry.getPromise()));
  }

  @Override
  public <RT extends RecordTemplate> void executeBatch(RestClient restClient,
      Batch<RestRequestBatchKey, Response<Object>> batch) {
  }

}
