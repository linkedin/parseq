package com.linkedin.restli.client;

import com.linkedin.parseq.promise.Promise;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.BatchingConfig;


class DryRunRestRequestBatchKey<T> extends RestRequestBatchKey {

  private final Promise<Response<T>> _promise;

  public DryRunRestRequestBatchKey(Request<Object> request, RequestContext requestContext,
      BatchingConfig bathcingConfig, Promise<Response<T>> promise) {
    super(request, requestContext, bathcingConfig);
    _promise = promise;
  }

  public Promise<Response<T>> getPromise() {
    return _promise;
  }

}
