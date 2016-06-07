package com.linkedin.restli.client.config;

import java.util.function.Function;

import com.linkedin.restli.client.InboundRequestContextFinder;
import com.linkedin.restli.client.ParSeqRestClientConfig;
import com.linkedin.restli.client.Request;

@FunctionalInterface
public interface RequestConfigProvider extends Function<Request<?>, RequestConfig> {

  /**
   * @throws RuntimeException
   */
  public static RequestConfigProvider build(ParSeqRestClientConfig config, InboundRequestContextFinder inboundRequestContextFinder) {
    try {
      RequestConfigProviderBuilder builder = new RequestConfigProviderBuilder();
      builder.setInboundRequestFinder(inboundRequestContextFinder)
        .addConfig(getDefaultConfigMap());
      if (config != null) {
        builder.addConfig(config);
      }
      return builder.build();
    } catch (RequestConfigKeyParsingException e) {
      throw new RuntimeException(e);
    }
  }

  public static ParSeqRestClientConfig getDefaultConfigMap() {
    return RequestConfigProviderImpl.DEFAULT_CONFIG;
  }
}
