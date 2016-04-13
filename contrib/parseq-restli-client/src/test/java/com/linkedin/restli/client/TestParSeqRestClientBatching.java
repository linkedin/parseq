package com.linkedin.restli.client;

import java.util.Collections;
import java.util.Optional;

import org.testng.annotations.Test;

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
    testGetRequests(this.getClass().getName() + ".testGetRequestsAreBatched", true);
  }

  @Test
  public void testGetRequestsAreBatchedWithError() {
    testGetRequestsWithError(this.getClass().getName() + ".testGetRequestsAreBatchedWithError", true);
  }

  @Test
  public void testBatchGetRequestsAreBatched() {
    testBatchGetRequests(this.getClass().getName() + ".testBatchGetRequestsAreBatched", true);
  }

  @Test
  public void testGetAndBatchGetRequestsAreBatched() {
    testGetAndBatchGetRequests(this.getClass().getName() + ".testGetAndBatchGetRequestsAreBatched", true);
  }

  @Test
  public void testSingleGetRequestIsNotBatched() {
    testSingleGetRequestIsNotBatched(this.getClass().getName() + ".testSingleGetRequestIsNotBatched");
  }

  @Test
  public void testDuplicateGetRequestIsNotBatched() {
    testDuplicateGetRequestIsNotBatched(this.getClass().getName() + ".testDuplicateGetRequestIsNotBatched");
  }
}
