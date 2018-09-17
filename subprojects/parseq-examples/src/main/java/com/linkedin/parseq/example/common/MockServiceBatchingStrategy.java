package com.linkedin.parseq.example.common;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.parseq.batching.BatchingStrategy;

public class MockServiceBatchingStrategy<RES> extends BatchingStrategy<Integer, MockRequest<RES>, RES> {

  protected final ScheduledExecutorService _scheduler;

  public MockServiceBatchingStrategy(ScheduledExecutorService scheduler) {
    _scheduler = scheduler;
  }

  @Override
  public void executeBatch(Integer group, Batch<MockRequest<RES>, RES> batch) {
    long maxLatency = batch.keys().stream().mapToLong(MockRequest::getLatency).max().getAsLong();
    _scheduler.schedule(() -> {
      try {
        batch.foreach((req, promise) -> {
          try {
            promise.done(req.getResult());
          } catch (Exception e) {
            promise.fail(e);
          }
        });
      } catch (Exception e) {
        batch.failAll(e);
      }
    }, maxLatency, TimeUnit.MILLISECONDS);
  }

  @Override
  public Integer classify(MockRequest<RES> key) {
    return 0;
  }
}
