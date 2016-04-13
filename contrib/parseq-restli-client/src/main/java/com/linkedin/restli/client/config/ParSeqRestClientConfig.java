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

package com.linkedin.restli.client.config;

import java.util.Collections;
import java.util.Map;

public class ParSeqRestClientConfig {

  public static final ParSeqRestClientConfig DEFAULT_PARSEQ_REST_CLIENT_CONFIG =
      new ParSeqRestClientConfig(Collections.emptyMap(), ResourceConfig.DEFAULT_RESOURCE_CONFIG);

  private final Map<String, ResourceConfig> _resourceConfig;
  private final ResourceConfig _defaultResourceConfig;

  public ParSeqRestClientConfig(Map<String, ResourceConfig> resourceConfig, ResourceConfig defaultResourceConfig) {
    _resourceConfig = resourceConfig;
    _defaultResourceConfig = defaultResourceConfig;
  }

  public ResourceConfig getResourceConfig(String resource) {
    return _resourceConfig.getOrDefault(resource, _defaultResourceConfig);
  }

  public Map<String, ResourceConfig> getResourceConfig() {
    return Collections.unmodifiableMap(_resourceConfig);
  }
}
