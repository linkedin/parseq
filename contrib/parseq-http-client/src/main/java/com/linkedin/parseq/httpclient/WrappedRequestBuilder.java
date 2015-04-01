package com.linkedin.parseq.httpclient;

import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.BodyGenerator;
import com.ning.http.client.ConnectionPoolPartitioning;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.client.Param;
import com.ning.http.client.ProxyServer;
import com.ning.http.client.Realm;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import com.ning.http.client.SignatureCalculator;
import com.ning.http.client.cookie.Cookie;
import com.ning.http.client.multipart.Part;
import com.ning.http.client.uri.Uri;

public class WrappedRequestBuilder {

  private final BoundRequestBuilder _delegate;
  private final String _method;

  public WrappedRequestBuilder(BoundRequestBuilder delegate, String method) {
    _delegate = delegate;
    _method = method;
  }

  public int hashCode() {
    return _delegate.hashCode();
  }

  public boolean equals(Object obj) {
    return _delegate.equals(obj);
  }

  public String toString() {
    return _delegate.toString();
  }

  public WrappedRequestBuilder setUri(Uri uri) {
    _delegate.setUri(uri);
    return this;
  }

  public WrappedRequestBuilder addBodyPart(Part part) {
    _delegate.addBodyPart(part);
    return this;
  }

  public WrappedRequestBuilder setInetAddress(InetAddress address) {
    _delegate.setInetAddress(address);
    return this;
  }

  public WrappedRequestBuilder setLocalInetAddress(InetAddress address) {
    _delegate.setLocalInetAddress(address);
    return this;
  }

  public WrappedRequestBuilder addCookie(Cookie cookie) {
    _delegate.addCookie(cookie);
    return this;
  }

  public WrappedRequestBuilder addHeader(String name, String value) {
    _delegate.addHeader(name, value);
    return this;
  }

  public WrappedRequestBuilder addFormParam(String key, String value) {
    _delegate.addFormParam(key, value);
    return this;
  }

  public WrappedRequestBuilder addQueryParam(String name, String value) {
    _delegate.addQueryParam(name, value);
    return this;
  }

  public Request build() {
    return _delegate.build();
  }

  public WrappedRequestBuilder setBody(byte[] data) {
    _delegate.setBody(data);
    return this;
  }

  public WrappedRequestBuilder setBody(InputStream stream) {
    _delegate.setBody(stream);
    return this;
 }

  public WrappedRequestBuilder setContentLength(int length) {
    _delegate.setContentLength(length);
    return this;
  }

  public WrappedRequestBuilder setBody(String data) {
    _delegate.setBody(data);
    return this;
  }

  public WrappedRequestBuilder setHeader(String name, String value) {
    _delegate.setHeader(name, value);
    return this;
  }

  public WrappedRequestBuilder setCookies(Collection<Cookie> cookies) {
    _delegate.setCookies(cookies);
    return this;
  }

  public WrappedRequestBuilder setHeaders(FluentCaseInsensitiveStringsMap headers) {
    _delegate.setHeaders(headers);
    return this;
  }

  public WrappedRequestBuilder setHeaders(Map<String, Collection<String>> headers) {
    _delegate.setHeaders(headers);
    return this;
  }

  public WrappedRequestBuilder addOrReplaceCookie(Cookie cookie) {
    _delegate.addOrReplaceCookie(cookie);
    return this;
  }

  public WrappedRequestBuilder setFormParams(Map<String, List<String>> params) {
    _delegate.setFormParams(params);
    return this;
  }

  public WrappedRequestBuilder setFormParams(List<Param> params) {
    _delegate.setFormParams(params);
    return this;
  }

  public WrappedRequestBuilder setUrl(String url) {
    _delegate.setUrl(url);
    return this;
  }

  public WrappedRequestBuilder setVirtualHost(String virtualHost) {
    _delegate.setVirtualHost(virtualHost);
    return this;
  }

  public void resetCookies() {
    _delegate.resetCookies();
  }

  public void resetQuery() {
    _delegate.resetQuery();
  }

  public WrappedRequestBuilder setSignatureCalculator(SignatureCalculator signatureCalculator) {
    _delegate.setSignatureCalculator(signatureCalculator);
    return this;
  }

  public void resetFormParams() {
    _delegate.resetFormParams();
  }

  public void resetNonMultipartData() {
    _delegate.resetNonMultipartData();
  }

  public void resetMultipartData() {
    _delegate.resetMultipartData();
  }

  public WrappedRequestBuilder setBody(File file) {
    _delegate.setBody(file);
    return this;
  }

  public WrappedRequestBuilder setBody(List<byte[]> data) {
    _delegate.setBody(data);
    return this;
  }

  public WrappedRequestBuilder setBody(BodyGenerator bodyGenerator) {
    _delegate.setBody(bodyGenerator);
    return this;
  }

  public WrappedRequestBuilder addQueryParams(List<Param> params) {
    _delegate.addQueryParams(params);
    return this;
  }

  public WrappedRequestBuilder setQueryParams(Map<String, List<String>> map) {
    _delegate.setQueryParams(map);
    return this;
  }

  public WrappedRequestBuilder setQueryParams(List<Param> params) {
    _delegate.setQueryParams(params);
    return this;
  }

  public WrappedRequestBuilder setProxyServer(ProxyServer proxyServer) {
    _delegate.setProxyServer(proxyServer);
    return this;
  }

  public WrappedRequestBuilder setRealm(Realm realm) {
    _delegate.setRealm(realm);
    return this;
  }

  public WrappedRequestBuilder setFollowRedirects(boolean followRedirects) {
    _delegate.setFollowRedirects(followRedirects);
    return this;
  }

  public WrappedRequestBuilder setRequestTimeout(int requestTimeout) {
    _delegate.setRequestTimeout(requestTimeout);
    return this;
  }

  public WrappedRequestBuilder setRangeOffset(long rangeOffset) {
    _delegate.setRangeOffset(rangeOffset);
    return this;
  }

  public WrappedRequestBuilder setMethod(String method) {
    _delegate.setMethod(method);
    return this;
  }

  public WrappedRequestBuilder setBodyEncoding(String charset) {
    _delegate.setBodyEncoding(charset);
    return this;
  }

  public WrappedRequestBuilder setConnectionPoolKeyStrategy(ConnectionPoolPartitioning connectionPoolKeyStrategy) {
    _delegate.setConnectionPoolKeyStrategy(connectionPoolKeyStrategy);
    return this;
  }

  public Task<Response> task(final String desc) {
    return Task.async(desc, () -> {
      final SettablePromise<Response> result = Promises.settable();
      _delegate.execute(new AsyncCompletionHandler<Response>() {

        @Override
        public Response onCompleted(final Response response) throws Exception {
          result.done(response);
          return response;
        }

        @Override
        public void onThrowable(Throwable t) {
          result.fail(t);
        }

      });
      return result;
    }, false);
  }

  public Task<Response> task() {
    return task(_method);
  }

}
