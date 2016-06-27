package com.linkedin.parseq.zk.recipes;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.zk.client.ZKClient;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * This class represents multiple zookeeper locks that has to be
 * acquired in an atomic manner. Note that Locks are acquired in
 * the natural ordering of its lockPath to avoid deadlock.
 *
 * @author Ang Xu
 */
public class MultiLocks implements Synchronizable {

  /**
   * an unmodifiable list of {@link ZKLock}s to be acquired in order.
   */
  private final List<ZKLock> _locks;

  public MultiLocks(ZKClient zkClient, String... lockPaths)  {
    List<ZKLock> locks = Arrays.stream(lockPaths)
        .sorted()
        .map(lockPath -> new ZKLock(lockPath, zkClient))
        .collect(Collectors.toList());
    _locks = Collections.unmodifiableList(locks);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T> Task<T> synchronize(Task<T> task, long deadline) {
    for (ZKLock lock : _locks) {
      task = lock.synchronize(task, deadline);
    }
    return task;
  }
}