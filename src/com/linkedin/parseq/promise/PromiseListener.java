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

package com.linkedin.parseq.promise;

/**
 * A listener can be registered with a promise. When that promise is resolved
 * (either it has a value or an error) the {@link #onResolved(Promise)} method will
 * be invoked.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 * @see Promise#addListener(PromiseListener)
 */
@FunctionalInterface
public interface PromiseListener<P>
{
  /**
   * A callback method that is invoked when the promise completes. The code
   * in this method should not block.
   *
   * @param promise the promise that was completed
   */
  void onResolved(Promise<P> promise);
}
