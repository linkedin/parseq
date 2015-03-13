package com.linkedin.parseq.trace;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TraceRelationship {
  private final long _from;
  private final long _to;
  private final Relationship _relationship;

  public TraceRelationship(long from, long to, Relationship relationship) {
    _from = from;
    _to = to;
    _relationship = relationship;
  }

  public long getFrom() {
    return _from;
  }

  public long getTo() {
    return _to;
  }

  public Relationship getRelationhsip() {
    return _relationship;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (_from ^ (_from >>> 32));
    result = prime * result + ((_relationship == null) ? 0 : _relationship.hashCode());
    result = prime * result + (int) (_to ^ (_to >>> 32));
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
    if (_from != other._from)
      return false;
    if (_relationship != other._relationship)
      return false;
    if (_to != other._to)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "TraceRelationship [from=" + _from + ", to=" + _to + ", relationhsip=" + _relationship + "]";
  }

}
