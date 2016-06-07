package com.linkedin.restli.client.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.restli.client.InboundRequestContextFinder;
import com.linkedin.restli.client.ParSeqRestClientConfig;
import com.linkedin.restli.client.ParSeqRestClientConfigBuilder;
import com.linkedin.restli.client.Request;

class RequestConfigProviderImpl implements RequestConfigProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestConfigProviderImpl.class);

  static final ParSeqRestClientConfig DEFAULT_CONFIG = createDefaultConfig();
  private final InboundRequestContextFinder _inboundRequestContextFinder;
  private final RequestConfigTree<Long> _timeoutMs = new RequestConfigTree<>();
  private final RequestConfigTree<Boolean> _batchingEnabled = new RequestConfigTree<>();
  private final RequestConfigTree<Integer> _maxBatchSize = new RequestConfigTree<>();
  private final ConcurrentMap<RequestConfigCacheKey, RequestConfig> _cache = new ConcurrentHashMap<>();

  public RequestConfigProviderImpl(InboundRequestContextFinder inboundRequestContextFinder, ParSeqRestClientConfig config) throws RequestConfigKeyParsingException {
    _inboundRequestContextFinder = inboundRequestContextFinder;
    initialize(config);
  }

  private void initialize(ParSeqRestClientConfig config) throws RequestConfigKeyParsingException {
    boolean failed = initializeProperty(config.getTimeoutMsConfig(), "timeoutMs") ||
                     initializeProperty(config.isBatchingEnabledConfig(), "batchingEnabled") ||
                     initializeProperty(config.getMaxBatchSizeConfig(), "maxBatchSize");
    if (failed) {
      throw new RequestConfigKeyParsingException("Configuration parsing error, see log file for details.");
    }
  }

  private boolean initializeProperty(Map<String, ?> config, String property) {
    boolean failed = false;
    List<RequestConfigElement> elements = new ArrayList<>();
    for (Map.Entry<String, ?> entry: config.entrySet()) {
      try {
        RequestConfigElement element = RequestConfigElement.parse(property, entry.getKey(), entry.getValue());
        processConfigElement(element);
        elements.add(element);
      } catch (RequestConfigKeyParsingException e) {
        LOGGER.error("Configuration parsing error", e);
        failed = true;
      }
    }
    if (!failed) {
      Collections.sort(elements);
      StringBuilder sb = new StringBuilder();
      sb.append("ParSeq RestLi Client Configuration for property " + property + " sorted by priority - first match gets applied:\n");
      elements.forEach(el -> sb.append(el.getKey())
                               .append(" = ")
                               .append(el.getValue())
                               .append("\n"));
      LOGGER.info(sb.toString());
    }
    return failed;
  }

  private void processConfigElement(RequestConfigElement element) throws RequestConfigKeyParsingException {
    switch (element.getProperty()) {
      case "timeoutMs": _timeoutMs.add(element); break;
      case "batchingEnabled": _batchingEnabled.add(element); break;
      case "maxBatchSize": _maxBatchSize.add(element); break;
      default: throw new RequestConfigKeyParsingException("Unrecognized property: " + element.getProperty());
    }
  }

  @Override
  public RequestConfig apply(Request<?> request) {
    RequestConfigCacheKey cacheKey = new RequestConfigCacheKey(_inboundRequestContextFinder.find(), request);
    return _cache.computeIfAbsent(cacheKey, this::resolve);
  }

  private RequestConfig resolve(RequestConfigCacheKey cacheKey) {
    return new RequestConfigBuilder()
      .setTimeoutMs(_timeoutMs.resolve(cacheKey))
      .setBatchingEnabled(_batchingEnabled.resolve(cacheKey))
      .setMaxBatchSize(_maxBatchSize.resolve(cacheKey))
      .build();
  }

  /**
   * Default configuration map must specify default values for all properties.
   */
  private static ParSeqRestClientConfig createDefaultConfig() {
    ParSeqRestClientConfigBuilder builder = new ParSeqRestClientConfigBuilder();
    builder.addTimeoutMs("*.*/*.*", TimeUnit.SECONDS.toMillis(10));
    builder.addBatchingEnabled("*.*/*.*", Boolean.FALSE);
    builder.addMaxBatchSize("*.*/*.*", 1024);
    return builder.build();
  }
}
