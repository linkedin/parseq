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

import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.BatchingConfig;

/**
 * Class used for deduplication. Two requests are considered equal
 * when Request and RequestContext objects are equal.
 */
class RestRequestBatchKey {
  private final Request<Object> _request;
  private final RequestContext _requestContext;
  private final BatchingConfig _bathcingConfig;

  public RestRequestBatchKey(Request<Object> request, RequestContext requestContext, BatchingConfig bathcingConfig) {
    _request = request;
    _requestContext = requestContext;
    _bathcingConfig = bathcingConfig;
  }

  public Request<Object> getRequest() {
    return _request;
  }

  public RequestContext getRequestContext() {
    return _requestContext;
  }

  public BatchingConfig getBathcingConfig() {
    return _bathcingConfig;
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


}
