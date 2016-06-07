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

import java.util.concurrent.TimeUnit;

import com.linkedin.common.callback.Callback;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchingStrategy;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.ConfigValue;
import com.linkedin.restli.client.config.ResourceConfig;
import com.linkedin.restli.client.config.ResourceConfigBuilder;
import com.linkedin.restli.client.config.ResourceConfigOverrides;
import com.linkedin.restli.client.config.ResourceConfigProvider;
import com.linkedin.restli.client.metrics.BatchingMetrics;
import com.linkedin.restli.client.metrics.Metrics;
import com.linkedin.restli.common.OperationNameGenerator;


/**
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
class ParSeqRestClientImpl extends BatchingStrategy<RequestGroup, RestRequestBatchKey, Response<Object>>
    implements ParSeqRestClient {

  private final RestClient _restClient;
  private final BatchingMetrics _batchingMetrics = new BatchingMetrics();
  private final ResourceConfigProvider _clientConfig;

  ParSeqRestClientImpl(final RestClient restClient, final ResourceConfigProvider config) {
    ArgumentUtil.requireNotNull(restClient, "restClient");
    ArgumentUtil.requireNotNull(config, "config");
    _restClient = restClient;
    _clientConfig = config;
  }

  @Override
  public <T> Promise<Response<T>> sendRequest(final Request<T> request) {
    return sendRequest(request, new RequestContext());
  }

  @Override
  public <T> Promise<Response<T>> sendRequest(final Request<T> request, final RequestContext requestContext) {
    final SettablePromise<Response<T>> promise = Promises.settable();
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

  @Override
  public <T> Task<Response<T>> createTask(final Request<T> request) {
    return createTask(request, new RequestContext());
  }

  @Override
  public <T> Task<Response<T>> createTask(final Request<T> request, final RequestContext requestContext) {
    return createTask(generateTaskName(request), request, requestContext, _clientConfig.apply(request));
  }

  @Override
  public <T> Task<Response<T>> createTask(Request<T> request, ResourceConfigOverrides configOverrides) {
    return createTask(request, new RequestContext(), configOverrides);
  }

  @Override
  public <T> Task<Response<T>> createTask(Request<T> request, RequestContext requestContext,
      ResourceConfigOverrides configOverrides) {
    ResourceConfig config = _clientConfig.apply(request);
    ResourceConfigBuilder configBuilder = new ResourceConfigBuilder(config);
    ResourceConfig effectiveConfig = configBuilder.applyOverrides(configOverrides).build();
    return createTask(generateTaskName(request), request, requestContext, effectiveConfig);
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

  private <T> Task<Response<T>> withTimeout(final Task<Response<T>> task, ResourceConfig config) {
    ConfigValue<Long> timeout = config.getTimeoutMs();
    if (timeout.getValue() != null) {
      if (timeout.getSource().isPresent()) {
        return task.withTimeout("src: " + timeout.getSource().get(), timeout.getValue(), TimeUnit.MILLISECONDS);
      } else {
        return task.withTimeout(timeout.getValue(), TimeUnit.MILLISECONDS);
      }
    } else {
      return task;
    }
  }

  private <T> Task<Response<T>> createTask(final String name, final Request<T> request,
      final RequestContext requestContext, ResourceConfig config) {
    if (RequestGroup.isBatchable(request, config)) {
      return withTimeout(createBatchableTask(name, request, requestContext, config), config);
    } else {
      return withTimeout(Task.async(name, () -> sendRequest(request, requestContext)), config);
    }
  }

  private RestRequestBatchKey createKey(Request<Object> request, RequestContext requestContext,
      ResourceConfig config) {
    return new RestRequestBatchKey(request, requestContext, config);
  }

  @SuppressWarnings("unchecked")
  private <T> Task<Response<T>> createBatchableTask(String name, Request<T> request, RequestContext requestContext,
      ResourceConfig config) {
    return cast(batchable(name, createKey((Request<Object>) request, requestContext, config)));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static <X> Task<X> cast(Task t) {
    return (Task<X>) t;
  }

  @Override
  public void executeBatch(RequestGroup group, Batch<RestRequestBatchKey, Response<Object>> batch) {
    if (group instanceof GetRequestGroup) {
      _batchingMetrics.recordBatchSize(group.getBaseUriTemplate(), batch.batchSize());
    }
    group.executeBatch(_restClient, batch);
  }

  @Override
  public RequestGroup classify(RestRequestBatchKey key) {
    Request<?> request = key.getRequest();
    return RequestGroup.fromRequest(request, key.getResourceConfig().getMaxBatchSize().getValue());
  }

  @Override
  public String getBatchName(RequestGroup group, Batch<RestRequestBatchKey, Response<Object>> batch) {
    return group.getBatchName(batch);
  }

  @Override
  public int keySize(RequestGroup group, RestRequestBatchKey key) {
    return group.keySize(key);
  }

  @Override
  public int maxBatchSizeForGroup(RequestGroup group) {
    return group.getMaxBatchSize();
  }

  public BatchingMetrics getBatchingMetrics() {
    return _batchingMetrics;
  }

  @Override
  public Metrics getMetrics() {
    return () -> _batchingMetrics;
  }

  @Override
  public <T> ResourceConfig getResourceConfig(Request<T> request) {
    return _clientConfig.apply(request);
  }

}
