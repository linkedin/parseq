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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Use this class to build new {@link ShallowTrace} instances.
 * <p/>
 * This class is thread-safe.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class ShallowTraceBuilder
{
  private volatile String _name;
  private volatile boolean _hidden;
  private volatile String _value;
  private volatile ResultType _resultType;
  private volatile Long _startNanos;
  private volatile Long _pendingNanos;
  private volatile Long _endNanos;
  private volatile boolean _systemHidden;
  private final Map<String,String> _attributes = new ConcurrentHashMap<String, String>();

  public ShallowTraceBuilder(final ShallowTrace shallowTrace)
  {
    this(shallowTrace.getName(), shallowTrace.getResultType());
    setValue(shallowTrace.getValue());
    setStartNanos(shallowTrace.getStartNanos());
    setPendingNanos(shallowTrace.getPendingNanos());
    setEndNanos(shallowTrace.getEndNanos());
    setHidden(shallowTrace.getHidden());
    setSystemHidden(shallowTrace.getSystemHidden());
    _attributes.putAll(shallowTrace.getAttributes());
  }

  public ShallowTraceBuilder(final ShallowTraceBuilder builder)
  {
    this(builder.build());
  }

  public ShallowTraceBuilder(String name, ResultType resultType)
  {
    this(resultType);
    setName(name);
  }

  public ShallowTraceBuilder(ResultType resultType)
  {
    setResultType(resultType);
  }

  public boolean getHidden()
  {
    return _hidden;
  }

  public ShallowTraceBuilder setHidden(boolean hidden)
  {
    _hidden = hidden;
    return this;
  }

  public boolean getSystemHidden()
  {
    return _systemHidden;
  }

  public ShallowTraceBuilder setSystemHidden(boolean systemHidden)
  {
    _systemHidden = systemHidden;
    return this;
  }

  public ShallowTraceBuilder setName(final String name)
  {
    ArgumentUtil.requireNotEmpty(name, "name");
    _name = name;
    return this;
  }

  public ShallowTraceBuilder setValue(String value)
  {
    _value = value;
    return this;
  }

  public ShallowTraceBuilder setResultType(ResultType resultType)
  {
    ArgumentUtil.requireNotNull(resultType, "resultType");
    _resultType = resultType;
    return this;
  }

  public ShallowTraceBuilder setStartNanos(Long startNanos)
  {
    _startNanos = startNanos;
    return this;
  }

  public ShallowTraceBuilder setPendingNanos(Long pendingNanos)
  {
    _pendingNanos = pendingNanos;
    return this;
  }

  public ShallowTraceBuilder setEndNanos(Long endNanos)
  {
    _endNanos = endNanos;
    return this;
  }

  public String getName()
  {
    return _name;
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

  public Map<String, String> getAttributes()
  {
    return Collections.unmodifiableMap(_attributes);
  }

  public ShallowTraceBuilder addAttribute(String key, String value)
  {
    ArgumentUtil.requireNotNull(key, "key");
    ArgumentUtil.requireNotNull(value, "value");
    _attributes.put(key, value);
    return this;
  }

  public ShallowTraceBuilder removeAttribute(String key)
  {
    ArgumentUtil.requireNotNull(key, "key");
    _attributes.remove(key);
    return this;
  }

  public ShallowTrace build()
  {
    return new ShallowTrace(_name, _hidden, _systemHidden, _resultType, _value, _startNanos, _pendingNanos, _endNanos, _attributes);
  }
}
