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

package com.linkedin.parseq.example.composite.classifier.client.impl;

import com.linkedin.parseq.example.composite.classifier.client.Request;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public abstract class AbstractRequest<T> implements Request<T>
{
  private static final int DEFAULT_LATENCY_MEAN = 100;
  private static final int DEFAULT_LATENCY_STDDEV = 50;

  @Override
  public int getLatencyMean()
  {
    return DEFAULT_LATENCY_MEAN;
  }

  @Override
  public int getLatencyStdDev()
  {
    return DEFAULT_LATENCY_STDDEV;
  }
}
