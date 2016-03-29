package com.linkedin.restli.client;

import java.util.HashMap;
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
import com.linkedin.restli.client.response.BatchKVResponse;
import com.linkedin.restli.common.BatchResponse;
import com.linkedin.restli.common.ComplexResourceKey;
import com.linkedin.restli.common.CompoundKey;
import com.linkedin.restli.common.ErrorResponse;
import com.linkedin.restli.common.ProtocolVersion;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.RestConstants;
import com.linkedin.restli.internal.client.ResponseImpl;
import com.linkedin.restli.internal.common.ProtocolVersionUtil;
import com.linkedin.restli.internal.common.ResponseUtils;

class GetRequestGroup implements RequestGroup {

  private final String _baseUriTemplate;
  private final Map<String, String> _headers;
  private final Map<String, Object> _queryParams;

  public GetRequestGroup(Request request) {
    _baseUriTemplate = request.getBaseUriTemplate();
    _headers = request.getHeaders();
    _queryParams = getQueryParamsForBatchingKey(request);
  }

  private static Map<String, Object> getQueryParamsForBatchingKey(Request<?> request)
  {
    final Map<String, Object> params = new HashMap<String, Object>(request.getQueryParamsObjects());
    params.remove(RestConstants.QUERY_BATCH_IDS_PARAM);
    params.remove(RestConstants.FIELDS_PARAM);
    return params;
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
    List<BatchGetKVRequest> listOfBatchGets = batch.entries().stream()
        .map(Entry::getKey)
        .map(RestRequestBatchKey::getRequest)
        .map(GetRequestGroup::toBatchGetKV)
        .collect(Collectors.toList());

    BatchGetKVRequest<K, RT> batchGet = BatchGetRequestBuilder.batchKV(toTypedKVList(listOfBatchGets));

    restClient.sendRequest(batchGet, new Callback<Response<BatchKVResponse<K, RT>>>() {

      @Override
      public void onSuccess(Response<BatchKVResponse<K, RT>> responseToBatch) {
        final ProtocolVersion version = ProtocolVersionUtil.extractProtocolVersion(responseToBatch.getHeaders());
        batch.entries().stream()
        .forEach(entry -> {
          try {
            Request request = entry.getKey().getRequest();
            if (request instanceof GetRequest) {
              String idString = ((GetRequest)request).getObjectId().toString();
              Object id = ResponseUtils.convertKey(idString, request.getResourceSpec().getKeyType(),
                  request.getResourceSpec().getKeyParts(), request.getResourceSpec().getComplexKeyType(),
                  version);
              Response rsp = unbatchKVResponse(batchGet, responseToBatch, id);
              entry.getValue().getPromise().done(rsp);
            } else {
              BatchGetKVRequest batchGetKVRequest = (BatchGetKVRequest)request;
              System.out.println("batchGetKVRequest: " + batchGetKVRequest);
              //TODO do try-catch thing and handle each entry individually

            }
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
    List<BatchGetRequest> listOfBatchGets = batch.entries().stream()
        .map(Entry::getKey)
        .map(RestRequestBatchKey::getRequest)
        .map(GetRequestGroup::toBatchGet)
        .collect(Collectors.toList());

      BatchGetRequest<RT> batchGet = BatchGetRequestBuilder.batch(toTypedList(listOfBatchGets));

      restClient.sendRequest(batchGet, new Callback<Response<BatchResponse<RT>>>() {

        @Override
        public void onSuccess(Response<BatchResponse<RT>> responseToBatch) {
          batch.entries().stream()
            .forEach(entry -> {
              try {
                Request request = entry.getKey().getRequest();
                if (request instanceof GetRequest) {
                  String id = ((GetRequest)request).getObjectId().toString();
                  Response rsp = BatchGetRequestUtil.unbatchResponse(batchGet, responseToBatch, id);
                  entry.getValue().getPromise().done(rsp);
                } else {
                  BatchGetRequest batchGetRequest = (BatchGetRequest)request;
                  System.out.println("batchGetRequest: " + batchGetRequest);
                  //TODO do try-catch thing and handle each entry individually

                }
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
    Class<?> keyClass = batch.entries().iterator().next().getKey().getRequest().getResourceSpec().getKeyClass();
    if (CompoundKey.class.isAssignableFrom(keyClass) || keyClass == ComplexResourceKey.class) {
      executeKVBatch(restClient, batch);
    } else {
      executeNonKVBatch(restClient, batch);
    }
  }

  public <RT extends RecordTemplate> void executeSingleton(RestClient restClient, RestRequestBatchKey key, BatchEntry<Response<Object>> entry) {
    restClient.sendRequest(key.getRequest(), key.getRequestContext(),
        new PromiseCallbackAdapter<Object>(entry.getPromise()));
  }

  static BatchGetRequest toBatchGet(Request request) {
    if (request instanceof GetRequest) {
      return BatchGetRequestBuilder.batch((GetRequest)request);
    } else {
      return (BatchGetRequest) request;
    }
  }

  static BatchGetKVRequest toBatchGetKV(Request request) {
    if (request instanceof GetRequest) {
      return BatchGetRequestBuilder.batchKV((GetRequest)request);
    } else {
      return (BatchGetKVRequest) request;
    }
  }

  public String getBaseUriTemplate() {
    return _baseUriTemplate;
  }

  public Map<String, String> getHeaders() {
    return _headers;
  }

  public Map<String, Object> getQueryParams() {
    return _queryParams;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_baseUriTemplate == null) ? 0 : _baseUriTemplate.hashCode());
    result = prime * result + ((_headers == null) ? 0 : _headers.hashCode());
    result = prime * result + ((_queryParams == null) ? 0 : _queryParams.hashCode());
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
    GetRequestGroup other = (GetRequestGroup) obj;
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
    if (_queryParams == null) {
      if (other._queryParams != null)
        return false;
    } else if (!_queryParams.equals(other._queryParams))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "GetRequestGroup [baseUriTemplate=" + _baseUriTemplate + ", headers=" + _headers
        + ", queryParams=" + _queryParams + "]";
  }

  public <K, V> String getBatchName(final Batch<K, V> batch) {
    return _baseUriTemplate + " " + (batch.size() == 1 ? ResourceMethod.GET : (ResourceMethod.BATCH_GET +
        "(" + batch.size() + ")"));
  }

}
