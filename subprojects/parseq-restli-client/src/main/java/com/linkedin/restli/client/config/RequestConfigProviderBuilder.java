package com.linkedin.restli.client.config;

import com.linkedin.restli.client.InboundRequestContextFinder;
import com.linkedin.restli.client.ParSeqRestliClientConfig;
import com.linkedin.restli.client.ParSeqRestliClientConfigBuilder;

class RequestConfigProviderBuilder {

  private final ParSeqRestliClientConfigBuilder _config = new ParSeqRestliClientConfigBuilder();
  private InboundRequestContextFinder _inboundRequestFinder;

  public RequestConfigProvider build() throws RequestConfigKeyParsingException {
    return new RequestConfigProviderImpl(_inboundRequestFinder, _config.build());
  }

  public RequestConfigProviderBuilder addConfig(ParSeqRestliClientConfig config) {
    _config.addConfig(config);
    return this;
  }

  public RequestConfigProviderBuilder setInboundRequestFinder(InboundRequestContextFinder inboundRequestFinder) {
    _inboundRequestFinder = inboundRequestFinder;
    return this;
  }

}
