package com.linkedin.parseq.internal;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.Cancellable;
import com.linkedin.parseq.DelayedExecutor;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.PlanActivityListener;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.internal.SerialExecutor.ActivityListener;
import com.linkedin.parseq.trace.TraceBuilder;


public class PlanContext {

  private static final Logger LOG = LoggerFactory.getLogger(PlanContext.class.getName());

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
  private final SerialExecutor _taskExecutor;

  /** Scheduler for running time delayed tasks. */
  private final DelayedExecutor _timerScheduler;

  private final TaskLogger _taskLogger;

  private final TraceBuilder _relationshipsBuilder;

  public PlanContext(final Engine engine, final Executor taskExecutor, final DelayedExecutor timerExecutor,
      final ILoggerFactory loggerFactory, final Logger allLogger, final Logger rootLogger, final String planClass,
      Task<?> root, final int maxRelationshipsPerTrace, final PlanActivityListener planActivityListener) {
    _id = IdGenerator.getNextId();
    _relationshipsBuilder = new TraceBuilder(maxRelationshipsPerTrace);
    _engine = engine;
    _taskExecutor = new SerialExecutor(taskExecutor, new CancelPlanRejectionHandler(root), new ActivityListener() {
      @Override
      public void deactivated() {
        planActivityListener.onPlanDeactivated(_id);
      }
      @Override
      public void activated() {
        planActivityListener.onPlanActivated(_id);
      }
    });
    _timerScheduler = timerExecutor;
    final Logger planLogger = loggerFactory.getLogger(Engine.LOGGER_BASE + ":planClass=" + planClass);
    _taskLogger = new TaskLogger(_id, root.getId(), allLogger, rootLogger, planLogger);
  }

  public Long getId() {
    return _id;
  }

  public void execute(Runnable runnable) {
    _taskExecutor.execute(runnable);
  }

  public Cancellable schedule(long time, TimeUnit unit, Runnable runnable) {
    return _timerScheduler.schedule(time, unit, runnable);
  }

  public Object getEngineProperty(String key) {
    return _engine.getProperty(key);
  }

  public TaskLogger getTaskLogger() {
    return _taskLogger;
  }

  public TraceBuilder getRelationshipsBuilder() {
    return _relationshipsBuilder;
  }

  private static class CancelPlanRejectionHandler implements RejectedSerialExecutionHandler {
    private final Task<?> _task;

    private CancelPlanRejectionHandler(Task<?> task) {
      _task = task;
    }

    @Override
    public void rejectedExecution(Throwable error) {
      final String msg = "Serial executor loop failed for plan: " + _task.getName();
      final SerialExecutionException ex = new SerialExecutionException(msg, error);
      final boolean wasCancelled = _task.cancel(ex);
      LOG.error(msg + ". The plan was " + (wasCancelled ? "" : "not ") + "cancelled.", ex);
    }
  }
}
