package com.linkedin.restli.client.config;

class ResourceConfigImpl implements ResourceConfig {

  private final ConfigValue<Long> _timeoutMs;
  private final ConfigValue<Boolean>  _batchingEnabled;
  private final ConfigValue<Integer> _maxBatchSize;
  private final ConfigValue<Boolean> _batchingDryRun;

  ResourceConfigImpl(ConfigValue<Long> timeoutMs, ConfigValue<Boolean> batchingEnabled, ConfigValue<Integer> maxBatchSize, ConfigValue<Boolean> batchingDryRun) {
    _timeoutMs = timeoutMs;
    _batchingEnabled = batchingEnabled;
    _maxBatchSize = maxBatchSize;
    _batchingDryRun = batchingDryRun;
  }

  public ConfigValue<Long> getTimeoutMs() {
    return _timeoutMs;
  }

  public ConfigValue<Boolean> isBatchingEnabled() {
    return _batchingEnabled;
  }

  public ConfigValue<Integer> getMaxBatchSize() {
    return _maxBatchSize;
  }

  public ConfigValue<Boolean> isBatchingDryRun() {
    return _batchingDryRun;
  }

  @Override
  public ResourceConfigBuilder copyToBuilder() {
    // TODO Auto-generated method stub
    return null;
  }
}
