package com.linkedin.restli.client;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.ParSeqRestClientConfig;
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
   *
   * @param request
   * @param requestContext
   * @param config
   * @return Task that returns response
   */
  public <T> Task<Response<T>> createTask(final Request<T> request, final RequestContext requestContext, ParSeqRestClientConfig config);

  /**
   * Returns ParSeq rest.li client's metrics.
   * @return metrics
   */
  public Metrics getMetrics();

}
