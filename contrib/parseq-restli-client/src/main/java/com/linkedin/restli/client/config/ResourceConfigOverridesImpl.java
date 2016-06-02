package com.linkedin.restli.client.config;

import java.util.Optional;

class ResourceConfigOverridesImpl implements ResourceConfigOverrides {

  private final Optional<ConfigValue<Long>> _timeoutMs;
  private final Optional<ConfigValue<Boolean>>  _batchingEnabled;
  private final Optional<ConfigValue<Integer>> _maxBatchSize;
  private final Optional<ConfigValue<Boolean>> _batchingDryRun;

  ResourceConfigOverridesImpl(Optional<ConfigValue<Long>> timeoutMs, Optional<ConfigValue<Boolean>> batchingEnabled, Optional<ConfigValue<Integer>> maxBatchSize, Optional<ConfigValue<Boolean>> batchingDryRun) {
    _timeoutMs = timeoutMs;
    _batchingEnabled = batchingEnabled;
    _maxBatchSize = maxBatchSize;
    _batchingDryRun = batchingDryRun;
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

  @Override
  public Optional<ConfigValue<Boolean>> isBatchingDryRun() {
    return _batchingDryRun;
  }
}
