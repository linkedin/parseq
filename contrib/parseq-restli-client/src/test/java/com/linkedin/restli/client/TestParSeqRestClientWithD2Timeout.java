/*
 * Copyright 2018 LinkedIn, Inc
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
import com.linkedin.r2.filter.R2Constants;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.RequestConfigOverridesBuilder;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.client.GreetingsBuilders;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class TestParSeqRestClientWithD2Timeout extends ParSeqRestClientIntegrationTest {

  private CapturingRestClient _capturingRestClient;

  @Override
  public ParSeqRestliClientConfig getParSeqRestClientConfig() {
    return new ParSeqRestliClientConfigBuilder()
        .addTimeoutMs("withD2Timeout.*/greetings.*", 5000L)
        .addTimeoutMs("*.*/greetings.GET", 9999L)
        .addTimeoutMs("*.*/greetings.*", 10001L)
        .addTimeoutMs("*.*/*.GET", 10002L)
        .addTimeoutMs("foo.*/greetings.GET", 10003L)
        .addTimeoutMs("foo.GET/greetings.GET", 10004L)
        .addTimeoutMs("foo.ACTION-*/greetings.GET", 10005L)
        .addTimeoutMs("foo.ACTION-bar/greetings.GET", 10006L)
        .addBatchingEnabled("withBatching.*/*.*", true)
        .addMaxBatchSize("withBatching.*/*.*", 3)
        .build();
  }

  @Override
  protected RestClient createRestClient() {
    _capturingRestClient = new CapturingRestClient(null,  null, super.createRestClient());
    return _capturingRestClient;
  }

  private <T> RequestContext createRequestContext(Request<T> request) {
    RequestContext requestContext = new RequestContext();
    requestContext.putLocalAttr("method", request.getMethod());
    return requestContext;
  }

  @Override
  protected void customizeParSeqRestliClient(ParSeqRestliClientBuilder parSeqRestliClientBuilder) {
    parSeqRestliClientBuilder.setRequestContextProvider(this::createRequestContext);
    parSeqRestliClientBuilder.setD2RequestTimeoutEnabled(true);
  }

  @Test
  public void testConfiguredD2TimeoutOutboundOverride() {
    Task<?> task = greetingGet(1L, new RequestConfigOverridesBuilder().setTimeoutMs(5555L).build());
    runAndWait(getTestClassName() + ".testConfiguredTimeoutOutbound", task);
    assertTrue(hasTask("withTimeout 5555ms", task.getTrace()));
  }

  @Test
  public void testConfiguredD2TimeoutOutboundOp() {
    setInboundRequestContext(new InboundRequestContextBuilder().setName("withD2Timeout").build());
    Task<?> task = greetingDel(9999L).toTry();
    runAndWait(getTestClassName() + ".testConfiguredD2TimeoutOutboundOp", task);
    assertTrue(hasTask("withTimeout 5000ms src: withD2Timeout.*/greetings.*", task.getTrace()));
  }

  @Test
  public void testTimeoutRequest() {
      setInboundRequestContext(new InboundRequestContextBuilder()
          .setName("withD2Timeout")
          .build());
      GetRequest<Greeting> request = new GreetingsBuilders().get().id(1L).build();
      Task<?> task = _parseqClient.createTask(request);
      runAndWait(getTestClassName() + ".testTimeoutRequest", task);
      verifyRequestContext(request, 5000L);
  }

  private void verifyRequestContext(Request<?> request, Long timeout) {
    assertTrue(_capturingRestClient.getCapturedRequestContexts().containsKey(request));
    assertNotNull(_capturingRestClient.getCapturedRequestContexts().get(request).getLocalAttr("method"));
    assertEquals(_capturingRestClient.getCapturedRequestContexts().get(request).getLocalAttr("method"), request.getMethod());
    if (timeout != null) {
      assertNotNull(_capturingRestClient.getCapturedRequestContexts().get(request).getLocalAttr(
          R2Constants.REQUEST_TIMEOUT));
      assertEquals(_capturingRestClient.getCapturedRequestContexts().get(request).getLocalAttr(R2Constants.REQUEST_TIMEOUT),
          Math.min(timeout, Integer.MAX_VALUE));
    }
  }
}