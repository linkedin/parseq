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
 * An object that can be cancelled with a reason.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public interface Cancellable {
  /**
   * Attempts to cancel the object with the given reason. Check the return
   * value to determine if cancellation was successful. Subsequent calls to
   * cancel will always return {@code false}.
   *
   * @param reason an Exception indicating why this object was cancelled
   * @return {@code true} iff the object was successfully cancelled as a result
   *         of this invocation.
   */
  boolean cancel(Exception reason);
}
