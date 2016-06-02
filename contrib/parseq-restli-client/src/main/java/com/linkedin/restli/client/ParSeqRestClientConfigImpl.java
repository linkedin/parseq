package com.linkedin.restli.client;

import java.util.Map;

class ParSeqRestClientConfigImpl implements ParSeqRestClientConfig {

  private final Map<String, Long> _timeoutMsConfig;
  private final Map<String, Boolean> _batchingEnabledConfig;
  private final Map<String, Integer> _maxBatchSizeConfig;
  private final Map<String, Boolean> _batchingDryRunConfig;

  public ParSeqRestClientConfigImpl(Map<String, Long> timeoutMsConfig, Map<String, Boolean> batchingEnabledConfig,
      Map<String, Integer> maxBatchSizeConfig, Map<String, Boolean> batchingDryRunConfig) {
    _timeoutMsConfig = timeoutMsConfig;
    _batchingEnabledConfig = batchingEnabledConfig;
    _maxBatchSizeConfig = maxBatchSizeConfig;
    _batchingDryRunConfig = batchingDryRunConfig;
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

  @Override
  public Map<String, Boolean> isBatchingDryRunConfig() {
    return _batchingDryRunConfig;
  }

}
