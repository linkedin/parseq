package com.linkedin.restli.client.config;

import java.util.Optional;

public class ConfigValue<T> {

  private final T _value;
  private final String _source;

  public ConfigValue(T value, String source) {
    _value = value;
    _source = source;
  }

  public T getValue() {
    return _value;
  }

  public Optional<String> getSource() {
    return Optional.ofNullable(_source);
  }

  @Override
  public String toString() {
    return "ConfigValue [value=" + _value + ", source=" + _source + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_source == null) ? 0 : _source.hashCode());
    result = prime * result + ((_value == null) ? 0 : _value.hashCode());
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
    @SuppressWarnings("rawtypes")
    ConfigValue other = (ConfigValue) obj;
    if (_source == null) {
      if (other._source != null)
        return false;
    } else if (!_source.equals(other._source))
      return false;
    if (_value == null) {
      if (other._value != null)
        return false;
    } else if (!_value.equals(other._value))
      return false;
    return true;
  }
}
