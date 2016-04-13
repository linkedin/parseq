package com.linkedin.restli.client;

import java.util.Collections;
import java.util.Optional;

import org.testng.annotations.Test;

import com.linkedin.restli.client.config.BatchingConfig;
import com.linkedin.restli.client.config.ParSeqRestClientConfig;
import com.linkedin.restli.client.config.ResourceConfig;


public class TestParSeqRestClientNoBatching extends ParSeqRestClientIntegrationTest {

  @Override
  public ParSeqRestClientConfig getParSeqRestClientGonfig() {
    //default batching config is: no batching
    BatchingConfig defaultBatchingConfig = new BatchingConfig(false, 1024, false);
    ResourceConfig defaultResourceConfig = new ResourceConfig(Collections.emptyMap(), Optional.empty(), defaultBatchingConfig);
    return new ParSeqRestClientConfig(Collections.emptyMap(), defaultResourceConfig);
  }

  @Test
  public void testGetRequestsAreBatched() {
    testGetRequests(this.getClass().getName() + ".testGetRequestsAreBatched", false);
  }

  @Test
  public void testGetRequestsAreBatchedWithError() {
    testGetRequestsWithError(this.getClass().getName() + ".testGetRequestsAreBatchedWithError", false);
  }

  @Test
  public void testBatchGetRequestsAreBatched() {
    testBatchGetRequests(this.getClass().getName() + ".testBatchGetRequestsAreBatched", false);
  }

  @Test
  public void testGetAndBatchGetRequestsAreBatched() {
    testGetAndBatchGetRequests(this.getClass().getName() + ".testGetAndBatchGetRequestsAreBatched", false);
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
