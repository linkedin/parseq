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

import com.linkedin.parseq.promise.PromiseListener;

/**
 * An object that can invoke listeners after a set of {@link com.linkedin.parseq.promise.Promise}s have
 * been resolved.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 * @author Chi Chan (ckchan@linkedin.com)
 */
public interface AfterPromises
{
  /**
   * When all {@link com.linkedin.parseq.promise.Promise}s are resolved then the given listener is invoked.
   * If all promises were already resolved then the listener is invoked
   * immediately.
   *
   * @param listener the listener to invoke once all promises are resolved
   */
  void call(PromiseListener listener);
}
