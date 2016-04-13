/*
 * Copyright 2012 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.parseq.trace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TraceBuilder {

  private final int _maxRelationshipsPerTrace;

  private final String _planClass;

  private final Long _planId;

  private static class RefCounted<T> {
    public RefCounted(int refCount, T value) {
      _refCount = refCount;
      _value = value;
    }
    int _refCount;
    T _value;
  }

  private final LinkedHashSet<TraceRelationship> _relationships;
  private final Map<Long, RefCounted<ShallowTraceBuilder>> _traceBuilders;

  public TraceBuilder(int maxRelationshipsCount, String planClass, Long planId) {
    _relationships = new LinkedHashSet<>();
    _traceBuilders = new HashMap<>();
    _maxRelationshipsPerTrace = maxRelationshipsCount;
    _planClass = planClass;
    _planId = planId;
  }

  public synchronized void addShallowTrace(final ShallowTraceBuilder shallowTrace) {
    _traceBuilders.putIfAbsent(shallowTrace.getId(), new RefCounted<>(0, shallowTrace));
  }

  public synchronized void addRelationship(final Relationship relationship, final ShallowTraceBuilder from,
      final ShallowTraceBuilder to) {
    if (_relationships.size() == _maxRelationshipsPerTrace) {
      TraceRelationship r = _relationships.iterator().next();
      _relationships.remove(r);
      decreaseRefCount(r.getFrom());
      decreaseRefCount(r.getTo());
    }
    addShallowTrace(from);
    addShallowTrace(to);
    final TraceRelationship rel = new TraceRelationship(from.getId(), to.getId(), relationship);
    _relationships.add(rel);
    increaseRefCount(from.getId());
    increaseRefCount(to.getId());
  }

  private void decreaseRefCount(Long id) {
    RefCounted<ShallowTraceBuilder> traceBuilderRefCount = _traceBuilders.get(id);
    traceBuilderRefCount._refCount--;
    if (traceBuilderRefCount._refCount == 0) {
      _traceBuilders.remove(id);
    }
  }

  private void increaseRefCount(Long id) {
    _traceBuilders.get(id)._refCount++;
  }

  public synchronized boolean containsRelationship(final TraceRelationship relationship) {
    return _relationships.contains(relationship);
  }

  public synchronized Trace build() {

    final Map<Long, ShallowTrace> traceMap = new HashMap<>();
    final Set<TraceRelationship> relationships = new HashSet<>();

    for (Entry<Long, RefCounted<ShallowTraceBuilder>> entry : _traceBuilders.entrySet()) {
      traceMap.put(entry.getKey(), entry.getValue()._value.build());
    }

    for (TraceRelationship rel : _relationships) {

      switch (rel.getRelationhsip()) {
        case SUCCESSOR_OF:
          relationships.remove(new TraceRelationship(rel.getFrom(), rel.getTo(), Relationship.POSSIBLE_SUCCESSOR_OF));
          relationships.add(rel);
          break;
        case POSSIBLE_SUCCESSOR_OF:
          if (!relationships.contains(new TraceRelationship(rel.getFrom(), rel.getTo(), Relationship.SUCCESSOR_OF))) {
            relationships.add(rel);
          }
          break;
        case CHILD_OF:
          relationships.remove(new TraceRelationship(rel.getTo(), rel.getFrom(), Relationship.POTENTIAL_PARENT_OF));
          relationships.add(new TraceRelationship(rel.getTo(), rel.getFrom(), Relationship.PARENT_OF));
          break;
        case POTENTIAL_CHILD_OF:
          if (!relationships.contains(new TraceRelationship(rel.getTo(), rel.getFrom(), Relationship.PARENT_OF))) {
            relationships.add(new TraceRelationship(rel.getTo(), rel.getFrom(), Relationship.POTENTIAL_PARENT_OF));
          }
          break;
        case POTENTIAL_PARENT_OF:
          if (!relationships.contains(new TraceRelationship(rel.getFrom(), rel.getTo(), Relationship.PARENT_OF))) {
            relationships.add(rel);
          }
          break;
        case PARENT_OF:
          relationships.remove(new TraceRelationship(rel.getFrom(), rel.getTo(), Relationship.POTENTIAL_PARENT_OF));
          relationships.add(rel);
          break;
        default:
          throw new IllegalStateException("Unknown relationship type: " + rel);
      }
    }

    return new Trace(traceMap, relationships, _planClass, _planId);
  }
}
