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

import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.Cancellable;

/**
 * An object that allows a task to be scheduled for execution after some delay.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public interface DelayedExecutor
{
  /**
   * Schedules a command to execute after some period of delay.
   *
   * @param delay the amount of time to wait before executing the command
   * @param unit the time unit for the delay parameter
   * @param command the command to execute
   * @return a handle that can be used to cancel a future if it has not already run.
   */
  Cancellable schedule(long delay, TimeUnit unit, Runnable command);
}
