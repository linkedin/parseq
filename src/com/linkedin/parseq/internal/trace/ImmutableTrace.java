package com.linkedin.parseq.internal.trace;

import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.RelationshipEntry;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceEntry;
import com.linkedin.parseq.trace.Trace;

import java.util.Collection;
import java.util.Set;

public class ImmutableTrace implements Trace
{
  private final MutableTrace _traceBuilder;

  /* package private */ ImmutableTrace(Trace trace)
  {
    _traceBuilder = new MutableTrace(trace);
  }

  public long getSnapshotNanos()
  {
    return _traceBuilder.getSnapshotNanos();
  }

  public Collection<ShallowTraceEntry> traces()
  {
    return _traceBuilder.traces();
  }

  public ShallowTrace getShallowTrace(int id)
  {
    return _traceBuilder.getShallowTrace(id);
  }

  public Collection<RelationshipEntry> relationships()
  {
    return _traceBuilder.relationships();
  }

  public Set<Relationship> getRelationships(int fromId, int toId)
  {
    return _traceBuilder.getRelationships(fromId, toId);
  }
}
