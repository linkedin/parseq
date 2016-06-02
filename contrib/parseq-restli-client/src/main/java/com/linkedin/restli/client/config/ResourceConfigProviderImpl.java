package com.linkedin.restli.client.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.restli.client.InboundRequestContext;
import com.linkedin.restli.client.InboundRequestContextFinder;
import com.linkedin.restli.client.Request;

class ResourceConfigProviderImpl implements ResourceConfigProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceConfigProviderImpl.class);

  static final Map<String, Map<String, Object>> DEFAULT_CONFIG = createDefaultConfigMap();
  private final InboundRequestContextFinder _inboundRequestContextFinder;
  private final ResourceConfigTree<Long> _timeoutMs = new ResourceConfigTree<>();
  private final ResourceConfigTree<Boolean> _batchingEnabled = new ResourceConfigTree<>();
  private final ResourceConfigTree<Integer> _maxBatchSize = new ResourceConfigTree<>();
  private final ResourceConfigTree<Boolean> _batchingDryRun = new ResourceConfigTree<>();

  public ResourceConfigProviderImpl(InboundRequestContextFinder inboundRequestContextFinder, Map<String, Map<String, Object>> config) throws ResourceConfigKeyParsingException {
    _inboundRequestContextFinder = inboundRequestContextFinder;
    initialize(config);
  }

  private void initialize(Map<String, Map<String, Object>> config) throws ResourceConfigKeyParsingException {
    boolean failed = false;
    List<String> properties = new ArrayList<>(config.keySet());
    Collections.sort(properties);
    for (String property: properties) {
      List<ResourceConfigElement> elements = new ArrayList<>();
      for (Map.Entry<String, Object> entry: config.get(property).entrySet()) {
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
    }
    if (failed) {
      throw new ResourceConfigKeyParsingException("Configuration parsing error, see log file for details.");
    }
  }

  private void processConfigElement(ResourceConfigElement element) throws ResourceConfigKeyParsingException {
    switch (element.getProperty()) {
      case "timeoutMs": _timeoutMs.add(element); break;
      case "batchingEnabled": _batchingEnabled.add(element); break;
      case "batchingDryRun": _batchingDryRun.add(element); break;
      case "maxBatchSize": _maxBatchSize.add(element); break;
      default: throw new ResourceConfigKeyParsingException("Unrecognized property: " + element.getProperty());
    }
  }

  @Override
  public ResourceConfig apply(Request<?> request) {
    //TODO cache result
    return resolve(_inboundRequestContextFinder.find(), request);
  }

  private ResourceConfig resolve(Optional<InboundRequestContext> inbound, Request<?> outbound) {
    return new ResourceConfigBuilder()
      .setTimeoutMs(_timeoutMs.resolve(inbound, outbound))
      .setBatchingEnabled(_batchingEnabled.resolve(inbound, outbound))
      .setMaxBatchSize(_maxBatchSize.resolve(inbound, outbound))
      .setBatchingDryRun(_batchingDryRun.resolve(inbound, outbound))
      .build();
  }

  /**
   * Default configuration map must specify default values for all properties.
   */
  private static Map<String, Map<String, Object>> createDefaultConfigMap() {
    Map<String, Map<String, Object>> config = new HashMap<>();
    addProperty(config, "timeoutMs", "*.*/*.*", TimeUnit.SECONDS.toMillis(10));
    addProperty(config, "batchingEnabled", "*.*/*.*", Boolean.FALSE);
    addProperty(config, "batchingDryRun", "*.*/*.*", Boolean.FALSE);
    addProperty(config, "maxBatchSize", "*.*/*.*", 1024);
    return Collections.unmodifiableMap(config);
  }

  private static <T> void addProperty(Map<String, Map<String, Object>> config, String property, String key, T value) {
    Map<String, Object> map = config.computeIfAbsent(property, k -> new HashMap<>());
    map.put(key, value);
  }

}
