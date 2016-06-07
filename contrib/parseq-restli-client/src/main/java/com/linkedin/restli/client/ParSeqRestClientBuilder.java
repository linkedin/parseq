package com.linkedin.restli.client;

import java.util.Optional;

import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.restli.client.config.RequestConfigProvider;

public class ParSeqRestClientBuilder {

  private RestClient _restClient;
  private ParSeqRestClientConfig _config;
  private BatchingSupport _batchingSupport;
  private InboundRequestContextFinder _inboundRequestContextFinder;

  /**
   * This method may throw RuntimeException e.g. when there is a problem with configuration.
   * @throws RuntimeException
   */
  public ParSeqRestClient build() {
    RequestConfigProvider configProvider =
        RequestConfigProvider.build(_config,
            _inboundRequestContextFinder == null ? () -> Optional.empty() : _inboundRequestContextFinder);
    ParSeqRestClientImpl parseqClient = new ParSeqRestClientImpl(_restClient, configProvider);
    if (_batchingSupport != null) {
      _batchingSupport.registerStrategy(parseqClient);
    }
    return parseqClient;
  }

  public RestClient getRestClient() {
    return _restClient;
  }

  public ParSeqRestClientBuilder setBatchingSupport(BatchingSupport batchingSupport) {
    _batchingSupport = batchingSupport;
    return this;
  }

  public ParSeqRestClientBuilder setRestClient(RestClient restClient) {
    ArgumentUtil.requireNotNull(restClient, "restClient");
    _restClient = restClient;
    return this;
  }

  public ParSeqRestClientBuilder setConfig(ParSeqRestClientConfig config) {
    ArgumentUtil.requireNotNull(config, "config");
    _config = config;
    return this;
  }

  public ParSeqRestClientBuilder setInboundRequestFinder(InboundRequestContextFinder inboundRequestContextFinder) {
    ArgumentUtil.requireNotNull(inboundRequestContextFinder, "inboundRequestContextFinder");
    _inboundRequestContextFinder = inboundRequestContextFinder;
    return this;
  }
}
