package com.linkedin.parseq;

import com.linkedin.parseq.internal.IdGenerator;
import com.linkedin.parseq.trace.ShallowTraceBuilder;


public class FusionTraceContext {

  private final ShallowTraceBuilder _propagationInitiator;
  private final Context _parent;
  private ShallowTraceBuilder _surrogate;
  private final String _desc;

  public FusionTraceContext(Context parent, ShallowTraceBuilder propagationInitiator, String desc) {
    _parent = parent;
    _propagationInitiator = propagationInitiator;
    _desc = desc;
  }

  public ShallowTraceBuilder getPropagationInitiator() {
    return _propagationInitiator;
  }

  public Context getParent() {
    return _parent;
  }

  public ShallowTraceBuilder getSurrogate() {
    return _surrogate;
  }

  public void createSurrogate() {
    if (_surrogate == null) {
      _surrogate = new ShallowTraceBuilder(IdGenerator.getNextId());
      _surrogate.setName(_desc);
      _parent.getShallowTraceBuilder().setName("fused").setSystemHidden(true);
    }
  }
}
