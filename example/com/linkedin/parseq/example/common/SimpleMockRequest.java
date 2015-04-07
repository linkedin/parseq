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

package com.linkedin.parseq.example.common;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class SimpleMockRequest<RES> implements MockRequest<RES> {
  private final long _latency;
  private final RES _result;

  public SimpleMockRequest(final long latency, final RES result) {
    _latency = latency;
    _result = result;
  }

  public long getLatency() {
    return _latency;
  }

  public RES getResult() {
    return _result;
  }
}
