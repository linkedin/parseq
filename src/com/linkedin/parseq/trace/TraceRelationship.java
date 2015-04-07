package com.linkedin.parseq.trace;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TraceRelationship {
  private final Long _from;
  private final Long _to;
  private final Relationship _relationship;

  public TraceRelationship(Long from, Long to, Relationship relationship) {
    _from = from;
    _to = to;
    _relationship = relationship;
  }

  public Long getFrom() {
    return _from;
  }

  public Long getTo() {
    return _to;
  }

  public Relationship getRelationhsip() {
    return _relationship;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_from == null) ? 0 : _from.hashCode());
    result = prime * result + ((_relationship == null) ? 0 : _relationship.hashCode());
    result = prime * result + ((_to == null) ? 0 : _to.hashCode());
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
    if (_from == null) {
      if (other._from != null)
        return false;
    } else if (!_from.equals(other._from))
      return false;
    if (_relationship != other._relationship)
      return false;
    if (_to == null) {
      if (other._to != null)
        return false;
    } else if (!_to.equals(other._to))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "TraceRelationship [from=" + _from + ", to=" + _to + ", relationhsip=" + _relationship + "]";
  }

}
