package com.linkedin.parseq;

import com.linkedin.parseq.trace.ShallowTraceBuilder;


public class FusionTraceContext {

  private final ShallowTraceBuilder _trigger;
  private final ShallowTraceBuilder _source;
  private final Context _context;

  public FusionTraceContext(Context context, ShallowTraceBuilder source, ShallowTraceBuilder trigger) {
    _context = context;
    _source = source;
    _trigger = trigger;
  }

  public ShallowTraceBuilder getTrigger() {
    return _trigger;
  }

  public ShallowTraceBuilder getSource() {
    return _source;
  }

  public Context getContext() {
    return _context;
  }
}
