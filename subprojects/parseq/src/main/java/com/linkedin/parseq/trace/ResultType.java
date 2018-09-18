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

import com.linkedin.parseq.Exceptions;
import com.linkedin.parseq.Task;


/**
 * An enumeration that classifies the state of a Task.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public enum ResultType {
  /**
   * Indicates that the task completed without error.
   */
  SUCCESS,

  /**
   * Indicates that the task completed with an error.
   */
  ERROR,

  /**
   * Indicates that the task was cancelled because it's parent was resolved
   * before the task itself was resolved.
   */
  EARLY_FINISH,

  /**
   * Indicates that the task has not yet been resolved.
   */
  UNFINISHED;

  /**
   * Given a task this method will return a {@link ResultType} classification.
   *
   * @param task the task to classify
   * @return the result type classification for the task
   */
  public static ResultType fromTask(final Task<?> task) {
    if (!task.isDone()) {
      return UNFINISHED;
    } else if (task.isFailed()) {
      if (Exceptions.isEarlyFinish(task.getError())) {
        return EARLY_FINISH;
      }

      return ERROR;
    }

    return SUCCESS;
  }
}
