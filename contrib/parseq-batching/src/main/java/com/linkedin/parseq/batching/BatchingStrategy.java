package com.linkedin.parseq.batching;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.BatchImpl.BatchBuilder;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;
import com.linkedin.parseq.internal.ContextImpl;
import com.linkedin.parseq.internal.PlanContext;
import com.linkedin.parseq.promise.CountDownPromiseListener;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.TraceBuilder;

public abstract class BatchingStrategy<G extends Group, K, T> {

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

  private Task<?> taskForBatch(final G group, Batch<K, T> batch) {
    final String batchTaskName = batch.getName();
    return Task.async(batchTaskName != null ? batchTaskName : "batchTask", ctx -> {
      final SettablePromise<T> result = Promises.settable();
      final PromiseListener<T> countDownListener =
          new CountDownPromiseListener<T>(batch.size(), result, null);

      //one of the batchable tasks becomes a parent of batch task
      //all other tasks become potential parents
      final TraceBuilder traceBuilder = ctx.getTraceBuilder();
      Relationship rel = Relationship.PARENT_OF;
      for (BatchEntry<T> entry : batch.values()) {
        for (ShallowTraceBuilder shallowTraceBuilder: entry.getShallowTraceBuilders()) {
          traceBuilder.addRelationship(rel, shallowTraceBuilder, ctx.getShallowTraceBuilder());
          rel = Relationship.POTENTIAL_PARENT_OF;
        }
        entry.getPromise().addListener(countDownListener);
      }

      try {
        if (batch.size() == 1) {
          Entry<K, BatchEntry<T>> entry = batch.entires().iterator().next();
          executeSingleton(group, entry.getKey(), entry.getValue());
        } else {
          executeBatch(group, batch);
        }
      } catch (Throwable t) {
        batch.failAllRemaining(t);
      }

      return result;
    });
  }

  private Collection<Task<?>> taskForBatches(Collection<Entry<G, Batch<K, T>>> batches) {
    return batches.stream().map(entry -> taskForBatch(entry.getKey(), entry.getValue())).collect(Collectors.toList());
  }

  void handleBatch(final PlanContext planContext) {
    final BatchBuilder<K, T> batchBuilder = _batches.remove(planContext.getId());
    if (batchBuilder != null) {
      final Batch<K, T> batch = batchBuilder.build();
      if (batch.size() > 0) {
        try {
          final Map<G, Batch<K, T>> batches = split(batch);
          if (batches.size() > 0) {
            taskForBatches(batches.entrySet()).forEach(task -> new ContextImpl(planContext, task).runTask());
          }
        } catch (Throwable t) {
          //we don't care if some of promises have already been completed
          //all we care is that all remaining promises have been failed
          batch.failAllRemaining(t);
        }
      }
    }
  }

  public abstract void executeBatch(G group, Batch<K, T> batch);

  public abstract void executeSingleton(G group, K key, BatchEntry<T> entry);

  public abstract G classify(K entry);

  public Map<G, Batch<K, T>> split(Batch<K, T> batch) {
    return batch.entires().stream()
        .collect(Collectors.groupingBy(entry -> classify(entry.getKey()), batchCollector()));
  }

  private Collector<Entry<K, BatchEntry<T>>, BatchBuilder<K, T>, Batch<K, T>> batchCollector() {
    return new Collector<Entry<K,BatchEntry<T>>, BatchBuilder<K,T>, Batch<K,T>>() {

      @Override
      public Supplier<BatchBuilder<K, T>> supplier() {
        return Batch::<K, T>builder;
      }

      @Override
      public BiConsumer<BatchBuilder<K, T>, Entry<K, BatchEntry<T>>> accumulator() {
        return (builder, entry) -> builder.add(entry.getKey(), entry.getValue());
      }

      private BatchBuilder<K, T> combine(BatchBuilder<K, T> larger, BatchBuilder<K, T> smaller) {
        return null;
      }

      @Override
      public BinaryOperator<BatchBuilder<K, T>> combiner() {
        return (a, b) -> {
          if (a.size() > b.size()) {
            return combine(a, b);
          } else {
            return combine(b, a);
          }
        };
      }

      @Override
      public Function<BatchBuilder<K, T>, Batch<K, T>> finisher() {
        return builder -> builder.build();
      }

      @Override
      public Set<Collector.Characteristics> characteristics() {
        return Collections.emptySet();
      }
    };
  }

}
