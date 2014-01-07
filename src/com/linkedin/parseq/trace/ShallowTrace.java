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
  private final Long _pendingNanos;
  private final Long _endNanos;

  /* package private */ ShallowTrace(final String name,
                                     final boolean hidden,
                                     final boolean systemHidden,
                                     final ResultType resultType,
                                     final String value,
                                     final Long startNanos,
                                     final Long pendingNanos,
                                     final Long endNanos)
  {
    assert name != null;
    assert resultType != null;

    _name = name;
    _hidden = hidden;
    _value = value;
    _resultType = resultType;
    _startNanos = startNanos;
    _pendingNanos = pendingNanos;
    _endNanos = endNanos;
    _systemHidden = systemHidden;

    switch (resultType)
    {
      case EARLY_FINISH:
        if (value != null)
        {
            throw new IllegalArgumentException("value cannot be set if the task is finished early");
        }
        ArgumentUtil.notNull(startNanos, "startNanos");
        ArgumentUtil.notNull(pendingNanos, "pendingNanos");
        ArgumentUtil.notNull(endNanos, "endNanos");
        break;
      case ERROR:
      case SUCCESS:
        ArgumentUtil.notNull(startNanos, "startNanos");
        ArgumentUtil.notNull(pendingNanos, "pendingNanos");
        ArgumentUtil.notNull(endNanos, "endNanos");
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

  public Long getPendingNanos()
  {
    return _pendingNanos;
  }

  public Long getEndNanos()
  {
    return _endNanos;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ShallowTrace that = (ShallowTrace) o;

    if (_hidden != that._hidden) return false;
    if (_systemHidden != that._systemHidden) return false;
    if (_pendingNanos != null ? !_pendingNanos.equals(that._pendingNanos) : that._pendingNanos != null)
      return false;
    if (_endNanos != null ? !_endNanos.equals(that._endNanos) : that._endNanos != null)
      return false;
    if (!_name.equals(that._name)) return false;
    if (_resultType != that._resultType) return false;
    if (_startNanos != null ? !_startNanos.equals(that._startNanos) : that._startNanos != null)
      return false;
    if (_value != null ? !_value.equals(that._value) : that._value != null)
      return false;

    return true;
  }

  @Override
  public int hashCode()
  {
    int result = _name.hashCode();
    result = 31 * result + (_hidden ? 1 : 0);
    result = 31 * result + (_systemHidden ? 1 : 0);
    result = 31 * result + _resultType.hashCode();
    result = 31 * result + (_value != null ? _value.hashCode() : 0);
    result = 31 * result + (_startNanos != null ? _startNanos.hashCode() : 0);
    result = 31 * result + (_pendingNanos != null ? _pendingNanos.hashCode() : 0);
    result = 31 * result + (_endNanos != null ? _endNanos.hashCode() : 0);
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
        ", _pendingNanos=" + _pendingNanos +
        ", _endNanos=" + _endNanos +
        '}';
  }
}
