package com.linkedin.restli.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.restli.client.Request;
import com.linkedin.restli.client.config.RequestConfig;
import com.linkedin.restli.client.config.RequestConfigProvider;

class MultipleRequestConfigProvider implements RequestConfigProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(MultipleRequestConfigProvider.class);

  private final Map<String, ParSeqRestliClientConfig> _configs;
  private final ParSeqRestliClientConfigChooser _chooser;
  private final InboundRequestContextFinder _inboundRequestContextFinder;

  private final ConcurrentHashMap<String, RequestConfigProvider> _providers = new ConcurrentHashMap<>();

  public MultipleRequestConfigProvider(Map<String, ParSeqRestliClientConfig> configs,
      ParSeqRestliClientConfigChooser chooser, InboundRequestContextFinder inboundRequestContextFinder) {
    _configs = configs;
    _chooser = chooser;
    _inboundRequestContextFinder = inboundRequestContextFinder;
    //initialize RequestConfigProviders at construction time to
    //avoid failures at runtime
    _configs.keySet().forEach(type -> {
      LOGGER.info("Initializing ParSeqRestClientConfig: {}", type);
      _providers.put(type, getProvider(type));
    });
  }

  private RequestConfigProvider getProvider(String type) {
    return RequestConfigProvider.build(_configs.get(type), _inboundRequestContextFinder);
  }

  @Override
  public RequestConfig apply(Request<?> request) {
    String type = _chooser.apply(_inboundRequestContextFinder.find(), request);
    RequestConfigProvider provider = _providers.computeIfAbsent(type, this::getProvider);
    return provider.apply(request);
  }

}
