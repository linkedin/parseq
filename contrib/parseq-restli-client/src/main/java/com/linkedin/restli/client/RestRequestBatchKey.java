package com.linkedin.restli.client;

import com.linkedin.r2.message.RequestContext;

/**
 * Class used for deduplication. Two requests are considered equal
 * when Request and RequestContext objects are equal.
 */
public class RestRequestBatchKey {
  private final Request<Object> _request;
  private final RequestContext _requestContext;

  public RestRequestBatchKey(Request<Object> request, RequestContext requestContext) {
    _request = request;
    _requestContext = requestContext;
  }

  public Request<Object> getRequest() {
    return _request;
  }

  public RequestContext getRequestContext() {
    return _requestContext;
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

}
