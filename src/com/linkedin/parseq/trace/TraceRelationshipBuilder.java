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

import com.linkedin.parseq.internal.ArgumentUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TraceRelationshipBuilder is a helper for building {@link Trace} instances.
 * <p/>
 * This class is not thread-safe.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class TraceRelationshipBuilder<K>
{
  private final Map<K, ShallowTrace> _traces = new HashMap<K, ShallowTrace>();
  private final Map<K, Set<Related<K>>> _outEdges = new HashMap<K, Set<Related<K>>>();

  public boolean containsKey(K key)
  {
    return _traces.containsKey(key);
  }

  public ShallowTrace getTrace(K key)
  {
    return _traces.get(key);
  }

  public TraceRelationshipBuilder<K> addTrace(K key, ShallowTrace trace)
  {
    ArgumentUtil.notNull(key, "key");
    ArgumentUtil.notNull(trace, "trace");

    if (containsKey(key))
    {
      throw new IllegalArgumentException("Task has already been added to trace builder: " + key);
    }

    _traces.put(key, trace);
    return this;
  }

  public TraceRelationshipBuilder<K> replaceTrace(K key, ShallowTrace newTrace)
  {
    ArgumentUtil.notNull(key, "key");
    ArgumentUtil.notNull(newTrace, "trace");

    ensureContainsKey(key);
    _traces.put(key, newTrace);

    return this;
  }

  public Set<Related<K>> getRelationships(K key)
  {
    Set<Related<K>> related = _outEdges.get(key);
    related = related == null
        ? Collections.<Related<K>>emptySet()
        : Collections.unmodifiableSet(related);
    return related;
  }

  public TraceRelationshipBuilder<K> addRelationship(Relationship relationship, K from, K to)
  {
    return addRelationship(relationship.name(), from, to);
  }

  public TraceRelationshipBuilder<K> addRelationship(String relationship, K from, K to)
  {
    validateRelationship(relationship, from, to);

    getOrInitEdges(_outEdges, from).add(new Related<K>(relationship, to));
    return this;
  }

  public TraceRelationshipBuilder<K> removeRelationship(Relationship relationship, K from, K to)
  {
    return removeRelationship(relationship.name(), from, to);
  }

  public boolean containsRelationship(String relationship, K from, K to)
  {
    validateRelationship(relationship, from, to);

    final Set<Related<K>> related = _outEdges.get(from);
    if (related == null || !related.contains(new Related<K>(relationship, to)))
    {
      return false;
    }
    return true;
  }

  public TraceRelationshipBuilder<K> removeRelationship(String relationship, K from, K to)
  {
    validateRelationship(relationship, from, to);

    final Set<Related<K>> related = _outEdges.get(from);
    if (related == null || !related.remove(new Related<K>(relationship, to)))
    {
      throw new IllegalArgumentException("No such relationship " + relationship + " from " + from + " to " + to);
    }

    return this;
  }

  /**
   * Returns {@code true} if there are no traces in this builder.
   *
   * @return {@code true} if there are no traces in this builder.
   */
  public boolean isEmpty()
  {
    return _traces.isEmpty();
  }

  /**
   * Builds a trace with the expectation that there is a single root in the
   * graph. This root will be detected by the trace builder.
   *
   * @return the trace from this builder
   * @throws IllegalStateException if there is more than one root
   */
  public Trace buildRoot()
  {
    final Set<K> roots = new HashSet<K>(_traces.keySet());
    for (Set<Related<K>> relatedSet : _outEdges.values())
    {
      for (Related<K> related : relatedSet)
      {
        roots.remove(related.getRelated());
      }
    }

    if (roots.isEmpty())
    {
      throw new IllegalStateException("No root found - is there a cycle?");
    }
    else if (roots.size() > 1)
    {
      throw new IllegalStateException("More than one root found: " + roots);
    }

    return buildTrace(roots.iterator().next());
  }

  /**
   * Builds a trace for the given task.
   *
   * @param key the key for which to build a trace
   * @return the constructed trace
   */
  public Trace buildTrace(K key)
  {
    ArgumentUtil.notNull(key, "task");
    return buildTrace(key, new HashSet<K>(), new HashMap<K, Trace>());
  }


  private void validateRelationship(String relationship, K from, K to)
  {
    ArgumentUtil.notNull(relationship, "relationship");
    ArgumentUtil.notNull(from, "from");
    ArgumentUtil.notNull(to, "to");

    ensureContainsKey(from);
    ensureContainsKey(to);
  }

  private void ensureContainsKey(final K key)
  {
    if (!containsKey(key))
    {
      throw new IllegalArgumentException("Key has not been added to trace builder: " + key);
    }
  }

  private Set<Related<K>> getOrInitEdges(final Map<K, Set<Related<K>>> edgeMap,
                                         final K key)
  {
    Set<Related<K>> edges = edgeMap.get(key);
    if (edges == null)
    {
      edges = new HashSet<Related<K>>();
      edgeMap.put(key, edges);
    }
    return edges;
  }

  private Trace buildTrace(final K key,
                           final Set<K> inProcess,
                           final Map<K, Trace> traceMap)
  {
    final Trace cachedTrace = traceMap.get(key);
    if (cachedTrace != null)
    {
      return cachedTrace;
    }

    if (inProcess.contains(key))
    {
      throw new IllegalStateException("Detected a cycle that includes: " + inProcess);
    }

    inProcess.add(key);

    final ShallowTrace shallowTrace = _traces.get(key);
    final Set<Related<K>> taskOutEdges = _outEdges.get(key);

    final Set<Related<Trace>> related;
    if (taskOutEdges == null || taskOutEdges.isEmpty())
    {
      related = Collections.emptySet();
    }
    else
    {
      related = new HashSet<Related<Trace>>(taskOutEdges.size());
      for (Related<K> taskEdge : taskOutEdges)
      {
        related.add(new Related<Trace>(taskEdge.getRelationship(),
                                       buildTrace(taskEdge.getRelated(), inProcess, traceMap)));
      }
    }

    inProcess.remove(key);
    final Trace trace = new TraceImpl(shallowTrace, related);
    traceMap.put(key, trace);

    return trace;
  }
}
