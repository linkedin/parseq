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
 * An object that invokes tasks after as set of
 * {@link com.linkedin.parseq.promise.Promise}s and {@link Task}s have completed.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public interface After
{
  /**
   * When all promises and tasks have been resolved then the given task is
   * run.
   *
   * @param task the task to run
   */
  void run(Task<?> task);
}
