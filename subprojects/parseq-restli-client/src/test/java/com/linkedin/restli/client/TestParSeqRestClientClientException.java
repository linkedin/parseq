package com.linkedin.restli.client;

import com.linkedin.common.callback.Callback;
import com.linkedin.common.util.None;
import com.linkedin.parseq.Task;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.multiplexer.MultiplexedRequest;
import com.linkedin.restli.client.multiplexer.MultiplexedResponse;
import org.testng.annotations.Test;


public class TestParSeqRestClientClientException extends ParSeqRestClientIntegrationTest {

  @Test
  public void testExceptionNotThrownInClosureAndCausesTaskFailure() {
    Task<?> task = greetingGet(1L);

    runAndWaitException(task, ClientException.class);
  }

  @Override
  protected ParSeqRestliClientConfig getParSeqRestClientConfig() {
    return new ParSeqRestliClientConfigBuilder().build();
  }

  @Override
  protected void customizeParSeqRestliClient(ParSeqRestliClientBuilder parSeqRestliClientBuilder) {
    parSeqRestliClientBuilder.setClient(new ExceptionClient());
  }

  private static class ExceptionClient implements Client {
    private ExceptionClient() {

    }

    @Override
    public void shutdown(Callback<None> callback) {

    }

    @Override
    public <T> ResponseFuture<T> sendRequest(Request<T> request, RequestContext requestContext) {
      return null;
    }

    @Override
    public <T> ResponseFuture<T> sendRequest(Request<T> request, RequestContext requestContext,
        ErrorHandlingBehavior errorHandlingBehavior) {
      return null;
    }

    @Override
    public <T> ResponseFuture<T> sendRequest(RequestBuilder<? extends Request<T>> requestBuilder,
        RequestContext requestContext) {
      return null;
    }

    @Override
    public <T> ResponseFuture<T> sendRequest(RequestBuilder<? extends Request<T>> requestBuilder,
        RequestContext requestContext, ErrorHandlingBehavior errorHandlingBehavior) {
      return null;
    }

    @Override
    public <T> void sendRequest(Request<T> request, RequestContext requestContext, Callback<Response<T>> callback) {
      throw new ClientException();
    }

    @Override
    public <T> void sendRequest(RequestBuilder<? extends Request<T>> requestBuilder, RequestContext requestContext,
        Callback<Response<T>> callback) {

    }

    @Override
    public <T> ResponseFuture<T> sendRequest(Request<T> request) {
      return null;
    }

    @Override
    public <T> ResponseFuture<T> sendRequest(Request<T> request, ErrorHandlingBehavior errorHandlingBehavior) {
      return null;
    }

    @Override
    public <T> ResponseFuture<T> sendRequest(RequestBuilder<? extends Request<T>> requestBuilder) {
      return null;
    }

    @Override
    public <T> ResponseFuture<T> sendRequest(RequestBuilder<? extends Request<T>> requestBuilder,
        ErrorHandlingBehavior errorHandlingBehavior) {
      return null;
    }

    @Override
    public <T> void sendRequest(Request<T> request, Callback<Response<T>> callback) {

    }

    @Override
    public <T> void sendRequest(RequestBuilder<? extends Request<T>> requestBuilder, Callback<Response<T>> callback) {

    }

    @Override
    public void sendRequest(MultiplexedRequest multiplexedRequest) {

    }

    @Override
    public void sendRequest(MultiplexedRequest multiplexedRequest, Callback<MultiplexedResponse> callback) {

    }

    @Override
    public void sendRequest(MultiplexedRequest multiplexedRequest, RequestContext requestContext,
        Callback<MultiplexedResponse> callback) {

    }
  }

  private static class ClientException extends RuntimeException {
    ClientException() {
      super("Exception thrown by client.");
    }
  }
}
