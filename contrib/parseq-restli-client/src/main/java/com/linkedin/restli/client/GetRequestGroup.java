package com.linkedin.restli.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.linkedin.common.callback.Callback;
import com.linkedin.data.DataMap;
import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.function.Tuple2;
import com.linkedin.parseq.function.Tuples;
import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.restli.client.response.BatchKVResponse;
import com.linkedin.restli.common.BatchResponse;
import com.linkedin.restli.common.EntityResponse;
import com.linkedin.restli.common.ErrorResponse;
import com.linkedin.restli.common.ProtocolVersion;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.ResourceSpec;
import com.linkedin.restli.common.RestConstants;
import com.linkedin.restli.internal.client.ResponseImpl;
import com.linkedin.restli.internal.common.ProtocolVersionUtil;
import com.linkedin.restli.internal.common.ResponseUtils;

class GetRequestGroup implements RequestGroup {

  private final String _baseUriTemplate; //taken from first request
  private final ResourceSpec _resourceSpec;  //taken from first request
  private final Map<String, String> _headers; //taken from first request
  private final RestliRequestOptions _requestOptions; //taken from first request
  private final Map<String, Object> _queryParams; //taken from first request, used to differentiate between groups
  private final boolean _dryRun;
  private final int _maxBatchSize;

  @SuppressWarnings("deprecation")
  public GetRequestGroup(Request<?> request, boolean dryRun, int maxBatchSize) {
    _baseUriTemplate = request.getBaseUriTemplate();
    _headers = request.getHeaders();
    _queryParams = getQueryParamsForBatchingKey(request);
    _resourceSpec = request.getResourceSpec();
    _requestOptions = request.getRequestOptions();
    _dryRun = dryRun;
    _maxBatchSize = maxBatchSize;
  }

  private static Map<String, Object> getQueryParamsForBatchingKey(Request<?> request)
  {
    final Map<String, Object> params = new HashMap<String, Object>(request.getQueryParamsObjects());
    params.remove(RestConstants.QUERY_BATCH_IDS_PARAM);
    params.remove(RestConstants.FIELDS_PARAM);
    return params;
  }


  /**
   * Extract the get response for this resource out of an auto-batched batch response.
   * This is pure rest.li logic, and it complements the auto-batching logic in BatchGetRequestBuilder.
   * @throws com.linkedin.r2.RemoteInvocationException if the server returned an error response for this resource,
   * or if it returned neither a result nor an error.
   */
  private static <K, RT extends RecordTemplate> Response<RT> unbatchResponse(BatchGetEntityRequest<K, RT> request,
      Response<BatchKVResponse<K, EntityResponse<RT>>> batchResponse, Object id) throws RemoteInvocationException {
    final BatchKVResponse<K, EntityResponse<RT>> batchEntity = batchResponse.getEntity();
    final ErrorResponse errorResponse = batchEntity.getErrors().get(id);
    if (errorResponse != null) {
      throw new RestLiResponseException(errorResponse);
    }

    final RT entityResult = batchEntity.getResults().get(id).getEntity();
    if (entityResult == null) {
      throw new RestLiDecodingException("No result or error for base URI " + request.getBaseUriTemplate() + ", id " + id
          + ". Verify that the batchGet endpoint returns response keys that match batchGet request IDs.", null);
    }

    return new ResponseImpl<RT>(batchResponse, entityResult);
  }

  private DataMap filterIdsInBatchResult(DataMap data, Set<String> ids, boolean stripEntities) {
    DataMap dm = new DataMap(data.size());
    data.forEach((key, value) -> {
      switch(key) {
        case BatchResponse.ERRORS:
          dm.put(key, filterIds((DataMap)value, ids, EntityResponse.ERROR));
          break;
        case BatchResponse.RESULTS:
          dm.put(key, filterIds((DataMap)value, ids, EntityResponse.ENTITY));
          break;
        case BatchResponse.STATUSES:
          dm.put(key, filterIds((DataMap)value, ids, EntityResponse.STATUS));
          break;
        default:
          dm.put(key, value);
          break;
      }
    });
    return dm;
  }

