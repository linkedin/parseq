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

import java.util.Map;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TruthMapClassifier implements Classifier
{
  private final Classification _classification;
  private final Map<Long, Boolean> _truthMap;

  public TruthMapClassifier(Classification classification, Map<Long, Boolean> truthMap)
  {
    _classification = classification;
    _truthMap = truthMap;
  }

  @Override
  public Classification classify(final long vieweeId)
  {
    final Boolean b = _truthMap.get(vieweeId);
    return b != null && b
        ? _classification
        : null;
  }
}
