package com.linkedin.restli.client.config;

import java.util.Optional;

class ResourceConfigOverridesImpl implements ResourceConfigOverrides {

  private final Optional<ConfigValue<Long>> _timeoutMs;
  private final Optional<ConfigValue<Boolean>>  _batchingEnabled;
  private final Optional<ConfigValue<Integer>> _maxBatchSize;

  ResourceConfigOverridesImpl(Optional<ConfigValue<Long>> timeoutMs, Optional<ConfigValue<Boolean>> batchingEnabled, Optional<ConfigValue<Integer>> maxBatchSize) {
    _timeoutMs = timeoutMs;
    _batchingEnabled = batchingEnabled;
    _maxBatchSize = maxBatchSize;
  }

  @Override
  public Optional<ConfigValue<Long>> getTimeoutMs() {
    return _timeoutMs;
  }

  @Override
  public Optional<ConfigValue<Boolean>> isBatchingEnabled() {
    return _batchingEnabled;
  }

  @Override
  public Optional<ConfigValue<Integer>> getMaxBatchSize() {
    return _maxBatchSize;
  }
}
