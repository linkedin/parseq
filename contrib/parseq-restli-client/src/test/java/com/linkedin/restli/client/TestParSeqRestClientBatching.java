package com.linkedin.restli.client;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Collections;
import java.util.Optional;

import org.testng.annotations.Test;

import com.linkedin.parseq.Task;
import com.linkedin.restli.client.config.BatchingConfig;
import com.linkedin.restli.client.config.ParSeqRestClientConfig;
import com.linkedin.restli.client.config.ResourceConfig;


public class TestParSeqRestClientBatching extends ParSeqRestClientIntegrationTest {

  @Override
  public ParSeqRestClientConfig getParSeqRestClientGonfig() {
    //default batching config is: batching
    BatchingConfig defaultBatchingConfig = new BatchingConfig(true, 1024, false);
    ResourceConfig defaultResourceConfig = new ResourceConfig(Collections.emptyMap(), Optional.empty(), defaultBatchingConfig);
    return new ParSeqRestClientConfig(Collections.emptyMap(), defaultResourceConfig);
  }

  @Test
  public void testGetRequestsAreBatched() {
    Task<?> task = Task.par(greeting(1L), greeting(2L));
    runAndWait("testGetRequestsAreBatched", task);
    assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
  }

  @Test
  public void testBatchGetRequestsAreBatched() {
    Task<?> task = Task.par(greetings(1L, 2L), greetings(3L, 4L));
    runAndWait("testBatchGetRequestsAreBatched", task);
    assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
  }

  @Test
  public void testGetAndBatchGetRequestsAreBatched() {
    Task<?> task = Task.par(greeting(1L), greetings(2L, 3L));
    runAndWait("testGetAndBatchGetRequestsAreBatched", task);
    assertTrue(hasTask("greetings batch_get(2)", task.getTrace()));
  }

  @Test
  public void testSingleGetRequestIsNotBatched() {
    Task<?> task = greeting(1L);
    runAndWait("testSingleGetRequestIsBatched", task);
    assertFalse(hasTask("greetings batch_get(1)", task.getTrace()));
  }

  @Test
  public void testDuplicateGetRequestIsNotBatched() {
    Task<?> task = Task.par(greeting(1L), greeting(1L));
    runAndWait("testDuplicateGetRequestIsNotBatched", task);
    assertFalse(hasTask("greetings batch_get(1)", task.getTrace()));
  }

}
