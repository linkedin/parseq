package com.linkedin.parseq.batching;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.Context;
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
 * {@code BatchingStrategy} helps build "batching clients" in ParSeq. "Client" means an object that given {@code K key}
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
 *     <li>Method {@code executeBatch(G group, Batch<K, T> batch)} is invoked for every batch</li>
 *   </ol>
 *   {@code executeBatch(G group, Batch<K, T> batch)} invocations are executed
 *   in the context of their own Task instances with description given by {@code getBatchName(G group, Batch<K, T> batch)}.
 *   Implementation of {@code BatchingStrategy} has to be fast because it is executed sequentially with respect to tasks belonging
 *   to the plan. It means that no other task will be executed until {@code BatchingStrategy} completes. Typically classify(K key)
 *   is a synchronous and fast operation whilst {@code executeBatch(G group, Batch<K, T> batch)} returns quickly and completes
 *   promises asynchronously.
 * </ol>
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 * @param <G> Type of a Group
 * @param <K> Type of a Key
 * @param <T> Type of a Value
 *
 * @see SimpleBatchingStrategy
 * @see TaskBatchingStrategy
 */
public abstract class BatchingStrategy<G, K, T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchingStrategy.class);
  public static final int DEFAULT_MAX_BATCH_SIZE = 1024;

  private final ConcurrentHashMap<Long, GroupBatchBuilder> _batches =
      new ConcurrentHashMap<>();

  private final BatchSizeMetric _batchSizeMetric = new BatchSizeMetric();
  private final BatchAggregationTimeMetric _batchAggregationTimeMetric = new BatchAggregationTimeMetric();

  /**
   * This method returns Task that returns value for a single key allowing this strategy to batch operations.
   * @param desc description of the task
   * @param key key
   * @return Task that returns value for a single key allowing this strategy to batch operations
   */
  public Task<T> batchable(final String desc, final K key) {
    return Task.async(desc, ctx -> {
      final SettablePromise<T> result = Promises.settable();
      final Long planId = ctx.getPlanId();
      final GroupBatchBuilder builder = _batches.computeIfAbsent(planId, k -> new GroupBatchBuilder());
      final G group = classify(key);
      Batch<K, T> fullBatch = builder.add(group, key, ctx.getShallowTraceBuilder(), result);
      if (fullBatch != null) {
        try {
          ctx.run(taskForBatch(group, fullBatch));
        } catch (Throwable t) {
          //we don't care if some of promises have already been completed
          //all we care is that all remaining promises have been failed
          fullBatch.failAll(t);
        }
      }
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
    _batchSizeMetric.record(batch.size());
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(debugInfo(group, batch));
    }
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
        executeBatchWithContext(group, batch, ctx);
      } catch (Throwable t) {
        batch.failAll(t);
      }

      ctx.getShallowTraceBuilder().setSystemHidden(true);

      return result;
    });
  }

  private void runBatch(final PlanContext planContext, G group, final Batch<K, T> batch) {
    try {
      new ContextImpl(planContext, taskForBatch(group, batch)).runTask();
    } catch (Throwable t) {
      //we don't care if some of promises have already been completed
      //all we care is that all remaining promises have been failed
      batch.failAll(t);
    }
  }

  void handleBatch(final PlanContext planContext) {
    final GroupBatchBuilder batchBuilder = _batches.remove(planContext.getId());
    if (batchBuilder != null) {
      batchBuilder.batches().forEach((group, builder) -> runBatch(planContext, group, builder.build()));
    }
  }

  private String debugInfo(G group, Batch<K, T> batch) {
    StringBuilder debugInfo = new StringBuilder("\n");
      debugInfo.append("group: ")
        .append(group)
        .append("\n")
        .append("batch keys: \n");
      batch.keys().forEach(key -> {
        debugInfo.append("    ").append(key).append("\n");
      });
    return debugInfo.toString();
  }


  public BatchSizeMetric getBatchSizeMetric() {
    return _batchSizeMetric;
  }

  public BatchAggregationTimeMetric getBatchAggregationTimeMetric() {
    return _batchAggregationTimeMetric;
  }

  /**
   * This method will be called for every {@code Batch}.
   * Implementation of this method must make sure that all {@code SettablePromise} contained in the {@code Batch}
   * will eventually be resolved - typically asynchronously. Failing to eventually resolve any
   * of the promises may lead to plan that never completes i.e. appears to hung and may lead to
   * a memory leak.
   * @param group group that represents the batch
   * @param batch batch contains collection of {@code SettablePromise} that eventually need to be resolved - typically asynchronously
   */
  public abstract void executeBatch(G group, Batch<K, T> batch);

  protected void executeBatchWithContext(G group, Batch<K, T> batch, Context ctx) {
    executeBatch(group, batch);
  }

  /**
   * Classify the {@code K Key} and by doing so assign it to a {@code G group}.
   * If two keys are classified by the same group then they will belong to the same {@code Batch}.
   * For each batch {@link #executeBatch(Object, Batch)} will be .
   * @param key key to be classified
   * @return Group that represents a batch the key will belong to
   */
  public abstract G classify(K key);

  /**
   * Overriding this method allows specifying maximum batch size for a given group.
   * Default value is {@value #DEFAULT_MAX_BATCH_SIZE}.
   * @param group group for which maximum batch size needs to be decided
   * @return maximum batch size for a given group
   */
  public int maxBatchSizeForGroup(G group) {
    return DEFAULT_MAX_BATCH_SIZE;
  }

  /**
   * Overriding this method allows providing custom name for a batch. Name will appear in the
   * ParSeq trace as a description of the task that executes the batch.
   * @param batch batch to be described
   * @param group group to be described
   * @return name for the batch and group
   */
  public String getBatchName(G group, Batch<K, T> batch) {
    return "batch(" + batch.size() + ")";
  }

  private class GroupBatchBuilder {
    private final Map<G, BatchBuilder<K, T>> _batchesByGroup =
        new HashMap<>();

    /**
     * Adds new entry to a batch specified by a given group and returns
     * that batch if it is full.
     * @return full batch if new entry made batch full or null otherwise
     */
    Batch<K, T> add(G group, K key, ShallowTraceBuilder traceBuilder, SettablePromise<T> promise) {
      BatchBuilder<K, T> builder =
        _batchesByGroup.computeIfAbsent(group, k -> Batch.builder(maxBatchSizeForGroup(group), _batchAggregationTimeMetric));
      //invariant: builder is not full - it is maintained by the fact that max batch size >= 1
      //and that we remove builder from the map after adding to it entry that makes it full
      builder.add(key, traceBuilder, promise);
      if (builder.isFull()) {
        _batchesByGroup.remove(group);
        return builder.build();
      }
      return null;
    }

    Map<G, BatchBuilder<K, T>> batches() {
      return _batchesByGroup;
    }

  }

}
