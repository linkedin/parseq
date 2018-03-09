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
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import org.testng.annotations.Test;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.Tuple2Task;
import com.linkedin.restli.client.config.RequestConfigOverrides;
import com.linkedin.restli.examples.greetings.api.Greeting;
import com.linkedin.restli.examples.greetings.api.Message;
import com.linkedin.restli.examples.greetings.client.GreetingsBuilders;


public abstract class ParSeqRestClientBatchingIntegrationTest extends ParSeqRestClientIntegrationTest {

  protected abstract boolean expectBatching();

  protected abstract boolean expectBatchingOverrides();

  protected abstract RequestConfigOverrides overrides();

  @Test
  public void testGetRequests() {
    Task<?> task = Task.par(greetingGet(1L), greetingGet(2L));
    runAndWait(getTestClassName() + ".testGetRequests", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    }
  }

  @Test
  public void testGetSubResourceRequests() {
    Tuple2Task<Response<Message>,Response<Message>> task = Task.par(associationsGet("a", "b", "x"), associationsGet("a", "b", "y"));
    if (expectBatching()) {
      runAndWaitException(task, RestLiResponseException.class);
      assertTrue(task.getError().getCause().getMessage().contains("associationsSub?ids=List(x,y)"));
    } else {
      runAndWait(getTestClassName() + ".testGetSubResourceRequests", task);
      assertEquals(task.get()._1().getEntity().getMessage(), "b");
      assertEquals(task.get()._1().getEntity().getId(), "a");
      assertEquals(task.get()._2().getEntity().getMessage(), "b");
      assertEquals(task.get()._2().getEntity().getId(), "a");
    }
  }

  @Test
  public void testGetRequestsOverrides() {
    Task<?> task = Task.par(greetingGet(1L, overrides()), greetingGet(2L, overrides()));
    runAndWait(getTestClassName() + ".testGetRequestsOverrides", task);
    if (expectBatchingOverrides()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    }
  }

  @Test
  public void testGetSubResourceRequestsOverrides() {
    Tuple2Task<Response<Message>,Response<Message>> task = Task.par(associationsGet("a", "b", "x", overrides()), associationsGet("a", "b", "y", overrides()));
    if (expectBatchingOverrides()) {
      runAndWaitException(task, RestLiResponseException.class);
      assertTrue(task.getError().getCause().getMessage().contains("associationsSub?ids=List(x,y)"));
    } else {
      runAndWait(getTestClassName() + ".testGetSubResourceRequestsOverrides", task);
      assertEquals(task.get()._1().getEntity().getMessage(), "b");
      assertEquals(task.get()._1().getEntity().getId(), "a");
      assertEquals(task.get()._2().getEntity().getMessage(), "b");
      assertEquals(task.get()._2().getEntity().getId(), "a");
    }
  }

  @Test
  public void testGetRequestsWithSameCustomHeaders() {
    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addHeader("H1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addHeader("H1", "V1").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithSameCustomHeaders", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    }
  }

