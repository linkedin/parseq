package com.linkedin.restli.client.config;

import java.util.Collections;
import java.util.Map;

public class ParSeqRestClientConfig {

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
