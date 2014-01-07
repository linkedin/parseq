package com.linkedin.parseq.internal;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;

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
