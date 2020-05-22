package com.linkedin.parseq.trace;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


public class Trace {

  private final Map<Long, ShallowTrace> _traceMap;
  private final Set<TraceRelationship> _relationships;
  private final Long _planId;
  private final String _planClass;

  // TODO: this constructor should be removed.
  // Need to fix in the next major version release.
  public Trace(Map<Long, ShallowTrace> traceMap, Set<TraceRelationship> relationships) {
    this(traceMap, relationships, TraceBuilder.UNKNOWN_PLAN_CLASS, TraceBuilder.UNKNOWN_PLAN_ID);
  }

  public Trace(Map<Long, ShallowTrace> traceMap, Set<TraceRelationship> relationships, String planClass, Long planId) {
    _traceMap = Collections.unmodifiableMap(traceMap);
    _relationships = Collections.unmodifiableSet(relationships);
    _planClass = planClass;
    _planId = planId;
  }

  public Map<Long, ShallowTrace> getTraceMap() {
    return _traceMap;
  }

  public Set<TraceRelationship> getRelationships() {
    return _relationships;
  }

  // TODO: this method should be removed.
  // Need to fix in the next major version release.
  public static Trace single(ShallowTrace shallowTrace) {
    return single(shallowTrace, TraceBuilder.UNKNOWN_PLAN_CLASS, TraceBuilder.UNKNOWN_PLAN_ID);
  }

  public static Trace single(ShallowTrace shallowTrace, String planClass, Long planId) {
    return new Trace(Collections.singletonMap(shallowTrace.getNativeId(), shallowTrace), Collections.emptySet(),
        planClass, planId);
  }

  public Long getPlanId() {
    return _planId;
  }

  public String getPlanClass() {
    return _planClass;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_relationships == null) ? 0 : _relationships.hashCode());
    result = prime * result + ((_traceMap == null) ? 0 : _traceMap.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Trace other = (Trace) obj;
    if (_relationships == null) {
      if (other._relationships != null)
        return false;
    } else if (!_relationships.equals(other._relationships))
      return false;
    if (_traceMap == null) {
      if (other._traceMap != null)
        return false;
    } else if (!_traceMap.equals(other._traceMap))
      return false;
    return true;
  }

  @Override
  public String toString() {
    try {
      return TraceUtil.getJsonTrace(this);
    } catch (IOException e) {
      throw new RuntimeException("Could not serialize trace to JSON", e);
    }
  }

}
