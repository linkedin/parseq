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

import java.util.List;

/**
 * A ParTask will execute the list of tasks in parallel and contains the result of the complete set.
 *
 * @author Chi Chan (ckchan@linkedin.com)
 * @see Task#par(Task, Task) Task.par
 */
public interface ParTask<T> extends Task<List<T>> {

  /**
   * Return the list of tasks that were related to this task.
   *
   * @return the list of tasks.
   */
  List<Task<T>> getTasks();

  /**
   * Get the list of successful values. Any task failure will not be included in the list. Therefore the results ordering
   * will not match the ordering of the tasks if any failure has occurred.
   * @return The list of successful values.
   */
  List<T> getSuccessful();

}
