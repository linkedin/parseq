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

package com.linkedin.parseq.example.composite.classifier;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class NetworkClassifier implements Classifier
{
  private final Network _subnet;

  public NetworkClassifier(final Network subnet)
  {
    _subnet = subnet;
  }

  @Override
  public Classification classify(final long vieweeId)
  {
    final Network.Distance distance = _subnet.getDistance(vieweeId);

    return Network.Distance.D2.equals(distance) || Network.Distance.D3.equals(distance)
        ? Classification.PARTIAL_VISIBILITY
        : null;
  }
}
