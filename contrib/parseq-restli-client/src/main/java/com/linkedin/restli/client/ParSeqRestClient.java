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

import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.ResourceConfig;
import com.linkedin.restli.client.config.ResourceConfigOverrides;
import com.linkedin.restli.client.config.ResourceConfigOverridesBuilder;
import com.linkedin.restli.client.metrics.Metrics;

/**
 * ParSeq rest.li client.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
public interface ParSeqRestClient {

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
  public <T> Promise<Response<T>> sendRequest(final Request<T> request);

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
  public <T> Promise<Response<T>> sendRequest(final Request<T> request, final RequestContext requestContext);

  /**
   * Creates a task that makes rest.li request and returns response.
   *
   * @param request
   * @return Task that returns response
   */
  public <T> Task<Response<T>> createTask(final Request<T> request);

  /**
   * Creates a task that makes rest.li request and returns response.
   *
   * @param request
   * @param requestContext
   * @return Task that returns response
   */
  public <T> Task<Response<T>> createTask(final Request<T> request, final RequestContext requestContext);

  /**
   * Creates a task that makes rest.li request and returns response.
   * <p>
   * Passed in {@code configOverrides} will override any existing configuration. Not all properties need to be set.
   * Use {@link ResourceConfigOverridesBuilder} to create instance of {@link ResourceConfigOverrides}.
   *
   * @param request
   * @param requestContext
   * @param config
   * @return Task that returns response
   */
  public <T> Task<Response<T>> createTask(final Request<T> request, final RequestContext requestContext, final ResourceConfigOverrides configOverrides);

  /**
   * Returns ParSeq rest.li client's metrics.
   * @return metrics
   */
  public Metrics getMetrics();

  /**
   * Returns ResourceConfig for specified Request.
   * @param request
   * @return
   */
  public <T> ResourceConfig getResourceConfig(final Request<T> request);

}
