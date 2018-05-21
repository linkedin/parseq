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

import com.linkedin.parseq.Exceptions;
import com.linkedin.parseq.function.Failure;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.internal.TimeUnitHelper;
import com.linkedin.r2.filter.R2Constants;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.linkedin.restli.client.config.RequestConfig;
import com.linkedin.restli.client.config.RequestConfigBuilder;
import com.linkedin.restli.client.config.RequestConfigOverrides;
import com.linkedin.restli.client.config.RequestConfigProvider;
import com.linkedin.restli.client.metrics.BatchingMetrics;
import com.linkedin.restli.client.metrics.Metrics;
import com.linkedin.restli.common.OperationNameGenerator;


/**
 * A ParSeq client that creates a ParSeq task from a rest.li {@link Request} by sending the request to underlying rest.li
 * {@link Client}. ParSeqRestClient delegates task execution to Rest.li Client {@link Client#sendRequest(Request, Callback)}
 * method that takes a {@link PromiseCallbackAdapter}. ParSeq task created from {@link ParSeqRestClient} may fail when
 * {@link PromiseCallbackAdapter} receives the following error conditions:
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

  ParSeqRestClient(final Client client, final RequestConfigProvider requestConfigProvider,
      Function<Request<?>, RequestContext> requestContextProvider, final boolean d2RequestTimeoutEnabled) {
    ArgumentUtil.requireNotNull(client, "client");
    ArgumentUtil.requireNotNull(requestConfigProvider, "requestConfigProvider");
    ArgumentUtil.requireNotNull(requestContextProvider, "requestContextProvider");
    _client = client;
    _requestConfigProvider = requestConfigProvider;
    _requestContextProvider = requestContextProvider;
    _d2RequestTimeoutEnabled = d2RequestTimeoutEnabled;
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
    _client.sendRequest(request, requestContext, new PromiseCallbackAdapter<T>(promise));
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
    if (timeout.getValue() != null && timeout.getValue() > 0) {
      if (timeout.getSource().isPresent()) {
        return task.withTimeout("src: " + timeout.getSource().get(), timeout.getValue(), TimeUnit.MILLISECONDS);
      } else {
        return task.withTimeout(timeout.getValue(), TimeUnit.MILLISECONDS);
      }
    } else {
      return task;
    }
  }

  private <T> Task<Response<T>> withD2Timeout(final Task<Response<T>> task, ConfigValue<Long> timeout) {
    if (timeout.getValue() != null && timeout.getValue() > 0) {
      // transform TimeoutException to be compatible with current Task timeout exception message
      String desc = timeout.getSource().map(src -> " src: " + src).orElse("");
      String timeoutTaskName = "withTimeout " + Math.min(timeout.getValue(), Integer.MAX_VALUE)
          + TimeUnitHelper.toString(TimeUnit.MILLISECONDS) + desc;
      return task.transform(timeoutTaskName, (Try<Response<T>> tryGet) -> {
        if (tryGet.isFailed() && tryGet.getError() instanceof TimeoutException) {
          String timeoutExceptionMessage = "task: '" + task.getName() + "' " + timeoutTaskName;
          return Failure.of(Exceptions.timeoutException(timeoutExceptionMessage));
        } else {
          return tryGet;
        }
      });
    } else {
      return task;
    }
  }

  private <T> Task<Response<T>> createTask(final String name, final Request<T> request,
      final RequestContext requestContext, RequestConfig config) {
    LOGGER.debug("createTask, name: '{}', config: {}", name, config);
    return createTaskWithTimeout(name, request, requestContext, config);
  }

  // reconcile timeout specified in requestContext with the given timeout
  private void reconcileRequestTimeout(final RequestContext requestContext, Long timeout) {
    Number contextTimeout = (Number) requestContext.getLocalAttr(R2Constants.REQUEST_TIMEOUT);
    int timeoutBound = (contextTimeout != null) ? contextTimeout.intValue() : Integer.MAX_VALUE;
    // d2 request timeout can only accept Integer timeout
    requestContext.putLocalAttr(R2Constants.REQUEST_TIMEOUT, Math.min(timeout, timeoutBound));
  }

  private <T> Task<Response<T>> createTaskWithTimeout(final String name, final Request<T> request,
      final RequestContext requestContext, RequestConfig config) {
    ConfigValue<Long> timeout = config.getTimeoutMs();
    if (timeout.getValue() != null && timeout.getValue() > 0 && _d2RequestTimeoutEnabled) {
      // reconcile timeout if request has already set timeout in its request context
      reconcileRequestTimeout(requestContext, timeout.getValue());
    }
    Task<Response<T>> requestTask;
    if (RequestGroup.isBatchable(request, config)) {
      requestTask = createBatchableTask(name, request, requestContext, config);
    } else {
      requestTask = Task.async(name, () -> sendRequest(request, requestContext));
    }
    if (!_d2RequestTimeoutEnabled) {
      return withTimeout(requestTask, timeout);
    } else {
      return withD2Timeout(requestTask, timeout);
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