  @Test
  public void testGetRequestsWithSameCustomHeadersOverrides() {
    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addHeader("H1", "V1").build(), overrides());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addHeader("H1", "V1").build(), overrides());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithSameCustomHeadersOverrides", task);
    if (expectBatchingOverrides()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    }
  }

  @Test
  public void testGetRequestsWithSameQueryParams() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addParam("K1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addParam("K1", "V1").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithSameQueryParams", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    }
  }

  @Test
  public void testGetRequestsWithSameQueryParamsOverrides() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addParam("K1", "V1").build(), overrides());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addParam("K1", "V1").build(), overrides());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithSameQueryParamsOverrides", task);
    if (expectBatchingOverrides()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    }
  }

  @Test
  public void testGetRequestsWithDifferentCustomQueryParamValuesNoBatching() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addParam("K1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addParam("K1", "V2").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomQueryParamValuesNoBatching", task);
    assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomQueryParamValuesNoBatchingOverrides() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addParam("K1", "V1").build(), overrides());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addParam("K1", "V2").build(), overrides());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomQueryParamValuesNoBatchingOverrides", task);
    assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomHeaderValuesNoBatching() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addHeader("H1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addHeader("H1", "V2").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomHeadersNoBatching", task);
    assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomHeaderValuesNoBatchingOverrides() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addHeader("H1", "V1").build(), overrides());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addHeader("H1", "V2").build(), overrides());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomHeaderValuesNoBatchingOverrides", task);
    assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomHeadersNoBatching() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addHeader("H1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addHeader("H2", "V1").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomHeadersNoBatching", task);
    assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomHeadersNoBatchingOverrides() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addHeader("H1", "V1").build(), overrides());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addHeader("H2", "V1").build(), overrides());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomHeadersNoBatchingOverrides", task);
    assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomQueryParamsNoBatching() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addParam("K1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addParam("K2", "V1").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomQueryParamsNoBatching", task);
    assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomQueryParamsNoBatchingOverrides() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addParam("K1", "V1").build(), overrides());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addParam("K2", "V1").build(), overrides());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomQueryParamsNoBatchingOverrides", task);
    assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithError() {
    Task<String> task = Task.par(toMessage(greetingGet(1L)), toMessage(greetingGet(-1L)).recover(e -> "failed"))
        .map("combine", (x, y) -> x + y);
    runAndWait(getTestClassName() + ".testGetRequestsWithError", task);
    assertEquals(task.get(), "Good morning!failed");
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    }
  }

  @Test
  public void testGetRequestsWithErrorOverrides() {
    Task<String> task = Task.par(toMessage(greetingGet(1L, overrides())), toMessage(greetingGet(-1L, overrides())).recover(e -> "failed"))
        .map("combine", (x, y) -> x + y);
    runAndWait(getTestClassName() + ".testGetRequestsWithErrorOverrides", task);
    assertEquals(task.get(), "Good morning!failed");
    if (expectBatchingOverrides()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 2)", task.getTrace()));
    }
  }

  @Test
  public void testBatchGetRequests() {
    Task<?> task = Task.par(greetings(1L, 2L), greetings(3L, 4L));
    runAndWait(getTestClassName() + ".testBatchGetRequests", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 4)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 4)", task.getTrace()));
    }
  }

  @Test
  public void testBatchGetRequestsWithProjection() {
    Task<?> task = Task.par(greetingsWithProjection(Arrays.asList(Greeting.fields().tone()), 1L, 2L),
        greetingsWithProjection(Arrays.asList(Greeting.fields().message()),3L, 4L));
    runAndWait(getTestClassName() + ".testBatchGetRequests", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 4)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 4)", task.getTrace()));
    }
  }

  @Test
  public void testBatchGetRequestsOverrides() {
    Task<?> task = Task.par(greetings(overrides(), 1L, 2L), greetings(overrides(), 3L, 4L));
    runAndWait(getTestClassName() + ".testBatchGetRequestsOverrides", task);
    if (expectBatchingOverrides()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 4)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 4)", task.getTrace()));
    }
  }

  @Test
  public void testGetAndBatchGetRequests() {
    Task<?> task = Task.par(greetingGet(1L), greetings(2L, 3L));
    runAndWait(getTestClassName() + ".testGetAndBatchGetRequests", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 3)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 3)", task.getTrace()));
    }
  }

  @Test
  public void testGetAndBatchGetRequestsWithProjection() {
    Task<?> task = Task.par(greetingGetWithProjection(1L, Greeting.fields().message()), greetingsWithProjection(
        Arrays.asList(Greeting.fields().tone()),2L, 3L));
    runAndWait(getTestClassName() + ".testGetAndBatchGetRequests", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 3)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 3)", task.getTrace()));
    }
  }

  @Test
  public void testGetAndBatchGetRequestsOverrides() {
    Task<?> task = Task.par(greetingGet(1L, overrides()), greetings(overrides(), 2L, 3L));
    runAndWait(getTestClassName() + ".testGetAndBatchGetRequestsOverrides", task);
    if (expectBatchingOverrides()) {
      assertTrue(hasTask("greetings batch_get(reqs: 2, ids: 3)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(reqs: 2, ids: 3)", task.getTrace()));
    }
  }

  @Test
  public void testSingleGetRequestIsNotBatched() {
    Task<?> task = greetingGet(1L);
    runAndWait(getTestClassName() + ".testSingleGetRequestIsNotBatched", task);
    assertFalse(hasTask("greetings batch_get(reqs: 1, ids: 1)", task.getTrace()));
  }

  @Test
  public void testSingleGetRequestIsNotBatchedOverrides() {
    Task<?> task = greetingGet(1L, overrides());
    runAndWait(getTestClassName() + ".testSingleGetRequestIsNotBatchedOverrides", task);
    assertFalse(hasTask("greetings batch_get(reqs: 1, ids: 1)", task.getTrace()));
  }

  @Test
  public void testDuplicateGetRequestIsNotBatched() {
    Task<?> task = Task.par(greetingGet(1L), greetingGet(1L));
    runAndWait(getTestClassName() + ".testDuplicateGetRequestIsNotBatched", task);
    assertFalse(hasTask("greetings batch_get(reqs: 1, ids: 1)", task.getTrace()));
  }

  @Test
  public void testDuplicateGetRequestIsNotBatchedOverrides() {
    Task<?> task = Task.par(greetingGet(1L, overrides()), greetingGet(1L, overrides()));
    runAndWait(getTestClassName() + ".testDuplicateGetRequestIsNotBatchedOverrides", task);
    assertFalse(hasTask("greetings batch_get(reqs: 1, ids: 1)", task.getTrace()));
  }

  @Test
  public void testBatchGetWithProjection() {
    try {
      setInboundRequestContext(new InboundRequestContextBuilder()
          .setName("withBatching")
          .build());
      Task<?> task = Task.par(
          greetingGetWithProjection(1L, Greeting.fields().tone()).map(Response::getEntity).map(Greeting::hasMessage),
          greetingGetWithProjection(2L, Greeting.fields().tone()).map(Response::getEntity).map(Greeting::hasMessage))
          .map((a, b) -> a || b);
      runAndWait(getTestClassName() + ".testBatchGetWithProjection", task);

      assertFalse((Boolean)task.get());
    } finally {
      clearInboundRequestContext();
    }
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
