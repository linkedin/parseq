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

  private static final Group ALL = new Group();

  @Override
  final public Group classify(K key) {
    return ALL;
  }

  @Override
  final public void executeBatch(Group group, Batch<K, T> batch) {
    executeBatch(batch);
  }

  public abstract void executeBatch(Batch<K, T> batch);

}
