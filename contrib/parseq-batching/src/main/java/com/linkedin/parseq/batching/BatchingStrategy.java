package com.linkedin.parseq.batching;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.Tasks;
import com.linkedin.parseq.batching.BatchImpl.BatchBuilder;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.parseq.internal.ContextImpl;
import com.linkedin.parseq.internal.PlanContext;
import com.linkedin.parseq.promise.CountDownPromiseListener;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.TraceBuilder;

public abstract class BatchingStrategy<K, T> {

  private final ConcurrentHashMap<Long, BatchBuilder<K, T>> _batches =
      new ConcurrentHashMap<>();

  public Task<T> batchable(final String desc, final K key) {
    return Task.async(desc, ctx -> {
      final SettablePromise<T> result = Promises.settable();
      Long planId = ctx.getPlanId();
      BatchBuilder<K, T> builder = _batches.get(planId);
      if (builder == null) {
        builder = Batch.builder();
        BatchBuilder<K, T> existingBuilder = _batches.putIfAbsent(planId, builder);
        if (existingBuilder != null) {
          builder = existingBuilder;
        }
      }
      builder.add(key, ctx.getShallowTraceBuilder(), result);
      return result;
    });
  }

  private Task<?> taskForBatch(final Batch<K, T> batch) {
    return Task.async("batch", ctx -> {
      final SettablePromise<T> result = Promises.settable();
      final PromiseListener<T> countDownListener =
          new CountDownPromiseListener<T>(batch.size(), result, null);

      //one of the batchable tasks becomes a parent of batch task
      //all other tasks become potential parents
      final TraceBuilder traceBuilder = ctx.getTraceBuilder();
      Relationship rel = Relationship.PARENT_OF;
      for (BatchEntry<T> entry : batch.values()) {
        traceBuilder.addRelationship(rel,
            entry.getShallowTraceBuilder(), ctx.getShallowTraceBuilder());
        rel = Relationship.POTENTIAL_PARENT_OF;
        entry.getPromise().addListener(countDownListener);
      }

      executeBatch(batch);

      return result;
    });
  }

  private Collection<Task<?>> taskForBatches(Collection<Batch<K, T>> batches) {
    return batches.stream().map(this::taskForBatch).collect(Collectors.toList());
  }

  void handleBatch(final PlanContext planContext) {
    final BatchBuilder<K, T> batchBuilder = _batches.remove(planContext.getId());
    if (batchBuilder != null) {
      final Batch<K, T> batch = batchBuilder.build();
      if (batch.size() > 0) {
        try {
          final Collection<Batch<K, T>> batches = split(batch);
          if (batches.size() > 0) {
            taskForBatches(batches).forEach(task -> new ContextImpl(planContext, task).runTask());
          }
        } catch (Throwable t) {
          //we don't care if some of promises have already been completed
          //all we care is that all remaining promises have been failed
          batch.failAllRemaining(t);
        }
      }
    }
  }

  public abstract void executeBatch(Batch<K, T> batch);

  public Collection<Batch<K, T>> split(Batch<K, T> batch) {
    return Collections.singleton(batch);
  }

}
