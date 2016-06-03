package com.linkedin.restli.client.config;

import java.util.function.Function;

import com.linkedin.restli.client.InboundRequestContextFinder;
import com.linkedin.restli.client.ParSeqRestClientConfig;
import com.linkedin.restli.client.Request;

@FunctionalInterface
public interface ResourceConfigProvider extends Function<Request<?>, ResourceConfig> {

  /**
   * @throws RuntimeException
   */
  public static ResourceConfigProvider build(ParSeqRestClientConfig config, InboundRequestContextFinder inboundRequestContextFinder) {
    try {
      ResourceConfigProviderBuilder builder = new ResourceConfigProviderBuilder();
      builder.setInboundRequestFinder(inboundRequestContextFinder)
        .addConfig(getDefaultConfigMap());
      if (config != null) {
        builder.addConfig(config);
      }
      return builder.build();
    } catch (ResourceConfigKeyParsingException e) {
      throw new RuntimeException(e);
    }
  }

  public static ParSeqRestClientConfig getDefaultConfigMap() {
    return ResourceConfigProviderImpl.DEFAULT_CONFIG;
  }
}
