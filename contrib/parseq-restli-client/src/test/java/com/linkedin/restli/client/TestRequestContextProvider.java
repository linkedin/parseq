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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.Collection;

import org.testng.annotations.Test;

import com.linkedin.parseq.Task;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.RequestConfigOverridesBuilder;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.client.GreetingsBuilders;

public class TestRequestContextProvider extends ParSeqRestClientIntegrationTest {

  private CapturingRestClient _capturingRestClient;

  @Override
  public ParSeqRestliClientConfig getParSeqRestClientConfig() {
    return new ParSeqRestliClientConfigBuilder()
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
  }

  @Test
  public void testNonBatchableRequest() {
    try {
      GetRequest<Greeting> request = new GreetingsBuilders().get().id(1L).build();
      Task<?> task = _parseqClient.createTask(request);
      runAndWait(getTestClassName() + ".testNonBatchableRequest", task);
      verifyRequestContext(request);
    } finally {
      _capturingRestClient.clearCapturedRequestContexts();
    }
  }

  @Test
  public void testBatchableRequestNotBatched() {
    try {
      setInboundRequestContext(new InboundRequestContextBuilder()
          .setName("withBatching")
          .build());
      GetRequest<Greeting> request = new GreetingsBuilders().get().id(1L).build();
      Task<?> task = _parseqClient.createTask(request);
      runAndWait(getTestClassName() + ".testBatchableRequestNotBatched", task);
      verifyRequestContext(request);
    } finally {
      _capturingRestClient.clearCapturedRequestContexts();
    }
  }

  @Test
  public void testBatchableRequestBatched() {
    try {
      setInboundRequestContext(new InboundRequestContextBuilder()
          .setName("withBatching")
          .build());
      GetRequest<Greeting> request1 = new GreetingsBuilders().get().id(1L).build();
      GetRequest<Greeting> request2 = new GreetingsBuilders().get().id(2L).build();
      Task<?> task = Task.par(_parseqClient.createTask(request1), _parseqClient.createTask(request2));

      runAndWait(getTestClassName() + ".testBatchableRequestBatched", task);

      Collection<RequestContext> contexts = _capturingRestClient.getCapturedRequestContexts().values();
      assertEquals(contexts.size(), 1);
      RequestContext context = contexts.iterator().next();
      assertNotNull(context.getLocalAttr("method"));
      assertEquals(context.getLocalAttr("method"), ResourceMethod.BATCH_GET);

    } finally {
      _capturingRestClient.clearCapturedRequestContexts();
    }
  }

  private void verifyRequestContext(Request<?> request) {
    assertTrue(_capturingRestClient.getCapturedRequestContexts().containsKey(request));
    assertNotNull(_capturingRestClient.getCapturedRequestContexts().get(request).getLocalAttr("method"));
    assertEquals(_capturingRestClient.getCapturedRequestContexts().get(request).getLocalAttr("method"), request.getMethod());
  }

}
