package com.linkedin.parseq.internal;

import com.linkedin.parseq.BaseTask;

/**
 * Base class for system hidden tasks. It has exactly the same behavior as {@link BaseTask}
 * except the visibility of a trace created by this class is system hidden.
 * <p>
 * Instead of extending this class consider using
 *
 * {@link com.linkedin.parseq.Task#async(String, com.linkedin.parseq.function.Function1, boolean) Task.async}.
 * @see com.linkedin.parseq.Task#async(String, com.linkedin.parseq.function.Function1, boolean) Task.async
 */
public abstract class SystemHiddenTask<T> extends BaseTask<T>
{
  protected SystemHiddenTask(String name)
  {
    super(name);
    _shallowTraceBuilder.setSystemHidden(true);
  }
}
