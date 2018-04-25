package com.linkedin.restli.client.config;

import java.util.Optional;

class RequestConfigOverridesImpl implements RequestConfigOverrides {

  private final Optional<ConfigValue<Boolean>> _d2RequestTimeoutEnabled;
  private final Optional<ConfigValue<Long>> _timeoutMs;
  private final Optional<ConfigValue<Boolean>>  _batchingEnabled;
  private final Optional<ConfigValue<Integer>> _maxBatchSize;

  RequestConfigOverridesImpl(Optional<ConfigValue<Boolean>> d2RequestTimeoutEnabled,
    Optional<ConfigValue<Long>> timeoutMs,
    Optional<ConfigValue<Boolean>> batchingEnabled,
    Optional<ConfigValue<Integer>> maxBatchSize) {
    _d2RequestTimeoutEnabled = d2RequestTimeoutEnabled;
    _timeoutMs = timeoutMs;
    _batchingEnabled = batchingEnabled;
    _maxBatchSize = maxBatchSize;
  }

  @Override
  public Optional<ConfigValue<Boolean>> isD2RequestTimeoutEnabled() {
    return _d2RequestTimeoutEnabled;
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
