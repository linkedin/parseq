package com.linkedin.parseq.internal;

/**
 * Abstraction over nanoseconds precision clock. Useful for unit testing.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
public interface Clock {

  /**
   *
   * @return the current value of the running Java Virtual Machine's
   * high-resolution time source, in nanoseconds.
   * Default implementation delegates to {@link System#nanoTime()}.
   */
  long nanoTime();

  /**
     * Performs a sleep using nanoseconds as a time unit.
     * Default implementation delegates to {@link Thread#sleep(long, int)}.
     *
     * @param nano the minimum time to sleep. If less than
     * or equal to zero, do not sleep at all.
     * @throws InterruptedException if interrupted while sleeping
   */
  void sleepNano(long nao) throws InterruptedException;

}
