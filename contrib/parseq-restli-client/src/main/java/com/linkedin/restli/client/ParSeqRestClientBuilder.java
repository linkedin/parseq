package com.linkedin.restli.client;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.restli.client.config.RequestConfigProvider;

public class ParSeqRestClientBuilder {

  private RestClient _restClient;
  private ParSeqRestClientConfig _config;
  Map<? extends Enum<?>, ParSeqRestClientConfig> _configs;
  ParSeqRestClientConfigChooser<? extends Enum<?>> _configChooser;

  private BatchingSupport _batchingSupport;
  private InboundRequestContextFinder _inboundRequestContextFinder;

  /**
   * This method may throw RuntimeException e.g. when there is a problem with configuration.
   * @throws RuntimeException e.g. when there is a problem with configuration
   * @return instance of ParSeqRestClient
   */
  public ParSeqRestClient build() {

    InboundRequestContextFinder inboundRequestContextFinder = _inboundRequestContextFinder == null ?
        () -> Optional.empty() : _inboundRequestContextFinder;

    if (_config != null) {
      _configs = Collections.singletonMap(DefaultConfigChoice.defaultconfig, _config);
      _configChooser = new ParSeqRestClientConfigChooser<DefaultConfigChoice>() {
        @Override
        public DefaultConfigChoice apply(Optional<InboundRequestContext> onbound, Request<?> oubound) {
          return DefaultConfigChoice.defaultconfig;
        }
      };
    } else if (_configs == null) {
      throw new IllegalStateException("One type of config has to be specified using either setConfig() or setMultipleConfigs().");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    RequestConfigProvider configProvider = new MultipleRequestConfigProvider(_configs, _configChooser, inboundRequestContextFinder);

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
    if (_configs != null) {
      throw new IllegalArgumentException("setMultipleConfigs() has already been called. Only one type of config can be specified using either setConfig() or setMultipleConfigs() but not both.");
    }
    _config = config;
    return this;
  }

  public <T extends Enum<T>> ParSeqRestClientBuilder setMultipleConfigs(Map<T, ParSeqRestClientConfig> configs,
      ParSeqRestClientConfigChooser<T> chooser) {
    ArgumentUtil.requireNotNull(configs, "configs");
    ArgumentUtil.requireNotNull(chooser, "chooser");
    if (_configs != null) {
      throw new IllegalArgumentException("setConfig() has already been called. Only one type of config can be specified using either setConfig() or setMultipleConfigs() but not both.");
    }
    _configs = configs;
    _configChooser = chooser;
    return this;
  }

  public ParSeqRestClientBuilder setInboundRequestContextFinder(InboundRequestContextFinder inboundRequestContextFinder) {
    ArgumentUtil.requireNotNull(inboundRequestContextFinder, "inboundRequestContextFinder");
    _inboundRequestContextFinder = inboundRequestContextFinder;
    return this;
  }

  private static enum DefaultConfigChoice  {
    defaultconfig;
  }
}