  private Object filterIds(DataMap data, Set<String> ids, String keyToStrip) {
    DataMap dm = new DataMap(data.size());
    data.forEach((key, value) -> {
      if (ids.contains(key)) {
        if (keyToStrip == null) {
          dm.put(key, value);
        } else {
          dm.put(key, ((DataMap)value).get(keyToStrip));
        }
      }
    });
    return dm;
  }

  private static Tuple2<Set<Object>, Set<PathSpec>> reduceIdAndFields(Tuple2<Set<Object>, Set<PathSpec>> state, Request<?> request) {
    if (request instanceof GetRequest) {
      GetRequest<?> getRequest = (GetRequest<?>)request;
      state._1().add(getRequest.getObjectId());
      return reduceFields(state, getRequest.getFields());
    } else if (request instanceof BatchRequest) {
      BatchRequest<?> batchRequest = (BatchRequest<?>)request;
      state._1().addAll(batchRequest.getObjectIds());
      return reduceFields(state, batchRequest.getFields());
    } else {
      throw new RuntimeException("ParSeqRestClient could not handled this type of GET request: " + request.getClass().getName());
    }
  }

  private static Tuple2<Set<Object>, Set<PathSpec>> reduceFields(final Tuple2<Set<Object>, Set<PathSpec>> state,
      final Set<PathSpec> requestFields) {
    if (requestFields != null && !requestFields.isEmpty()) {
      if (state._2() != null) {
        state._2().addAll(requestFields);
      }
      return state;
    } else {
      return Tuples.tuple(state._1(), null);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private <K, RT extends RecordTemplate> void doExecuteBatch(final RestClient restClient, final Batch<RestRequestBatchKey, Response<Object>> batch) {

    final BatchGetEntityRequestBuilder<K, RT> builder = new BatchGetEntityRequestBuilder<>(_baseUriTemplate, _resourceSpec, _requestOptions);
    builder.setHeaders(_headers);
    _queryParams.forEach((key, value) -> builder.setParam(key, value));

    final Tuple2<Set<Object>, Set<PathSpec>> idsAndFields = batch.entries().stream()
      .map(Entry::getKey)
      .map(RestRequestBatchKey::getRequest)
      .reduce(Tuples.tuple(new HashSet<>(), new HashSet<>()),
          GetRequestGroup::reduceIdAndFields,
          (a, b) -> a);

    builder.setParam(RestConstants.QUERY_BATCH_IDS_PARAM, idsAndFields._1());
    if (idsAndFields._2() != null && !idsAndFields._2().isEmpty()) {
      builder.setParam(RestConstants.FIELDS_PARAM, idsAndFields._2().toArray());
    }

    final BatchGetEntityRequest<K, RT> batchGet = builder.build();

    restClient.sendRequest(batchGet, new Callback<Response<BatchKVResponse<K, EntityResponse<RT>>>>() {

      @SuppressWarnings({ "deprecation" })
      @Override
      public void onSuccess(Response<BatchKVResponse<K, EntityResponse<RT>>> responseToBatch) {
        final ProtocolVersion version = ProtocolVersionUtil.extractProtocolVersion(responseToBatch.getHeaders());
        batch.entries().stream()
        .forEach(entry -> {
          try {
            Request request = entry.getKey().getRequest();
            if (request instanceof GetRequest) {
              String idString = ((GetRequest) request).getObjectId().toString();
              Object id = ResponseUtils.convertKey(idString, request.getResourceSpec().getKeyType(),
                  request.getResourceSpec().getKeyParts(), request.getResourceSpec().getComplexKeyType(), version);
              Response rsp = unbatchResponse(batchGet, responseToBatch, id);
              entry.getValue().getPromise().done(rsp);
            } else if (request instanceof BatchGetKVRequest) {
              BatchGetKVRequest batchGetKVRequest = (BatchGetKVRequest) request;
              Set<String> ids = extractIds(batchGetKVRequest);
              DataMap dm = filterIdsInBatchResult(responseToBatch.getEntity().data(), ids, true);
              BatchKVResponse br = new BatchKVResponse(dm, request.getResourceSpec().getKeyType(),
                  request.getResourceSpec().getValueType(), request.getResourceSpec().getKeyParts(),
                  request.getResourceSpec().getComplexKeyType(), version);
              Response rsp = new ResponseImpl(responseToBatch, br);
              entry.getValue().getPromise().done(rsp);
            } else if (request instanceof BatchGetRequest) {
              BatchGetRequest batchGetRequest = (BatchGetRequest) request;
              Set<String> ids = extractIds(batchGetRequest);
              DataMap dm = filterIdsInBatchResult(responseToBatch.getEntity().data(), ids, true);
              BatchResponse br = new BatchResponse<>(dm, batchGetRequest.getResponseDecoder().getEntityClass());
              Response rsp = new ResponseImpl(responseToBatch, br);
              entry.getValue().getPromise().done(rsp);
            } else if (request instanceof BatchGetEntityRequest) {
              BatchGetEntityRequest batchGetEntityRequest = (BatchGetEntityRequest) request;
              Set<String> ids = extractIds(batchGetEntityRequest);
              DataMap dm = filterIdsInBatchResult(responseToBatch.getEntity().data(), ids, false);
              BatchKVResponse br = new BatchKVResponse(dm, request.getResourceSpec().getKeyType(),
                  request.getResourceSpec().getValueType(), request.getResourceSpec().getKeyParts(),
                  request.getResourceSpec().getComplexKeyType(), version);
              Response rsp = new ResponseImpl(responseToBatch, br);
              entry.getValue().getPromise().done(rsp);
            } else {
              entry.getValue().getPromise().fail(new Exception(
                  "ParSeqRestClient could not handled this type of GET request: " + request.getClass().getName()));
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


  private <RT extends RecordTemplate> Set<String> extractIds(BatchRequest<RT> request) {
    return ((Set<Object>) request.getObjectIds()).stream().map(Object::toString).collect(Collectors.toSet());
  }

  @Override
  public <RT extends RecordTemplate> void executeBatch(final RestClient restClient, final Batch<RestRequestBatchKey, Response<Object>> batch) {
    doExecuteBatch(restClient, batch);
  }

  @Override
  public String getBaseUriTemplate() {
    return _baseUriTemplate;
  }

  public Map<String, String> getHeaders() {
    return _headers;
  }

  public Map<String, Object> getQueryParams() {
    return _queryParams;
  }

  public ResourceSpec getResourceSpec() {
    return _resourceSpec;
  }

  public RestliRequestOptions getRequestOptions() {
    return _requestOptions;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_baseUriTemplate == null) ? 0 : _baseUriTemplate.hashCode());
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
    if (_queryParams == null) {
      if (other._queryParams != null)
        return false;
    } else if (!_queryParams.equals(other._queryParams))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "GetRequestGroup [_baseUriTemplate=" + _baseUriTemplate + ", _queryParams=" + _queryParams
        + ", _requestOptions=" + _requestOptions + ", _headers=" + _headers + ", _resourceSpec=" + _resourceSpec
        + ", _dryRun=" + _dryRun + ", _maxBatchSize=" + _maxBatchSize + "]";
  }

  public <K, V> String getBatchName(final Batch<K, V> batch) {
    return _baseUriTemplate + " " + (batch.size() == 1 ? ResourceMethod.GET : (ResourceMethod.BATCH_GET +
        "(" + batch.size() + ")"));
  }

  @Override
  public boolean isDryRun() {
    return _dryRun;
  }

  @Override
  public int getMaxBatchSize() {
    return _maxBatchSize;
  }

}
