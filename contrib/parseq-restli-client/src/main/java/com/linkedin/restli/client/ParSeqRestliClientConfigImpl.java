package com.linkedin.restli.client;

import java.util.Map;

class ParSeqRestliClientConfigImpl implements ParSeqRestliClientConfig {

  private final Map<String, Boolean> _d2RequestTimeoutEnabledConfig;
  private final Map<String, Long> _timeoutMsConfig;
  private final Map<String, Boolean> _batchingEnabledConfig;
  private final Map<String, Integer> _maxBatchSizeConfig;

  public ParSeqRestliClientConfigImpl(Map<String, Boolean> d2RequestTimeoutEnabledConfig,
      Map<String, Long> timeoutMsConfig,
      Map<String, Boolean> batchingEnabledConfig,
      Map<String, Integer> maxBatchSizeConfig) {
    _d2RequestTimeoutEnabledConfig = d2RequestTimeoutEnabledConfig;
    _timeoutMsConfig = timeoutMsConfig;
    _batchingEnabledConfig = batchingEnabledConfig;
    _maxBatchSizeConfig = maxBatchSizeConfig;
  }

  @Override
  public Map<String, Boolean> isD2RequestTimeoutEnabledConfig() {
    return _d2RequestTimeoutEnabledConfig;
  }

  @Override
  public Map<String, Long> getTimeoutMsConfig() {
    return _timeoutMsConfig;
  }

  @Override
  public Map<String, Boolean> isBatchingEnabledConfig() {
    return _batchingEnabledConfig;
  }

  @Override
  public Map<String, Integer> getMaxBatchSizeConfig() {
    return _maxBatchSizeConfig;
  }
}
