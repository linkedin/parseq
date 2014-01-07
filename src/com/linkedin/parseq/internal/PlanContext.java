package com.linkedin.parseq.internal;

import com.linkedin.parseq.Cancellable;
import com.linkedin.parseq.DelayedExecutor;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.internal.trace.TraceCapturer;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class PlanContext
{
  /** Unique identifier for this plan. */
  private final long _id;

  /** The engine used to execute this plan. */
  private final Engine _engine;

  /**
   *  An executor that provides two guarantees:
   *
   * 1. Only one task is executed at a time
   * 2. The completion of a task happens-before the execution of the next task
   *
   * For more on the happens-before constraint see the java.util.concurrent
   * package documentation.
   */
  private final Executor _taskExecutor;

  /** Scheduler for running time delayed tasks. */
  private final DelayedExecutor _timerScheduler;

  /** The trace builder for this plan. */
  private final TraceCapturer _traceCapturer;

  private final TaskLogger _taskLogger;

  public PlanContext(final long id,
                     final Engine engine,
                     final Executor taskExecutor,
                     final DelayedExecutor timerScheduler,
                     final TraceCapturer traceCapturer,
                     final TaskLogger taskLogger)
  {
    _id = id;
    _engine = engine;
    _taskExecutor = new SerialExecutor(taskExecutor);
    _timerScheduler = timerScheduler;
    _traceCapturer = traceCapturer;
    _taskLogger = taskLogger;
  }

  public long getId()
  {
    return _id;
  }

  public TraceCapturer getTraceCapturer()
  {
    return _traceCapturer;
  }

  public void execute(Runnable runnable)
  {
    _taskExecutor.execute(runnable);
  }

  public Cancellable schedule(long time, TimeUnit unit, Runnable runnable)
  {
    return _timerScheduler.schedule(time, unit, runnable);
  }

  public Object getEngineProperty(String key)
  {
    return _engine.getProperty(key);
  }

  public TaskLogger getTaskLogger()
  {
    return _taskLogger;
  }
}
