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

/**
 * BatchingStrategy helps building "batching clients" in ParSeq. "Client" means on object that given {@code K key}
 * provides a task that returns {@code T value}. "Batching" means that it can group together keys to resolve values
 * in batches. The benefit of this approach is that batching happens transparently in the background and user's code
 * does not have to deal with logic needed to implement batching.
 * <p>
 * Example of a batching client might be ParSeq client for a key-value store that provides batch get operation. For
 * the sake of simplicity of the example we are using dummy, synchronous key-value store interface:
 * <blockquote><pre>
 *  interface KVStore {
 *
 *    String get(Long key);
 *
 *    Map{@code <Long, String>} batchGet(Collection{@code <Long>} keys);
 *  }
 * </pre></blockquote>
 *
 * We can then implement a {@code BatchingStrategy} in the following way:
 * <blockquote><pre>
 *  public static class BatchingKVStoreClient extends BatchingStrategy{@code <Integer, Long, String>} {
 *    private final KVStore _store;
 *
 *    public BatchingKVStoreClient(KVStore store) {
 *      _store = store;
 *    }
 *
 *    {@code @Override}
 *    public void executeBatch(Integer group, Batch{@code <Long, String>} batch) {
 *      Map{@code <Long, String>} batchResult = _store.batchGet(batch.keys());
 *      batch.foreach((key, promise) {@code ->} promise.done(batchResult.get(key)));
 *    }
 *
 *    {@code @Override}
 *    public void executeSingleton(Integer group, Long key, BatchEntry{@code <String>} entry) {
 *      entry.getPromise().done(_store.get(key));
 *    }
 *
 *    {@code @Override}
 *    public Integer classify(Long entry) {
 *      return 0;
 *    }
 *  }
 * </pre></blockquote>
 *
 * In above example there is an assumption that all keys can be grouped together. This is why method {@code classify()}
 * trivially returns a constant {@code 0}. In practice {@code classify()} returns an equivalence class. All keys that
 * returns equal equivalence class will constitute a batch.
 * <p>
 * The interaction between ParSeq and BatchingStrategy is the following:
 * <ol>
 *   <li>{@code batchable(String desc, K key)} is invoked to create Task instance</li>
 *   <li>Plan is started by {@code Engine.run()}</li>
 *   <li>When Task returned by {@code batchable(String desc, K key)} is started, the key {@code K} is remembered by a BatchingStrategy</li>
 *   <li>When Plan can't make immediate progress BatchingStrategy will be invoked to run batchable operations:
 *   <ol>
 *     <li>Every {@code K key} is classified using {@code classify(K key)} method</li>
 *     <li>Keys, together with adequate Promises, are batched together based on {@code G group} returned by previous step</li>
 *     <li>If batch contains single element, method {@code executeSingleton(G group, K key, BatchEntry<T> entry)} is invoked for it</li>
 *     <li>If batch contains at least two elements, method {@code executeBatch(G group, Batch<K, T> batch)} is invoked for it</li>
 *   </ol>
 *   Both {@code executeSingleton(G group, K key, BatchEntry<T> entry)} and {@code executeBatch(G group, Batch<K, T> batch)} are invoked
 *   in the context of their own Task instance with description given by {@code getBatchName(G group, Batch<K, T> batch)}.
 * </ol>
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 * @param <G> Type of a Group
 * @param <K> Type of a Key
 * @param <T> Type of a Value
 */
public abstract class BatchingStrategy<G, K, T> {

  private final ConcurrentHashMap<Long, BatchBuilder<K, T>> _batches =
      new ConcurrentHashMap<>();

  /**
   * This method returns Task that returns value for a single key allowing this strategy to batch operations.
   * @param desc description of the task
   * @param key key
   * @return Task that returns value for a single key allowing this strategy to batch operations
   */
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

  /**
   * This method returns Task that returns value for a single key allowing this strategy to batch operations.
   * @param key key
   * @return Task that returns value for a single key allowing this strategy to batch operations
   */
  public Task<T> batchable(final K key) {
    return batchable("taskForKey: " + key.toString(), key);
  }

  private Task<?> taskForBatch(final G group, final Batch<K, T> batch) {
    return Task.async(getBatchName(batch, group), ctx -> {
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
          Entry<K, BatchEntry<T>> entry = batch.entries().iterator().next();
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

  /**
   * Overriding this method allows providing custom name for a batch. Name will appear in the
   * ParSeq trace as a description of the task that executes the batch.
   * @param batch batch
   * @param group group
   * @return name for the batch
   */
  public String getBatchName(Batch<K, T> batch, G group) {
    return "batch(" + batch.size() + ")";
  }

  private Map<G, Batch<K, T>> split(Batch<K, T> batch) {
    return batch.entries().stream()
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
        throw new UnsupportedOperationException();
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
