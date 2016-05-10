package com.linkedin.restli.client.config;

import java.util.HashMap;
import java.util.Map;

import com.linkedin.restli.client.InboundRequestFinder;

class ResourceConfigProviderBuilder {

  private final Map<String, Object> _config = new HashMap<>();
  private InboundRequestFinder _inboundRequestFinder;

  public ResourceConfigProvider build() {
    // TODO Auto-generated method stub
    return null;
  }

  public ResourceConfigProviderBuilder add(Map<String, Object> config) {
    _config.putAll(config);
    return this;
  }

  public ResourceConfigProviderBuilder setInboundRequestFinder(InboundRequestFinder inboundRequestFinder) {
    _inboundRequestFinder = inboundRequestFinder;
    return this;
  }

}
