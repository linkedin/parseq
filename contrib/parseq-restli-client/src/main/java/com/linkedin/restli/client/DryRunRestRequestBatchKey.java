package com.linkedin.restli.client;

import com.linkedin.parseq.promise.Promise;
import com.linkedin.r2.message.RequestContext;

class DryRunRestRequestBatchKey<T> extends RestRequestBatchKey {

  private final Promise<Response<T>> _promise;

  public DryRunRestRequestBatchKey(Request<Object> request, RequestContext requestContext, Promise<Response<T>> promise) {
    super(request, requestContext);
    _promise = promise;
  }

  public Promise<Response<T>> getPromise() {
    return _promise;
  }
  
}
