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
 */
public class SerialExecutor implements Executor {
  private final Executor _executor;
  private final RejectedSerialExecutionHandler _rejectionHandler;
  private final ExecutorLoop _executorLoop = new ExecutorLoop();
  private final FIFOPriorityQueue<Runnable> _queue = new FIFOPriorityQueue<Runnable>();
  private final AtomicInteger _pendingCount = new AtomicInteger();
  private final ActivityListener _activityHandler;

  public SerialExecutor(final Executor executor,
      final RejectedSerialExecutionHandler rejectionHandler,
      final ActivityListener activityHandler) {
    ArgumentUtil.requireNotNull(executor, "executor");
    ArgumentUtil.requireNotNull(rejectionHandler, "rejectionHandler" );
    ArgumentUtil.requireNotNull(activityHandler, "activityHandler" );

    _executor = executor;
    _rejectionHandler = rejectionHandler;
    _activityHandler = activityHandler;
  }

  public void execute(final Runnable runnable) {
    _queue.add(runnable);
    if (_pendingCount.getAndIncrement() == 0) {
      tryExecuteLoop();
    }
  }

  private void tryExecuteLoop() {
    try {
      _executor.execute(_executorLoop);
    } catch (Throwable t) {
      _rejectionHandler.rejectedExecution(t);
    }
  }

  private class ExecutorLoop implements Runnable {

    private boolean _active = false;

    @Override
    public void run() {
      if (!_active) {
        _active = true;
        _activityHandler.activated();
      }
      // This runnable is only scheduled when the queue is non-empty.
      final Runnable runnable = _queue.poll();

      // This seemingly unnecessary call ensures that we have full visibility
      // of any changes that occurred since the last task executed. It, in
      // combination with _pendingCount.decrementAndGet() below, effectively
      // causes this code to have memory consistency similar to entering and
      // exiting a monitor without the associated mutual exclusion. We need
      // this because there is no other happens-before guarantee when a new
      // task is added while this executor is currently processing a task.
      _pendingCount.get();
      try {
        runnable.run();
      } finally {
        if (_pendingCount.get() == 1) {
          _active = false;
          _activityHandler.deactivated();
        }
        // In addition to its obvious use, this CAS operation acts like an
        // exit from a monitor for memory consistency purposes. See the note
        // above for more details.
        if (_pendingCount.decrementAndGet() > 0) {
          tryExecuteLoop();
        }
      }
    }
  }

  public static interface ActivityListener {

    void activated();

    void deactivated();

  }
}
