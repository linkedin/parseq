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

import com.linkedin.restli.client.config.ResourceConfigOverrides;
import com.linkedin.restli.client.config.ResourceConfigOverridesBuilder;

public class TestParSeqRestClientNoBatching extends ParSeqRestClientBatchingIntegrationTest {

  @Override
  public ParSeqRestClientConfig getParSeqRestClientConfig() {
    return new ParSeqRestClientConfigBuilder().build();
  }

  @Override
  protected boolean expectBatching() {
    return false;
  }

  @Override
  protected ResourceConfigOverrides overrides() {
    return new ResourceConfigOverridesBuilder().build();
  }

  @Override
  protected boolean expectBatchingOverrides() {
    return false;
  }
}
