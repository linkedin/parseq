package com.linkedin.parseq.trace;

import java.util.Collection;
import java.util.Set;

/**
 * An object that contains tracing information for a plan. A trace can be saved
 * with a {@link com.linkedin.parseq.trace.codec.TraceCodec}, such as
 * {@link com.linkedin.parseq.trace.codec.json.JsonTraceCodec}.
 */
public interface Trace
{
  /**
   * Returns the value of {@link System#nanoTime()} at the time that this trace
   * was generated.
   */
  long getSnapshotNanos();

  /**
   * Returns all of the shallow traces entries registered with this trace. The
   * shallow trace entry includes the shallow trace itself and the id, which
   * can be used with {@link #getShallowTrace(int)} and
   * {@link #getRelationships(int, int)}.
   */
  Collection<ShallowTraceEntry> traces();

  /**
   * Returns the shallow trace for the given id or {@code null} if there is no
   * registered shallow trace with the id.
   */
  ShallowTrace getShallowTrace(int id);

  /**
   * Returns all of the relationship entries registered with this trace. A
   * relationship entry contains the ids of the related traces and their
   * relationship type.
   */
  Collection<RelationshipEntry> relationships();

  /**
   * Returns a set of all relationships from {@code fromId} to {@code toId}.
   */
  Set<Relationship> getRelationships(int fromId, int toId);
}
