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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper to build a set of relationships.
 * This class is thread-safe.
 *
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class RelationshipBuilder<K>
{
  private final Set<Related<K>> _relationships;
  public RelationshipBuilder()
  {
    _relationships = Collections.newSetFromMap(new ConcurrentHashMap<Related<K>, Boolean>());
  }

  public Set<Related<K>> getRelationships()
  {
    return Collections.unmodifiableSet(_relationships);
  }

  public void addRelationship(Relationship relationship, K to)
  {
    ArgumentUtil.requireNotNull(relationship, "relationship");
    ArgumentUtil.requireNotNull(to, "to");
    _relationships.add(new Related<K>(relationship, to));
  }

  public void removeRelationship(Relationship relationship, K to)
  {
    ArgumentUtil.requireNotNull(relationship, "relationship");
    ArgumentUtil.requireNotNull(to, "to");
    if (!_relationships.remove(new Related<K>(relationship, to)))
    {
      throw new IllegalArgumentException("No such relationship " + relationship + " to " + to);
    }
  }
}
