package com.linkedin.parseq.batching;

/**
 * A simple {@link BatchingStrategy} that groups all keys into one batch.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 * @param <K> Type of a Key
 * @param <T> Type of a Value
 */
public abstract class SimpleBatchingStrategy<K, T> extends BatchingStrategy<SimpleBatchingStrategy.Group, K, T>{

  static final class Group {
    private Group() {
    }
  }

  static final Group ALL = new Group();

  @Override
  final public Group classify(K key) {
    return ALL;
  }

  @Override
  final public void executeBatch(Group group, Batch<K, T> batch) {
    executeBatch(batch);
  }

  @Override
  final public String getBatchName(Group group, Batch<K, T> batch) {
    return getBatchName(batch);
  }

  /**
   * Overriding this method allows providing custom name for a batch. Name will appear in the
   * ParSeq trace as a description of the task that executes the batch.
   * @param batch batch to be described
   * @return name for the batch
   */
  public String getBatchName(Batch<K, T> batch) {
    return super.getBatchName(ALL, batch);
  }

  /**
   * This method will be called for a {@code Batch}.
   * Implementation of this method must make sure that all {@code SettablePromise} contained in the {@code Batch}
   * will eventually be resolved - typically asynchronously. Failing to eventually resolve any
   * of the promises may lead to plan that never completes i.e. appears to hung and may lead to
   * a memory leak.
   * @param batch batch contains collection of {@code SettablePromise} that eventually need to be resolved - typically asynchronously
   */
  public abstract void executeBatch(Batch<K, T> batch);

}
