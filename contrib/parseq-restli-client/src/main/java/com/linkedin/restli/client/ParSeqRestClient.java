/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.restli.client;

import com.linkedin.common.callback.Callback;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.parseq.batching.BatchingStrategy;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.common.OperationNameGenerator;


public class ParSeqRestClient
    extends BatchingStrategy<RequestGroup, ParSeqRestClient.RestRequestBatchKey, Response<Object>> {
  private final RestClient _restClient;

  public ParSeqRestClient(final RestClient restClient) {
    _restClient = restClient;
  }

  /**
   * Sends a type-bound REST request, returning a promise.
   *
   * @deprecated Use higher level API that returns Task instance. This method is
   * left for backwards compatibility.
   * @param request to send
   * @return response promise
   */
  @Deprecated
  public <T> Promise<Response<T>> sendRequest(final Request<T> request) {
    return sendRequest(request, new RequestContext());
  }

  /**
   * Sends a type-bound REST request, returning a promise.
   *
   * @deprecated Use higher level API that returns Task instance. This method is
   * left for backwards compatibility.
   * @param request to send
   * @param requestContext context for the request
   * @return response promise
   */
  @Deprecated
  public <T> Promise<Response<T>> sendRequest(final Request<T> request, final RequestContext requestContext) {
    final SettablePromise<Response<T>> promise = Promises.settable();

    // wrapper around the callback interface
    // when the request finishes, the callback updates the promise with the corresponding
    // result
    _restClient.sendRequest(request, requestContext, new PromiseCallbackAdapter<T>(promise));
    return promise;
  }

  static class PromiseCallbackAdapter<T> implements Callback<Response<T>> {
    private final SettablePromise<Response<T>> _promise;

    public PromiseCallbackAdapter(final SettablePromise<Response<T>> promise) {
      this._promise = promise;
    }

    @Override
    public void onSuccess(final Response<T> result) {
      try {
        _promise.done(result);
      } catch (Exception e) {
        onError(e);
      }
    }

    @Override
    public void onError(final Throwable e) {
      _promise.fail(e);
    }
  }

  /**
   * Return a task that will send a type-bound REST request when run.
   *
   * @param request to send
   * @return response task
   */
  public <T> Task<Response<T>> createTask(final Request<T> request) {
    return createTask(request, new RequestContext());
  }

  /**
   * Return a task that will send a type-bound REST request when run. The task's name
   * defaults to information about the request.
   *
   * @param request to send
   * @param requestContext context for the request
   * @return response task
   */
  public <T> Task<Response<T>> createTask(final Request<T> request, final RequestContext requestContext) {
    return createTask(generateTaskName(request), request, requestContext);
  }

  /**
   * Generates a task name for the current task.
   * @param request the outgoing request
   * @return a task name
   */
  static String generateTaskName(final Request<?> request) {
    return request.getBaseUriTemplate() + " "
        + OperationNameGenerator.generate(request.getMethod(), request.getMethodName());
  }

  /**
   * Return a task that will send a type-bound REST request when run.
   *
   * @param request to send
   * @param requestContext context for the request
   * @param name the name of the tasks
   * @return response task
   */
  @SuppressWarnings("unchecked")
  public <T> Task<Response<T>> createTask(final String name, final Request<T> request,
      final RequestContext requestContext) {
    return cast(batchable(name, new RestRequestBatchKey((Request<Object>) request, requestContext)));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static <X> Task<X> cast(Task t) {
    return (Task<X>) t;
  }

  /**
   * Class used for deduplication. Two requests are considered equal
   * when Request and RequestContext objects are equal.
   */
  public static class RestRequestBatchKey {
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

  @Override
  public void executeBatch(RequestGroup group, Batch<RestRequestBatchKey, Response<Object>> batch) {
    group.executeBatch(_restClient, batch);
  }

  @Override
  public void executeSingleton(RequestGroup group, RestRequestBatchKey key, BatchEntry<Response<Object>> entry) {
    group.executeSingleton(_restClient, key, entry);
  }

  @Override
  public RequestGroup classify(RestRequestBatchKey key) {
    return RequestGroup.fromRequest(key.getRequest());
  }

}
