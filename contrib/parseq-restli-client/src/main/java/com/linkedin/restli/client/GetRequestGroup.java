/*
 * Copyright 2016 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
import com.linkedin.parseq.function.Tuple3;
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
import com.linkedin.restli.internal.client.response.BatchEntityResponse;
import com.linkedin.restli.internal.common.ProtocolVersionUtil;
import com.linkedin.restli.internal.common.ResponseUtils;

class GetRequestGroup implements RequestGroup {

  private final String _baseUriTemplate; //taken from first request, used to differentiate between groups
  private final ResourceSpec _resourceSpec;  //taken from first request
  private final Map<String, String> _headers; //taken from first request, used to differentiate between groups
  private final RestliRequestOptions _requestOptions; //taken from first request, used to differentiate between groups
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
    final Map<String, Object> params = new HashMap<>(request.getQueryParamsObjects());
    params.remove(RestConstants.QUERY_BATCH_IDS_PARAM);
    params.remove(RestConstants.FIELDS_PARAM);
    return params;
  }

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

    return new ResponseImpl<>(batchResponse, entityResult);
  }

  private DataMap filterIdsInBatchResult(DataMap data, Set<String> ids, boolean stripEntities) {
    DataMap dm = new DataMap(data.size());
    data.forEach((key, value) -> {
      switch(key) {
        case BatchResponse.ERRORS:
          dm.put(key, filterIds((DataMap)value, ids, stripEntities ? EntityResponse.ERROR : null));
          break;
        case BatchResponse.RESULTS:
          dm.put(key, filterIds((DataMap)value, ids, stripEntities ? EntityResponse.ENTITY : null));
          break;
        case BatchResponse.STATUSES:
          dm.put(key, filterIds((DataMap)value, ids, stripEntities ? EntityResponse.STATUS : null));
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


  //Tuple3: (keys, fields, contains-batch-get)
  private static Tuple3<Set<Object>, Set<PathSpec>, Boolean> reduceRequests(final Tuple3<Set<Object>, Set<PathSpec>, Boolean> state,
      final Request<?> rq) {
    return reduceContainsBatch(reduceIds(reduceFields(state, rq), rq), rq);
  }

  //Tuple3: (keys, fields, contains-batch-get)
  private static Tuple3<Set<Object>, Set<PathSpec>, Boolean> reduceContainsBatch(Tuple3<Set<Object>, Set<PathSpec>, Boolean> state,
      Request<?> request) {
    if (request instanceof GetRequest) {
      return state;
    } else if (request instanceof BatchRequest) {
      return Tuples.tuple(state._1(), state._2(), true);
    } else {
      throw new RuntimeException("ParSeqRestClient could not handled this type of GET request: " + request.getClass().getName());
    }
  }

  //Tuple3: (keys, fields, contains-batch-get)
  private static Tuple3<Set<Object>, Set<PathSpec>, Boolean> reduceIds(Tuple3<Set<Object>, Set<PathSpec>, Boolean> state,
      Request<?> request) {
    if (request instanceof GetRequest) {
      GetRequest<?> getRequest = (GetRequest<?>)request;
      state._1().add(getRequest.getObjectId());
      return state;
    } else if (request instanceof BatchRequest) {
      BatchRequest<?> batchRequest = (BatchRequest<?>)request;
      state._1().addAll(batchRequest.getObjectIds());
      return state;
    } else {
      throw new RuntimeException("ParSeqRestClient could not handled this type of GET request: " + request.getClass().getName());
    }
  }

  //Tuple3: (keys, fields, contains-batch-get)
  private static Tuple3<Set<Object>, Set<PathSpec>, Boolean> reduceFields(Tuple3<Set<Object>, Set<PathSpec>, Boolean> state,
      Request<?> request) {
    if (request instanceof GetRequest || request instanceof BatchRequest) {
      final Set<PathSpec> requestFields = request.getFields();
      if (requestFields != null && !requestFields.isEmpty()) {
        if (state._2() != null) {
          state._2().addAll(requestFields);
        }
        return state;
      } else {
        return Tuples.tuple(state._1(), null, state._3());
      }
    } else {
      throw new RuntimeException("ParSeqRestClient could not handled this type of GET request: " + request.getClass().getName());
    }
  }

  private <K, RT extends RecordTemplate> void doExecuteBatch(final RestClient restClient, final Batch<RestRequestBatchKey, Response<Object>> batch) {

    final Tuple3<Set<Object>, Set<PathSpec>, Boolean> reductionResults = reduceRequests(batch);
    final Set<Object> ids = reductionResults._1();
    final Set<PathSpec> fields = reductionResults._2();
    final boolean containsBatchGet = reductionResults._3();

    if (ids.size() == 1 && !containsBatchGet) {
      doExecuteGet(restClient, batch, ids, fields);
    } else {
      doExecuteBatchGet(restClient, batch, ids, fields);
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private <K, RT extends RecordTemplate> void doExecuteBatchGet(final RestClient restClient,
      final Batch<RestRequestBatchKey, Response<Object>> batch, final Set<Object> ids, final Set<PathSpec> fields) {
    final BatchGetEntityRequestBuilder<K, RT> builder = new BatchGetEntityRequestBuilder<>(_baseUriTemplate, _resourceSpec, _requestOptions);
    builder.setHeaders(_headers);
    _queryParams.forEach((key, value) -> builder.setParam(key, value));

    builder.setParam(RestConstants.QUERY_BATCH_IDS_PARAM, ids);
    if (fields != null && !fields.isEmpty()) {
      builder.setParam(RestConstants.FIELDS_PARAM, fields.toArray());
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
              BatchKVResponse br = new BatchEntityResponse<>(dm, request.getResourceSpec().getKeyType(),
                  request.getResourceSpec().getValueType(), request.getResourceSpec().getKeyParts(),
                  request.getResourceSpec().getComplexKeyType(), version);
              Response rsp = new ResponseImpl(responseToBatch, br);
              entry.getValue().getPromise().done(rsp);
            } else {
              entry.getValue().getPromise().fail(new Exception(
                  "ParSeqRestClient could not handle this type of GET request: " + request.getClass().getName()));
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

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private <K, RT extends RecordTemplate> void doExecuteGet(final RestClient restClient,
      final Batch<RestRequestBatchKey, Response<Object>> batch, final Set<Object> ids, final Set<PathSpec> fields) {

    final GetRequestBuilder<K, RT> builder = (GetRequestBuilder<K, RT>) new GetRequestBuilder<>(_baseUriTemplate,
        _resourceSpec.getValueClass(), _resourceSpec, _requestOptions);
    builder.setHeaders(_headers);
    _queryParams.forEach((key, value) -> builder.setParam(key, value));

    builder.id((K) ids.iterator().next());
    if (fields != null && !fields.isEmpty()) {
      builder.setParam(RestConstants.FIELDS_PARAM, fields.toArray());
    }

    final GetRequest<RT> get = builder.build();

    restClient.sendRequest(get, new Callback<Response<RT>>() {

      @Override
      public void onError(Throwable e) {
        batch.failAll(e);
      }

      @Override
      public void onSuccess(Response<RT> responseToGet) {
        batch.entries().stream().forEach(entry -> {
          Request request = entry.getKey().getRequest();
          if (request instanceof GetRequest) {
            entry.getValue().getPromise().done(new ResponseImpl<>(responseToGet, responseToGet.getEntity()));
          } else {
            entry.getValue().getPromise().fail(new Exception(
                "ParSeqRestClient could not handle this type of GET request: " + request.getClass().getName()));
          }
        });
      }

    });
  }

  //Tuple3: (keys, fields, contains-batch-get)
  private Tuple3<Set<Object>, Set<PathSpec>, Boolean> reduceRequests(
      final Batch<RestRequestBatchKey, Response<Object>> batch) {
    return batch.entries().stream()
      .map(Entry::getKey)
      .map(RestRequestBatchKey::getRequest)
      .reduce(Tuples.tuple(new HashSet<>(), new HashSet<>(), false),
          GetRequestGroup::reduceRequests,
          GetRequestGroup::combine);
  }

  private static Tuple3<Set<Object>, Set<PathSpec>, Boolean> combine(Tuple3<Set<Object>, Set<PathSpec>, Boolean> a,
      Tuple3<Set<Object>, Set<PathSpec>, Boolean> b) {
    Set<Object> ids = a._1();
    ids.addAll(b._1());
    Set<PathSpec> paths = a._2();
    paths.addAll(b._2());
    return Tuples.tuple(ids, paths, a._3() || b._3());
  }

  private <RT extends RecordTemplate> Set<String> extractIds(BatchRequest<RT> request) {
    return request.getObjectIds().stream().map(Object::toString).collect(Collectors.toSet());
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
    result = prime * result + ((_headers == null) ? 0 : _headers.hashCode());
    result = prime * result + ((_queryParams == null) ? 0 : _queryParams.hashCode());
    result = prime * result + ((_requestOptions == null) ? 0 : _requestOptions.hashCode());
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
    if (_requestOptions == null) {
      if (other._requestOptions != null)
        return false;
    } else if (!_requestOptions.equals(other._requestOptions))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "GetRequestGroup [_baseUriTemplate=" + _baseUriTemplate + ", _queryParams=" + _queryParams
        + ", _requestOptions=" + _requestOptions + ", _headers=" + _headers
        + ", _dryRun=" + _dryRun + ", _maxBatchSize=" + _maxBatchSize + "]";
  }

  @Override
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
