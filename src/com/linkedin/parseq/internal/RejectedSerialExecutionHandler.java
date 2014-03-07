package com.linkedin.parseq.internal;

/**
 * A handler that is invoked if the {@link SerialExecutor}'s execution loop
 * fails during resubmission to the underlying executor.
 */
public interface RejectedSerialExecutionHandler
{
  /**
   * This method is invoked if a {@link SerialExecutor}'s execution loop cannot
   * be resubmitted to the underlying executor.
   *
   * @param error the error that was raised by the underlying executor.
   */
  void rejectedExecution(Throwable error);
}
