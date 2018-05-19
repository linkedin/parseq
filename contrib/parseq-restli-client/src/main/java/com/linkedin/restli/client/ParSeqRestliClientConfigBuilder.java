package com.linkedin.restli.client;

import com.linkedin.parseq.ParSeqGlobalConfiguration;
import java.util.HashMap;
import java.util.Map;

public class ParSeqRestliClientConfigBuilder {

  private final Map<String, Long> _timeoutMsConfig = new HashMap<>();
  private final Map<String, Boolean> _batchingEnabledConfig = new HashMap<>();
  private final Map<String, Integer> _maxBatchSizeConfig = new HashMap<>();

  public ParSeqRestliClientConfigBuilder() {
  }

  public ParSeqRestliClientConfigBuilder(ParSeqRestliClientConfig config) {
    addConfig(config);
  }

  public void addConfig(ParSeqRestliClientConfig config) {
    addTimeoutMsConfigMap(config.getTimeoutMsConfig());
    addBatchingEnabledConfigMap(config.isBatchingEnabledConfig());
    addMaxBatchSizeConfigMap(config.getMaxBatchSizeConfig());
  }

  public ParSeqRestliClientConfig build() {
    return new ParSeqRestliClientConfigImpl(_timeoutMsConfig, _batchingEnabledConfig, _maxBatchSizeConfig);
  }

  public ParSeqRestliClientConfigBuilder addTimeoutMs(String key, long value) {
    _timeoutMsConfig.put(key, value);
    return this;
  }

  public ParSeqRestliClientConfigBuilder addTimeoutMsConfigMap(Map<String, Long> config) {
    _timeoutMsConfig.putAll(config);
    return this;
  }

  public ParSeqRestliClientConfigBuilder addBatchingEnabled(String key, boolean value) {
    _batchingEnabledConfig.put(key, value);
    return this;
  }

  public ParSeqRestliClientConfigBuilder addBatchingEnabledConfigMap(Map<String, Boolean> config) {
    _batchingEnabledConfig.putAll(config);
    return this;
  }

  public ParSeqRestliClientConfigBuilder addMaxBatchSize(String key, int value) {
    _maxBatchSizeConfig.put(key, value);
    return this;
  }

  public ParSeqRestliClientConfigBuilder addMaxBatchSizeConfigMap(Map<String, Integer> config) {
    _maxBatchSizeConfig.putAll(config);
    return this;
  }
}
