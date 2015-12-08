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
import com.linkedin.parseq.promise.PromiseResolvedException;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.TraceBuilder;

/**
 * {@code BatchingStrategy} helps building "batching clients" in ParSeq. "Client" means on object that given {@code K key}
 * provides a task that returns {@code T value}. "Batching" means that it can group together keys to resolve values
 * in batches. The benefit of this approach is that batching happens transparently in the background and user's code
 * does not have to deal with logic needed to implement batching.
 * <p>
 * Example of a batching client might be ParSeq client for a key-value store that provides batch get operation. For
 * the sake of simplicity of the example we are using dummy, synchronous key-value store interface:
 * <blockquote><pre>
 *  interface KVStore {
 *    String get(Long key);
 *    Map{@code <Long, String>} batchGet(Collection{@code <Long>} keys);
 *  }
 * </pre></blockquote>
 *
 * We can then implement a {@code BatchingStrategy} in the following way:
 * <blockquote><pre>
 *  public static class BatchingKVStoreClient extends BatchingStrategy{@code <Integer, Long, String>} {
 *    private final KVStore _store;
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
 * trivially returns a constant {@code 0}. In practice {@code classify()} returns a group for a key. Keys that have
 * the same group will be batched together.
 * <p>
 * The interaction between ParSeq and {@code BatchingStrategy} is the following:
 * <ol>
 *   <li>{@code batchable(String desc, K key)} is invoked to create Task instance</li>
 *   <li>Plan is started by {@code Engine.run()}</li>
 *   <li>When Task returned by {@code batchable(String desc, K key)} is started, the key {@code K} is remembered by a {@code BatchingStrategy}</li>
 *   <li>When Plan can't make immediate progress {@code BatchingStrategy} will be invoked to run batchable operations:
 *   <ol>
 *     <li>Every {@code K key} is classified using {@code classify(K key)} method</li>
 *     <li>Keys, together with adequate Promises, are batched together based on {@code G group} returned by previous step</li>
 *     <li>If batch contains single element, method {@code executeSingleton(G group, K key, BatchEntry<T> entry)} is invoked for it</li>
 *     <li>If batch contains at least two elements, method {@code executeBatch(G group, Batch<K, T> batch)} is invoked for it</li>
 *   </ol>
 *   Both {@code executeSingleton(G group, K key, BatchEntry<T> entry)} and {@code executeBatch(G group, Batch<K, T> batch)} are invoked
 *   in the context of their own Task instance with description given by {@code getBatchName(G group, Batch<K, T> batch)}.
 *   Implementation of {@code BatchingStrategy} has to be fast because it is executed sequentially with respect to tasks belonging
 *   to the plan. It means that no other task will be executed until {@code BatchingStrategy} completes. Typically classify(K key)
 *   is a synchronous and fast operation whilst {@code executeBatch(G group, Batch<K, T> batch)} and
 *   {@code executeSingleton(G group, K key, BatchEntry<T> entry)} return quickly and complete promises asynchronously.
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
    return Task.async("batch: " + desc, ctx -> {
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
    return batchable("batchableTaskForKey: " + key.toString(), key);
  }

  private Task<?> taskForBatch(final G group, final Batch<K, T> batch) {
    return Task.async(getBatchName(group, batch), ctx -> {
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
        batch.failAll(t);
      }

      ctx.getShallowTraceBuilder().setSystemHidden(true);

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
          batch.failAll(t);
        }
      }
    }
  }

  /**
   * This method will be called for every {@code Batch} that contains at least two elements.
   * Implementation of this method must make sure that all {@code SettablePromise} contained in the {@code Batch}
   * will eventually be resolved - typically asynchronously. Failing to eventually resolve any
   * of the promises may lead to plan that never completes i.e. appears to hung and may lead to
   * a memory leak.
   * @param group group that represents the batch
   * @param batch batch contains collection of {@code SettablePromise} that eventually need to be resolved - typically asynchronously
   */
  public abstract void executeBatch(G group, Batch<K, T> batch);

  /**
   * This method will be called for every {@code Batch} that contains only one element.
   * Implementation of this method should make sure that {@code SettablePromise} contained in the {@code BatchEntry}
   * will eventually be resolved - typically asynchronously.
   * @param group group that represents the batch
   * @param key key of the single element classified to the given group
   * @param entry entry contains a {@code SettablePromise} that eventually needs to be resolved - typically asynchronously
   */
  public abstract void executeSingleton(G group, K key, BatchEntry<T> entry);

  /**
   * Classify the {@code K Key} and by doing so assign it to a {@code G group}.
   * If two keys are classified by the same group then they will belong to the same {@code Batch}.
   * For each batch either {@link #executeBatch(Object, Batch)} will be called if batch contains at least two elements
   * or {@link #executeSingleton(Object, Object, BatchEntry)} will be called if batch contains only one element.
   * @param key key to be classified
   * @return Group that represents a batch the key will belong to
   */
  public abstract G classify(K key);

  /**
   * Overriding this method allows providing custom name for a batch. Name will appear in the
   * ParSeq trace as a description of the task that executes the batch.
   * @param batch batch to be described
   * @param group group to be described
   * @return name for the batch and group
   */
  public String getBatchName(G group, Batch<K, T> batch) {
    if (batch.size() == 1) {
      return "singleton";
    } else {
      return "batch(" + batch.size() + ")";
    }
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
