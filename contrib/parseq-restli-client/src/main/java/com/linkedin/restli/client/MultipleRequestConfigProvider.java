package com.linkedin.restli.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.linkedin.restli.client.Request;
import com.linkedin.restli.client.config.RequestConfig;
import com.linkedin.restli.client.config.RequestConfigProvider;

class MultipleRequestConfigProvider<T extends Enum<T>> implements RequestConfigProvider {

  private final Map<T, ParSeqRestClientConfig> _configs;
  private final ParSeqRestClientConfigChooser<T> _chooser;
  private final InboundRequestContextFinder _inboundRequestContextFinder;

  private final ConcurrentHashMap<T, RequestConfigProvider> _providers = new ConcurrentHashMap<>();

  public MultipleRequestConfigProvider(Map<T, ParSeqRestClientConfig> configs,
      ParSeqRestClientConfigChooser<T> chooser, InboundRequestContextFinder inboundRequestContextFinder) {
    _configs = configs;
    _chooser = chooser;
    _inboundRequestContextFinder = inboundRequestContextFinder;
  }

  private RequestConfigProvider getProvider(T type) {
    return RequestConfigProvider.build(_configs.get(type), _inboundRequestContextFinder);
  }

  @Override
  public RequestConfig apply(Request<?> request) {
    T type = _chooser.apply(_inboundRequestContextFinder.find(), request);
    RequestConfigProvider provider = _providers.computeIfAbsent(type, this::getProvider);
    return provider.apply(request);
  }

}
