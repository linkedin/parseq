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

package com.linkedin.parseq.internal.trace;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.trace.Trace;

/**
 * Generate trace data.
 *
 * @author Chi Chan (ckchan@linkedin.com)
 */
public interface TraceBuilder
{
  /**
   * Returns a {@link com.linkedin.parseq.trace.Trace} instance with tracing information for the
   * provided task. The trace will include the task, its predecessors, and its children.
   *
   * @param task the task used to generate the trace
   * @return the trace related to the task
   */
  Trace getTrace(Task<?> task);
}
