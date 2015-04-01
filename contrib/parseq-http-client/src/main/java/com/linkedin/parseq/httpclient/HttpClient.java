package com.linkedin.parseq.httpclient;

import java.util.concurrent.atomic.AtomicReference;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;

public class HttpClient {

  private static final AtomicReference<AsyncHttpClient> _client =
      new AtomicReference<AsyncHttpClient>();

  /**
   * Returns raw http client. If client has not been initialized yet
   * then new client is created with default configuration.
   * @return
   */
  public static synchronized AsyncHttpClient getNingClient() {
    if (_client.get() == null) {
      initialize(new AsyncHttpClientConfig.Builder().build());
    }
    return _client.get();
  }

  /**
   * Initializes HttpClient with custom configuration.
   * @param cfg client configuration
   * @see AsyncHttpClientConfig
   */
  @SuppressWarnings("resource")
  public static synchronized void initialize(AsyncHttpClientConfig cfg) {
    if (!_client.compareAndSet(null, new AsyncHttpClient(cfg))) {
      throw new RuntimeException("async http client concurrently initialized");
    }
  }

  public static void close() {
    if (_client.get() != null) {
      _client.get().close();
    }
  }

  public static WrappedRequestBuilder get(String url) {
    return new WrappedRequestBuilder(getNingClient().prepareGet(url), "GET " + url);
  }

  public static WrappedRequestBuilder connect(String url) {
    return new WrappedRequestBuilder(getNingClient().prepareConnect(url), "CONNECT " + url);
  }

  public static WrappedRequestBuilder options(String url) {
    return new WrappedRequestBuilder(getNingClient().prepareOptions(url), "OPTIONS " + url);
  }

  public static WrappedRequestBuilder head(String url) {
    return new WrappedRequestBuilder(getNingClient().prepareHead(url), "HEAD " + url);
  }

  public static WrappedRequestBuilder post(String url) {
    return new WrappedRequestBuilder(getNingClient().preparePost(url), "POST " + url);
  }

  public static WrappedRequestBuilder put(String url) {
    return new WrappedRequestBuilder(getNingClient().preparePut(url), "PUT " + url);
  }

  public static WrappedRequestBuilder delete(String url) {
    return new WrappedRequestBuilder(getNingClient().prepareDelete(url), "DELETE " + url);
  }

}
