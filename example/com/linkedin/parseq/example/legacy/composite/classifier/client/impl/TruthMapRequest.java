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

package com.linkedin.parseq.example.legacy.composite.classifier.client.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TruthMapRequest extends AbstractRequest<Map<Long, Boolean>> {
  private final String _name;
  private final int _remainder;
  private final Set<Long> _memberIds;

  public TruthMapRequest(final String name, final int remainder, final Set<Long> memberIds) {
    _name = name;
    _remainder = remainder;
    _memberIds = memberIds;
  }

  @Override
  public Map<Long, Boolean> getResponse() {
    final Map<Long, Boolean> result = new HashMap<Long, Boolean>();
    for (Long memberId : _memberIds) {
      result.put(memberId, memberId % 10 == _remainder);
    }
    return result;
  }

  @Override
  public String getName() {
    return _name;
  }
}
