package com.linkedin.restli.client.config;

import java.util.Map;
import java.util.Optional;

import com.linkedin.restli.common.ResourceMethod;

public class ResourceConfig {

  private final Map<ResourceMethod, BatchingConfig> _batchingConfig;
  private final Optional<Long> _timeoutNs;
  private final BatchingConfig _defaultBatchingConfig;

  public ResourceConfig(Map<ResourceMethod, BatchingConfig> batchingConfig, Optional<Long> timeoutNs,
      BatchingConfig defaultBatchingConfig) {
    _batchingConfig = batchingConfig;
    _timeoutNs = timeoutNs;
    _defaultBatchingConfig = defaultBatchingConfig;
  }

  public BatchingConfig getBatchingConfig(ResourceMethod method) {
    return _batchingConfig.getOrDefault(method, _defaultBatchingConfig);
  }

  public Optional<Long> getTimeoutNs() {
    return _timeoutNs;
  }
}
