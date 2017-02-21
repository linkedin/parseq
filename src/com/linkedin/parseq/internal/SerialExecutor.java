/*
 * Copyright 2012 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.parseq.internal;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * An executor that provides the following guarantees:
 * <p>
 * 1. Only one Runnable may be executed at a time
 * 2. The completion of a Runnable happens-before the execution of the next Runnable
 * <p>
 * For more on the happens-before constraint see the {@code java.util.concurrent}
 * package documentation.
 * <p>
 * It is possible for the underlying executor to throw an exception signaling
 * that it is not able to accept new work. For example, this can occur with an
 * executor that has a bounded queue size and an
 * {@link java.util.concurrent.ThreadPoolExecutor.AbortPolicy}. If this occurs
 * the executor will run the {@code rejectionHandler} to signal this failure
 * to a layer that can more appropriate handle this event.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class SerialExecutor {

  /*
   * Below is a proof and description of a mechanism that ensures that
   * "completion of a Runnable happens-before the execution of the next Runnable".
   * Let's call the above proposition P from now on.
   *
   * A Runnable can only be executed by tryExecuteLoop. tryExecuteLoop makes sure that
   * there is a happens-before relationship between the current thread that executes
   * tryExecuteLoop and the execution of the next Runnable. Lets call this property HB.
   *
   * tryExecuteLoop can be invoked in two ways:
   *
   * 1) Recursively from within the ExecutorLoop.run method after completion of a Runnable
   * on a thread belonging to the underlying executor.
   * In this case HB ensures that P is true.
   *
   * 2) Through the SerialExecutor.execute method on an arbitrary thread.
   * There are two cases:
   * a) The submitted Runnable is the first Runnable executed by this SerialExecutor. In this case P is trivially true.
   * b) The submitted Runnable is not the first Runnable executed by this SerialExecutor. In this case the thread that executed
   * the last runnable, after it's completion, must have invoked _pendingCount.decrementAndGet, and got 0. Since the
   * thread executing SerialExecutor.execute invoked _pendingCount.getAndIncrement, and got the value 0, it means
   * that there is a happens-before relationship between the thread completing last Runnable and the current thread
   * executing SerialExecutor.execute. Combined with HB1 this means that P is true.
   */

  private final Executor _executor;
  private final UncaughtExceptionHandler _uncaughtExecutionHandler;
  private final ExecutorLoop _executorLoop = new ExecutorLoop();
  private final TaskQueue<PrioritizableRunnable> _queue;
  private final AtomicInteger _pendingCount = new AtomicInteger();
  private final DeactivationListener _deactivationListener;

  public SerialExecutor(final Executor executor,
      final UncaughtExceptionHandler uncaughtExecutionHandler,
      final DeactivationListener deactivationListener,
      final TaskQueue<PrioritizableRunnable> taskQueue) {
    ArgumentUtil.requireNotNull(executor, "executor");
    ArgumentUtil.requireNotNull(uncaughtExecutionHandler, "uncaughtExecutionHandler" );
    ArgumentUtil.requireNotNull(deactivationListener, "deactivationListener" );

    _executor = executor;
    _uncaughtExecutionHandler = uncaughtExecutionHandler;
    _queue = taskQueue;
    _deactivationListener = deactivationListener;
  }

  public void execute(final PrioritizableRunnable runnable) {
    _queue.add(runnable);
    // Guarantees that execution loop is scheduled only once to the underlying executor.
    // Also makes sure that all memory effects of last Runnable are visible to the next Runnable
    // in case value returned by decrementAndGet == 0.
    if (_pendingCount.getAndIncrement() == 0) {
      tryExecuteLoop();
    }
  }

  /*
   * This method acts as a happen-before relation between current thread and next Runnable that will
   * be executed by this executor because of properties of underlying _executor.execute().
   */
  private void tryExecuteLoop() {
    try {
      _executor.execute(_executorLoop);
    } catch (Throwable t) {
      _uncaughtExecutionHandler.uncaughtException(t);
    }
  }

  private class ExecutorLoop implements Runnable {
    @Override
    public void run() {
      // Entering state:
      // - _queue.size() > 0
      // - _pendingCount.get() > 0
      for (;;) {
        final Runnable runnable = _queue.poll();
        try {
          runnable.run();

          // Deactivation listener is called before _pendingCount.decrementAndGet() so that
          // it does not run concurrently with any other Runnable submitted to this Executor.
          // _pendingCount.get() == 1 means that there are no more Runnables submitted to this
          // executor waiting to be executed. Since _pendingCount can be changed in other threads
          // in is possible to get _pendingCount.get() == 1 and _pendingCount.decrementAndGet() > 0
          // to be true few lines below.
          if (_pendingCount.get() == 1) {
            _deactivationListener.deactivated();
          }
        } catch (Throwable t) {
          _uncaughtExecutionHandler.uncaughtException(t);
        } finally {
          if (_pendingCount.decrementAndGet() == 0) {
            break;
          }
        }
      }
    }
  }

  /**
   * A priority queue which stores runnables to be executed within a {@link SerialExecutor}.
   * The implementation has to make sure runnables are sorted in the descending order based
   * on their priority.
   */
  public interface TaskQueue<T extends Prioritizable> {
    void add(T value);
    T poll();
  }

  /*
   * Deactivation listener is notified when this executor finished executing a Runnable
   * and there are no other Runnables waiting in queue.
   * It is executed sequentially with respect to other Runnables executed by this Executor.
   */
  interface DeactivationListener {
    void deactivated();
  }
}
