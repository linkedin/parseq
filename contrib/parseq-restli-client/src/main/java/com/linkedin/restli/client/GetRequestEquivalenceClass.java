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
import com.linkedin.restli.client.response.BatchKVResponse;
import com.linkedin.restli.common.BatchResponse;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ErrorResponse;
import com.linkedin.restli.internal.client.ResponseImpl;

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

  <K, V extends RecordTemplate> List<BatchGetKVRequest<K, V>> toTypedKVList(List l) {
    return (List<BatchGetKVRequest<K, V>>)l;
  }


  /**
   * Extract the get response for this resource out of an auto-batched batch response.
   * This is pure rest.li logic, and it complements the auto-batching logic in BatchGetRequestBuilder.
   * @throws com.linkedin.r2.RemoteInvocationException if the server returned an error response for this resource,
   * or if it returned neither a result nor an error.
   */
  private static <K, RT extends RecordTemplate> Response<RT> unbatchKVResponse(BatchGetKVRequest<K, RT> request,
                                                                              Response<BatchKVResponse<K, RT>> batchResponse,
                                                                              Object id)
      throws RemoteInvocationException
  {
    final BatchKVResponse<K, RT> batchEntity = batchResponse.getEntity();
    final ErrorResponse errorResponse = batchEntity.getErrors().get(id);
    if (errorResponse != null)
    {
      throw new RestLiResponseException(errorResponse);
    }

    final RT entityResult = batchEntity.getResults().get(id);
    if (entityResult == null)
    {
      throw new RestLiDecodingException("No result or error for base URI " + request.getBaseUriTemplate() + ", id " + id +
                                             ". Verify that the batchGet endpoint returns response keys that match batchGet request IDs.", null);
    }

    return new ResponseImpl<RT>(batchResponse, entityResult);
  }

  private <K, RT extends RecordTemplate> void executeKVBatch(final RestClient restClient, final Batch<RestRequestBatchKey, Response<Object>> batch) {
    List<BatchGetKVRequest> listOfBatchGets = batch.entires().stream()
        .map(Entry::getKey)
        .map(RestRequestBatchKey::getRequest)
        .map(GetRequestEquivalenceClass::toGetRequest)
        .map(GetRequestEquivalenceClass::toBatchGetKV)
        .collect(Collectors.toList());

    BatchGetKVRequest<K, RT> batchGet = BatchGetRequestBuilder.batchKV(toTypedKVList(listOfBatchGets));

    restClient.sendRequest(batchGet, new Callback<Response<BatchKVResponse<K, RT>>>() {

      @Override
      public void onSuccess(Response<BatchKVResponse<K, RT>> responseToBatch) {
        batch.entires().stream()
        .forEach(entry -> {
          try {
            String id = toGetRequest(entry.getKey().getRequest()).getObjectId().toString();
            Response rsp = unbatchKVResponse(batchGet, responseToBatch, id);
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

  public <RT extends RecordTemplate> void executeNonKVBatch(final RestClient restClient, final Batch<RestRequestBatchKey, Response<Object>> batch) {
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


  @Override
  public <RT extends RecordTemplate> void executeBatch(final RestClient restClient, final Batch<RestRequestBatchKey, Response<Object>> batch) {

    //TODO this needs better API from rest.li team
    Class<?> keyClass = batch.entires().iterator().next().getKey().getRequest().getResourceSpec().getKeyClass();
    if (CompoundKey.class.isAssignableFrom(keyClass) || keyClass == ComplexResourceKey.class) {
      executeKVBatch(restClient, batch);
    } else {
      executeNonKVBatch(restClient, batch);
    }
  }

  static GetRequest toGetRequest(Request request) {
    return (GetRequest) request;
  }

  static BatchGetRequest toBatchGet(GetRequest getRequest) {
    return BatchGetRequestBuilder.batch(getRequest);
  }

  static BatchGetKVRequest toBatchGetKV(GetRequest getRequest) {
    return BatchGetRequestBuilder.batchKV(getRequest);
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
