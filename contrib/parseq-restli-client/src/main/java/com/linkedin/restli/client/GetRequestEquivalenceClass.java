package com.linkedin.restli.client;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.linkedin.common.callback.Callback;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.ParSeqRestClient.PromiseCallbackAdapter;
import com.linkedin.restli.client.ParSeqRestClient.RestRequestBatchKey;
import com.linkedin.restli.common.BatchResponse;
import com.linkedin.restli.common.ResourceMethod;

public class GetRequestEquivalenceClass extends RequestEquivalenceClass {

  public GetRequestEquivalenceClass(String baseUriTemplate, ResourceMethod method, Map<String, String> headers) {
    super(baseUriTemplate, method, headers, false);
  }

  <RT extends RecordTemplate> List<BatchGetRequest<RT>> toTypedList(List l) {
    return (List<BatchGetRequest<RT>>)l;
  }

  @Override
  <RT extends RecordTemplate> void executeBatch(final RestClient restClient, final Batch<RestRequestBatchKey, Response<Object>> batch) {

    //TODO this needs better API from rest.li team

    List<BatchGetRequest> listOfBatchGets = batch.entires().stream()
      .map(Entry::getKey)
      .map(RestRequestBatchKey::getRequest)
      .map(GetRequestEquivalenceClass::toGetRequest)
      .map(GetRequestEquivalenceClass::toBatchGet)
      .collect(Collectors.toList());

    BatchGetRequest<RT> batchGet = BatchGetRequestBuilder.batch(toTypedList(listOfBatchGets));

    restClient.sendRequest(batchGet, new Callback<Response<BatchResponse<RT>>>() {

      @Override
      public void onSuccess(Response<BatchResponse<RT>> responseToBatch) {
        batch.entires().stream()
          .forEach(entry -> {
            try {
              String id = toGetRequest(entry.getKey().getRequest()).getObjectId().toString();
              Response rsp = BatchGetRequestUtil.unbatchResponse(batchGet, responseToBatch, id);
              entry.getValue().getPromise().done(rsp);
            } catch (RemoteInvocationException e) {
              entry.getValue().getPromise().fail(e);
            }
          });
      }

      @Override
      public void onError(Throwable e) {
        batch.failAll(e);
      }
    });

  }

  static GetRequest toGetRequest(Request request) {
    return (GetRequest) request;
  }

  static BatchGetRequest toBatchGet(GetRequest getRequest) {
    return BatchGetRequestBuilder.batch(getRequest);
  }

  @Override
  <RT extends RecordTemplate> void executeSingleton(RestClient restClient, RestRequestBatchKey key, BatchEntry<Response<Object>> entry) {
    restClient.sendRequest(key.getRequest(), key.getRequestContext(),
        new PromiseCallbackAdapter<Object>(entry.getPromise()));
  }

}
