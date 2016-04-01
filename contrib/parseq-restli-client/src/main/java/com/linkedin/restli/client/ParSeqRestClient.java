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

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.linkedin.common.callback.Callback;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchingStrategy;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.BatchingConfig;
import com.linkedin.restli.client.config.ParSeqRestClientConfig;
import com.linkedin.restli.client.config.ResourceConfig;
import com.linkedin.restli.common.OperationNameGenerator;


/**
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
public class ParSeqRestClient
    extends BatchingStrategy<RequestGroup, RestRequestBatchKey, Response<Object>> {

  public static final int DEFAULT_MAX_BATCH_SIZE = 1024;

  public static final BatchingConfig DEFAULT_BATCHING_CONFIG =
      new BatchingConfig(false, ParSeqRestClient.DEFAULT_MAX_BATCH_SIZE, true);

  public static final ResourceConfig DEFAULT_RESOURCE_CONFIG =
      new ResourceConfig(Collections.emptyMap(), Optional.empty(), DEFAULT_BATCHING_CONFIG);

  public static final ParSeqRestClientConfig DEFAULT_PARSEQ_REST_CLIENT_CONFIG =
      new ParSeqRestClientConfig(Collections.emptyMap(), DEFAULT_RESOURCE_CONFIG);

  private final RestClient _restClient;
  private final BatchingMetrics _batchingMetrics = new BatchingMetrics();
  private final ParSeqRestClientConfig _clientConfig;

  public ParSeqRestClient(final RestClient restClient) {
    this(restClient, DEFAULT_PARSEQ_REST_CLIENT_CONFIG);
  }

  public ParSeqRestClient(final RestClient restClient, final ParSeqRestClientConfig config) {
    _restClient = restClient;
    _clientConfig = config;
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

  public <T> Task<Response<T>> createTask(final Request<T> request) {
    return createTask(request, new RequestContext());
  }

  public <T> Task<Response<T>> createTask(final Request<T> request, final RequestContext requestContext) {
    return createTask(generateTaskName(request), request, requestContext);
  }

  /**
   * Generates a task name for the request.
   * @param request
   * @return a task name
   */
  static String generateTaskName(final Request<?> request) {
    return request.getBaseUriTemplate() + " "
        + OperationNameGenerator.generate(request.getMethod(), request.getMethodName());
  }

  public <T> Task<Response<T>> createTask(final String name, final Request<T> request,
      final RequestContext requestContext) {
    return createTask(name, request, requestContext, _clientConfig);
  }

  public <T> Task<Response<T>> createTask(final String name, final Request<T> request,
      final RequestContext requestContext, ParSeqRestClientConfig config) {
    ResourceConfig resourceConfig = config.getResourceConfig(request.getBaseUriTemplate());
    BatchingConfig bathcingConfig = resourceConfig.getBatchingConfig(request.getMethod());
    final Task<Response<T>> task = createTask(name, request, requestContext, bathcingConfig);
    if (resourceConfig.getTimeoutNs().isPresent()) {
      return task.withTimeout(resourceConfig.getTimeoutNs().get(), TimeUnit.NANOSECONDS);
    } else {
      return task;
    }
  }

  private <T> Task<Response<T>> createTask(final String name, final Request<T> request,
      final RequestContext requestContext, BatchingConfig bathcingConfig) {
    if (RequestGroup.isBatchable(request, bathcingConfig)) {
      return createBatchableTask(name, request, requestContext, bathcingConfig);
    } else {
      return Task.async(name, () -> {
        return sendRequest(request, requestContext);
      });
    }
  }

  private RestRequestBatchKey createKey(String name, Request<Object> request, RequestContext requestContext,
      BatchingConfig bathcingConfig) {
    if (bathcingConfig.isDryRun()) {
      return new DryRunRestRequestBatchKey<>(request, requestContext, bathcingConfig, sendRequest(request, requestContext));
    } else {
      return new RestRequestBatchKey(request, requestContext, bathcingConfig);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> Task<Response<T>> createBatchableTask(String name, Request<T> request, RequestContext requestContext,
      BatchingConfig bathcingConfig) {
    return cast(batchable(name, createKey(name, (Request<Object>) request, requestContext, bathcingConfig)));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static <X> Task<X> cast(Task t) {
    return (Task<X>) t;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void executeBatch(RequestGroup group, Batch<RestRequestBatchKey, Response<Object>> batch) {
    if (group instanceof GetRequestGroup) {
      _batchingMetrics.recordBatchSize(group.getBaseUriTemplate(), batch.size());
    }
    if (group.isDryRun()) {
      batch.foreach((key, promise) -> Promises.propagateResult(((DryRunRestRequestBatchKey)key).getPromise(), promise));
    } else {
      group.executeBatch(_restClient, batch);
    }
  }

  @Override
  public RequestGroup classify(RestRequestBatchKey key) {
    Request<Object> request = key.getRequest();
    return RequestGroup.fromRequest(request, key.getBathcingConfig().isDryRun(), key.getBathcingConfig().getMaxBatchSize());
  }

  @Override
  public String getBatchName(RequestGroup group, Batch<RestRequestBatchKey, Response<Object>> batch) {
    return group.getBatchName(batch);
  }

  @Override
  public int maxBatchSizeForGroup(RequestGroup group) {
    return group.getMaxBatchSize();
  }

  public BatchingMetrics getBatchingMetrics() {
    return _batchingMetrics;
  }
}
