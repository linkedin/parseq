package com.linkedin.parseq.batching;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.internal.ContextImpl;
import com.linkedin.parseq.internal.PlanContext;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.ShallowTraceBuilder;

public abstract class BatchingStrategy<K, G> {

  private final ConcurrentHashMap<Long, List<InternalBatchEntry<K, Object>>> batches =
      new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  public <T> Task<T> batchable(final String desc, final Callable<BatchEntry<K, T>> callable) {
    return Task.async("batchable: " + desc, ctx -> {
      BatchEntry<K, T> batchEntry = callable.call();

      Long planId = ctx.getPlanId();
      List<InternalBatchEntry<K, Object>> list = batches.get(planId);
      if (list == null) {
        list = new ArrayList<>();
        List<InternalBatchEntry<K, Object>> existing = batches.putIfAbsent(planId, list);
        if (existing != null) {
          list = existing;
        }
      }
      list.add((InternalBatchEntry<K, Object>) new InternalBatchEntry<>(ctx.getShallowTraceBuilder(),
          batchEntry.getKey(), batchEntry.getPromise()));

      return batchEntry.getPromise();
    });
  }

  void handleBatch(final PlanContext planContext) {
    final List<InternalBatchEntry<K, Object>> list = batches.remove(planContext.getId());
    if (list != null) {
      try {
        list.stream().collect(Collectors.groupingBy(entry -> groupForKey(entry.getKey())))
          .forEach((group, batch) -> handleGroupBatch(group, batch, planContext));
      } catch (Throwable t) {
        failAll(list, t);
      }
    }
  }

  private void failAll(final List<InternalBatchEntry<K, Object>> list, final Throwable t) {
    for (InternalBatchEntry<K, Object> entry: list) {
      entry.getPromise().fail(t);
    }
  }

  void handleGroupBatch(final G group, final List<InternalBatchEntry<K, Object>> batch, final PlanContext planContext) {
    try {
      Task<?> task = taskForBatch(group, batch);
      //one of the batchable tasks becomes a parent of batch task
      //all other tasks become potential parents
      Relationship rel = Relationship.PARENT_OF;
      for (InternalBatchEntry<K, ?> entry : batch) {
        planContext.getRelationshipsBuilder().addRelationship(rel,
            entry.getShallowTraceBuilder(), task.getShallowTraceBuilder());
        rel = Relationship.POTENTIAL_PARENT_OF;
      }
      new ContextImpl(planContext, task).runTask();
    } catch (Throwable t) {
      failAll(batch, t);
    }
  }

  public abstract G groupForKey(K key);

  public abstract Task<?> taskForBatch(G group, List<? extends BatchEntry<K, Object>> batch);

  private static class InternalBatchEntry<K, T> extends BatchEntry<K, T> {

    private final ShallowTraceBuilder _shallowTraceBuilder;

    public InternalBatchEntry(ShallowTraceBuilder shallowTraceBuilder, K key, SettablePromise<T> promise) {
      super(key, promise);
      _shallowTraceBuilder = shallowTraceBuilder;
    }

    public ShallowTraceBuilder getShallowTraceBuilder() {
      return _shallowTraceBuilder;
    }

  }
}
