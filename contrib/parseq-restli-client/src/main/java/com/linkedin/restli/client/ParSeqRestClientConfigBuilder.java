package com.linkedin.restli.client;

import java.util.HashMap;
import java.util.Map;

public class ParSeqRestClientConfigBuilder {

  private final Map<String, Long> _timeoutMsConfig = new HashMap<>();
  private final Map<String, Boolean> _batchingEnabledConfig = new HashMap<>();
  private final Map<String, Integer> _maxBatchSizeConfig = new HashMap<>();
  private final Map<String, Boolean> _batchingDryRunConfig = new HashMap<>();

  public ParSeqRestClientConfigBuilder() {
  }

  public ParSeqRestClientConfigBuilder(ParSeqRestClientConfig config) {
    addConfig(config);
  }

  public void addConfig(ParSeqRestClientConfig config) {
    addTimeoutMsConfigMap(config.getTimeoutMsConfig());
    addBatchingEnabledConfigMap(config.isBatchingEnabledConfig());
    addMaxBatchSizeConfigMap(config.getMaxBatchSizeConfig());
    addBatchingDryRunConfigMap(config.isBatchingDryRunConfig());
  }

  public ParSeqRestClientConfig build() {
    return new ParSeqRestClientConfigImpl(_timeoutMsConfig, _batchingEnabledConfig, _maxBatchSizeConfig, _batchingDryRunConfig);
  }

  public ParSeqRestClientConfigBuilder addTimeoutMs(String key, long value) {
    _timeoutMsConfig.put(key, value);
    return this;
  }

  public ParSeqRestClientConfigBuilder addTimeoutMsConfigMap(Map<String, Long> config) {
    _timeoutMsConfig.putAll(config);
    return this;
  }

  public ParSeqRestClientConfigBuilder addBatchingEnabled(String key, boolean value) {
    _batchingEnabledConfig.put(key, value);
    return this;
  }

  public ParSeqRestClientConfigBuilder addBatchingEnabledConfigMap(Map<String, Boolean> config) {
    _batchingEnabledConfig.putAll(config);
    return this;
  }

  public ParSeqRestClientConfigBuilder addMaxBatchSize(String key, int value) {
    _maxBatchSizeConfig.put(key, value);
    return this;
  }

  public ParSeqRestClientConfigBuilder addMaxBatchSizeConfigMap(Map<String, Integer> config) {
    _maxBatchSizeConfig.putAll(config);
    return this;
  }

  public ParSeqRestClientConfigBuilder addBatchingDryRun(String key, boolean value) {
    _batchingDryRunConfig.put(key, value);
    return this;
  }

  public ParSeqRestClientConfigBuilder addBatchingDryRunConfigMap(Map<String, Boolean> config) {
    _batchingDryRunConfig.putAll(config);
    return this;
  }
}
