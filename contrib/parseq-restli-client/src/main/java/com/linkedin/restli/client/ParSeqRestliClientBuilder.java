package com.linkedin.restli.client;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.restli.client.config.RequestConfigProvider;

public class ParSeqRestliClientBuilder {

  private static final String DEFAULT_CONFIG = "default";

  private RestClient _restClient;
  private ParSeqRestliClientConfig _config;
  Map<String, ParSeqRestliClientConfig> _configs;
  ParSeqRestliClientConfigChooser _configChooser;

  private BatchingSupport _batchingSupport;
  private InboundRequestContextFinder _inboundRequestContextFinder;

  /**
   * This method may throw RuntimeException e.g. when there is a problem with configuration.
   * @throws RuntimeException e.g. when there is a problem with configuration
   * @return instance of ParSeqRestClient
   */
  public ParSeqRestClient build() {

    //TODO add debug logging

    InboundRequestContextFinder inboundRequestContextFinder = _inboundRequestContextFinder == null ?
        () -> Optional.empty() : _inboundRequestContextFinder;

    if (_config != null) {
      _configs = Collections.singletonMap(DEFAULT_CONFIG, _config);
      _configChooser = (irc, r) -> DEFAULT_CONFIG;
    } else if (_configs == null) {
      throw new IllegalStateException("One type of config has to be specified using either setConfig() or setMultipleConfigs().");
    }

    RequestConfigProvider configProvider = new MultipleRequestConfigProvider(_configs, _configChooser, inboundRequestContextFinder);

    ParSeqRestClient parseqClient = new ParSeqRestClient(_restClient, configProvider);
    if (_batchingSupport != null) {
      _batchingSupport.registerStrategy(parseqClient);
    }
    return parseqClient;
  }

  public RestClient getRestClient() {
    return _restClient;
  }

  public ParSeqRestliClientBuilder setBatchingSupport(BatchingSupport batchingSupport) {
    _batchingSupport = batchingSupport;
    return this;
  }

  public ParSeqRestliClientBuilder setRestClient(RestClient restClient) {
    ArgumentUtil.requireNotNull(restClient, "restClient");
    _restClient = restClient;
    return this;
  }

  public ParSeqRestliClientBuilder setConfig(ParSeqRestliClientConfig config) {
    ArgumentUtil.requireNotNull(config, "config");
    if (_configs != null) {
      throw new IllegalArgumentException("setMultipleConfigs() has already been called. Only one type of config can be specified using either setConfig() or setMultipleConfigs() but not both.");
    }
    _config = config;
    return this;
  }

  public ParSeqRestliClientBuilder setMultipleConfigs(Map<String, ParSeqRestliClientConfig> configs,
      ParSeqRestliClientConfigChooser chooser) {
    ArgumentUtil.requireNotNull(configs, "configs");
    ArgumentUtil.requireNotNull(chooser, "chooser");
    if (_configs != null) {
      throw new IllegalArgumentException("setConfig() has already been called. Only one type of config can be specified using either setConfig() or setMultipleConfigs() but not both.");
    }
    _configs = configs;
    _configChooser = chooser;
    return this;
  }

  public ParSeqRestliClientBuilder setInboundRequestContextFinder(InboundRequestContextFinder inboundRequestContextFinder) {
    ArgumentUtil.requireNotNull(inboundRequestContextFinder, "inboundRequestContextFinder");
    _inboundRequestContextFinder = inboundRequestContextFinder;
    return this;
  }
}
