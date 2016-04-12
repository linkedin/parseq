package com.linkedin.restli.client.config;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.linkedin.restli.common.ResourceMethod;

public class ResourceConfig {

  private final Map<ResourceMethod, BatchingConfig> _batchingConfig;
  private final Optional<Long> _timeoutNs;
  private final BatchingConfig _defaultBatchingConfig;
  public static final ResourceConfig DEFAULT_RESOURCE_CONFIG =
  new ResourceConfig(Collections.emptyMap(), Optional.empty(), BatchingConfig.DEFAULT_BATCHING_CONFIG);

  public ResourceConfig(Map<ResourceMethod, BatchingConfig> batchingConfig, Optional<Long> timeoutNs,
      BatchingConfig defaultBatchingConfig) {
    _batchingConfig = batchingConfig;
    _timeoutNs = timeoutNs;
    _defaultBatchingConfig = defaultBatchingConfig;
  }

  public BatchingConfig getBatchingConfig(ResourceMethod method) {
    return _batchingConfig.getOrDefault(method, _defaultBatchingConfig);
  }

  public Map<ResourceMethod, BatchingConfig> getBatchingConfig() {
    return Collections.unmodifiableMap(_batchingConfig);
  }

  public Optional<Long> getTimeoutNs() {
    return _timeoutNs;
  }

}
