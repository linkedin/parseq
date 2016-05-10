/*
 * Copyright 2016 LinkedIn, Inc
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

package com.linkedin.restli.client;

import java.util.Collections;
import java.util.Map;


public class TestParSeqRestClientNoBatching extends ParSeqRestClientIntegrationTest {

  @Override
  public Map<String, Object> getParSeqRestClientGonfig() {
    return Collections.emptyMap();
  }

  @Override
  protected boolean expectBatching() {
    return false;
  }

}
