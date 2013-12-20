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

package com.linkedin.parseq.internal.trace;

import com.linkedin.parseq.trace.Related;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.Trace;

import java.util.Map;
import java.util.Set;

/**
 * @author Chi Chan (ckchan@linkedin.com)
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
/* package private */ class TraceImpl implements Trace
{
  private final ShallowTrace _shallowTrace;
  private final Set<Related<Trace>> _related;

  public TraceImpl(final ShallowTrace trace,
                   final Set<Related<Trace>> related)
  {
    assert trace != null;
    assert related != null;
    _shallowTrace = trace;
    _related = related;
  }

  public String getName()
  {
    return _shallowTrace.getName();
  }

  public boolean getHidden()
  {
    return _shallowTrace.getHidden();
  }

  @Override
  public boolean getSystemHidden()
  {
    return _shallowTrace.getSystemHidden();
  }

  public String getValue()
  {
    return _shallowTrace.getValue();
  }

  public ResultType getResultType()
  {
    return _shallowTrace.getResultType();
  }

  public Long getStartNanos()
  {
    return _shallowTrace.getStartNanos();
  }

  public Long getPendingNanos()
  {
    return _shallowTrace.getPendingNanos();
  }

  public Long getEndNanos()
  {
    return _shallowTrace.getEndNanos();
  }

  public Set<Related<Trace>> getRelated()
  {
    return _related;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final TraceImpl trace = (TraceImpl) o;

    return _shallowTrace.equals(trace._shallowTrace) &&
           _related.equals(trace._related);
  }

  @Override
  public int hashCode()
  {
    int result = _shallowTrace.hashCode();
    result = 31 * result + _related.hashCode();
    return result;
  }

  @Override
  public String toString()
  {
    return "TraceImpl{" +
        "_shallowTrace=" + _shallowTrace +
        ", _related=" + _related +
        '}';
  }
}
