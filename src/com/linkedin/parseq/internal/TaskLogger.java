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

package com.linkedin.parseq.internal;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTrace;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The trace logger will log to the first logger in the following sequence that
 * accepts debug or trace logging.
 *
 * <ol>
 *   <li>plan logger - logs all tasks that are created by a root task of a specified class</li>
 *   <li>root logger - logs all root tasks</li>
 *   <li>all logger - logs all tasks</li>
 * </ol>
 *
 * Use the trace level to get additional logging beyond that provided by the debug level.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TaskLogger
{
  private static final String START_TASK_FORMAT = "[plan={}]: Starting task '{}'";
  private static final String END_TASK_DEBUG_FORMAT = "[plan={}]: Ending task '{}'. Elapsed: {}ms, Result type: {}.";
  private static final String END_TASK_TRACE_FORMAT = "[plan={}]: Ending task '{}'. Elapsed: {}ms, Result type: {}. Value: {}.";

  private static final AtomicLong _nextPlanId = new AtomicLong();

  /**
   * The root task for the plan. Used to determine if a given task should be
   * logged using the plan logger.
   */
  private final Task<?> _root;

  /** A locally assigned plan id to disambiguate plan logging. */
  private final long _planId;

  /** Logs every trace it finds. */
  private final Logger _allLogger;

  /** Logs only root traces. */
  private final Logger _rootLogger;

  /** Logs all tasks for this this plan. */
  private final Logger _planLogger;

  public TaskLogger(final Task<?> root, final Logger allLogger,
                    final Logger rootLogger, final Logger planLogger)
  {
    _allLogger = allLogger;
    _rootLogger = rootLogger;
    _planLogger = planLogger;
    _root = root;
    _planId = _nextPlanId.getAndIncrement();
  }

  public void logTaskStart(final Task<?> task)
  {
    if (_planLogger.isDebugEnabled())
    {
      _planLogger.debug(START_TASK_FORMAT, _planId, task.getName());
    }
    else if (_rootLogger.isDebugEnabled() && _root == task)
    {
      _rootLogger.debug(START_TASK_FORMAT, _planId, task.getName());
    }
    else if (_allLogger.isDebugEnabled())
    {
      _allLogger.debug(START_TASK_FORMAT, _planId, task.getName());
    }
  }

  public void logTaskEnd(final Task<?> task)
  {
    if (_planLogger.isTraceEnabled())
    {
      _planLogger.trace(END_TASK_TRACE_FORMAT,
          new Object[]{_planId, task.getName(),
              elapsedMillis(task),
              ResultType.fromTask(task), stringValue(task)});
    }
    else if (_planLogger.isDebugEnabled())
    {
      _planLogger.debug(END_TASK_DEBUG_FORMAT,
                        new Object[] {_planId, task.getName(),
                                      elapsedMillis(task), ResultType.fromTask(task)});
    }
    else if (_root == task && _rootLogger.isTraceEnabled())
    {
      _rootLogger.trace(END_TASK_TRACE_FORMAT,
          new Object[]{_planId, task.getName(),
              elapsedMillis(task),
              ResultType.fromTask(task), stringValue(task)});
    }
    else if (_root == task && _rootLogger.isDebugEnabled())
    {
      _rootLogger.debug(END_TASK_DEBUG_FORMAT,
                        new Object[] {_planId, task.getName(),
                            elapsedMillis(task), ResultType.fromTask(task)});
    }
    else if (_allLogger.isTraceEnabled())
    {
      _allLogger.trace(END_TASK_TRACE_FORMAT,
                       new Object[] {_planId, task.getName(),
                           elapsedMillis(task),
                           ResultType.fromTask(task), stringValue(task)});
    }
    else if (_allLogger.isDebugEnabled())
    {
      _allLogger.debug(END_TASK_DEBUG_FORMAT,
                       new Object[] {_planId, task.getName(),
                           elapsedMillis(task), ResultType.fromTask(task)});
    }
  }

  private String stringValue(final Task<?> task)
  {
    if (task.isFailed())
    {
      return task.getError().toString();
    }
    else
    {
      final Object value = task.get();
      if (value != null)
      {
        return value.toString();
      }
      else
      {
        return "null";
      }
    }
  }

  private long elapsedMillis(final Task<?> task)
  {
    final ShallowTrace trace = task.getShallowTrace();
    return (trace.getEndNanos() - trace.getStartNanos()) / 1000000;
  }
}
