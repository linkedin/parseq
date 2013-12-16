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

import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.promise.DelegatingPromise;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.Related;
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.internal.trace.RelationshipBuilder;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.internal.trace.TraceBuilder;
import com.linkedin.parseq.internal.trace.TraceBuilderImpl;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
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
    // A task in this state in cancellable, but cannot have its priority
    // changed.
    PENDING,

    // The task is resolved.
    //
    // A task in this state in not cancellable and cannot have its priority
    // changed.
    DONE
  }

  private final AtomicReference<State> _stateRef;
  private final String _name;
  private final ShallowTraceBuilder _shallowTraceBuilder;
  private final RelationshipBuilder<Task<?>> _relationshipBuilder;

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

    final State state = new State(StateType.INIT, Priority.DEFAULT_PRIORITY);
    _shallowTraceBuilder = new ShallowTraceBuilder(ResultType.UNFINISHED);
    _relationshipBuilder = new RelationshipBuilder<Task<?>>();
    _stateRef = new AtomicReference<State>(state);

  }

  @Override
  public int getPriority()
  {
    return _stateRef.get()._priority;
  }

  @Override
  public boolean setPriority(final int priority)
  {
    if (priority < Priority.MIN_PRIORITY || priority > Priority.MAX_PRIORITY)
    {
      throw new IllegalArgumentException("Priority out of bounds: " + priority);
    }

    State state;
    State newState;
    do
    {
      state = _stateRef.get();
      if (state._type != StateType.INIT)
      {
        return false;
      }

      newState = new State(state._type, priority);
    }
    while (!_stateRef.compareAndSet(state, newState));

    return true;
  }

  @Override
  public final void contextRun(final Context context,
                               final TaskLogger taskLogger,
                               final Task<?> parent,
                               final Collection<Task<?>> predecessors)
  {
    if (transitionRun())
    {
      final Promise<T> promise;
      try
      {
        if (parent != null)
        {
          _relationshipBuilder.addRelationship(Relationship.CHILD_OF, parent);
        }
        _relationshipBuilder.addRelationship(Relationship.SUCCESSOR_OF, predecessors);

        taskLogger.logTaskStart(this);
        try
        {
          promise = doContextRun(context);
        }
        finally
        {
          transitionPending();
        }

        promise.addListener(new PromiseListener<T>()
        {
          @Override
          public void onResolved(Promise<T> resolvedPromise)
          {
            if (promise.isFailed())
            {
              fail(promise.getError() , taskLogger);
            }
            else
            {
              done(promise.get(), taskLogger);
            }
          }
        });
      }
      catch (Throwable t)
      {
        fail(t, taskLogger);
      }
    }
    else
    {
      if (parent != null)
      {
        _relationshipBuilder.addRelationship(Relationship.POTENTIAL_CHILD_OF, parent);
      }
      _relationshipBuilder.addRelationship(Relationship.POSSIBLE_SUCCESSOR_OF, predecessors);
    }
  }

  @SuppressWarnings("unchecked")
  private Promise<T> doContextRun(final Context context) throws Throwable
  {
    return (Promise<T>)run(new WrappedContext(context));
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
      getSettableDelegate().fail(reason);
      return true;
    }
    return false;
  }

  @Override
  public ShallowTrace getShallowTrace()
  {
    _shallowTraceBuilder.setResultType(ResultType.fromTask(this));
    _shallowTraceBuilder.setName(getName());
    return _shallowTraceBuilder.build();
  }

  @Override
  public Trace getTrace()
  {
    TraceBuilder builder = new TraceBuilderImpl();
    return builder.getTrace(this);
  }

  @Override
  public Set<Related<Task<?>>> getRelationships()
  {
    return _relationshipBuilder.getRelationships();
  }

  /**
   * This template method is invoked when the task is run.
   *
   * @param context the context to use while running this task
   * @return a promise that will have its value set when this task is finished
   * @throws Exception if an error occurs while running the task
   */
  protected abstract Promise<? extends T> run(final Context context) throws Throwable;

  private void done(final T value, final TaskLogger taskLog)
  {
    if (transitionDone())
    {
      getSettableDelegate().done(value);
      taskLog.logTaskEnd(BaseTask.this);
    }
  }

  private void fail(final Throwable error, final TaskLogger taskLog)
  {
    if (transitionDone())
    {
      getSettableDelegate().fail(error);
      taskLog.logTaskEnd(BaseTask.this);
    }
  }

  private boolean transitionRun()
  {
    State state;
    State newState;
    final long startNanos = System.nanoTime();
    do
    {
      state = _stateRef.get();
      if (state._type != StateType.INIT)
      {
        return false;
      }
      newState = new State(StateType.RUN, state._priority);
    }
    while (!_stateRef.compareAndSet(state, newState));
    _shallowTraceBuilder.setStartNanos(startNanos);

    return true;
  }

  private void transitionPending()
  {
    State state;
    State newState;
    final long pendingNanos = System.nanoTime();
    do
    {
      state = _stateRef.get();
      if (state._type != StateType.RUN)
      {
        return;
      }
      newState = new State(StateType.PENDING, state._priority);
    }
    while (!_stateRef.compareAndSet(state, newState));
    _shallowTraceBuilder.setPendingNanos(pendingNanos);
  }

  private boolean transitionCancel()
  {
    State state;
    State newState;
    final long endNanos = System.nanoTime();
    do
    {
      state = _stateRef.get();
      final StateType type = state._type;
      if (type == StateType.RUN || type == StateType.DONE)
      {
        return false;
      }

      newState = new State(StateType.DONE, state._priority);
    }
    while (!_stateRef.compareAndSet(state, newState));

    if (_shallowTraceBuilder.getStartNanos() == null)
    {
      _shallowTraceBuilder.setStartNanos(endNanos);
    }
    if (_shallowTraceBuilder.getPendingNanos() == null)
    {
      _shallowTraceBuilder.setPendingNanos(endNanos);
    }
    _shallowTraceBuilder.setEndNanos(endNanos);

    return true;
  }

  private boolean transitionDone()
  {
    State state;
    State newState;
    long endNanos;
    do
    {
      state = _stateRef.get();
      if (state._type == StateType.DONE)
      {
        return false;
      }

      endNanos = System.nanoTime();

      newState = new State(StateType.DONE, state._priority);
    }
    while (!_stateRef.compareAndSet(state, newState));

    _shallowTraceBuilder.setEndNanos(endNanos);
    return true;
  }

  private SettablePromise<T> getSettableDelegate()
  {
    return (SettablePromise<T>)super.getDelegate();
  }

  private static class State
  {
    private final StateType _type;
    private final int _priority;

    private State(final StateType type,
                  final int priority)
    {
      _type = type;
      _priority = priority;
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
      _relationshipBuilder.addRelationship(Relationship.POTENTIAL_PARENT_OF, task);
      return cancellable;
    }

    @Override
    public void run(final Task<?>... tasks)
    {
      _context.run(tasks);
      for(Task<?> task : tasks)
      {
        _relationshipBuilder.addRelationship(Relationship.POTENTIAL_PARENT_OF, task);
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
      _relationshipBuilder.addRelationship(Relationship.POTENTIAL_PARENT_OF, task);
    }
  }
}
