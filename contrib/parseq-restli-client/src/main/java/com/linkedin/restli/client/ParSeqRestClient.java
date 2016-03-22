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
import com.linkedin.parseq.batching.BatchingStrategy;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.common.OperationNameGenerator;


/**
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
public class ParSeqRestClient
    extends BatchingStrategy<RequestGroup, RestRequestBatchKey, Response<Object>> {

  private final RestClient _restClient;
  private final boolean _batchingDryRun;
  private final BatchingMetrics _batchingMetrics = new BatchingMetrics();


  public ParSeqRestClient(final RestClient restClient) {
    this(restClient, true);
  }

  public ParSeqRestClient(final RestClient restClient, final boolean batchingDryRun) {
    _restClient = restClient;
    _batchingDryRun = batchingDryRun;
  }

  /**
   * Sends a type-bound REST request, returning a promise.
   *
   * @deprecated Use higher level API that returns Task instance, see {@link #createTask(Request)}. This method is
   * left for backwards compatibility.
   * @param request to send
   * @return response promise
   * @see #createTask(Request)
   */
  @Deprecated
  public <T> Promise<Response<T>> sendRequest(final Request<T> request) {
    return sendRequest(request, new RequestContext());
  }

  /**
   * Sends a type-bound REST request, returning a promise.
   *
   * @deprecated Use higher level API that returns Task instance, see {@link #createTask(Request, RequestContext)}. This method is
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
  public <T> Task<Response<T>> createTask(final String name, final Request<T> request,
      final RequestContext requestContext) {
    if (RequestGroup.isBatchable(request)) {
      return createBatchableTask(name, request, requestContext);
    } else {
      return Task.async(name, () -> {
        return sendRequest(request, requestContext);
      });
    }
  }

  private RestRequestBatchKey createKey(String name, Request<Object> request, RequestContext requestContext) {
    if (_batchingDryRun) {
      return new DryRunRestRequestBatchKey<>(request, requestContext, sendRequest(request, requestContext));
    } else {
      return new RestRequestBatchKey(request, requestContext);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> Task<Response<T>> createBatchableTask(String name, Request<T> request, RequestContext requestContext) {
    return cast(batchable(name, createKey(name, (Request<Object>) request, requestContext)));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static <X> Task<X> cast(Task t) {
    return (Task<X>) t;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void executeBatch(RequestGroup group, Batch<RestRequestBatchKey, Response<Object>> batch) {
    if (group instanceof GetRequestGroup) {
      _batchingMetrics.recordBatchSize(((GetRequestGroup)group).getBaseUriTemplate(), batch.size());
    }
    if (_batchingDryRun) {
      batch.foreach((key, promise) -> Promises.propagateResult(((DryRunRestRequestBatchKey)key).getPromise(), promise));
    } else {
      group.executeBatch(_restClient, batch);
    }
  }

  @Override
  public RequestGroup classify(RestRequestBatchKey key) {
    return RequestGroup.fromRequest(key.getRequest());
  }

  @Override
  public String getBatchName(RequestGroup group, Batch<RestRequestBatchKey, Response<Object>> batch) {
    return group.getBatchName(batch);
  }

  public BatchingMetrics getBatchingMetrics() {
    return _batchingMetrics;
  }
}
