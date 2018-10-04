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

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import com.linkedin.data.template.RecordTemplate;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.RequestConfig;

/**
 * Class used for deduplication. Two requests are considered equal
 * when Request and RequestContext objects are equal.
 */
class RestRequestBatchKey {
  private final Request<?> _request;
  private final RequestContext _requestContext;
  private final RequestConfig _batchingConfig;
  private Set<String> _extractedIds;

  public RestRequestBatchKey(Request<Object> request, RequestContext requestContext, RequestConfig batchingConfig) {
    _request = request;
    _requestContext = requestContext;
    _batchingConfig = batchingConfig;
  }

  public Request<?> getRequest() {
    return _request;
  }

  public RequestContext getRequestContext() {
    return _requestContext;
  }

  public RequestConfig getRequestConfig() {
    return _batchingConfig;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_request == null) ? 0 : _request.hashCode());
    result = prime * result + ((_requestContext == null) ? 0 : _requestContext.hashCode());
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
    RestRequestBatchKey other = (RestRequestBatchKey) obj;
    if (_request == null) {
      if (other._request != null)
        return false;
    } else if (!_request.equals(other._request))
      return false;
    if (_requestContext == null) {
      if (other._requestContext != null)
        return false;
    } else if (!_requestContext.equals(other._requestContext))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "RestRequestBatchKey [request=" + _request + ", requestContext=" + _requestContext + "]";
  }


  public Set<String> ids() {
    if (_extractedIds == null) {
      _extractedIds = extractIds();
    }
    return _extractedIds;
  }

  @SuppressWarnings("unchecked")
  private <RT extends RecordTemplate> Set<String> extractIds() {
    if (_request instanceof GetRequest) {
      return Collections.singleton(((GetRequest<?>) _request).getObjectId().toString());
    } else {
      return extractIds((BatchRequest<RT>) _request);
    }
  }

  private <RT extends RecordTemplate> Set<String> extractIds(BatchRequest<RT> request) {
    return request.getObjectIds().stream().map(Object::toString).collect(Collectors.toSet());
  }
}
