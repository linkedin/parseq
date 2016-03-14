package com.linkedin.restli.client;

import java.util.Map.Entry;

import com.linkedin.data.template.RecordTemplate;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.restli.client.ParSeqRestClient.PromiseCallbackAdapter;

public class SingletonRequestGroup implements RequestGroup {

  private final String _name;

  public SingletonRequestGroup(String name) {
    _name = name;
  }

  @Override
  public <RT extends RecordTemplate> void executeBatch(RestClient restClient,
      Batch<RestRequestBatchKey, Response<Object>> batch) {
    if (batch.size() > 1) {
      throw new UnsupportedOperationException("singleton request should not be executed as a batch");
    } else {
      Entry<RestRequestBatchKey, BatchEntry<Response<Object>>> entry = batch.entries().iterator().next();
      restClient.sendRequest(entry.getKey().getRequest(), entry.getKey().getRequestContext(),
          new PromiseCallbackAdapter<Object>(entry.getValue().getPromise()));
    }
  }

  @Override
  public <K, V> String getBatchName(Batch<K, V> batch) {
    return _name;
  }

}
