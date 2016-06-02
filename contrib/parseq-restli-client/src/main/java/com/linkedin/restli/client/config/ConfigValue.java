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
}
