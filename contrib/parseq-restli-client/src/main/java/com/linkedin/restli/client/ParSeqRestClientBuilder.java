package com.linkedin.restli.client;

import java.util.Map;

import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.restli.client.config.ResourceConfigProvider;

public class ParSeqRestClientBuilder {

  private RestClient _restClient;
  private Map<String, Object> _config;
  private BatchingSupport _batchingSupport;
  private InboundRequestFinder _inboundRequestFinder;

  public ParSeqRestClient build() {
    ResourceConfigProvider configProvider  = ResourceConfigProvider.fromMap(_config, _inboundRequestFinder);
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

  public ParSeqRestClientBuilder setConfig(Map<String, Object> config) {
    ArgumentUtil.requireNotNull(config, "config");
    _config = config;
    return this;
  }

  public ParSeqRestClientBuilder setInboundRequestFinder(InboundRequestFinder inboundRequestFinder) {
    _inboundRequestFinder = inboundRequestFinder;
    return this;
  }
}
