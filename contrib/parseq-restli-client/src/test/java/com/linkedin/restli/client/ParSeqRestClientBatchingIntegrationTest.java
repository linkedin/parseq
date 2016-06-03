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

import org.testng.annotations.Test;

import com.linkedin.parseq.Task;
import com.linkedin.restli.examples.greetings.client.GreetingsBuilders;


public abstract class ParSeqRestClientBatchingIntegrationTest extends ParSeqRestClientIntegrationTest {

  protected abstract boolean expectBatching();

  @Test
  public void testGetRequests() {
    Task<?> task = Task.par(greetingGet(1L), greetingGet(2L));
    runAndWait(getTestClassName() + ".testGetRequests", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
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
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
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
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
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
    assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomHeaderValuesNoBatching() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addHeader("H1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addHeader("H1", "V2").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomHeadersNoBatching", task);
    assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomHeadersNoBatching() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addHeader("H1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addHeader("H2", "V1").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomHeadersNoBatching", task);
    assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithDifferentCustomQueryParamsNoBatching() {

    Task<?> t1 = _parseqClient.createTask(new GreetingsBuilders().get().id(1L)
        .addParam("K1", "V1").build());

    Task<?> t2 = _parseqClient.createTask(new GreetingsBuilders().get().id(2L)
        .addParam("K2", "V1").build());

    Task<?> task = Task.par(t1, t2);

    runAndWait(getTestClassName() + ".testGetRequestsWithDifferentCustomQueryParamsNoBatching", task);
    assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
  }

  @Test
  public void testGetRequestsWithError() {
    Task<String> task = Task.par(toMessage(greetingGet(1L)), toMessage(greetingGet(-1L)).recover(e -> "failed"))
        .map("combine", (x, y) -> x + y);
    runAndWait(getTestClassName() + ".testGetRequestsWithError", task);
    assertEquals(task.get(), "Good morning!failed");
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
    }
  }

  @Test
  public void testBatchGetRequests() {
    Task<?> task = Task.par(greetings(1L, 2L), greetings(3L, 4L));
    runAndWait(getTestClassName() + ".testBatchGetRequests", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
    }
  }

  @Test
  public void testGetAndBatchGetRequests() {
    Task<?> task = Task.par(greetingGet(1L), greetings(2L, 3L));
    runAndWait(getTestClassName() + ".testGetAndBatchGetRequests", task);
    if (expectBatching()) {
      assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
    } else {
      assertFalse(hasTask("greetings batch_get(2)", task.getTrace()));
    }
  }

  @Test
  public void testSingleGetRequestIsNotBatched() {
    Task<?> task = greetingGet(1L);
    runAndWait(getTestClassName() + ".testSingleGetRequestIsNotBatched", task);
    assertFalse(hasTask("greetings batch_get(1)", task.getTrace()));
  }

  @Test
  public void testDuplicateGetRequestIsNotBatched() {
    Task<?> task = Task.par(greetingGet(1L), greetingGet(1L));
    runAndWait(getTestClassName() + ".testDuplicateGetRequestIsNotBatched", task);
    assertFalse(hasTask("greetings batch_get(1)", task.getTrace()));
  }

}
