package com.linkedin.parseq.internal;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.trace.ShallowTrace;

public interface TaskListener
{
  void onUpdate(Task<?> task, ShallowTrace shallowTrace);
}
