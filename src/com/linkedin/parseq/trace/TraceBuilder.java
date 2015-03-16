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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.linkedin.parseq.internal.ArgumentUtil;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TraceBuilder {

  private final Set<TraceRelationship> _relationships;
  private final Map<Long, ShallowTraceBuilder> _traceBuilders;

  public TraceBuilder() {
    _relationships = Collections.newSetFromMap(new ConcurrentHashMap<TraceRelationship, Boolean>());
    _traceBuilders = new ConcurrentHashMap<>();
  }

  public void addShallowTrace(final ShallowTraceBuilder shallowTrace) {
    _traceBuilders.putIfAbsent(shallowTrace.getId(), shallowTrace);
  }

  public void addRelationship(final Relationship relationship, final ShallowTraceBuilder from, final ShallowTraceBuilder to) {
    ArgumentUtil.requireNotNull(relationship, "relationship");
    ArgumentUtil.requireNotNull(from, "from");
    ArgumentUtil.requireNotNull(to, "to");
    addShallowTrace(from);
    addShallowTrace(to);
    final TraceRelationship rel = new TraceRelationship(from.getId(), to.getId(), relationship);
    if (!_relationships.add(rel)) {
      throw new IllegalArgumentException("Relationship already exists: " + rel);
    }
  }

  public boolean containsRelationship(final TraceRelationship relationship) {
    return _relationships.contains(relationship);
  }

  private void findPotentialParents(final Map<Long, Set<Long>> potentialParents, final Set<TraceRelationship> relationships) {
    for (TraceRelationship rel: relationships) {
      switch(rel.getRelationhsip())
      {
        case SUCCESSOR_OF:
          break;
        case POSSIBLE_SUCCESSOR_OF:
          break;
        case CHILD_OF:
          potentialParents.putIfAbsent(rel.getFrom(), new HashSet<>());
          potentialParents.get(rel.getFrom()).add(rel.getTo());
          break;
        case POTENTIAL_CHILD_OF:
          potentialParents.putIfAbsent(rel.getFrom(), new HashSet<>());
          potentialParents.get(rel.getFrom()).add(rel.getTo());
          break;
        case POTENTIAL_PARENT_OF:
          potentialParents.putIfAbsent(rel.getTo(), new HashSet<>());
          potentialParents.get(rel.getTo()).add(rel.getFrom());
          break;
        case PARENT_OF:
          potentialParents.putIfAbsent(rel.getTo(), new HashSet<>());
          potentialParents.get(rel.getTo()).add(rel.getFrom());
          break;
        default:
          throw new IllegalStateException("Unknown relationship type: " + rel);
      }
    }
  }

  public Trace build() {
    final Map<Long, ShallowTrace> traceMap = new HashMap<>();
    final Set<TraceRelationship> relationships = new HashSet<>();

    final Map<Long, Set<Long>> potentialParents = new HashMap<>();

    //shallow copy of relationship to avoid modifications
    final Set<TraceRelationship> frozenRelationship = new HashSet<>(_relationships);
    for (Entry<Long, ShallowTraceBuilder> entry: _traceBuilders.entrySet()) {
      traceMap.put(entry.getKey(), entry.getValue().build());
    }
    findPotentialParents(potentialParents, frozenRelationship);

    for (TraceRelationship rel: frozenRelationship) {

      switch(rel.getRelationhsip())
      {
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
            if (potentialParents.get(rel.getTo()).size() > 1) {
              relationships.add(rel);
            } else {
              relationships.remove(new TraceRelationship(rel.getFrom(), rel.getTo(), Relationship.POTENTIAL_PARENT_OF));
              relationships.add(new TraceRelationship(rel.getFrom(), rel.getTo(), Relationship.PARENT_OF));
            }
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

    return new Trace(traceMap, relationships);
  }
}
