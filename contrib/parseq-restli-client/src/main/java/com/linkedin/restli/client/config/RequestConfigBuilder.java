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

public class RequestConfigBuilder {

  private ConfigValue<Long> _timeoutMs;
  private ConfigValue<Boolean>  _batchingEnabled;
  private ConfigValue<Integer> _maxBatchSize;

  public RequestConfigBuilder() {
  }

  public RequestConfigBuilder(RequestConfig config) {
    _timeoutMs = config.getTimeoutMs();
    _batchingEnabled = config.isBatchingEnabled();
    _maxBatchSize = config.getMaxBatchSize();
  }

  public RequestConfig build() {
    return new RequestConfigImpl(_timeoutMs, _batchingEnabled, _maxBatchSize);
  }

  public ConfigValue<Long> getTimeoutMs() {
    return _timeoutMs;
  }

  public RequestConfigBuilder setTimeoutMs(ConfigValue<Long> timeoutMs) {
    _timeoutMs = timeoutMs;
    return this;
  }

  public ConfigValue<Boolean> getBatchingEnabled() {
    return _batchingEnabled;
  }

  public RequestConfigBuilder setBatchingEnabled(ConfigValue<Boolean> batchingEnabled) {
    _batchingEnabled = batchingEnabled;
    return this;
  }

  public ConfigValue<Integer> getMaxBatchSize() {
    return _maxBatchSize;
  }

  public RequestConfigBuilder setMaxBatchSize(ConfigValue<Integer> maxBatchSize) {
    _maxBatchSize = maxBatchSize;
    return this;
  }

  public RequestConfigBuilder applyOverrides(RequestConfigOverrides configOverrides) {
    configOverrides.getTimeoutMs().ifPresent(this::setTimeoutMs);
    configOverrides.isBatchingEnabled().ifPresent(this::setBatchingEnabled);
    configOverrides.getMaxBatchSize().ifPresent(this::setMaxBatchSize);
    return this;
  }

}
