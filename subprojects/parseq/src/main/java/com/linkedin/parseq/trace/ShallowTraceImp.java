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

import java.util.Map;

import com.linkedin.parseq.internal.ArgumentUtil;


/**
 * A shallow trace is a trace without any relationship information. Use
 * {@link ShallowTraceBuilder} to construct new instances of this class.
 * <p>
 * This class is immutable and thread-safe.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class ShallowTraceImp implements ShallowTrace {
  private final long _id;
  private final String _name;
  private final boolean _hidden;
  private final boolean _systemHidden;
  private final ResultType _resultType;
  private final String _value;
  private final long _startNanos;
  private final long _pendingNanos;
  private final long _endNanos;
  private final Map<String, String> _attributes;
  private final String _taskType;

  /* package private */ ShallowTraceImp(final long id, final String name, final boolean hidden,
      final boolean systemHidden, final ResultType resultType, final String value, final long startNanos,
      final long pendingNanos, final long endNanos, final Map<String, String> attributes, String taskType) {
    assert id >= 0 : "Bad argument: id must be >= 0";
    ArgumentUtil.requireNotNull(name, "name");
    ArgumentUtil.requireNotNull(resultType, "resultType");

    _id = id;
    _name = name;
    _hidden = hidden;
    _value = value;
    _resultType = resultType;
    _startNanos = startNanos;
    _pendingNanos = pendingNanos;
    _endNanos = endNanos;
    _systemHidden = systemHidden;
    _attributes = attributes;
    _taskType = taskType;

    switch (resultType) {
      case EARLY_FINISH:
        if (value != null) {
          throw new IllegalArgumentException("value cannot be set if the task is finished early");
        }
        assert startNanos >= 0 : "Bad argument: startNanos must be >= 0";
        assert pendingNanos >= 0 : "Bad argument: pendingNanos must be >= 0";
        assert endNanos >= 0 : "Bad argument: endNanos must be >= 0";
        break;
      case ERROR:
      case SUCCESS:
        assert startNanos >= 0 : "Bad argument: startNanos must be >= 0";
        assert pendingNanos >= 0 : "Bad argument: pendingNanos must be >= 0";
        assert endNanos >= 0 : "Bad argument: endNanos must be >= 0";
        break;
      case UNFINISHED:
        if (value != null) {
          throw new IllegalArgumentException("value cannot be set if the task is UNFINISHED");
        }
        break;
      default:
        throw new IllegalArgumentException("Unexpected result type: " + resultType);
    }

    if (startNanos != -1 && resultType != ResultType.UNFINISHED) {
      assert pendingNanos >= 0 : "Bad argument: pendingNanos must be >= 0";
      assert endNanos >= 0 : "Bad argument: endNanos must be >= 0";
    }
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getName()
   */
  @Override
  public String getName() {
    return _name;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getHidden()
   */
  @Override
  public boolean getHidden() {
    return _hidden;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getSystemHidden()
   */
  @Override
  public boolean getSystemHidden() {
    return _systemHidden;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getValue()
   */
  @Override
  public String getValue() {
    return _value;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getResultType()
   */
  @Override
  public ResultType getResultType() {
    return _resultType;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getStartNanos()
   */
  @Override
  @Deprecated
  public Long getStartNanos() {
    if (_startNanos == -1) {
      return null;
    }
    return _startNanos;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getPendingNanos()
   */
  @Override
  @Deprecated
  public Long getPendingNanos() {
    if (_pendingNanos == -1) {
      return null;
    }
    return _pendingNanos;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getEndNanos()
   */
  @Override
  @Deprecated
  public Long getEndNanos() {
    if (_endNanos == -1) {
      return null;
    }
    return _endNanos;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getNativeStartNanos()
   */
  @Override
  public long getNativeStartNanos() {
    return _startNanos;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getNativePendingNanos()
   */
  @Override
  public long getNativePendingNanos() {
    return _pendingNanos;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getNativeEndNanos()
   */
  @Override
  public long getNativeEndNanos() {
    return _endNanos;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getAttributes()
   */
  @Override
  public Map<String, String> getAttributes() {
    return _attributes;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getId()
   */
  @Override
  @Deprecated
  public Long getId() {
    return _id;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getNativeId()
   */
  @Override
  public long getNativeId() {
    return _id;
  }

  /* (non-Javadoc)
   * @see com.linkedin.parseq.trace.ShallowTraceI#getTaskType()
   */
  @Override
  public String getTaskType() {
    return _taskType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_attributes == null) ? 0 : _attributes.hashCode());
    result = prime * result + ((_endNanos == -1) ? 0 : Long.hashCode(_endNanos));
    result = prime * result + (_hidden ? 1231 : 1237);
    result = prime * result + Long.hashCode(_id);
    result = prime * result + ((_name == null) ? 0 : _name.hashCode());
    result = prime * result + ((_pendingNanos == -1) ? 0 : Long.hashCode(_pendingNanos));
    result = prime * result + ((_resultType == null) ? 0 : _resultType.hashCode());
    result = prime * result + ((_startNanos == -1) ? 0 : Long.hashCode(_startNanos));
    result = prime * result + (_systemHidden ? 1231 : 1237);
    result = prime * result + ((_value == null) ? 0 : _value.hashCode());
    result = prime * result + ((_taskType == null) ? 0 : _taskType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ShallowTraceImp other = (ShallowTraceImp) obj;
    if (_attributes == null) {
      if (other._attributes != null)
        return false;
    } else if (!_attributes.equals(other._attributes))
      return false;
    if (_endNanos != other._endNanos)
      return false;
    if (_hidden != other._hidden)
      return false;
    if (_id != other._id)
      return false;
    if (_name == null) {
      if (other._name != null)
        return false;
    } else if (!_name.equals(other._name))
      return false;
    if (_pendingNanos != other._pendingNanos)
      return false;
    if (_resultType != other._resultType)
      return false;
    if (_startNanos != other._startNanos)
      return false;
    if (_systemHidden != other._systemHidden)
      return false;
    if (_value == null) {
      if (other._value != null)
        return false;
    } else if (!_value.equals(other._value))
      return false;
    if (_taskType == null) {
      if (other._taskType != null)
        return false;
    } else if (!_taskType.equals(other._taskType))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ShallowTrace [id=" + _id + ", name=" + _name + ", hidden=" + _hidden + ", systemHidden=" + _systemHidden
        + ", resultType=" + _resultType + ", value=" + _value + ", startNanos=" + _startNanos + ", pendingNanos="
        + _pendingNanos + ", endNanos=" + _endNanos + ", attributes=" + _attributes + ", taskType=" + _taskType + "]";
  }

}
