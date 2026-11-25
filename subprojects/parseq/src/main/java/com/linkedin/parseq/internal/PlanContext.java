package com.linkedin.parseq.internal;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.After;
import com.linkedin.parseq.Cancellable;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.DelayedExecutor;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.TraceBuilder;


public class PlanContext {

  /**
   * Shared builder and empty trace for plans with tracing disabled.
   */
  public static final TraceBuilder DISABLED_TRACE_BUILDER = new TraceBuilder(0, "none", 0L);

  private static final Context DISABLED_CANCEL_CONTEXT = new Context() {
    @Override public Cancellable createTimer(long time, TimeUnit unit, Task<?> task) { throw new UnsupportedOperationException(); }
    @Override public void run(Task<?>... tasks) { throw new UnsupportedOperationException(); }
    @Override public void runSideEffect(Task<?>... tasks) { throw new UnsupportedOperationException(); }
    @Override public After after(Promise<?>... promises) { throw new UnsupportedOperationException(); }
    @Override public Object getEngineProperty(String key) { return null; }
    @Override public ShallowTraceBuilder getShallowTraceBuilder() { return null; }
    @Override public TraceBuilder getTraceBuilder() { return DISABLED_TRACE_BUILDER; }
    @Override public Long getPlanId() { return 0L; }
    @Override public String getPlanClass() { return "none"; }
    @Override public Long getTaskId() { return null; }
    @Override public TaskLogger getTaskLogger() { return null; }
  };

  /**
   * Cancels a task belonging to a plan with tracing disabled, binding it to
   * {@link #DISABLED_TRACE_BUILDER} and freezing its metadata builder once done.
   */
  public static boolean cancelDisabled(final Task<?> task, final Exception reason) {
    final boolean cancelled = task.cancel(reason);
    if (task.isDone()) {
      task.contextRun(DISABLED_CANCEL_CONTEXT, null, Collections.emptyList());
    }
    return cancelled;
  }

  private static final Logger LOG = LoggerFactory.getLogger(PlanContext.class.getName());

  /** Unique identifier for this plan. */
  private final Long _id;

  /** The engine used to execute this plan. */
  private final Engine _engine;

  private final String _planClass;

  private final Task<?> _root;

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

  private final PlanCompletionListener _planCompletionListener;

  /** The number of uncompleted plans forked from this plan (including itself). */
  private final AtomicInteger _pending;

  public PlanContext(final Engine engine, final Executor taskExecutor, final DelayedExecutor timerExecutor,
      final ILoggerFactory loggerFactory, final Logger allLogger, final Logger rootLogger, final String planClass,
      Task<?> root, final int maxRelationshipsPerTrace, final PlanDeactivationListener planDeactivationListener,
      PlanCompletionListener planCompletionListener, final SerialExecutor.TaskQueue<PrioritizableRunnable> taskQueue,
      final boolean drainSerialExecutorQueue, ExecutionMonitor executionMonitor) {
    _id = IdGenerator.getNextId();
    _root = root;
    _relationshipsBuilder = maxRelationshipsPerTrace == 0 ? DISABLED_TRACE_BUILDER
        : new TraceBuilder(maxRelationshipsPerTrace, planClass, _id);
    _engine = engine;
    _taskExecutor = new SerialExecutor(taskExecutor, new CancellingPlanExceptionHandler(root, _relationshipsBuilder), () -> {
      try {
        planDeactivationListener.onPlanDeactivated(PlanContext.this);
      } catch (Throwable t) {
        LOG.error("Failed to notify deactivation listener " + planDeactivationListener, t);
      }
    }, taskQueue, drainSerialExecutorQueue, executionMonitor);
    _timerScheduler = timerExecutor;
    final Logger planLogger = loggerFactory.getLogger(Engine.LOGGER_BASE + ":planClass=" + planClass);
    _taskLogger = new TaskLogger(_id, root.getId(), allLogger, rootLogger, planLogger);
    _planClass = planClass;
    _planCompletionListener = planCompletionListener;
    _pending = new AtomicInteger(1);
    _root.addListener(p -> done());
  }

  private PlanContext(Task<?> root,
      Long id,
      Engine engine,
      SerialExecutor serialExecutor,
      DelayedExecutor scheduler,
      String planClass,
      TaskLogger taskLogger,
      TraceBuilder relationshipsBuilder,
      PlanCompletionListener planCompletionListener) {
    _root = root;
    _id = id;
    _engine = engine;
    _taskExecutor = serialExecutor;
    _timerScheduler = scheduler;
    _planClass = planClass;
    _taskLogger = taskLogger;
    _relationshipsBuilder = relationshipsBuilder;
    _planCompletionListener = planCompletionListener;
    _pending = new AtomicInteger(1);
    _root.addListener(p -> done());
  }

  public Long getId() {
    return _id;
  }

  public void execute(PrioritizableRunnable runnable) {
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

  public String getPlanClass() {
    return _planClass;
  }

  public Task<?> getRootTask() {
    return _root;
  }

  /**
   * Creates a new {@link PlanContext} from the current plan for the given root
   * {@link Task}. The new plan can be completed independently from the current plan.
   *
   * @param root root task of the sub plan
   * @return a new PlanContext if this plan is not completed. Otherwise, returns null.
   */
  public PlanContext fork(Task<?> root) {
    int pending;
    while ((pending = _pending.get()) > 0) {
      if (_pending.compareAndSet(pending, pending + 1)) {
        return new PlanContext(root, _id, _engine, _taskExecutor,
            _timerScheduler, _planClass, _taskLogger, _relationshipsBuilder, p -> done());
      }
    }
    return null;
  }

  /**
   * Decrements the pending count by 1. Invokes {@link #_planCompletionListener}
   * if there is no more pending (sub-)plans.
   */
  private void done() {
    if (_pending.decrementAndGet() == 0) {
      _planCompletionListener.onPlanCompleted(this);
    }
  }

  private static class CancellingPlanExceptionHandler implements UncaughtExceptionHandler {
    private final Task<?> _task;
    private final TraceBuilder _traceBuilder;

    private CancellingPlanExceptionHandler(Task<?> task, TraceBuilder traceBuilder) {
      _task = task;
      _traceBuilder = traceBuilder;
    }

    @Override
    public void uncaughtException(Throwable error) {
      final String msg = "Serial executor loop failed for plan: " + _task.getName();
      final SerialExecutionException ex = new SerialExecutionException(msg, error);
      final boolean wasCancelled = _traceBuilder.isEnabled()
          ? _task.cancel(ex)
          : cancelDisabled(_task, ex);
      LOG.error(msg + ". The plan was " + (wasCancelled ? "" : "not ") + "cancelled.", ex);
    }

  }
}
