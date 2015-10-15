package com.linkedin.restli.client;

import java.util.Map;

import com.linkedin.data.template.RecordTemplate;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.restli.client.ParSeqRestClient.RestRequestBatchKey;
import com.linkedin.restli.common.ResourceMethod;

abstract class RequestEquivalenceClass {
  private final String _baseUriTemplate;
  private final ResourceMethod _method;
  private final Map<String, String> _headers;
  private final boolean _unique;

  RequestEquivalenceClass(String baseUriTemplate, ResourceMethod method, Map<String, String> headers, boolean unique) {
    _baseUriTemplate = baseUriTemplate;
    _method = method;
    _headers = headers;
    _unique = unique;
  }

  public String getBaseUriTemplate() {
    return _baseUriTemplate;
  }

  public ResourceMethod getMethod() {
    return _method;
  }

  public Map<String, String> getHeaders() {
    return _headers;
  }

  @Override
  public String toString() {
    return "RequestEquivalenceClass [_baseUriTemplate=" + _baseUriTemplate + ", _method=" + _method + ", _headers="
        + _headers + ", _unique=" + _unique + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_baseUriTemplate == null) ? 0 : _baseUriTemplate.hashCode());
    result = prime * result + ((_headers == null) ? 0 : _headers.hashCode());
    result = prime * result + ((_method == null) ? 0 : _method.hashCode());
    result = prime * result + (_unique ? 1231 : 1237);
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
    RequestEquivalenceClass other = (RequestEquivalenceClass) obj;
    //make sure unique equivalence classes are unique
    if (_unique || other._unique)
      return false;
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
    if (_method != other._method)
      return false;
    return true;
  }

  public static RequestEquivalenceClass fromRequest(final Request<?> request) {
    switch (request.getMethod()) {
      case GET:
        return new GetRequestEquivalenceClass(request.getBaseUriTemplate(), request.getMethod(), request.getHeaders());
      default:
        return new UniqueRequestEquivalenceClass(request.getBaseUriTemplate(), request.getMethod(), request.getHeaders());
    }
  }

  abstract <RT extends RecordTemplate> void executeBatch(RestClient restClient, Batch<RestRequestBatchKey, Response<Object>> batch);

  abstract <RT extends RecordTemplate> void executeSingleton(RestClient restClient, RestRequestBatchKey key, BatchEntry<Response<Object>> entry);

}