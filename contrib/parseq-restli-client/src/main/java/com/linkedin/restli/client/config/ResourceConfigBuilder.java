/*
 * Copyright 2016 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.restli.client.config;

public class ResourceConfigBuilder {

  private ConfigValue<Long> _timeoutMs;
  private ConfigValue<Boolean>  _batchingEnabled;
  private ConfigValue<Integer> _maxBatchSize;
  private ConfigValue<Boolean> _batchingDryRun;

  public ResourceConfigBuilder() {
  }

  public ResourceConfigBuilder(ResourceConfig config) {
    _timeoutMs = config.getTimeoutMs();
    _batchingEnabled = config.isBatchingEnabled();
    _maxBatchSize = config.getMaxBatchSize();
    _batchingDryRun = config.isBatchingDryRun();
  }

  public ResourceConfig build() {
    return new ResourceConfigImpl(_timeoutMs, _batchingEnabled, _maxBatchSize, _batchingDryRun);
  }

  public ConfigValue<Long> getTimeoutMs() {
    return _timeoutMs;
  }

  public ResourceConfigBuilder setTimeoutMs(ConfigValue<Long> timeoutMs) {
    _timeoutMs = timeoutMs;
    return this;
  }

  public ConfigValue<Boolean> getBatchingEnabled() {
    return _batchingEnabled;
  }

  public ResourceConfigBuilder setBatchingEnabled(ConfigValue<Boolean> batchingEnabled) {
    _batchingEnabled = batchingEnabled;
    return this;
  }

  public ConfigValue<Integer> getMaxBatchSize() {
    return _maxBatchSize;
  }

  public ResourceConfigBuilder setMaxBatchSize(ConfigValue<Integer> maxBatchSize) {
    _maxBatchSize = maxBatchSize;
    return this;
  }

  public ConfigValue<Boolean> getBatchingDryRun() {
    return _batchingDryRun;
  }

  public ResourceConfigBuilder setBatchingDryRun(ConfigValue<Boolean> batchingDryRun) {
    _batchingDryRun = batchingDryRun;
    return this;
  }

  public ResourceConfigBuilder applyOverrides(ResourceConfigOverrides configOverrides) {
    configOverrides.getTimeoutMs().ifPresent(this::setTimeoutMs);
    configOverrides.isBatchingEnabled().ifPresent(this::setBatchingEnabled);
    configOverrides.getMaxBatchSize().ifPresent(this::setMaxBatchSize);
    configOverrides.isBatchingDryRun().ifPresent(this::setBatchingDryRun);
    return this;
  }

}
