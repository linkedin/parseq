package com.linkedin.parseq;

import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.TraceBuilder;


public class FusionTraceContext {

  private final ShallowTraceBuilder _parent;
  private final ShallowTraceBuilder _source;
  private final TraceBuilder _traceBuilder;
  private final TaskLogger _taskLogger;

  public FusionTraceContext(TraceBuilder traceBuilder, ShallowTraceBuilder source, ShallowTraceBuilder parent,
      TaskLogger taskLogger) {
    _traceBuilder = traceBuilder;
    _source = source;
    _parent = parent;
    _taskLogger = taskLogger;
  }

  public ShallowTraceBuilder getParent() {
    return _parent;
  }

  public ShallowTraceBuilder getSource() {
    return _source;
  }

  public TraceBuilder getTraceBuilder() {
    return _traceBuilder;
  }

  public TaskLogger getTaskLogger() {
    return _taskLogger;
  }

}
