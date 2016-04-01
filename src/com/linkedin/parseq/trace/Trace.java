package com.linkedin.parseq.trace;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;


public class Trace {

  private final Map<Long, ShallowTrace> _traceMap;
  private final Set<TraceRelationship> _relationships;

  public Trace(Map<Long, ShallowTrace> traceMap, Set<TraceRelationship> relationships) {
    _traceMap = Collections.unmodifiableMap(traceMap);
    _relationships = Collections.unmodifiableSet(relationships);
  }

  public Map<Long, ShallowTrace> getTraceMap() {
    return _traceMap;
  }

  public Set<TraceRelationship> getRelationships() {
    return _relationships;
  }

  public static Trace single(ShallowTrace shallowTrace) {
    return new Trace(Collections.singletonMap(shallowTrace.getId(), shallowTrace), Collections.emptySet());
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
