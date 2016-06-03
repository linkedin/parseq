package com.linkedin.restli.client.config;

import com.linkedin.restli.client.InboundRequestContextFinder;
import com.linkedin.restli.client.ParSeqRestClientConfig;
import com.linkedin.restli.client.ParSeqRestClientConfigBuilder;

class ResourceConfigProviderBuilder {

  private final ParSeqRestClientConfigBuilder _config = new ParSeqRestClientConfigBuilder();
  private InboundRequestContextFinder _inboundRequestFinder;

  public ResourceConfigProvider build() throws ResourceConfigKeyParsingException {
    return new ResourceConfigProviderImpl(_inboundRequestFinder, _config.build());
  }

  public ResourceConfigProviderBuilder addConfig(ParSeqRestClientConfig config) {
    _config.addConfig(config);
    return this;
  }

  public ResourceConfigProviderBuilder setInboundRequestFinder(InboundRequestContextFinder inboundRequestFinder) {
    _inboundRequestFinder = inboundRequestFinder;
    return this;
  }

}
