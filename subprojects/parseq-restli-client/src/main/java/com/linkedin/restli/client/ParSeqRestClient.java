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

import com.google.common.util.concurrent.MoreExecutors;
import com.linkedin.common.callback.Callback;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchingStrategy;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.r2.filter.R2Constants;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.ConfigValue;
import com.linkedin.restli.client.config.RequestConfig;
import com.linkedin.restli.client.config.RequestConfigBuilder;
import com.linkedin.restli.client.config.RequestConfigOverrides;
import com.linkedin.restli.client.config.RequestConfigProvider;
import com.linkedin.restli.client.metrics.BatchingMetrics;
import com.linkedin.restli.client.metrics.Metrics;
import com.linkedin.restli.common.OperationNameGenerator;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A ParSeq client that creates a ParSeq task from a rest.li {@link Request} by sending the request to underlying rest.li
 * {@link Client}. ParSeqRestClient delegates task execution to Rest.li Client {@link Client#sendRequest(Request, Callback)}
 * method that takes a {@link com.linkedin.restli.client.ParSeqRestClient.PromiseCallbackAdapter}. ParSeq task created
 * from {@link ParSeqRestClient} may fail when
 * {@link com.linkedin.restli.client.ParSeqRestClient.PromiseCallbackAdapter} receives the following error conditions:
 * <p>
 * 1. @{link RestLiResponseExcepion}: Request has reached Rest.li server and rest.li server throws RestLiServiceException.
 * 2. @{link RemoteInvocationException}: Request failed before reaching rest.li server, for example, RestException thrown
 * from request filters, {@link javax.naming.ServiceUnavailableException} when client cannot find available server instance
 * that could serve the request, etc.
 * 3. @{link TimeoutException}: Request times out after configured timeoutMs.
 * </p>
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 * @author Min Chen (mnchen@linkedin.com)
 *
 */
public class ParSeqRestClient extends BatchingStrategy<RequestGroup, RestRequestBatchKey, Response<Object>>
    implements ParSeqRestliClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(ParSeqRestClient.class);

  private final Client _client;
  private final BatchingMetrics _batchingMetrics = new BatchingMetrics();
  private final RequestConfigProvider _requestConfigProvider;
  private final boolean _d2RequestTimeoutEnabled;
  private final Function<Request<?>, RequestContext> _requestContextProvider;
  private final Executor _executor;

  ParSeqRestClient(final Client client, final RequestConfigProvider requestConfigProvider,
      Function<Request<?>, RequestContext> requestContextProvider, final boolean d2RequestTimeoutEnabled,
      Executor executor) {
    ArgumentUtil.requireNotNull(client, "client");
    ArgumentUtil.requireNotNull(requestConfigProvider, "requestConfigProvider");
    ArgumentUtil.requireNotNull(requestContextProvider, "requestContextProvider");
    _client = client;
    _requestConfigProvider = requestConfigProvider;
    _requestContextProvider = requestContextProvider;
    _d2RequestTimeoutEnabled = d2RequestTimeoutEnabled;
    _executor = executor;
  }

  ParSeqRestClient(final Client client, final RequestConfigProvider requestConfigProvider,
      Function<Request<?>, RequestContext> requestContextProvider, final boolean d2RequestTimeoutEnabled) {
    ArgumentUtil.requireNotNull(client, "client");
    ArgumentUtil.requireNotNull(requestConfigProvider, "requestConfigProvider");
    ArgumentUtil.requireNotNull(requestContextProvider, "requestContextProvider");
    _client = client;
    _requestConfigProvider = requestConfigProvider;
    _requestContextProvider = requestContextProvider;
    _d2RequestTimeoutEnabled = d2RequestTimeoutEnabled;
    _executor = MoreExecutors.directExecutor();
  }

  /**
   * Creates new ParSeqRestClient with default configuration.
   *
   * @deprecated Please use {@link ParSeqRestliClientBuilder} to create instances.
   */
  @Deprecated
  public ParSeqRestClient(final Client client) {
    ArgumentUtil.requireNotNull(client, "client");
    _client = client;
    _requestConfigProvider = RequestConfigProvider.build(new ParSeqRestliClientConfigBuilder().build(), () -> Optional.empty());
    _requestContextProvider = request -> new RequestContext();
    _d2RequestTimeoutEnabled = false;
    _executor = MoreExecutors.directExecutor();
  }

  /**
   * Creates new ParSeqRestClient with default configuration.
   *
   * @deprecated Please use {@link ParSeqRestliClientBuilder} to create instances.
   */
  @Deprecated
  public ParSeqRestClient(final RestClient client) {
    ArgumentUtil.requireNotNull(client, "client");
    _client = client;
    _requestConfigProvider = RequestConfigProvider.build(new ParSeqRestliClientConfigBuilder().build(), () -> Optional.empty());
    _requestContextProvider = request -> new RequestContext();
    _d2RequestTimeoutEnabled = false;
    _executor = MoreExecutors.directExecutor();
  }

  @Override
  @Deprecated
  public <T> Promise<Response<T>> sendRequest(final Request<T> request) {
    return sendRequest(request, _requestContextProvider.apply(request));
  }

  @Override
  @Deprecated
  public <T> Promise<Response<T>> sendRequest(final Request<T> request, final RequestContext requestContext) {
    final SettablePromise<Response<T>> promise = Promises.settable();
    _executor.execute(() -> {
      try {
        _client.sendRequest(request, requestContext, new PromiseCallbackAdapter<T>(promise));
      } catch (Throwable e) {
        promise.fail(e);
      }
    });
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
    return createTask(request, _requestContextProvider.apply(request));
  }

  @Override
  public <T> Task<Response<T>> createTask(final Request<T> request, final RequestContext requestContext) {
    return createTask(generateTaskName(request), request, requestContext, _requestConfigProvider.apply(request));
  }

  /**
   * @deprecated ParSeqRestClient generates consistent names for tasks based on request parameters and it is
   * recommended to us default names.
   */
  @Deprecated
  public <T> Task<Response<T>> createTask(final String name, final Request<T> request, final RequestContext requestContext) {
    return createTask(name, request, requestContext, _requestConfigProvider.apply(request));
  }

  @Override
  public <T> Task<Response<T>> createTask(Request<T> request, RequestConfigOverrides configOverrides) {
    return createTask(request,  _requestContextProvider.apply(request), configOverrides);
  }

  @Override
  public <T> Task<Response<T>> createTask(Request<T> request, RequestContext requestContext,
      RequestConfigOverrides configOverrides) {
    RequestConfig config = _requestConfigProvider.apply(request);
    RequestConfigBuilder configBuilder = new RequestConfigBuilder(config);
    RequestConfig effectiveConfig = configBuilder.applyOverrides(configOverrides).build();
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

  private <T> Task<Response<T>> withTimeout(final Task<Response<T>> task, ConfigValue<Long> timeout) {
    if (timeout.getSource().isPresent()) {
      return task.withTimeout("src: " + timeout.getSource().get(), timeout.getValue(), TimeUnit.MILLISECONDS);
    } else {
      return task.withTimeout(timeout.getValue(), TimeUnit.MILLISECONDS);
    }
  }

  private <T> Task<Response<T>> createTask(final String name, final Request<T> request,
      final RequestContext requestContext, RequestConfig config) {
    LOGGER.debug("createTask, name: '{}', config: {}", name, config);
    if (_d2RequestTimeoutEnabled) {
      return createTaskWithD2Timeout(name, request, requestContext, config);
    } else {
      return createTaskWithTimeout(name, request, requestContext, config);
    }
  }

  // Check whether per-request timeout is specified in the given request context.
  private boolean hasRequestContextTimeout(RequestContext requestContext) {
    Object requestTimeout = requestContext.getLocalAttr(R2Constants.REQUEST_TIMEOUT);
    return (requestTimeout instanceof Number) && (((Number)requestTimeout).intValue() > 0);
  }

  // check whether we need to apply timeout to a rest.li request task.
  private boolean needApplyTaskTimeout(RequestContext requestContext, ConfigValue<Long> timeout) {
    // return false if no timeout configured or per-request timeout already specified in request context
    return timeout.getValue() != null && timeout.getValue() > 0 && !hasRequestContextTimeout(requestContext);
  }

  // Apply timeout to a ParSeq rest.li request task through parseq timer task.
  private <T> Task<Response<T>> createTaskWithTimeout(final String name, final Request<T> request,
      final RequestContext requestContext, RequestConfig config) {
    ConfigValue<Long> timeout = config.getTimeoutMs();
    Task<Response<T>> requestTask;
    if (RequestGroup.isBatchable(request, config)) {
      requestTask = createBatchableTask(name, request, requestContext, config);
    } else {
      requestTask = Task.async(name, () -> sendRequest(request, requestContext));
    }
    if (!needApplyTaskTimeout(requestContext, timeout)) {
      return requestTask;
    } else {
      return withTimeout(requestTask, timeout);
    }
  }

  /**
   * We will distinguish two cases in applying timeout to a ParSeq rest.li request task through D2 request timeout.
   * Case 1: There is no per request timeout specified in request context of rest.li request, timeout is configured
   * through ParSeqRestClient configuration. For this case, we will update request context as:
   *    REQUEST_TIMEOUT = configured timeout value
   *    REQUEST_TIMEOUT_IGNORE_IF_HIGHER_THAN_DEFAULT = true
   * since in this case, ParSeqRestClient just wants to timeout this request from client side within configured timeout
   * without disturbing any lower layer load balancing behaviors.
   *
   * Case 2: There is per request timeout specified in rest.li request, and there may or may not have timeout specified
   * through ParSeqRestClient configuration. For this case, per request timeout specified in rest.li request always
   * takes precedence, ParSeq will interpret that users would like to use this to impact lower layer LB behavior, and
   * thus will pass down request context unchanged down.
   */
  private <T> Task<Response<T>> createTaskWithD2Timeout(final String name, final Request<T> request,
      final RequestContext requestContext, RequestConfig config) {
    ConfigValue<Long> timeout = config.getTimeoutMs();
    boolean taskNeedTimeout = needApplyTaskTimeout(requestContext, timeout);
    if (taskNeedTimeout) {
       // configure request context before creating parseq task from the request
      requestContext.putLocalAttr(R2Constants.REQUEST_TIMEOUT, timeout.getValue().intValue());
      requestContext.putLocalAttr(R2Constants.REQUEST_TIMEOUT_IGNORE_IF_HIGHER_THAN_DEFAULT, true);
    }
    Task<Response<T>> requestTask;
    if (RequestGroup.isBatchable(request, config)) {
      requestTask = createBatchableTask(name, request, requestContext, config);
    } else {
      requestTask = Task.async(name, () -> sendRequest(request, requestContext));
    }
    if (!taskNeedTimeout) {
      return requestTask;
    } else {
      // still enforce parseq client timeout if for some reason downstream services are not timed out properly.
      return withTimeout(requestTask, timeout);
    }
  }

  private RestRequestBatchKey createKey(Request<Object> request, RequestContext requestContext,
      RequestConfig config) {
    return new RestRequestBatchKey(request, requestContext, config);
  }

  @SuppressWarnings("unchecked")
  private <T> Task<Response<T>> createBatchableTask(String name, Request<T> request, RequestContext requestContext,
      RequestConfig config) {
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
    group.executeBatch(_client, batch, _requestContextProvider);
  }

  @Override
  public RequestGroup classify(RestRequestBatchKey key) {
    Request<?> request = key.getRequest();
    return RequestGroup.fromRequest(request, key.getRequestConfig().getMaxBatchSize().getValue());
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

}
