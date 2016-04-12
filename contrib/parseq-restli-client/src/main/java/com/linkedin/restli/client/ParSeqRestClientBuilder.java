package com.linkedin.restli.client;

import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.restli.client.config.ParSeqRestClientConfig;

public class ParSeqRestClientBuilder {

  private RestClient _restClient;
  private ParSeqRestClientConfig _config;
  private BatchingSupport _batchingSupport;

  public ParSeqRestClient build() {
    ParSeqRestClientImpl parseqClient = new ParSeqRestClientImpl(_restClient, _config != null ? _config :
      ParSeqRestClientConfig.DEFAULT_PARSEQ_REST_CLIENT_CONFIG);
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
    _restClient = restClient;
    return this;
  }

  public ParSeqRestClientConfig getConfig() {
    return _config;
  }

  public ParSeqRestClientBuilder setConfig(ParSeqRestClientConfig config) {
    _config = config;
    return this;
  }

}
