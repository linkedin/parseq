package com.linkedin.parseq;

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;

/**
 * A {@link Task} that will run another task as a side effect once the primary task
 * completes successfully.
 * The side effect will not be run if the primary task fails or is canceled.
 * The entire task is marked {@link com.linkedin.parseq.BaseTask.StateType#DONE} once the
 * base task completes, even if the side effect has not been run.
 * Use {@link Tasks#withSideEffect(Task, Task)} to create instances of this class.
 *
 * @author Moira Tagle (mtagle@linkedin.com)
 */
public class WithSideEffectTask<T> extends BaseTask<T>
{
  private final Task<T> _parentTask;
  private final Task<?> _sideEffectTask;

  public WithSideEffectTask(final String name, Task<T> parentTask, Task<?> sideEffectTask)
  {
    super(name);
    if (parentTask == null)
    {
      throw new IllegalArgumentException("parentTask cannot be null");
    }
    if (sideEffectTask == null)
    {
      throw new IllegalArgumentException("sideEffectTask cannot be null");
    }
    _parentTask = parentTask;
    _sideEffectTask = sideEffectTask;
  }

  @Override
  protected Promise<? extends T> run(Context context) throws Exception
  {
    context.after(_parentTask).runSideEffect(_sideEffectTask);
    context.run(_parentTask);
    return _parentTask;
  }

  @Override
  public ShallowTrace getShallowTrace()
  {
    ShallowTrace shallowTrace = super.getShallowTrace();
    ShallowTraceBuilder builder = new ShallowTraceBuilder(shallowTrace);
    builder.setSystemHidden(true);
    return builder.build();
  }
}
