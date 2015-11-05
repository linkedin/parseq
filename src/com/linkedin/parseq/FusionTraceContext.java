package com.linkedin.parseq;

import com.linkedin.parseq.internal.IdGenerator;
import com.linkedin.parseq.trace.ShallowTraceBuilder;


/**
 * FusionTraceContext encapsulates information necessary to generate trace
 * of fusion tasks.
 *
 * This is internal class and it should not be extended or used outside ParSeq.
 * It may change or be removed at any time in a backwards incompatible way.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
class FusionTraceContext {

  /* Task that initiated propagation */
  private final ShallowTraceBuilder _propagationInitiator;

  /* Context of a parent task, it will be different than propagationInitiator if fusion is applied on async task */
  private final Context _parent;

  /* Surrogate trace is a trace of last transformation in a chain - outermost propagator */
  private ShallowTraceBuilder _surrogate;

  /* Description of the last transformation in the chain */
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
