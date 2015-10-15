package com.linkedin.restli.client;

import java.util.Map;

import com.linkedin.data.template.RecordTemplate;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.restli.client.ParSeqRestClient.PromiseCallbackAdapter;
import com.linkedin.restli.client.ParSeqRestClient.RestRequestBatchKey;
import com.linkedin.restli.common.ResourceMethod;

public class UniqueRequestEquivalenceClass extends RequestEquivalenceClass {

  public UniqueRequestEquivalenceClass(String baseUriTemplate, ResourceMethod method, Map<String, String> headers) {
    super(baseUriTemplate, method, headers, true);
  }

  @Override
  <RT extends RecordTemplate> void executeSingleton(RestClient restClient, RestRequestBatchKey key, BatchEntry<Response<Object>> entry) {
    restClient.sendRequest(key.getRequest(), key.getRequestContext(),
        new PromiseCallbackAdapter<Object>(entry.getPromise()));
  }

  @Override
  <RT extends RecordTemplate> void executeBatch(RestClient restClient,
      Batch<RestRequestBatchKey, Response<Object>> batch) {
  }

}
