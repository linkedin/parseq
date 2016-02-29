package com.linkedin.parseq.zk.recipes;

import com.linkedin.parseq.Task;


/**
 * @author Ang Xu
 */
public interface Synchronizable {
  /**
   * Runs the given task while holding this synchronizable.
   *
   * @param task task to run
   * @param deadline the absolute time, in milliseconds, to wait for the synchronizable.
   *
   * @return a new task wrapped with {@link ZKLock#synchronize(Task, long)}
   */
  <T> Task<T> synchronize(Task<T> task, long deadline);
}
