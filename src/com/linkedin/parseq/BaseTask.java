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

import com.linkedin.parseq.internal.InternalContext;
import com.linkedin.parseq.internal.TaskListener;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromiseException;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.PromiseUnresolvedException;
import com.linkedin.parseq.internal.trace.MutableTrace;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * An abstract base class that can be used to build implementations of
 * {@link Task}.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public abstract class BaseTask<T> implements Task<T>, Promise<T>
{
  private static enum StateType
  {
    // The initial state of the task.
    //
    // A task in this state can be cancelled and have its priority changed.
    INIT,

    // A context has been assigned to the task. The task can transition to run
    // from the assigned state. It can also be cancelled. Priority cannot be
    // changed.
    ASSIGNED,

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

  private static final Logger LOG = LoggerFactory.getLogger(BaseTask.class);

  private final String _name;
  private final AtomicReference<State<T>> _stateRef = new AtomicReference<State<T>>();
  private final ConcurrentLinkedQueue<PromiseListener<T>> _promiseListeners = new ConcurrentLinkedQueue<PromiseListener<T>>();
  private final ConcurrentLinkedQueue<TaskListener> _taskListeners = new ConcurrentLinkedQueue<TaskListener>();
  private final CountDownLatch _doneLatch = new CountDownLatch(1);

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
    _name = name != null ? name : getClass().getName();
    _stateRef.set(new State<T>(StateType.INIT,
                  null, null,
                  new ShallowTraceBuilder(_name, ResultType.UNFINISHED).build(),
                  null, Priority.DEFAULT_PRIORITY));
  }

  @Override
  public String getName()
  {
    return _name;
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

    State<T> state;
    State<T> newState;
    do
    {
      state = _stateRef.get();
      if (state._type != StateType.INIT)
        return false;

      newState = new State<T>(state._type,
          state._value, state._error,
          state._trace,
          state._context,
          priority);
    }
    while (!_stateRef.compareAndSet(state, newState));
    return true;
  }

  @Override
  public boolean assignContext(Context context)
  {
    return transitionAssigned(context);
  }

  @Override
  public final void contextRun()
  {
    final State runState = transitionRun();
    if (runState == null)
    {
      throw new IllegalStateException("contextRun called more than once!");
    }

    final Promise<T> promise;
    try
    {
      try
      {
        promise = doContextRun(runState._context);
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
            fail(promise.getError());
          }
          else
          {
            done(promise.get());
          }
        }
      });
    }
    catch (Throwable t)
    {
      fail(t);
    }
  }

  public boolean cancel(final Exception reason)
  {
    return transitionDone(null, reason, true);
  }

  @Override
  public ShallowTrace getShallowTrace()
  {
    return buildShallowTrace(_stateRef.get());
  }

  @Override
  public Trace getTrace()
  {
    final State state = _stateRef.get();
    final Context context = state._context;
    if (context instanceof InternalContext)
    {
      final Trace trace = ((InternalContext)context).getTrace();
      if (trace != null)
      {
        return trace;
      }
    }

    final MutableTrace trace = new MutableTrace();
    trace.addTrace(0, getShallowTrace());
    return trace.freeze();
  }

  @Override
  public T get() throws PromiseException
  {
    final State<T> state = _stateRef.get();
    ensureDone(state);
    if (state._error != null)
    {
      throw new PromiseException(state._error);
    }
    return state._value;
  }

  @Override
  public Throwable getError() throws PromiseUnresolvedException
  {
    final State state = _stateRef.get();
    ensureDone(state);
    return state._error;
  }

  @Override
  public T getOrDefault(T defaultValue) throws PromiseUnresolvedException
  {
    final State<T> state = _stateRef.get();
    ensureDone(state);
    if (state._error != null)
    {
      return defaultValue;
    }
    return state._value;
  }

  @Override
  public void await() throws InterruptedException
  {
    _doneLatch.await();
  }

  @Override
  public boolean await(long time, TimeUnit unit) throws InterruptedException
  {
    return _doneLatch.await(time, unit);
  }

  @Override
  public void addListener(PromiseListener<T> listener)
  {
    _promiseListeners.add(listener);

    final State state = _stateRef.get();
    if (state._type == StateType.DONE)
      purgeListeners(state);
  }

  @Override
  public void addTaskListener(TaskListener listener)
  {
    State state = _stateRef.get();
    if (state._type == StateType.DONE)
    {
      listener.onUpdate(this, buildShallowTrace(state));
      return;
    }

    _taskListeners.add(listener);

    state = _stateRef.get();
    if (state._type == StateType.DONE)
      purgeListeners(state);
  }

  @Override
  public boolean isDone()
  {
    return _stateRef.get()._type == StateType.DONE;
  }

  @Override
  public boolean isFailed()
  {
    final State state = _stateRef.get();
    return state._type == StateType.DONE && state._error != null;
  }

  @SuppressWarnings("unchecked")
  private Promise<T> doContextRun(final Context context) throws Throwable
  {
    return (Promise<T>)run(context);
  }

  /**
   * This template method is invoked when the task is run.
   *
   * @param context the context to use while running this task
   * @return a promise that will have its value set when this task is finished
   * @throws Exception if an error occurs while running the task
   */
  protected abstract Promise<? extends T> run(final Context context) throws Throwable;

  private void done(final T value)
  {
    transitionDone(value, null, false);
  }

  private void fail(final Throwable error)
  {
    transitionDone(null, error, false);
  }

  private boolean transitionAssigned(Context context)
  {
    State<T> state;
    State<T> newState;
    do
    {
      state = _stateRef.get();
      if (state._type != StateType.INIT)
        return false;

      newState = new State<T>(StateType.ASSIGNED,
          null, null,
          state._trace,
          context,
          state._priority);
    }
    while (!_stateRef.compareAndSet(state, newState));
    return true;
  }

  private State transitionRun()
  {
    State<T> state;
    State<T> newState;
    final long startNanos = System.nanoTime();
    do
    {
      state = _stateRef.get();
      if (state._type != StateType.ASSIGNED)
        return null;

      newState = new State<T>(StateType.RUN,
          null, null,
          new ShallowTraceBuilder(state._trace).setStartNanos(startNanos).build(),
          state._context,
          state._priority);
    }
    while (!_stateRef.compareAndSet(state, newState));
    notifyTaskListeners(newState);
    return newState;
  }

  private void transitionPending()
  {
    State<T> state;
    State<T> newState;
    final long pendingNanos = System.nanoTime();
    do
    {
      state = _stateRef.get();
      if (state._type != StateType.RUN)
        return;

      newState = new State<T>(StateType.PENDING,
          null, null,
          new ShallowTraceBuilder(state._trace).setPendingNanos(pendingNanos).build(),
          state._context,
          state._priority);
    }
    while (!_stateRef.compareAndSet(state, newState));
    notifyTaskListeners(newState);
  }

  private boolean transitionDone(T value, Throwable error, boolean cancellation)
  {
    State<T> state;
    State<T> newState;
    final long endNanos = System.nanoTime();
    do
    {
      state = _stateRef.get();
      if (state._type == StateType.DONE || (cancellation && state._type == StateType.RUN))
        return false;

      final ShallowTraceBuilder stateBuilder = new ShallowTraceBuilder(state._trace);
      stateBuilder.setEndNanos(endNanos);

      if (error == null)
      {
        stateBuilder.setResultType(ResultType.SUCCESS);
      }
      else
      {
        stateBuilder.setResultType(error instanceof EarlyFinishException ? ResultType.EARLY_FINISH : ResultType.ERROR);

        if (stateBuilder.getStartNanos() == null)
        {
          stateBuilder.setStartNanos(endNanos);

          if (stateBuilder.getPendingNanos() == null)
          {
            stateBuilder.setPendingNanos(endNanos);
          }
        }
      }

      newState = new State<T>(StateType.DONE,
          value, error,
          stateBuilder.build(),
          state._context,
          state._priority);
    }
    while (!_stateRef.compareAndSet(state, newState));
    purgeListeners(newState);
    _doneLatch.countDown();
    return true;
  }

  private void purgeListeners(State state)
  {
    if (!_taskListeners.isEmpty())
    {
      final ShallowTrace trace = buildShallowTrace(state);
      TaskListener taskListener;
      while ((taskListener = _taskListeners.poll()) != null)
      {
        try
        {
          taskListener.onUpdate(this, trace);
        }
        catch (Exception e)
        {
          LOG.warn("Promise listener threw exception. Ignoring and continuing. Listener: " + taskListener, e);
        }
      }
    }

    PromiseListener<T> promiseListener;
    while ((promiseListener = _promiseListeners.poll()) != null)
    {
      try
      {
        promiseListener.onResolved(this);
      }
      catch (Exception e)
      {
        LOG.warn("Promise listener threw exception. Ignoring and continuing. Listener: " + promiseListener, e);
      }
    }
  }

  private void notifyTaskListeners(State state)
  {
    if (!_taskListeners.isEmpty())
    {
      final ShallowTrace trace = buildShallowTrace(state);
      for (TaskListener taskListener : _taskListeners)
      {
        try
        {
          taskListener.onUpdate(this, trace);
        }
        catch (Exception e)
        {
          LOG.warn("Promise listener threw exception. Ignoring and continuing. Listener: " + taskListener, e);
        }
      }
    }
  }

  private ShallowTrace buildShallowTrace(State state)
  {
    // We defer computation of the string value representation until it is
    // needed. This adds a cost to each retrieval of a shallow trace, but avoids
    // penalizing the case where it is never needed.
    if (state._type == StateType.DONE)
    {
      final ShallowTraceBuilder builder = new ShallowTraceBuilder(state._trace);
      if (state._error != null)
      {
        if (!(state._error instanceof EarlyFinishException))
        {
          builder.setValue(state._error.toString());
        }
      }
      else
      {
        builder.setValue(state._value != null ? state._value.toString() : null);
      }
      return builder.build();
    }

    return state._trace;
  }

  private void ensureDone(State state) throws PromiseUnresolvedException
  {
    if (state._type != StateType.DONE)
    {
      throw new PromiseUnresolvedException("Promise has not yet been satisfied");
    }
  }

  private static class State<T>
  {
    private final StateType _type;
    private final T _value;
    private final Throwable _error;
    private final ShallowTrace _trace;
    private final Context _context;
    private final int _priority;

    private State(StateType type,
                  T value, Throwable error,
                  ShallowTrace trace,
                  Context context,
                  int priority)
    {
      _type = type;
      _value = value;
      _error = error;
      _trace = trace;
      _context = context;
      _priority = priority;
    }
  }
}
