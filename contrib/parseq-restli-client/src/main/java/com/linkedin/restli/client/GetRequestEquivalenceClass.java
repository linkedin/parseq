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

public class GetRequestEquivalenceClass implements RequestEquivalenceClass {

  private final String _baseUriTemplate;
  private final Map<String, String> _headers;

  public GetRequestEquivalenceClass(String baseUriTemplate, Map<String, String> headers) {
    _baseUriTemplate = baseUriTemplate;
    _headers = headers;
  }

  <RT extends RecordTemplate> List<BatchGetRequest<RT>> toTypedList(List l) {
    return (List<BatchGetRequest<RT>>)l;
  }

  @Override
  public <RT extends RecordTemplate> void executeBatch(final RestClient restClient, final Batch<RestRequestBatchKey, Response<Object>> batch) {

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
  public <RT extends RecordTemplate> void executeSingleton(RestClient restClient, RestRequestBatchKey key, BatchEntry<Response<Object>> entry) {
    restClient.sendRequest(key.getRequest(), key.getRequestContext(),
        new PromiseCallbackAdapter<Object>(entry.getPromise()));
  }

  public String getBaseUriTemplate() {
    return _baseUriTemplate;
  }

  public Map<String, String> getHeaders() {
    return _headers;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_baseUriTemplate == null) ? 0 : _baseUriTemplate.hashCode());
    result = prime * result + ((_headers == null) ? 0 : _headers.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    GetRequestEquivalenceClass other = (GetRequestEquivalenceClass) obj;
    if (_baseUriTemplate == null) {
      if (other._baseUriTemplate != null)
        return false;
    } else if (!_baseUriTemplate.equals(other._baseUriTemplate))
      return false;
    if (_headers == null) {
      if (other._headers != null)
        return false;
    } else if (!_headers.equals(other._headers))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "GetRequestEquivalenceClass [_baseUriTemplate=" + _baseUriTemplate + ", _headers=" + _headers + "]";
  }

}
