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
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

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
 * TODO break tasks referencing each other through context, replace with ids
 *
 *
 * An abstract base class that can be used to build implementations of
 * {@link Task}.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public abstract class BaseTask<T> extends DelegatingPromise<T> implements Task<T>
{
  private static enum StateType
  {
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
  private final AtomicReference<State<T>> _stateRef;
  private final String _name;
  protected final ShallowTraceBuilder _shallowTraceBuilder;

  protected volatile Function<T, String> _traceValueProvider;

  /**
   * Constructs a base task without a specified name. The name for this task
   * will be the {@link #toString} representation for this instance. It is
   * usually best to use {@link BaseTask#BaseTask(String)}.
   */
  public BaseTask()
  {
    this(null);
  }

  /**
   * Constructs a base task with a name.
   *
   * @param name the name for this task.
   */
  public BaseTask(final String name)
  {
    super(Promises.<T>settable());

    _name = name;
    final State<T> state = new State<T>(StateType.INIT, Priority.DEFAULT_PRIORITY, null, null);
    _shallowTraceBuilder = new ShallowTraceBuilder(_id);
    _shallowTraceBuilder.setName(getName());
    _shallowTraceBuilder.setResultType(ResultType.UNFINISHED);
    _stateRef = new AtomicReference<State<T>>(state);
  }

  @Override
  public Long getId() {
    return _id;
  }

  @Override
  public int getPriority()
  {
    return _stateRef.get().getPriority();
  }

  @Override
  public boolean setPriority(final int priority)
  {
    if (priority < Priority.MIN_PRIORITY || priority > Priority.MAX_PRIORITY)
    {
      throw new IllegalArgumentException("Priority out of bounds: " + priority);
    }

    State<T> state;
    State<T> newState;
    do
    {
      state = _stateRef.get();
      if (state.getType() != StateType.INIT)
      {
        return false;
      }

      newState = new State<T>(state.getType(), priority, state.getContextRunWrapper(), state.getTraceBuilder());
    }
    while (!_stateRef.compareAndSet(state, newState));

    return true;
  }

  @Override
  public TraceBuilder getTraceBuilder() {
    return _stateRef.get().getTraceBuilder();
  }

  @Override
  public final void contextRun(final Context context,
                               final Task<?> parent,
                               final Collection<Task<?>> predecessors)
  {
    final TaskLogger taskLogger = context.getTaskLogger();
    final TraceBuilder traceBuilder = context.getTraceBuilder();
    if (transitionRun(traceBuilder))
    {
      final Promise<T> promise;
      try
      {
        if (parent != null)
        {
          traceBuilder.addRelationship(Relationship.CHILD_OF, getShallowTraceBuilder(), parent.getShallowTraceBuilder());
        }
        for (Task<?> predecessor: predecessors) {
          traceBuilder.addRelationship(Relationship.SUCCESSOR_OF, getShallowTraceBuilder(), predecessor.getShallowTraceBuilder());
        }

        taskLogger.logTaskStart(this);
        try
        {
          final Context wrapperContext = new WrappedContext(context);
          final ContextRunWrapper<T> contextRunWrapper = _stateRef.get().getContextRunWrapper();
          if (contextRunWrapper != null) {
            contextRunWrapper.before(wrapperContext);
            promise = contextRunWrapper.after(wrapperContext, doContextRun(wrapperContext));
          } else {
            promise = doContextRun(wrapperContext);
          }
        }
        finally
        {
          transitionPending();
        }

        promise.addListener(resolvedPromise ->
          {
            if (resolvedPromise.isFailed())
            {
              traceAndFail(resolvedPromise.getError(), taskLogger);
            }
            else
            {
              traceAndDone(resolvedPromise.get(), taskLogger);
            }
          });
      }
      catch (Throwable t)
      {
        traceAndFail(t, taskLogger);
      }
    }
    else
    {
      //this is possible when task was cancelled or has been executed multiple times
      //e.g. task has multiple paths it can be executed with or has been completed by
      //a FusionTask
      if (parent != null) {
        traceBuilder.addRelationship(Relationship.POTENTIAL_CHILD_OF, getShallowTraceBuilder(), parent.getShallowTraceBuilder());
      }
      for (Task<?> predecessor: predecessors) {
        traceBuilder.addRelationship(Relationship.POSSIBLE_SUCCESSOR_OF, getShallowTraceBuilder(), predecessor.getShallowTraceBuilder());
      }
    }
  }

  @SuppressWarnings("unchecked")
  private Promise<T> doContextRun(final Context context) throws Throwable
  {
    return (Promise<T>)run(context);
  }


  /**
   * Returns the name of this task. If no name was set during construction
   * this method will return the value of {@link #toString()}. In most
   * cases it is preferable to explicitly set a name.
   *
   * @return the name of this task
   */
  @Override
  public String getName()
  {
    return _name == null ? toString() : _name;
  }

  @Override
  public boolean cancel(final Exception reason)
  {
    if (transitionCancel())
    {
      if (reason instanceof EarlyFinishException) {
        _shallowTraceBuilder.setResultType(ResultType.EARLY_FINISH);
      } else {
        _shallowTraceBuilder.setResultType(ResultType.ERROR);
      }
      getSettableDelegate().fail(reason);
      return true;
    }
    return false;
  }

  @Override
  public ShallowTraceBuilder getShallowTraceBuilder() {
    return _shallowTraceBuilder;
  }

  @Override
  public ShallowTrace getShallowTrace()
  {
    return _shallowTraceBuilder.build();
  }

  @Override
  public void traceValue(final Function<T, String> traceValueProvider) {
    _traceValueProvider = traceValueProvider;
  }

  @Override
  public Trace getTrace() {
    TraceBuilder traceBuilder = getTraceBuilder();
    if (traceBuilder != null) {
      return traceBuilder.build();
    } else {
      return Trace.single(getShallowTrace());
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

  private void traceAndDone(final T value, final TaskLogger taskLogger) {
    _shallowTraceBuilder.setResultType(ResultType.SUCCESS);
    final Function<T, String> traceValueProvider = _traceValueProvider;
    if (traceValueProvider != null) {
      try {
      _shallowTraceBuilder.setValue(traceValueProvider.apply(value));
      } catch (Exception e) {
        _shallowTraceBuilder.setValue(e.toString());
      }
    }
    done(value, taskLogger);
  }

  private void done(final T value, final TaskLogger taskLogger)
  {
    if (transitionDone())
    {
      getSettableDelegate().done(value);
      taskLogger.logTaskEnd(BaseTask.this, _traceValueProvider);
    }
  }

  private void traceAndFail(final Throwable error, final TaskLogger taskLogger) {
    if (error instanceof EarlyFinishException) {
      _shallowTraceBuilder.setResultType(ResultType.EARLY_FINISH);
    } else {
      _shallowTraceBuilder.setResultType(ResultType.ERROR);
      _shallowTraceBuilder.setValue(error.toString());
    }
    fail(error , taskLogger);
  }

  private void fail(final Throwable error, final TaskLogger taskLogger)
  {
    if (transitionDone())
    {
      getSettableDelegate().fail(error);
      taskLogger.logTaskEnd(BaseTask.this, _traceValueProvider);
    }
  }


  protected boolean transitionRun(final TraceBuilder traceBuilder)
  {
    State<T> state;
    State<T> newState;
    final long startNanos = System.nanoTime();
    do
    {
      state = _stateRef.get();
      if (state.getType() != StateType.INIT)
      {
        return false;
      }
      newState = new State<T>(StateType.RUN, state.getPriority(), state.getContextRunWrapper(),
          traceBuilder);
    }
    while (!_stateRef.compareAndSet(state, newState));
    traceBuilder.addShallowTrace(_shallowTraceBuilder);
    _shallowTraceBuilder.setStartNanos(startNanos);

    return true;
  }

  protected void transitionPending()
  {
    State<T> state;
    State<T> newState;
    final long pendingNanos = System.nanoTime();
    do
    {
      state = _stateRef.get();
      if (state.getType() != StateType.RUN)
      {
        return;
      }
      newState = new State<T>(StateType.PENDING, state.getPriority(), state.getContextRunWrapper(), state.getTraceBuilder());
    }
    while (!_stateRef.compareAndSet(state, newState));
    _shallowTraceBuilder.setPendingNanos(pendingNanos);
  }

  protected boolean transitionCancel()
  {
    State<T> state;
    State<T> newState;

    //TODO if previous state was PENDING then notify
    //asynchronous execution about the cancellation

    do
    {
      state = _stateRef.get();
      final StateType type = state.getType();
      if (type == StateType.RUN || type == StateType.DONE)
      {
        return false;
      }

      newState = new State<T>(StateType.DONE, state.getPriority(), state.getContextRunWrapper(), state.getTraceBuilder());
    }
    while (!_stateRef.compareAndSet(state, newState));

    return true;
  }

  protected boolean transitionDone()
  {
    State<T> state;
    State<T> newState;
    do
    {
      state = _stateRef.get();
      if (state.getType() == StateType.DONE)
      {
        return false;
      }

      newState = new State<T>(StateType.DONE, state.getPriority(), state.getContextRunWrapper(), state.getTraceBuilder());
    }
    while (!_stateRef.compareAndSet(state, newState));
    return true;
  }

  protected SettablePromise<T> getSettableDelegate()
  {
    return (SettablePromise<T>)super.getDelegate();
  }

  protected static class State<T>
  {
    private final StateType _type;
    private final int _priority;
    private final ContextRunWrapper<T> _contextRunWrapper;
    private final TraceBuilder _traceBuilder;


    private State(final StateType type,
                  final int priority,
                  ContextRunWrapper<T>contextRunWrapper,
                  TraceBuilder traceBuilder)
    {
      _type = type;
      _priority = priority;
      _contextRunWrapper = contextRunWrapper;
      _traceBuilder = traceBuilder;
    }

    public StateType getType() {
      return _type;
    }

    public int getPriority() {
      return _priority;
    }

    public ContextRunWrapper<T> getContextRunWrapper() {
      return _contextRunWrapper;
    }

    public TraceBuilder getTraceBuilder() {
      return _traceBuilder;
    }

  }

  private class WrappedContext implements Context
  {
    private final Context _context;

    public WrappedContext(final Context context)
    {
      _context = context;
    }

    @Override
    public Cancellable createTimer(final long time, final TimeUnit unit,
                                   final Task<?> task)
    {
      final Cancellable cancellable = _context.createTimer(time, unit, task);
      getTraceBuilder().addRelationship(Relationship.POTENTIAL_PARENT_OF, getShallowTraceBuilder(), task.getShallowTraceBuilder());
      return cancellable;
    }

    @Override
    public void run(final Task<?>... tasks)
    {
      _context.run(tasks);
      for(Task<?> task : tasks)
      {
        getTraceBuilder().addRelationship(Relationship.POTENTIAL_PARENT_OF, getShallowTraceBuilder(), task.getShallowTraceBuilder());
      }
    }

    @Override
    public After after(final Promise<?>... promises)
    {
      return new WrappedAfter(_context.after(promises));
    }

    @Override
    public Object getEngineProperty(String key)
    {
      return _context.getEngineProperty(key);
    }

    @Override
    public void runSubTask(Task<?> task, Task<?> rootTask) {
      _context.runSubTask(task, rootTask);
      getTraceBuilder().addRelationship(Relationship.POTENTIAL_PARENT_OF, getShallowTraceBuilder(), task.getShallowTraceBuilder());
    }

    @Override
    public After afterTask(Task<Object> rootTask, Promise<?>... promises) {
      return new WrappedAfter(_context.afterTask(rootTask, promises));
    }

    @Override
    public TraceBuilder getTraceBuilder() {
      return _context.getTraceBuilder();
    }

    private class WrappedAfter implements After
    {
      private final After _after;

      public WrappedAfter(final After after)
      {
        _after = after;
      }

      @Override
      public void run(final Task<?> task)
      {
        _after.run(task);
        getTraceBuilder().addRelationship(Relationship.POTENTIAL_PARENT_OF, getShallowTraceBuilder(), task.getShallowTraceBuilder());
      }

      @Override
      public void run(Supplier<Optional<Task<?>>> taskSupplier) {
        _after.run(() -> {
          Optional<Task<?>> task = taskSupplier.get();
          if (task.isPresent()) {
            getTraceBuilder().addRelationship(Relationship.POTENTIAL_PARENT_OF, getShallowTraceBuilder(), task.get().getShallowTraceBuilder());
          }
          return task;
        });
      }

      @Override
      public void runSideEffect(final Task<?> task)
      {
        _after.runSideEffect(task);
        getTraceBuilder().addRelationship(Relationship.POTENTIAL_PARENT_OF, getShallowTraceBuilder(), task.getShallowTraceBuilder());
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
    public TaskLogger getTaskLogger() {
      return _context.getTaskLogger();
    }
  }

  @Override
  public void wrapContextRun(final ContextRunWrapper<T> wrapper) {
    State<T> state;
    State<T> newState;
    do
    {
      state = _stateRef.get();
      if (state.getType() != StateType.INIT)
      {
        throw new RuntimeException("wrapContextRun can be only called on task in INIT state, current state: " + state.getType());
      }
      if (state.getContextRunWrapper() != null) {
        newState = new State<T>(state.getType(), state.getPriority(), state.getContextRunWrapper().compose(wrapper), state.getTraceBuilder());
      } else {
        newState = new State<T>(state.getType(), state.getPriority(), wrapper, state.getTraceBuilder());
      }
    }
    while (!_stateRef.compareAndSet(state, newState));
  }

  @Override
  public String toString() {
    return "Task [id=" + _id + ", name=" + _name + "]";
  }

}
