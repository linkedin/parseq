package com.linkedin.parseq.trace;

import com.linkedin.parseq.internal.ArgumentUtil;

public class RelationshipEntry
{
  private final int _fromId;
  private final int _toId;
  private final Relationship _relationship;

  public RelationshipEntry(int fromId, int toId, Relationship relationship)
  {
    ArgumentUtil.notNull(relationship, "relationship");

    _fromId = fromId;
    _toId = toId;
    _relationship = relationship;
  }

  public int getFromId()
  {
    return _fromId;
  }

  public int getToId()
  {
    return _toId;
  }

  public Relationship getRelationship()
  {
    return _relationship;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RelationshipEntry that = (RelationshipEntry) o;

    if (_fromId != that._fromId) return false;
    if (_toId != that._toId) return false;
    if (_relationship != that._relationship) return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = _fromId;
    result = 31 * result + _toId;
    result = 31 * result + _relationship.hashCode();
    return result;
  }

  @Override
  public String toString()
  {
    return "RelationshipEntry{" +
        "_fromId='" + _fromId + '\'' +
        ", _toId='" + _toId + '\'' +
        ", _relationship=" + _relationship +
        '}';
  }
}
