package com.linkedin.parseq.trace;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TraceRelationship {
  final ShallowTraceBuilder _from;
  final ShallowTraceBuilder _to;

  private final Relationship _relationship;

  public TraceRelationship(ShallowTraceBuilder from, ShallowTraceBuilder to, Relationship relationship) {
    _from = from;
    _to = to;
    _relationship = relationship;
  }

  public Long getFrom() {
    return _from.getNativeId();
  }

  public Long getTo() {
    return _to.getNativeId();
  }

  public Relationship getRelationhsip() {
    return _relationship;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Long.hashCode(_from.getNativeId());
    result = prime * result + ((_relationship == null) ? 0 : _relationship.hashCode());
    result = prime * result + Long.hashCode(_to.getNativeId());
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
    TraceRelationship other = (TraceRelationship) obj;
    if (_from.getNativeId() != other._from.getNativeId())
      return false;
    if (_relationship != other._relationship)
      return false;
    if (_to.getNativeId() != other._to.getNativeId())
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "TraceRelationship [from=" + _from.getId() + ", to=" + _to.getId() + ", relationhsip=" + _relationship + "]";
  }

}
