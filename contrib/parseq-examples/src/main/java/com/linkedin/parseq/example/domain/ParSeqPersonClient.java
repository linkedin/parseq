package com.linkedin.parseq.example.domain;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.parseq.batching.SimpleBatchingStrategy;

public class ParSeqPersonClient extends SimpleBatchingStrategy<Long, Person> {

  private final AsyncPersonClient _client;

  public ParSeqPersonClient(AsyncPersonClient client) {
    _client = client;
  }

  public Task<Person> get(Long id) {
    return batchable("fetch Person " + id, id);
  }

  @Override
  public void executeBatch(Batch<Long, Person> batch) {
    _client.batchGet(batch.keys()).whenComplete((results, exception) -> {
      if (exception != null) {
        // batch operation failed so we need to fail all promises
        batch.failAll(exception);
      } else {
        // complete promises with values from results
        batch.foreach((key, promise) -> promise.done(results.get(key)));
      }
    });
  }

}
