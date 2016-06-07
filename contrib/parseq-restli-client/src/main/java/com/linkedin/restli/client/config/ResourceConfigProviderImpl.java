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

class ResourceConfigProviderImpl implements ResourceConfigProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceConfigProviderImpl.class);

  static final ParSeqRestClientConfig DEFAULT_CONFIG = createDefaultConfig();
  private final InboundRequestContextFinder _inboundRequestContextFinder;
  private final ResourceConfigTree<Long> _timeoutMs = new ResourceConfigTree<>();
  private final ResourceConfigTree<Boolean> _batchingEnabled = new ResourceConfigTree<>();
  private final ResourceConfigTree<Integer> _maxBatchSize = new ResourceConfigTree<>();
  private final ConcurrentMap<ResourceConfigCacheKey, ResourceConfig> _cache = new ConcurrentHashMap<>();

  public ResourceConfigProviderImpl(InboundRequestContextFinder inboundRequestContextFinder, ParSeqRestClientConfig config) throws ResourceConfigKeyParsingException {
    _inboundRequestContextFinder = inboundRequestContextFinder;
    initialize(config);
  }

  private void initialize(ParSeqRestClientConfig config) throws ResourceConfigKeyParsingException {
    boolean failed = initializeProperty(config.getTimeoutMsConfig(), "timeoutMs") ||
                     initializeProperty(config.isBatchingEnabledConfig(), "batchingEnabled") ||
                     initializeProperty(config.getMaxBatchSizeConfig(), "maxBatchSize");
    if (failed) {
      throw new ResourceConfigKeyParsingException("Configuration parsing error, see log file for details.");
    }
  }

  private boolean initializeProperty(Map<String, ?> config, String property) {
    boolean failed = false;
    List<ResourceConfigElement> elements = new ArrayList<>();
    for (Map.Entry<String, ?> entry: config.entrySet()) {
      try {
        ResourceConfigElement element = ResourceConfigElement.parse(property, entry.getKey(), entry.getValue());
        processConfigElement(element);
        elements.add(element);
      } catch (ResourceConfigKeyParsingException e) {
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

  private void processConfigElement(ResourceConfigElement element) throws ResourceConfigKeyParsingException {
    switch (element.getProperty()) {
      case "timeoutMs": _timeoutMs.add(element); break;
      case "batchingEnabled": _batchingEnabled.add(element); break;
      case "maxBatchSize": _maxBatchSize.add(element); break;
      default: throw new ResourceConfigKeyParsingException("Unrecognized property: " + element.getProperty());
    }
  }

  @Override
  public ResourceConfig apply(Request<?> request) {
    ResourceConfigCacheKey cacheKey = new ResourceConfigCacheKey(_inboundRequestContextFinder.find(), request);
    return _cache.computeIfAbsent(cacheKey, this::resolve);
  }

  private ResourceConfig resolve(ResourceConfigCacheKey cacheKey) {
    return new ResourceConfigBuilder()
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
