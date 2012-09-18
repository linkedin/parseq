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
import java.util.Map;

/**
 * A shallow trace is a trace without any relationship information. Use
 * {@link ShallowTraceBuilder} to construct new instances of this class.
 * <p/>
 * This class is immutable and thread-safe.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class ShallowTrace
{
  private final String _name;
  private final boolean _hidden;
  private final boolean _systemHidden;
  private final ResultType _resultType;
  private final String _value;
  private final Long _startNanos;
  private final Long _endNanos;
  private final Map<String, String> _attributes;

  /* package private */ ShallowTrace(final String name,
                                     final boolean hidden,
                                     final boolean systemHidden,
                                     final ResultType resultType,
                                     final String value,
                                     final Long startNanos,
                                     final Long endNanos,
                                     final Map<String, String> attributes)
  {
    assert name != null;
    assert resultType != null;

    _name = name;
    _hidden = hidden;
    _value = value;
    _resultType = resultType;
    _startNanos = startNanos;
    _endNanos = endNanos;
    _systemHidden = systemHidden;

    Map<String, String> attributeMap = new HashMap<String, String>(attributes);
    _attributes = Collections.unmodifiableMap(attributeMap);

    switch (resultType)
    {
      case EARLY_FINISH:
        if (value != null)
        {
          throw new IllegalArgumentException("value cannot be set if the task is finished early");
        }
        ArgumentUtil.notNull(startNanos, "startNanos");
        break;
      case ERROR:
      case SUCCESS:
        ArgumentUtil.notNull(startNanos, "startNanos");
        break;
      case UNFINISHED:
        if (value != null)
        {
          throw new IllegalArgumentException("value cannot be set if the task is UNFINISHED");
        }
        break;
      default:
        throw new IllegalArgumentException("Unexpected result type: " + resultType);
    }

    if (startNanos != null && resultType != ResultType.UNFINISHED)
    {
      ArgumentUtil.notNull(endNanos, "endNanos");
    }
  }

  public String getName()
  {
    return _name;
  }

  public boolean getHidden()
  {
    return _hidden;
  }

  public boolean getSystemHidden()
  {
    return _systemHidden;
  }

  public String getValue()
  {
    return _value;
  }

  public ResultType getResultType()
  {
    return _resultType;
  }

  public Long getStartNanos()
  {
    return _startNanos;
  }

  public Long getEndNanos()
  {
    return _endNanos;
  }

  public Map<String,String> getAttributes()
  {
    return _attributes;
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final ShallowTrace trace = (ShallowTrace) o;

    if (_attributes.size() != trace.getAttributes().size())
      return false;

    for(Map.Entry<String, String> entry : trace.getAttributes().entrySet())
    {
      if (!_attributes.containsKey(entry.getKey()))
        return false;

      if (!_attributes.get(entry.getKey()).equals(entry.getValue()))
        return false;
    }
    if (_endNanos != null ? !_endNanos.equals(trace._endNanos) : trace._endNanos != null)
      return false;
    if (!_name.equals(trace._name)) return false;
    if (_hidden != trace.getHidden()) return false;
    if (_systemHidden != trace.getSystemHidden()) return false;
    if (_resultType != trace._resultType) return false;
    if (_startNanos != null ? !_startNanos.equals(trace._startNanos) : trace._startNanos != null)
      return false;
    if (_value != null ? !_value.equals(trace._value) : trace._value != null)
      return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = _name.hashCode();
    result = 31 * result + (_hidden == true ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode());
    result = 31 * result + (_systemHidden == true ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode());
    result = 31 * result + _resultType.hashCode();
    result = 31 * result + (_value != null ? _value.hashCode() : 0);
    result = 31 * result + (_startNanos != null ? _startNanos.hashCode() : 0);
    result = 31 * result + (_endNanos != null ? _endNanos.hashCode() : 0);

    for(Map.Entry<String, String> attribute : _attributes.entrySet())
    {
      result = 31 * result + attribute.hashCode();
    }
    return result;
  }

  @Override
  public String toString()
  {
    return "ShallowTrace{" +
        "_name='" + _name + '\'' +
        ", _hidden=" + _hidden +
        ", _systemHidden=" + _systemHidden +
        ", _resultType=" + _resultType +
        ", _value='" + _value + '\'' +
        ", _startNanos=" + _startNanos +
        ", _endNanos=" + _endNanos +
        ", _attributes=" + _attributes +
        '}';
  }
}
