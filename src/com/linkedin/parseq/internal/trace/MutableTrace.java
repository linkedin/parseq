package com.linkedin.parseq.internal.trace;

import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.RelationshipEntry;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceEntry;
import com.linkedin.parseq.trace.Trace;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MutableTrace implements Trace
{
  private long _snapshotNanos;
  private final Map<Integer, ShallowTraceEntry> _traces;
  private final Map<EdgeId, Set<Relationship>> _relationships;

  public MutableTrace(Trace trace)
  {
    this();

    setSnapshotNanos(trace.getSnapshotNanos());

    for (ShallowTraceEntry entry : trace.traces())
    {
      addTrace(entry.getId(), entry.getShallowTrace());
    }

    for (RelationshipEntry entry : trace.relationships())
    {
      addRelationship(entry.getFromId(), entry.getToId(), entry.getRelationship());
    }
  }

  public MutableTrace()
  {
    _snapshotNanos = System.nanoTime();
    _traces = new HashMap<Integer, ShallowTraceEntry>();
    _relationships = new HashMap<EdgeId, Set<Relationship>>();
  }

  public void setSnapshotNanos(long snapshotNanos)
  {
    _snapshotNanos = snapshotNanos;
  }

  @Override
  public long getSnapshotNanos()
  {
    return _snapshotNanos;
  }

  @Override
  public Collection<ShallowTraceEntry> traces()
  {
    return Collections.unmodifiableCollection(_traces.values());
  }

  public void addTrace(int traceId, ShallowTrace shallowTrace)
  {
    ArgumentUtil.notNull(shallowTrace, "trace");
    if (_traces.containsKey(traceId))
    {
      throw new IllegalArgumentException("Graph already contains trace with id: " + traceId);
    }
    _traces.put(traceId, new ShallowTraceEntry(traceId, shallowTrace));
  }

  public ShallowTrace getShallowTrace(int id)
  {
    return _traces.get(id).getShallowTrace();
  }

  @Override
  public Collection<RelationshipEntry> relationships()
  {
    final Collection<RelationshipEntry> relationships = new HashSet<RelationshipEntry>();
    for (Map.Entry<EdgeId, Set<Relationship>> entry : _relationships.entrySet())
    {
      for (Relationship relationship : entry.getValue())
      {
        relationships.add(new RelationshipEntry(entry.getKey()._fromId, entry.getKey()._toId, relationship));
      }
    }
    return relationships;
  }

  public void addRelationship(int fromId, int toId, Relationship relationship)
  {
    assertHasTrace(fromId);
    assertHasTrace(toId);

    final EdgeId edgeId = new EdgeId(fromId, toId);
    Set<Relationship> relationships = _relationships.get(edgeId);
    if (relationships == null)
    {
      relationships = new HashSet<Relationship>();
      _relationships.put(edgeId, relationships);
    }

    relationships.add(relationship);
  }

  @Override
  public Set<Relationship> getRelationships(int fromId, int toId)
  {
    return _relationships.get(new EdgeId(fromId, toId));
  }

  public ImmutableTrace freeze()
  {
    return new ImmutableTrace(this);
  }

  private void assertHasTrace(int traceId)
  {
    if (!_traces.containsKey(traceId))
    {
      throw new IllegalArgumentException("Trace graph does not contain trace: " + traceId);
    }
  }

  private static class EdgeId
  {
    private final int _fromId;
    private final int _toId;

    EdgeId(int fromId, int toId)
    {
      _fromId = fromId;
      _toId = toId;
    }


    @Override
    public boolean equals(Object o)
    {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      EdgeId edgeId = (EdgeId) o;

      if (_fromId != edgeId._fromId) return false;
      if (_toId != edgeId._toId) return false;

      return true;
    }

    @Override
    public int hashCode()
    {
      int result = _fromId;
      result = 31 * result + _toId;
      return result;
    }
  }
}
