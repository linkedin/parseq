package com.linkedin.parseq.httpclient;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder;
import org.asynchttpclient.Dsl;


public class HttpClient {

  private static final AtomicReference<AsyncHttpClient> _client =
      new AtomicReference<AsyncHttpClient>();

  /**
   * Returns raw http client. If client has not been initialized yet
   * then new client is created with default configuration.
   * @return raw http client
   */
  public static synchronized AsyncHttpClient getAsyncHttpClient() {
    if (_client.get() == null) {
      initialize(new Builder().build());
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
    if (!_client.compareAndSet(null, Dsl.asyncHttpClient(cfg))) {
      throw new RuntimeException("async http client concurrently initialized");
    }
  }

  public static void close() throws IOException {
    if (_client.get() != null) {
      _client.get().close();
    }
  }

  public static WrappedRequestBuilder get(String url) {
    return new WrappedRequestBuilder(getAsyncHttpClient().prepareGet(url), "GET " + url);
  }

  public static WrappedRequestBuilder connect(String url) {
    return new WrappedRequestBuilder(getAsyncHttpClient().prepareConnect(url), "CONNECT " + url);
  }

  public static WrappedRequestBuilder options(String url) {
    return new WrappedRequestBuilder(getAsyncHttpClient().prepareOptions(url), "OPTIONS " + url);
  }

  public static WrappedRequestBuilder head(String url) {
    return new WrappedRequestBuilder(getAsyncHttpClient().prepareHead(url), "HEAD " + url);
  }

  public static WrappedRequestBuilder post(String url) {
    return new WrappedRequestBuilder(getAsyncHttpClient().preparePost(url), "POST " + url);
  }

  public static WrappedRequestBuilder put(String url) {
    return new WrappedRequestBuilder(getAsyncHttpClient().preparePut(url), "PUT " + url);
  }

  public static WrappedRequestBuilder delete(String url) {
    return new WrappedRequestBuilder(getAsyncHttpClient().prepareDelete(url), "DELETE " + url);
  }

}
