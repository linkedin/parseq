package com.linkedin.parseq.example.common;

import java.util.concurrent.ScheduledExecutorService;

import com.linkedin.parseq.Task;

public class BatchableMockService<RES> extends MockService<RES> {

  private final MockServiceBatchingStrategy<RES> _strategy;

  public BatchableMockService(ScheduledExecutorService scheduler) {
    super(scheduler);
    _strategy = new MockServiceBatchingStrategy<>(scheduler);
  }

  public MockServiceBatchingStrategy<RES> getStrategy() {
    return _strategy;
  }

  Task<RES> task(String desc, MockRequest<RES> request) {
    return _strategy.batchable(desc, request);
  }

}
