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

package com.linkedin.parseq;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.internal.IdGenerator;
import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.promise.DelegatingPromise;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.TraceBuilder;


/**
 * An abstract base class that can be used to build implementations of
 * {@link Task}.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public abstract class BaseTask<T> extends DelegatingPromise<T>implements Task<T> {

  private static final int TASK_NAME_MAX_LENGTH = 1024;

  static final Logger LOGGER = LoggerFactory.getLogger(BaseTask.class);
  private static final String CANONICAL_NAME = BaseTask.class.getCanonicalName();

  private static enum StateType {
    // The initial state of the task.
    //
    // A task in this state can be cancelled and have its priority changed.
    INIT,

    // The task is currently executing. That is, a thread is in the run()
    // method for the TaskDef.
    //
    // A task in this state in not cancellable and cannot have its priority
    // changed.
    RUN,

    // The task has finished running, but the result has not yet been set.
    // This occurs for Tasks with AsyncTaskDefs.
    //
    // A task in this state in cancellable, and cannot have its priority
    // changed.
    PENDING,

    // The task is resolved.
    //
    // A task in this state in not cancellable and cannot have its priority
    // changed.
    DONE
  }

  private final Long _id = IdGenerator.getNextId();
  private final AtomicReference<State> _stateRef;
  private final String _name;
  protected final ShallowTraceBuilder _shallowTraceBuilder;

  protected volatile Function<T, String> _traceValueProvider;

  private volatile TraceBuilder _traceBuilder;

  private final Throwable _taskStackTraceHolder;

  /**
   * Constructs a base task without a specified name. The name for this task
   * will be the {@link #toString} representation for this instance. It is
   * usually best to use {@link BaseTask#BaseTask(String)}.
   */
  public BaseTask() {
    this(null);
  }

  /**
   * Constructs a base task with a name.
   *
   * @param name the name for this task.
   */
  public BaseTask(final String name) {
    this(name, null);
  }

  /**
   * Constructs a base task with a name and type of task
   *
   * @param name the name for this task.
   * @param taskType the type of the task
   */
  public BaseTask(final String name, final String taskType) {
    super(Promises.settable());
    _name = truncate(name);
    final State state = State.INIT;
    _shallowTraceBuilder = new ShallowTraceBuilder(_id);
    _shallowTraceBuilder.setName(getName());
    _shallowTraceBuilder.setResultType(ResultType.UNFINISHED);
    if (taskType != null) {
      _shallowTraceBuilder.setTaskType(taskType);
    }
    _stateRef = new AtomicReference<>(state);

    if (ParSeqGlobalConfiguration.isCrossThreadStackTracesEnabled()) {
      _taskStackTraceHolder = new Throwable();
    } else {
      _taskStackTraceHolder = null;
    }
  }

  private String truncate(String name) {
    if (name == null || name.length() <= TASK_NAME_MAX_LENGTH) {
      return name;
    } else {
      return name.substring(0, TASK_NAME_MAX_LENGTH);
    }
  }

  @Override
  public Long getId() {
    return _id;
  }

  @Override
  public int getPriority() {
    return _stateRef.get().getPriority();
  }

  @Override
  public boolean setPriority(final int priority) {
    if (priority < Priority.MIN_PRIORITY || priority > Priority.MAX_PRIORITY) {
      throw new IllegalArgumentException("Priority out of bounds: " + priority);
    }

    State state;
    State newState;
    do {
      state = _stateRef.get();
      if (state.getType() != StateType.INIT) {
        return false;
      }

      newState = new State(state.getType(), priority);
    } while (!_stateRef.compareAndSet(state, newState));

    return true;
  }

  @Override
  public TraceBuilder getTraceBuilder() {
    return _traceBuilder;
  }

  @Override
  public final void contextRun(final Context context, final Task<?> parent, final Collection<Task<?>> predecessors) {
    final TaskLogger taskLogger = context.getTaskLogger();
    final TraceBuilder traceBuilder = context.getTraceBuilder();
    if (transitionRun(traceBuilder)) {
      markTaskStarted();
      final Promise<T> promise;
      try {
        if (parent != null) {
          traceBuilder.addRelationship(Relationship.CHILD_OF, getShallowTraceBuilder(),
              parent.getShallowTraceBuilder());
        }
        for (Task<?> predecessor : predecessors) {
          traceBuilder.addRelationship(Relationship.SUCCESSOR_OF, getShallowTraceBuilder(),
              predecessor.getShallowTraceBuilder());
        }

        taskLogger.logTaskStart(this);
        try {
          final Context wrapperContext = new WrappedContext(context);
          promise = doContextRun(wrapperContext);
        } finally {
          transitionPending();
        }

        promise.addListener(resolvedPromise -> {
          if (resolvedPromise.isFailed()) {
            fail(resolvedPromise.getError(), taskLogger);
          } else {
            done(resolvedPromise.get(), taskLogger);
          }
        } );
      } catch (Throwable t) {
        fail(t, taskLogger);
      }
    } else {
      //this is possible when task was cancelled or has been executed multiple times
      //e.g. task has multiple paths it can be executed with or has been completed by
      //a FusionTask
      if (parent != null) {
        traceBuilder.addRelationship(Relationship.POTENTIAL_CHILD_OF, getShallowTraceBuilder(),
            parent.getShallowTraceBuilder());
      }
      for (Task<?> predecessor : predecessors) {
        traceBuilder.addRelationship(Relationship.POSSIBLE_SUCCESSOR_OF, getShallowTraceBuilder(),
            predecessor.getShallowTraceBuilder());
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Promise<T> doContextRun(final Context context) throws Throwable {
    return (Promise<T>) run(context);
  }

  /**
   * Returns the name of this task. If no name was set during construction
   * this method will return the value of {@link #toString()}. In most
   * cases it is preferable to explicitly set a name.
   *
   * @return the name of this task
   */
  @Override
  public String getName() {
    return _name == null ? toString() : _name;
  }

  @Override
  public boolean cancel(final Exception rootReason) {
    if (transitionCancel(rootReason)) {
      final Exception reason = new CancellationException(rootReason);
      try {
        traceFailure(reason);
      } catch (Throwable ex) {
        LOGGER.warn("Exception thrown in logging trace for failure!", ex);
      } finally {
        // guard any exception that may throw from catch block
        getSettableDelegate().fail(reason);
      }
      return true;
    }
    return false;
  }

  protected void traceFailure(final Throwable reason) {
    if (Exceptions.isEarlyFinish(reason)) {
      _shallowTraceBuilder.setResultType(ResultType.EARLY_FINISH);
    } else {
      _shallowTraceBuilder.setResultType(ResultType.ERROR);
      _shallowTraceBuilder.setValue(Exceptions.failureToString(reason));
    }
  }

  @Override
  public ShallowTraceBuilder getShallowTraceBuilder() {
    return _shallowTraceBuilder;
  }

  @Override
  public ShallowTrace getShallowTrace() {
    return _shallowTraceBuilder.build();
  }

  @Override
  public void setTraceValueSerializer(final Function<T, String> traceValueProvider) {
    _traceValueProvider = traceValueProvider;
  }

  @Override
  public Trace getTrace() {
    TraceBuilder traceBuilder = getTraceBuilder();
    if (traceBuilder != null) {
      return traceBuilder.build();
    } else {
      return Trace.single(getShallowTrace(), "none", 0L);
    }
  }

  /**
   * This template method is invoked when the task is run.
   *
   * @param context the context to use while running this task
   * @return a promise that will have its value set when this task is finished
   * @throws Exception if an error occurs while running the task
   */
  protected abstract Promise<? extends T> run(final Context context) throws Throwable;

  private void traceDone(final T value) {
    _shallowTraceBuilder.setResultType(ResultType.SUCCESS);
    final Function<T, String> traceValueProvider = _traceValueProvider;
    if (traceValueProvider != null) {
      try {
        _shallowTraceBuilder.setValue(traceValueProvider.apply(value));
      } catch (Exception e) {
        _shallowTraceBuilder.setValue(Exceptions.failureToString(e));
      }
    }
  }

  private void done(final T value, final TaskLogger taskLogger) {
    if (transitionDone()) {
      traceDone(value);
      getSettableDelegate().done(value);
      taskLogger.logTaskEnd(BaseTask.this, _traceValueProvider);
    }
  }

  private void fail(final Throwable error, final TaskLogger taskLogger) {
    if (transitionDone()) {
      try {
        appendTaskStackTrace(error);
        traceFailure(error);
      } catch (Throwable ex) {
        LOGGER.warn("Exception thrown in logging trace for failure!", ex);
      } finally {
        // guard any exception that may throw from catch block
        getSettableDelegate().fail(error);
        taskLogger.logTaskEnd(BaseTask.this, _traceValueProvider);
      }
    }
  }

  // Concatenate stack traces if kept the original stack trace from the task creation
  private void appendTaskStackTrace(final Throwable error) {
    StackTraceElement[] taskStackTrace = _taskStackTraceHolder != null ? _taskStackTraceHolder.getStackTrace() : null;

    // At a minimum, any stack trace should have at least 3 stack frames (caller + BaseTask + getStackTrace).
    // So if there are less than 3 stack frames available then there's something fishy and it's better to ignore them.
    if (!ParSeqGlobalConfiguration.isCrossThreadStackTracesEnabled() || error == null ||
        taskStackTrace == null || taskStackTrace.length <= 2) {
      return;
    }

    StackTraceElement[] errorStackTrace = error.getStackTrace();
    if (errorStackTrace.length <= 2) {
      return;
    }

    // Skip stack frames up to the BaseTask (useless java.util.concurrent stuff)
    int skipErrorFrames = 1;
    while (skipErrorFrames < errorStackTrace.length) {
      int index = errorStackTrace.length - 1 - skipErrorFrames;
      if (!errorStackTrace[index].getClassName().equals(CANONICAL_NAME) &&
          errorStackTrace[index + 1].getClassName().equals(CANONICAL_NAME)) {
        break;
      }
      skipErrorFrames++;
    }

    // Safeguard against accidentally removing entire stack trace
    if (skipErrorFrames == errorStackTrace.length) {
      skipErrorFrames = 0;
    }

    // Skip stack frames up to the BaseTask (useless Thread.getStackTrace stuff)
    int skipTaskFrames = 1;
    while (skipTaskFrames < taskStackTrace.length) {
      if (!taskStackTrace[skipTaskFrames].getClassName().equals(CANONICAL_NAME) &&
          taskStackTrace[skipTaskFrames - 1].getClassName().equals(CANONICAL_NAME)) {
        break;
      }
      skipTaskFrames++;
    }

    // Safeguard against accidentally removing entire stack trace
    if (skipTaskFrames == taskStackTrace.length) {
      skipTaskFrames = 0;
    }

    int combinedLength = errorStackTrace.length - skipErrorFrames + taskStackTrace.length - skipTaskFrames;
    if (combinedLength <= 0) {
      return;
    }

    StackTraceElement[] concatenatedStackTrace = new StackTraceElement[combinedLength + 1];
    System.arraycopy(errorStackTrace, 0, concatenatedStackTrace,
        0, errorStackTrace.length - skipErrorFrames);
    concatenatedStackTrace[errorStackTrace.length - skipErrorFrames] =
        new StackTraceElement("********** Task \"" + getName() + "\" (above) was instantiated as following (below): **********", "",null, 0);
    System.arraycopy(taskStackTrace, skipTaskFrames, concatenatedStackTrace,
        errorStackTrace.length - skipErrorFrames + 1, taskStackTrace.length - skipTaskFrames);
    error.setStackTrace(concatenatedStackTrace);
  }

  protected boolean transitionRun(final TraceBuilder traceBuilder) {
    State state;
    State newState;
    do {
      state = _stateRef.get();
      if (state.getType() != StateType.INIT) {
        return false;
      }
      newState = state.transitionRun();
    } while (!_stateRef.compareAndSet(state, newState));
    _traceBuilder = traceBuilder;
    traceBuilder.addShallowTrace(_shallowTraceBuilder);
    return true;
  }

  protected void markTaskStarted() {
    _shallowTraceBuilder.setNativeStartNanos(System.nanoTime());
  }

  protected void transitionPending() {
    State state;
    State newState;
    do {
      state = _stateRef.get();
      if (state.getType() != StateType.RUN) {
        return;
      }
      newState = state.transitionPending();
    } while (!_stateRef.compareAndSet(state, newState));
    markTaskPending();
  }

  protected void markTaskPending() {
    _shallowTraceBuilder.setNativePendingNanos(System.nanoTime());
  }

  protected boolean transitionCancel(final Exception reason) {
    State state;
    State newState;
    do {
      state = _stateRef.get();
      final StateType type = state.getType();
      if (type == StateType.RUN || type == StateType.DONE) {
        return false;
      }
      newState = state.transitionDone();
    } while (!_stateRef.compareAndSet(state, newState));
    return true;
  }

  protected boolean transitionDone() {
    State state;
    State newState;
    do {
      state = _stateRef.get();
      if (state.getType() == StateType.DONE) {
        return false;
      }
      newState = state.transitionDone();
    } while (!_stateRef.compareAndSet(state, newState));
    return true;
  }

  protected SettablePromise<T> getSettableDelegate() {
    return (SettablePromise<T>) super.getDelegate();
  }

  protected static class State {
    private final StateType _type;
    private final int _priority;

    private State(final StateType type, final int priority) {
      _type = type;
      _priority = priority;
    }

    public StateType getType() {
      return _type;
    }

    public int getPriority() {
      return _priority;
    }

    public State transitionRun() {
      return new State(StateType.RUN, _priority);
    }

    public State transitionPending() {
      return new State(StateType.PENDING, _priority);
    }

    public State transitionDone() {
      return new State(StateType.DONE, _priority);
    }

    public static final State INIT = new State(StateType.INIT, Priority.DEFAULT_PRIORITY) {
      @Override
      final public State transitionDone() {
        return DONE;
      };

      @Override
      final public State transitionRun() {
        return RUN;
      }

      @Override
      final public State transitionPending() {
        return PENDING;
      }
    };

    public static final State RUN = new State(StateType.RUN, Priority.DEFAULT_PRIORITY) {
      @Override
      final public State transitionDone() {
        return DONE;
      };

      @Override
      final public State transitionRun() {
        return RUN;
      }

      @Override
      final public State transitionPending() {
        return PENDING;
      }
    };

    public static final State PENDING = new State(StateType.PENDING, Priority.DEFAULT_PRIORITY) {
      @Override
      final public State transitionDone() {
        return DONE;
      };

      @Override
      final public State transitionRun() {
        return RUN;
      }

      @Override
      final public State transitionPending() {
        return PENDING;
      }
    };

    public static final State DONE = new State(StateType.DONE, Priority.DEFAULT_PRIORITY) {
      @Override
      final public State transitionDone() {
        return DONE;
      };

      @Override
      final public State transitionRun() {
        return RUN;
      }

      @Override
      final public State transitionPending() {
        return PENDING;
      }
    };
  }

  private class WrappedContext implements Context {
    private final Context _context;

    public WrappedContext(final Context context) {
      _context = context;
    }

    @Override
    public Cancellable createTimer(final long time, final TimeUnit unit, final Task<?> task) {
      final Cancellable cancellable = _context.createTimer(time, unit, task);
      getTraceBuilder().addRelationship(Relationship.POTENTIAL_PARENT_OF, getShallowTraceBuilder(),
          task.getShallowTraceBuilder());
      return cancellable;
    }

    @Override
    public void scheduleAndRun(Task<?>... tasks) {
      _context.scheduleAndRun(tasks);
    }

    @Override
    public void run(final Task<?>... tasks) {
      _context.run(tasks);
      for (Task<?> task : tasks) {
        getTraceBuilder().addRelationship(Relationship.POTENTIAL_PARENT_OF, getShallowTraceBuilder(),
            task.getShallowTraceBuilder());
      }
    }

    @Override
    public After after(final Promise<?>... promises) {
      return new WrappedAfter(_context.after(promises));
    }

    @Override
    public Object getEngineProperty(String key) {
      return _context.getEngineProperty(key);
    }

    @Override
    public TraceBuilder getTraceBuilder() {
      return _context.getTraceBuilder();
    }

    private class WrappedAfter implements After {
      private final After _after;

      public WrappedAfter(final After after) {
        _after = after;
      }

      @Override
      public void run(final Task<?> task) {
        _after.run(task);
        getTraceBuilder().addRelationship(Relationship.POTENTIAL_PARENT_OF, getShallowTraceBuilder(),
            task.getShallowTraceBuilder());
      }

      @Override
      public void run(Supplier<Task<?>> taskSupplier) {
        _after.run(() -> {
          Task<?> task = taskSupplier.get();
          if (task != null) {
            getTraceBuilder().addRelationship(Relationship.POTENTIAL_PARENT_OF, getShallowTraceBuilder(),
                task.getShallowTraceBuilder());
          }
          return task;
        } );
      }
    }

    @Override
    public ShallowTraceBuilder getShallowTraceBuilder() {
      return _context.getShallowTraceBuilder();
    }

    @Override
    public Long getPlanId() {
      return _context.getPlanId();
    }

    @Override
    public Long getTaskId() {
      return _context.getTaskId();
    }

    @Override
    public TaskLogger getTaskLogger() {
      return _context.getTaskLogger();
    }

    @Override
    public void runSideEffect(Task<?>... tasks) {
      _context.runSideEffect(tasks);
      for (Task<?> task : tasks) {
        getTraceBuilder().addRelationship(Relationship.POTENTIAL_PARENT_OF, getShallowTraceBuilder(),
            task.getShallowTraceBuilder());
      }
    }

    @Override
    public String getPlanClass() {
      return _context.getPlanClass();
    }
  }

  @Override
  public String toString() {
    return "Task [id=" + _id + ", name=" + _name + "]";
  }

}
