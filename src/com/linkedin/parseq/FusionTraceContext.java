package com.linkedin.parseq;

import com.linkedin.parseq.trace.ShallowTraceBuilder;


public class FusionTraceContext {

  private final ShallowTraceBuilder _trigger;
  private final Context _context;

  public FusionTraceContext(Context context, ShallowTraceBuilder trigger) {
    _context = context;
    _trigger = trigger;
  }

  public ShallowTraceBuilder getTrigger() {
    return _trigger;
  }

  public Context getContext() {
    return _context;
  }
}
