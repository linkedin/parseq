package com.linkedin.parseq;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Global parseq configuration, applies to all Engine and Task instances
 */
public class ParSeqGlobalConfiguration {
  private static final ParSeqGlobalConfiguration _instance = new ParSeqGlobalConfiguration();

  private AtomicBoolean _crossThreadStackTracesEnabled = new AtomicBoolean(false);

  public static ParSeqGlobalConfiguration getInstance() {
    return _instance;
  }

  private ParSeqGlobalConfiguration() {
  }

  /**
   * Returns current state of cross-thread (cross-task) stack tracing.
   *
   * Normally tasks are executed in different thread from the one creating it and at a different time. This makes it
   * hard to debug because if task throws an exception, its call stack ends in the execution engine that actually
   * starts a thread that serves the task. This feature collects stack trace in advance, when task is created, so that
   * if the task throws the exception then the parent stack trace is appended to it. This has a small performance
   * impact even if the task doesn't throw any exception because stack trace is collected in task constructor.
   *
   * @return true if cross-thread stack tracing is enabled, false otherwise
   */
  public boolean isCrossThreadStackTracesEnabled() {
    return _crossThreadStackTracesEnabled.get();
  }

  /**
   * Modifies the current state of cross-thread (cross-task) stack tracing.
   *
   * Normally tasks are executed in different thread from the one creating it and at a different time. This makes it
   * hard to debug because if task throws an exception, its call stack ends in the execution engine that actually
   * starts a thread that serves the task. This feature collects stack trace in advance, when task is created, so that
   * if the task throws the exception then the parent stack trace is appended to it. This has a small performance
   * impact even if the task doesn't throw any exception because stack trace is collected in task constructor.
   *
   * @param enabled true if cross-thread stack tracing is enabled, false otherwise
   */
  public void setCrossThreadStackTracesEnabled(boolean enabled) {
    _crossThreadStackTracesEnabled.set(enabled);
  }
}
