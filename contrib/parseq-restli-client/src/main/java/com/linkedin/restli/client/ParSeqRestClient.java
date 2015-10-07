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

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

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


public class ParSeqRestClient extends BatchingStrategy<ParSeqRestClient.RestRequestBatchEntry, Response<Object>>
{
  private final RestClient            _wrappedClient;

  public ParSeqRestClient(final RestClient wrappedClient)
  {
    _wrappedClient = wrappedClient;
  }

  /**
   * Sends a type-bound REST request, returning a promise.
   *
   * @deprecated Use higher level API that returns Task instance.
   * @param request to send
   * @return response promise
   */
  @Deprecated
  public <T> Promise<Response<T>> sendRequest(final Request<T> request)
  {
    return sendRequest(request, new RequestContext());
  }

  /**
   * Sends a type-bound REST request, returning a promise.
   *
   * @deprecated Use higher level API that returns Task instance.
   * @param request to send
   * @param requestContext context for the request
   * @return response promise
   */
  @Deprecated
  public <T> Promise<Response<T>> sendRequest(final Request<T> request,
                                              final RequestContext requestContext)
  {
    final SettablePromise<Response<T>> promise = Promises.settable();

    // wrapper around the callback interface
    // when the request finishes, the callback updates the promise with the corresponding
    // result
    _wrappedClient.sendRequest(request, requestContext, new PromiseCallbackAdapter<T>(promise));
    return promise;
  }

  private static class PromiseCallbackAdapter<T> implements Callback<Response<T>>
  {
    private final SettablePromise<Response<T>> _promise;

    public PromiseCallbackAdapter(final SettablePromise<Response<T>> promise)
    {
      this._promise = promise;
    }

    @Override
    public void onSuccess(final Response<T> result)
    {
      try
      {
        _promise.done(result);
      }
      catch (Exception e)
      {
        onError(e);
      }
    }

    @Override
    public void onError(final Throwable e)
    {
      _promise.fail(e);
    }
  }

  /**
   * Return a task that will send a type-bound REST request when run.
   *
   * @param request to send
   * @return response task
   */
  public <T> Task<Response<T>> createTask(final Request<T> request)
  {
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
  public <T> Task<Response<T>> createTask(final Request<T> request,
                                          final RequestContext requestContext)
  {
    return createTask(generateTaskName(request), request, requestContext);
  }

  /**
   * Generates a task name for the current task.
   * @param request the outgoing request
   * @return a task name
   */
  private String generateTaskName(final Request<?> request)
  {
    StringBuilder sb = new StringBuilder(request.getBaseUriTemplate());
    sb.append(" ");
    sb.append(OperationNameGenerator.generate(request.getMethod(), request.getMethodName()));
    return sb.toString();
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
  public <T> Task<Response<T>> createTask(final String name,
                                          final Request<T> request,
                                          final RequestContext requestContext)
  {
    return cast(batchable(name, new RestRequestBatchEntry((Request<Object>) request, requestContext)));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static <X> Task<X> cast(Task t) {
    return (Task<X>)t;
  }

  public static class RestRequestBatchEntry {
    private final Request<Object> _request;
    private final RequestContext _requestContext;

    public RestRequestBatchEntry(Request<Object> request, RequestContext requestContext) {
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
      RestRequestBatchEntry other = (RestRequestBatchEntry) obj;
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
  public Collection<Batch<RestRequestBatchEntry, Response<Object>>> split(final Batch<RestRequestBatchEntry, Response<Object>> batch) {
    //split batch into collection of singletons
    return batch.entires().stream().map(entry -> Batch.<RestRequestBatchEntry, Response<Object>> builder()
        .add(entry.getKey(), entry.getValue()).build()).collect(Collectors.toList());
  }

  @Override
  public void executeBatch(Batch<RestRequestBatchEntry, Response<Object>> batch) {
    if (batch.size() > 0) {
      if (batch.size() == 1) {
        Map.Entry<RestRequestBatchEntry, BatchEntry<Response<Object>>> req = batch.entires().iterator().next();
        _wrappedClient.sendRequest(req.getKey().getRequest(), req.getKey().getRequestContext(),
            new PromiseCallbackAdapter<Object>(req.getValue().getPromise()));
      } else {
        throw new UnsupportedOperationException("batch size: " + batch.size());
      }
    }
  }

}