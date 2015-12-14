package com.linkedin.parseq.example.domain;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface AsyncPersonClient {
  CompletableFuture<Person> get(Long id);
  CompletableFuture<Map<Long, Person>> batchGet(Collection<Long> ids);
}
