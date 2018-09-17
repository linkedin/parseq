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

import static org.testng.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;

import com.linkedin.data.DataMap;
import com.linkedin.parseq.Task;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.internal.client.response.BatchEntityResponse;

public class TestRequest404WithBatching extends ParSeqRestClientIntegrationTest {

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

  private Object remove404(Object o) {
    if (o instanceof Response) {
      Response r = (Response) o;
      Object entity = r.getEntity();
      if (entity instanceof BatchEntityResponse) {
        BatchEntityResponse ber = (BatchEntityResponse) entity;
        DataMap data = ber.data();
        DataMap errors = (DataMap) data.getDataMap("errors");
        Set<String> keys = new HashSet<>(errors.keySet());
        keys.forEach(key -> {
          DataMap error = errors.getDataMap(key);
          if (error.getInteger("status").equals(404)) {
            errors.remove(key);
          }
        });
      }
    }
    return o;
  }

  @Override
  protected RestClient createRestClient() {
    _capturingRestClient = new CapturingRestClient(null,  null, super.createRestClient(), this::remove404);
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
  public void testBatchGet404() {
    try {
      setInboundRequestContext(new InboundRequestContextBuilder()
          .setName("withBatching")
          .build());
      Task<?> task = Task.par(greetingGet(1L).map(Response::getEntity).map(Greeting::getMessage),
                              greetingGet(2L).map(Response::getEntity).map(Greeting::getMessage),
                              greetingGet(400L).map(Response::getEntity).map(Greeting::getMessage).recover(t -> t.toString()))
          .map((a, b, c) -> a + b + c);
      runAndWait(getTestClassName() + ".testBatchGet404", task);

      assertTrue(task.get().toString().contains("Good morning!"));
      assertTrue(task.get().toString().contains("Guten Morgen!"));
      assertTrue(task.get().toString().contains("com.linkedin.restli.client.RestLiResponseException: Response status 404"));
    } finally {
      clearInboundRequestContext();
    }
  }

}
