package com.linkedin.parseq.internal;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import com.linkedin.parseq.Cancellable;
import com.linkedin.parseq.DelayedExecutor;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.trace.TraceBuilder;

public class PlanContext
{
  /** Unique identifier for this plan. */
  private final Long _id;

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

  private final TaskLogger _taskLogger;

  private final TraceBuilder _relationshipsBuilder;

  public PlanContext(Engine engine, Executor taskExecutor, DelayedExecutor timerExecutor, ILoggerFactory loggerFactory,
      Logger allLogger, Logger rootLogger, String planClass, Long rootId, int maxRelationshipsPerTrace) {
    _id = IdGenerator.getNextId();
    _relationshipsBuilder = new TraceBuilder(maxRelationshipsPerTrace);
    _engine = engine;
    _taskExecutor = taskExecutor;
    _timerScheduler = timerExecutor;
    final Logger planLogger = loggerFactory.getLogger(Engine.LOGGER_BASE + ":planClass=" + planClass);
    _taskLogger = new TaskLogger(_id, rootId, allLogger, rootLogger, planLogger);
  }

  public Long getId()
  {
    return _id;
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

  public TraceBuilder getRelationshipsBuilder() {
    return _relationshipsBuilder;
  }

}
