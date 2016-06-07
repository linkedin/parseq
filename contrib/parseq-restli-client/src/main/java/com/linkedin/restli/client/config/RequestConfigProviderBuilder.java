package com.linkedin.restli.client.config;

import com.linkedin.restli.client.InboundRequestContextFinder;
import com.linkedin.restli.client.ParSeqRestClientConfig;
import com.linkedin.restli.client.ParSeqRestClientConfigBuilder;

class RequestConfigProviderBuilder {

  private final ParSeqRestClientConfigBuilder _config = new ParSeqRestClientConfigBuilder();
  private InboundRequestContextFinder _inboundRequestFinder;

  public RequestConfigProvider build() throws RequestConfigKeyParsingException {
    return new RequestConfigProviderImpl(_inboundRequestFinder, _config.build());
  }

  public RequestConfigProviderBuilder addConfig(ParSeqRestClientConfig config) {
    _config.addConfig(config);
    return this;
  }

  public RequestConfigProviderBuilder setInboundRequestFinder(InboundRequestContextFinder inboundRequestFinder) {
    _inboundRequestFinder = inboundRequestFinder;
    return this;
  }

}
