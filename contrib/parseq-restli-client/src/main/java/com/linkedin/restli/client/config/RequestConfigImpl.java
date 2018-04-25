package com.linkedin.restli.client.config;

class RequestConfigImpl implements RequestConfig {

  private final ConfigValue<Boolean> _d2RequestTimeoutEnabled;
  private final ConfigValue<Long> _timeoutMs;
  private final ConfigValue<Boolean>  _batchingEnabled;
  private final ConfigValue<Integer> _maxBatchSize;

  RequestConfigImpl(ConfigValue<Boolean> d2RequestTimeoutEnabled, ConfigValue<Long> timeoutMs,
      ConfigValue<Boolean> batchingEnabled, ConfigValue<Integer> maxBatchSize) {
    _d2RequestTimeoutEnabled = d2RequestTimeoutEnabled;
    _timeoutMs = timeoutMs;
    _batchingEnabled = batchingEnabled;
    _maxBatchSize = maxBatchSize;
  }

  @Override
  public ConfigValue<Boolean> isD2RequestTimeoutEnabled() {
    return _d2RequestTimeoutEnabled;
  }

  @Override
  public ConfigValue<Long> getTimeoutMs() {
    return _timeoutMs;
  }

  @Override
  public ConfigValue<Boolean> isBatchingEnabled() {
    return _batchingEnabled;
  }

  @Override
  public ConfigValue<Integer> getMaxBatchSize() {
    return _maxBatchSize;
  }

  @Override
  public String toString() {
    return "RequestConfigImpl d2RequestTimeoutEnabled=" + _d2RequestTimeoutEnabled + ", _timeoutMs=" + _timeoutMs
        + ", batchingEnabled=" + _batchingEnabled
        + ", maxBatchSize=" + _maxBatchSize + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_batchingEnabled == null) ? 0 : _batchingEnabled.hashCode());
    result = prime * result + ((_maxBatchSize == null) ? 0 : _maxBatchSize.hashCode());
    result = prime * result + ((_d2RequestTimeoutEnabled == null) ? 0 : _d2RequestTimeoutEnabled.hashCode());
    result = prime * result + ((_timeoutMs == null) ? 0 : _timeoutMs.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RequestConfigImpl other = (RequestConfigImpl) obj;
    if (_batchingEnabled == null) {
      if (other._batchingEnabled != null)
        return false;
    } else if (!_batchingEnabled.equals(other._batchingEnabled))
      return false;
    if (_maxBatchSize == null) {
      if (other._maxBatchSize != null)
        return false;
    } else if (!_maxBatchSize.equals(other._maxBatchSize))
      return false;
    if (_d2RequestTimeoutEnabled == null) {
      if (other._d2RequestTimeoutEnabled != null)
        return false;
    } else if (!_d2RequestTimeoutEnabled.equals(other._d2RequestTimeoutEnabled))
      return false;
    if (_timeoutMs == null) {
      if (other._timeoutMs != null)
        return false;
    } else if (!_timeoutMs.equals(other._timeoutMs))
      return false;
    return true;
  }
}
