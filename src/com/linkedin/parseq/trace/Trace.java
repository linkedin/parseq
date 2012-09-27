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
import java.util.Set;

/**
 * A Trace represents a point-in-time snapshot of a Task's state. Generally,
 * this point in time will be after the task has completed, but traces can
 * also be taken of tasks that are currently executing.
 * <p/>
 * Traces are immutable and thread-safe. Use {@link TraceBuilder} to
 * create new traces.
 * <p/>
 * Use a {@link com.linkedin.parseq.trace.codec.TraceCodec} to serialize and
 * deserialize Traces.
 *
 * @author Chi Chan (ckchan@linkedin.com)
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public interface Trace
{
  /**
   * Returns the task name for this trace
   *
   * @return the task name for this trace
   */
  String getName();

  /**
   * Returns if the trace will be hidden from the visualizations.
   *
   * @return if the trace is hidden
   */
  boolean getHidden();
  /**
   * Returns if the trace will be system hidden from the visualizations.
   *
   * @return if the trace is system hidden.
   */
  boolean getSystemHidden();

  /**
   * Returns a String representation of the value or error produced by the
   * traced task. If the task was unfinished then the string representation
   * will be null.
   *
   * @return String representation of the task's value
   */
  String getValue();

  /**
   * Returns the result type of the task.
   *
   * @return the result type of the task
   */
  ResultType getResultType();

  /**
   * Returns the time at which the task was started in nanoseconds. If
   * the task had not yet started, this method will return {@code null}.
   *
   * @return the time at which this task was started or {@code null}
   */
  Long getStartNanos();

  /**
   * Returns the time at which the task was finished in nanoseconds. If the
   * task had not yet finished, this method will return {@code null}.
   *
   * @return the time at which this task was finished or {@code null}
   */
  Long getEndNanos();

  /**
   * Returns a set of traces that are related to this trace.
   *
   * @return a set of traces that are related to this trace.
   */
  Set<Related<Trace>> getRelated();

  /**
   * Get the set of attributes related to this trace.
   * @return the map of attributes related to this trace.
   */
  Map<String, String> getAttributes();
}
