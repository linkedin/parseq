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

/**
 * A relationship with another node of a specified type.
 * <p/>
 * This class is immutable and thread-safe.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class Related<T>
{
  private final String _relationship;
  private final T _related;

  public Related(final Relationship relationship, final T related)
  {
    _relationship = relationship.name();
    _related = related;
  }

  public Related(final String relationship, final T related)
  {
    _relationship = relationship;
    _related = related;
  }

  public String getRelationship()
  {
    return _relationship;
  }

  public T getRelated()
  {
    return _related;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Related<?> related = (Related<?>) o;

    return _relationship.equals(related._relationship) &&
           _related.equals(related._related);
  }

  @Override
  public int hashCode()
  {
    int result = _relationship.hashCode();
    result = 31 * result + _related.hashCode();
    return result;
  }

  @Override
  public String toString()
  {
    return "Related{" +
        "_relationship='" + _relationship + '\'' +
        ", _related=" + _related +
        '}';
  }
}
