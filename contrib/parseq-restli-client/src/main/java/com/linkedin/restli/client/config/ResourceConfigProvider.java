package com.linkedin.restli.client.config;

import java.util.Map;
import java.util.function.Function;

import com.linkedin.restli.client.InboundRequestContextFinder;
import com.linkedin.restli.client.Request;

@FunctionalInterface
public interface ResourceConfigProvider extends Function<Request<?>, ResourceConfig> {

  /**
   * @throws RuntimeException
   */
  public static ResourceConfigProvider fromMap(Map<String, Map<String, Object>> map, InboundRequestContextFinder inboundRequestContextFinder) {
    try {
      return new ResourceConfigProviderBuilder()
          .setInboundRequestFinder(inboundRequestContextFinder)
          .add(getDefaultConfigMap())
          .add(map).build();
    } catch (ResourceConfigKeyParsingException e) {
      throw new RuntimeException(e);
    }
  }

  public static Map<String, Map<String, Object>> getDefaultConfigMap() {
    return ResourceConfigProviderImpl.DEFAULT_CONFIG;
  }
}
