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
import com.linkedin.parseq.trace.ShallowTrace;
import org.slf4j.Logger;

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
public class TaskLogger implements TaskListener
{
  private static final String START_TASK_FORMAT = "[plan={}]: Starting task '{}'";
  private static final String END_TASK_DEBUG_FORMAT = "[plan={}]: Ending task '{}'. Elapsed: {}ms, Result type: {}.";
  private static final String END_TASK_TRACE_FORMAT = "[plan={}]: Ending task '{}'. Elapsed: {}ms, Result type: {}. Value: {}.";

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

  public TaskLogger(final long planId,
                    final Task<?> root,
                    final Logger allLogger,
                    final Logger rootLogger,
                    final Logger planLogger)
  {
    _planId = planId;
    _root = root;
    _allLogger = allLogger;
    _rootLogger = rootLogger;
    _planLogger = planLogger;
  }

  /**
   * {@code true} if one or more of the loggers in this object would log events
   * for the given task.
   */
  public boolean isEnabled(final Task<?> task)
  {
    return (_planLogger.isDebugEnabled() ||
           (_rootLogger.isDebugEnabled() && _root == task) ||
           (_allLogger.isDebugEnabled()));
  }

  @Override
  public void onUpdate(Task<?> task, ShallowTrace shallowTrace)
  {
    if (shallowTrace.getStartNanos() != null && shallowTrace.getPendingNanos() == null && shallowTrace.getEndNanos() == null)
    {
      logTaskStart(task);
    }
    else if (shallowTrace.getEndNanos() != null)
    {
      logTaskEnd(task, shallowTrace);
    }
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

  public void logTaskEnd(final Task<?> task, final ShallowTrace shallowTrace)
  {
    if (_planLogger.isTraceEnabled())
    {
      _planLogger.trace(END_TASK_TRACE_FORMAT,
                        new Object[] {_planId, task.getName(),
                                      elapsedMillis(shallowTrace),
                                      shallowTrace.getResultType(),
                                      shallowTrace.getValue()});
    }
    else if (_planLogger.isDebugEnabled())
    {
      _planLogger.debug(END_TASK_DEBUG_FORMAT,
                        new Object[] {_planId, task.getName(),
                                      elapsedMillis(shallowTrace),
                                      shallowTrace.getResultType()});
    }
    else if (_root == task && _rootLogger.isTraceEnabled())
    {
      _rootLogger.trace(END_TASK_TRACE_FORMAT,
                        new Object[] {_planId, task.getName(),
                            elapsedMillis(shallowTrace),
                            shallowTrace.getResultType(),
                            shallowTrace.getValue()});
    }
    else if (_root == task && _rootLogger.isDebugEnabled())
    {
      _rootLogger.debug(END_TASK_DEBUG_FORMAT,
                        new Object[] {_planId, task.getName(),
                            elapsedMillis(shallowTrace),
                            shallowTrace.getResultType()});
    }
    else if (_allLogger.isTraceEnabled())
    {
      _allLogger.trace(END_TASK_TRACE_FORMAT,
                       new Object[] {_planId, task.getName(),
                           elapsedMillis(shallowTrace),
                           shallowTrace.getResultType(),
                           shallowTrace.getValue()});
    }
    else if (_allLogger.isDebugEnabled())
    {
      _allLogger.debug(END_TASK_DEBUG_FORMAT,
                       new Object[] {_planId, task.getName(),
                           elapsedMillis(shallowTrace),
                           shallowTrace.getResultType()});
    }
  }

  private long elapsedMillis(final ShallowTrace shallowTrace)
  {
    return (shallowTrace.getEndNanos() - shallowTrace.getStartNanos()) / 1000000;
  }
}
