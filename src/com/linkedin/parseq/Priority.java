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

/**
 * Priority describes the order in which tasks should be executed when there
 * is more than one task available for execution.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @see BaseTask#setPriority(int)
 */
public class Priority
{
  private Priority() {}

  /**
   * Tasks with minimum priority will only be executed after all other tasks
   * have been executed.
   */
  public static int MIN_PRIORITY = Integer.MIN_VALUE + 1;

  /**
   * Tasks with default priority can be run in any arbitrary order w.r.t to
   * other tasks of default priority.
   */
  public static int DEFAULT_PRIORITY = 0;

  /**
   * Tasks with maximum priority will be executed before all other tasks.
   */
  public static int MAX_PRIORITY = Integer.MAX_VALUE - 1;
}
