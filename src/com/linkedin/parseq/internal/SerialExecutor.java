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
 * 1. Only one task may be executed at a time
 * 2. The completion of a task happens-before the execution of the next task
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
public class SerialExecutor implements Executor {
  private final Executor _executor;
  private final RejectedSerialExecutionHandler _rejectionHandler;
  private final ExecutorLoop _executorLoop = new ExecutorLoop();
  private final FIFOPriorityQueue<Runnable> _queue = new FIFOPriorityQueue<Runnable>();
  private final AtomicInteger _pendingCount = new AtomicInteger();
  private final DeactivationListener _deactivationListener;

  public SerialExecutor(final Executor executor,
      final RejectedSerialExecutionHandler rejectionHandler,
      final DeactivationListener deactivationListener) {
    ArgumentUtil.requireNotNull(executor, "executor");
    ArgumentUtil.requireNotNull(rejectionHandler, "rejectionHandler" );
    ArgumentUtil.requireNotNull(deactivationListener, "deactivationListener" );

    _executor = executor;
    _rejectionHandler = rejectionHandler;
    _deactivationListener = deactivationListener;
  }

  public void execute(final Runnable runnable) {
    _queue.add(runnable);
    // Guarantee that execution loop is schedule only once.
    if (_pendingCount.getAndIncrement() == 0) {
      tryExecuteLoop();
    }
  }

  private void tryExecuteLoop() {
    try {
      // Executor.execute() method has a memory consistency effect
      // such that actions in a thread prior to submitting a Runnable
      // to an Executor happen-before its execution begins, perhaps in another thread.
      // Above property ensures that the completion of a task happens-before
      // the execution of the next task.
      _executor.execute(_executorLoop);
    } catch (Throwable t) {
      _rejectionHandler.rejectedExecution(t);
    }
  }

  private class ExecutorLoop implements Runnable {
    @Override
    public void run() {
      // This runnable is only scheduled when the queue is non-empty.
      // Memory consistency of concurrent collections is such that actions in a thread
      // prior to placing an object into a collection happen-before actions subsequent
      // to the access or removal of that element from the collection, perhaps in another thread.
      // Above property ensures that the adding task to a queue happens-before
      // executing it. This is relevant in a case when task was added to the plan while
      // another task was being executed.
      final Runnable runnable = _queue.poll();
      try {
        runnable.run();
      } finally {
        // Guarantees that execution loop is scheduled by only one thread.
        if (_pendingCount.decrementAndGet() > 0) {
          tryExecuteLoop();
        } else {
          _deactivationListener.deactivated();
        }
      }
    }
  }

  static interface DeactivationListener {
    void deactivated();
  }
}
