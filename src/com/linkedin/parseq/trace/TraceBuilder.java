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

import java.util.ArrayList;
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

  static final long UNKNOWN_PLAN_ID = -1L;
  static final String UNKNOWN_PLAN_CLASS = "unknown";
  private static final int INITIAL_RELATIONSHIP_ARRAY_SIZE = 128;
  private static final int INITIAL_BUILDER_ARRAY_SIZE = 128;

  private final int _maxRelationshipsPerTrace;

  private final String _planClass;

  private final Long _planId;

  private final ArrayList<TraceRelationship> _relationships;
  private final ArrayList<ShallowTraceBuilder> _traceBuilders;

  // TODO: this constructor should be removed.
  // Need to fix in the next major version release.
  public TraceBuilder(int maxRelationshipsCount) {
    this(maxRelationshipsCount, UNKNOWN_PLAN_CLASS, UNKNOWN_PLAN_ID);
  }

  public TraceBuilder(int maxRelationshipsCount, String planClass, Long planId) {
    _relationships = new ArrayList<>(INITIAL_RELATIONSHIP_ARRAY_SIZE);
    _traceBuilders = new ArrayList<>(INITIAL_BUILDER_ARRAY_SIZE);
    _maxRelationshipsPerTrace = maxRelationshipsCount;
    _planClass = planClass;
    _planId = planId;
  }

  public synchronized void addShallowTrace(final ShallowTraceBuilder shallowTrace) {
    if (_traceBuilders.size() < _maxRelationshipsPerTrace) {
      _traceBuilders.add(shallowTrace);
    }
  }

  public synchronized void addRelationship(final Relationship relationship, final ShallowTraceBuilder from,
      final ShallowTraceBuilder to) {
    if (_relationships.size() < _maxRelationshipsPerTrace) {
      TraceRelationship rel = new TraceRelationship(from, to, relationship);
      _relationships.add(rel);
    }
  }

  public synchronized Trace build() {

    final Map<Long, ShallowTrace> traceMap = new HashMap<>();
    final Set<TraceRelationship> relationships = new HashSet<>();

    for (ShallowTraceBuilder builder : _traceBuilders) {
      traceMap.put(builder.getId(), builder.build());
    }

    for (TraceRelationship rel : _relationships) {

      traceMap.computeIfAbsent(rel._from.getId(), key -> rel._from.build());
      traceMap.computeIfAbsent(rel._to.getId(), key -> rel._to.build());

      switch (rel.getRelationhsip()) {
        case SUCCESSOR_OF:
          relationships.remove(new TraceRelationship(rel._from, rel._to, Relationship.POSSIBLE_SUCCESSOR_OF));
          relationships.add(rel);
          break;
        case POSSIBLE_SUCCESSOR_OF:
          if (!relationships.contains(new TraceRelationship(rel._from, rel._to, Relationship.SUCCESSOR_OF))) {
            relationships.add(rel);
          }
          break;
        case CHILD_OF:
          relationships.remove(new TraceRelationship(rel._to, rel._from, Relationship.POTENTIAL_PARENT_OF));
          relationships.add(new TraceRelationship(rel._to, rel._from, Relationship.PARENT_OF));
          break;
        case POTENTIAL_CHILD_OF:
          if (!relationships.contains(new TraceRelationship(rel._to, rel._from, Relationship.PARENT_OF))) {
            relationships.add(new TraceRelationship(rel._to, rel._from, Relationship.POTENTIAL_PARENT_OF));
          }
          break;
        case POTENTIAL_PARENT_OF:
          if (!relationships.contains(new TraceRelationship(rel._from, rel._to, Relationship.PARENT_OF))) {
            relationships.add(rel);
          }
          break;
        case PARENT_OF:
          relationships.remove(new TraceRelationship(rel._from, rel._to, Relationship.POTENTIAL_PARENT_OF));
          relationships.add(rel);
          break;
        default:
          throw new IllegalStateException("Unknown relationship type: " + rel);
      }
    }

    return new Trace(traceMap, relationships, _planClass, _planId);
  }
}
