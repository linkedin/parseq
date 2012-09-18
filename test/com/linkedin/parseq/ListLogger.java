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

package com.linkedin.parseq;

import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Simple {@link org.slf4j.Logger} that allows test code to capture log
 * messages.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class ListLogger extends MarkerIgnoringBase
{
  public static final int LEVEL_TRACE = 0;
  public static final int LEVEL_DEBUG = 1;
  public static final int LEVEL_INFO = 2;
  public static final int LEVEL_WARN = 3;
  public static final int LEVEL_ERROR = 4;

  private static final long serialVersionUID = 1;

  private final ConcurrentLinkedQueue<Entry> _entries = new ConcurrentLinkedQueue<Entry>();
  private volatile int _logLevel;

  public ListLogger(final String name)
  {
    this.name = name;
    reset();
  }

  public List<Entry> getEntries()
  {
    return Collections.unmodifiableList(new ArrayList<Entry>(_entries));
  }

  public void setLogLevel(final int level)
  {
    assert level >= LEVEL_TRACE && level <= LEVEL_ERROR;
    _logLevel = level;
  }

  public final void reset()
  {
    _entries.clear();
    _logLevel = LEVEL_INFO;
  }

  @Override
  public boolean isTraceEnabled()
  {
    return isLevelEnabled(LEVEL_TRACE);
  }

  @Override
  public void trace(final String s)
  {
    log(LEVEL_TRACE, s);
  }

  @Override
  public void trace(final String s, final Object o)
  {
    log(LEVEL_TRACE, s, o);
  }

  @Override
  public void trace(final String s, final Object o, final Object o1)
  {
    log(LEVEL_TRACE, s, o, o1);
  }

  @Override
  public void trace(final String s, final Object[] objects)
  {
    log(LEVEL_TRACE, s, objects);
  }

  @Override
  public void trace(final String s, final Throwable throwable)
  {
    log(LEVEL_TRACE, s, throwable);
  }

  @Override
  public boolean isDebugEnabled()
  {
    return isLevelEnabled(LEVEL_DEBUG);
  }

  @Override
  public void debug(final String s)
  {
    log(LEVEL_DEBUG, s);
  }

  @Override
  public void debug(final String s, final Object o)
  {
    log(LEVEL_DEBUG, s, o);
  }

  @Override
  public void debug(final String s, final Object o, final Object o1)
  {
    log(LEVEL_DEBUG, s, o, o1);
  }

  @Override
  public void debug(final String s, final Object[] objects)
  {
    log(LEVEL_DEBUG, s, objects);
  }

  @Override
  public void debug(final String s, final Throwable throwable)
  {
    log(LEVEL_DEBUG, s, throwable);
  }

  @Override
  public boolean isInfoEnabled()
  {
    return isLevelEnabled(LEVEL_INFO);
  }

  @Override
  public void info(final String s)
  {
    log(LEVEL_INFO, s);
  }

  @Override
  public void info(final String s, final Object o)
  {
    log(LEVEL_INFO, s, o);
  }

  @Override
  public void info(final String s, final Object o, final Object o1)
  {
    log(LEVEL_INFO, s, o, o1);
  }

  @Override
  public void info(final String s, final Object[] objects)
  {
    log(LEVEL_INFO, s, objects);
  }

  @Override
  public void info(final String s, final Throwable throwable)
  {
    log(LEVEL_INFO, s, throwable);
  }

  @Override
  public boolean isWarnEnabled()
  {
    return isLevelEnabled(LEVEL_WARN);
  }

  @Override
  public void warn(final String s)
  {
    log(LEVEL_WARN, s);
  }

  @Override
  public void warn(final String s, final Object o)
  {
    log(LEVEL_WARN, s, o);
  }

  @Override
  public void warn(final String s, final Object[] objects)
  {
    log(LEVEL_WARN, s, objects);
  }

  @Override
  public void warn(final String s, final Object o, final Object o1)
  {
    log(LEVEL_WARN, s, o, o1);
  }

  @Override
  public void warn(final String s, final Throwable throwable)
  {
    log(LEVEL_WARN, s, throwable);
  }

  @Override
  public boolean isErrorEnabled()
  {
    return isLevelEnabled(LEVEL_ERROR);
  }

  @Override
  public void error(final String s)
  {
    log(LEVEL_ERROR, s);
  }

  @Override
  public void error(final String s, final Object o)
  {
    log(LEVEL_ERROR, s, o);
  }

  @Override
  public void error(final String s, final Object o, final Object o1)
  {
    log(LEVEL_ERROR, s, o, o1);
  }

  @Override
  public void error(final String s, final Object[] objects)
  {
    log(LEVEL_ERROR, s, objects);
  }

  @Override
  public void error(final String s, final Throwable throwable)
  {
    log(LEVEL_ERROR, s, throwable);
  }

  private void log(final int level, final String s)
  {
    _entries.add(new Entry(level, s));
  }

  private void log(final int level, final String s, final Object o)
  {
    if (isLevelEnabled(level))
    {
      final String msg = MessageFormatter.format(s, o);
      _entries.add(new Entry(level, msg));
    }
  }

  private void log(final int level, final String s, final Object o,
                   final Object o1)
  {
    if (isLevelEnabled(level))
    {
      final String msg = MessageFormatter.format(s, o, o1);
      _entries.add(new Entry(level, msg));
    }
  }

  private void log(final int level, final String s, final Object[] objects)
  {
    if (isLevelEnabled(level))
    {
      final String msg = MessageFormatter.arrayFormat(s, objects);
      _entries.add(new Entry(level, msg));
    }
  }

  private boolean isLevelEnabled(final int level)
  {
    return _logLevel <= level;
  }

  public static class Entry
  {
    private final int _level;
    private final String _message;

    public Entry(final int level, final String message)
    {
      _level = level;
      _message = message;
    }

    public int getLevel()
    {
      return _level;
    }

    public String getMessage()
    {
      return _message;
    }

    @Override
    public String toString()
    {
      return "Entry{" +
          "_level=" + _level +
          ", _message='" + _message + '\'' +
          '}';
    }
  }
}
