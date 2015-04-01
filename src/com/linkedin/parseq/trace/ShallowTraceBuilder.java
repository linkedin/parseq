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

  private final Long _id;
  private volatile String _name;
  private volatile boolean _hidden;
  private volatile String _value;
  private volatile ResultType _resultType;
  private volatile Long _startNanos;
  private volatile Long _pendingNanos;
  private volatile Long _endNanos;
  private volatile boolean _systemHidden;
  private Map<String, String> _attributes;

  public ShallowTraceBuilder(Long id) {
    _id = id;
  }

  public ShallowTraceBuilder(final ShallowTrace shallowTrace) {
    this(shallowTrace.getId());
    setResultType(shallowTrace.getResultType());
    setName(shallowTrace.getName());
    setValue(shallowTrace.getValue());
    setStartNanos(shallowTrace.getStartNanos());
    setPendingNanos(shallowTrace.getPendingNanos());
    setEndNanos(shallowTrace.getEndNanos());
    setHidden(shallowTrace.getHidden());
    setSystemHidden(shallowTrace.getSystemHidden());
    _attributes.putAll(shallowTrace.getAttributes());
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
      if (_startNanos == null) {
        _startNanos = nanoTime;
      }
      if (_pendingNanos == null) {
        _pendingNanos = nanoTime;
      }
      if (_endNanos == null) {
        _endNanos = nanoTime;
      }
    }
    _resultType = resultType;
    return this;
  }

  public ShallowTraceBuilder setStartNanos(Long startNanos) {
    _startNanos = startNanos;
    return this;
  }

  public ShallowTraceBuilder setPendingNanos(Long pendingNanos) {
    _pendingNanos = pendingNanos;
    return this;
  }

  public ShallowTraceBuilder setEndNanos(Long endNanos) {
    _endNanos = endNanos;
    return this;
  }

  public Long getId() {
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

  public Long getStartNanos() {
    return _startNanos;
  }

  public Long getPendingNanos() {
    return _pendingNanos;
  }

  public Long getEndNanos() {
    return _endNanos;
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
    final Long startNanos = _startNanos;
    Long endNanos = _endNanos;
    final ResultType resultType = _resultType;
    if (resultType == ResultType.UNFINISHED && startNanos != null && endNanos == null) {
      endNanos = System.nanoTime();
    }
    return new ShallowTraceImp(_id, _name, _hidden, _systemHidden, resultType, _value, startNanos, _pendingNanos,
        endNanos, getAttributes());
  }
}
