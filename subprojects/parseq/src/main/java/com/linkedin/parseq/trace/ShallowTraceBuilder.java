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
import java.util.Map;

import com.linkedin.parseq.internal.ArgumentUtil;


/**
 * Use this class to build new {@link ShallowTraceImp} instances.
 * <p>
 * This class is thread-safe.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class ShallowTraceBuilder {

  private final long _id;
  private volatile String _name;
  private volatile boolean _hidden;
  private volatile String _value;
  private volatile ResultType _resultType = ResultType.UNFINISHED
  private volatile long _startNanos = -1;
  private volatile long _pendingNanos = -1;
  private volatile long _endNanos = -1;
  private volatile boolean _systemHidden;
  private Map<String, String> _attributes;
  private volatile String _taskType;

  @Deprecated
  public ShallowTraceBuilder(Long id) {
    if (id == null) {
      _id = -1;
    } else {
      _id = id;
    }
  }

  public ShallowTraceBuilder(long id) {
    _id = id;
  }

  public ShallowTraceBuilder(final ShallowTrace shallowTrace) {
    this(shallowTrace.getNativeId());
    setResultType(shallowTrace.getResultType());
    setName(shallowTrace.getName());
    setValue(shallowTrace.getValue());
    setNativeStartNanos(shallowTrace.getNativeStartNanos());
    setNativePendingNanos(shallowTrace.getNativePendingNanos());
    setNativeEndNanos(shallowTrace.getNativeEndNanos());
    setHidden(shallowTrace.getHidden());
    setSystemHidden(shallowTrace.getSystemHidden());
    Map<String, String> attributes = shallowTrace.getAttributes();
    if (!attributes.isEmpty()) {
      _attributes = new HashMap<String, String>();
      _attributes.putAll(attributes);
    }
    setTaskType(shallowTrace.getTaskType());
  }

  public boolean getHidden() {
    return _hidden;
  }

  public ShallowTraceBuilder setHidden(boolean hidden) {
    _hidden = hidden;
    return this;
  }

  public boolean getSystemHidden() {
    return _systemHidden;
  }

  public ShallowTraceBuilder setSystemHidden(boolean systemHidden) {
    _systemHidden = systemHidden;
    return this;
  }

  public ShallowTraceBuilder setName(final String name) {
    ArgumentUtil.requireNotNull(name, "name");
    _name = name;
    return this;
  }

  public ShallowTraceBuilder setValue(final String value) {
    _value = value;
    return this;
  }

  public ShallowTraceBuilder setResultType(final ResultType resultType) {
    ArgumentUtil.requireNotNull(resultType, "resultType");
    if (resultType != ResultType.UNFINISHED) {
      long nanoTime = System.nanoTime();
      if (_startNanos == -1) {
        _startNanos = nanoTime;
      }
      if (_pendingNanos == -1) {
        _pendingNanos = nanoTime;
      }
      if (_endNanos == -1) {
        _endNanos = nanoTime;
      }
    }
    _resultType = resultType;
    return this;
  }

  /**
   * @deprecated Use {@link #setNativeStartNanos(long)} instead
   */
  @Deprecated
  public ShallowTraceBuilder setStartNanos(Long startNanos) {
    if (startNanos == null) {
      return this;
    }
    _startNanos = startNanos;
    return this;
  }

  /**
   * @deprecated Use {@link #setNativePendingNanos(long)} instead
   */
  @Deprecated
  public ShallowTraceBuilder setPendingNanos(Long pendingNanos) {
    if (pendingNanos == null) {
      return this;
    }
    _pendingNanos = pendingNanos;
    return this;
  }

  /**
   * @deprecated Use {@link #setNativeEndNanos(long)} instead
   */
  @Deprecated
  public ShallowTraceBuilder setEndNanos(Long endNanos) {
    if (endNanos == null) {
      return this;
    }
    _endNanos = endNanos;
    return this;
  }

  public ShallowTraceBuilder setNativeStartNanos(long startNanos) {
    _startNanos = startNanos;
    return this;
  }

  public ShallowTraceBuilder setNativePendingNanos(long pendingNanos) {
    _pendingNanos = pendingNanos;
    return this;
  }

  public ShallowTraceBuilder setNativeEndNanos(long endNanos) {
    _endNanos = endNanos;
    return this;
  }

  public ShallowTraceBuilder setTaskType(String taskType) {
    _taskType = taskType;
    return this;
  }

  /**
   * @deprecated Use {@link #getNativeId()} instead
   */
  @Deprecated
  public Long getId() {
    if (_id == -1) {
      return null;
    }
    return _id;
  }

  public long getNativeId() {
    return _id;
  }

  public String getName() {
    return _name;
  }

  public String getValue() {
    return _value;
  }

  public ResultType getResultType() {
    return _resultType;
  }

  /**
   * @deprecated Use {@link #getNativeStartNanos()} instead
   */
  @Deprecated
  public Long getStartNanos() {
    if (_startNanos == -1) {
      return null;
    }
    return _startNanos;
  }

  /**
   * @deprecated Use {@link #getNativePendingNanos()} instead
   */
  @Deprecated
  public Long getPendingNanos() {
    if (_pendingNanos == -1) {
      return null;
    }
    return _pendingNanos;
  }

  /**
   * @deprecated Use {@link #getNativeEndNanos()} instead
   */
  @Deprecated
  public Long getEndNanos() {
    if (_endNanos == -1) {
      return null;
    }
    return _endNanos;
  }

  public long getNativeStartNanos() {
    return _startNanos;
  }

  public long getNativePendingNanos() {
    return _pendingNanos;
  }

  public long getNativeEndNanos() {
    return _endNanos;
  }

  public String getTaskType() {
    return _taskType;
  }

  public synchronized Map<String, String> getAttributes() {
    if (_attributes == null) {
      return Collections.emptyMap();
    } else {
      return Collections.unmodifiableMap(_attributes);
    }
  }

  public synchronized ShallowTraceBuilder addAttribute(String key, String value) {
    ArgumentUtil.requireNotNull(key, "key");
    ArgumentUtil.requireNotNull(value, "value");
    if (_attributes == null) {
      _attributes = new HashMap<>();
    }
    _attributes.put(key, value);
    return this;
  }

  public synchronized ShallowTraceBuilder removeAttribute(String key) {
    ArgumentUtil.requireNotNull(key, "key");
    if (_attributes != null) {
      _attributes.remove(key);
      if (_attributes.isEmpty()) {
        _attributes = null;
      }
    }
    return this;
  }

  public ShallowTraceImp build() {
    /*
     * Order of reading volatile variable matters.
     * Make sure you understand it before changing the following lines.
     * For example it is important to read _resultType before _endNanos because
     * ShallowTraceImpl expects _endNanos to be set for certain types of _resultType.
     */
    final String value = _value;
    final ResultType resultType = _resultType;
    long endNanos = _endNanos;
    final long pendingNanos = _pendingNanos;
    final long startNanos = _startNanos;
    if (resultType == ResultType.UNFINISHED && startNanos != -1 && endNanos == -1) {
      endNanos = System.nanoTime();
    }
    return new ShallowTraceImp(_id, _name, _hidden, _systemHidden, resultType, value, startNanos, pendingNanos,
        endNanos, getAttributes(), _taskType);
  }
}
