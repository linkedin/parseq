package com.linkedin.restli.client.config;

import java.util.HashMap;
import java.util.Map;

import com.linkedin.restli.client.InboundRequestContextFinder;

class ResourceConfigProviderBuilder {

  private final Map<String, Map<String, Object>> _config = new HashMap<>();
  private InboundRequestContextFinder _inboundRequestFinder;

  public ResourceConfigProvider build() throws ResourceConfigKeyParsingException {
    return new ResourceConfigProviderImpl(_inboundRequestFinder, _config);
  }

  public ResourceConfigProviderBuilder add(Map<String, Map<String, Object>> config) {
    config.forEach((property, map) -> {
      Map<String, Object> cfgMap = _config.computeIfAbsent(property, k -> new HashMap<>());
      cfgMap.putAll(map);
    });
    return this;
  }

  public ResourceConfigProviderBuilder setInboundRequestFinder(InboundRequestContextFinder inboundRequestFinder) {
    _inboundRequestFinder = inboundRequestFinder;
    return this;
  }

}
