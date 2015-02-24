package com.linkedin.parseq.internal;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;

/**
 * @deprecated  As of 2.0.0, replaced by
 * {@link com.linkedin.parseq.Task#async(String, java.util.function.Function, boolean) Task.async},
 * @see com.linkedin.parseq.Task#async(String, java.util.function.Function, boolean) Task.async
 */
public abstract class SystemHiddenTask<T> extends BaseTask<T>
{
  protected SystemHiddenTask(String name)
  {
    super(name);
  }

  @Override
  public ShallowTrace getShallowTrace()
  {
    return new ShallowTraceBuilder(super.getShallowTrace())
        .setSystemHidden(true)
        .build();
  }
}
