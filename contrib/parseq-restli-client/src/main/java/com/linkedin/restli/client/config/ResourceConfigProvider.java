package com.linkedin.restli.client.config;

import java.util.Map;
import java.util.function.Function;

import com.linkedin.restli.client.InboundRequestFinder;
import com.linkedin.restli.client.Request;

@FunctionalInterface
public interface ResourceConfigProvider extends Function<Request<?>, ResourceConfig> {

  public static ResourceConfigProvider fromMap(Map<String, Object> map, InboundRequestFinder inboundRequestFinder) {
    return new ResourceConfigProviderBuilder()
        .setInboundRequestFinder(inboundRequestFinder)
        .add(getDefaultConfigMap())
        .add(map).build();
  }

  public static Map<String, Object> getDefaultConfigMap() {
    return ResourceConfigProviderImpl.DEFAULT_CONFIG;
  }
}
