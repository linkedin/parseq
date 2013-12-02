package com.linkedin.parseq.internal;

import com.linkedin.parseq.trace.Trace;

public interface InternalContext
{
  /**
   * Returns a {@link com.linkedin.parseq.trace.Trace} representing the current
   * state of the execution of this context's plan. If tracing is not enabled,
   * this returns {@code null}.
   */
  Trace getTrace();
}
