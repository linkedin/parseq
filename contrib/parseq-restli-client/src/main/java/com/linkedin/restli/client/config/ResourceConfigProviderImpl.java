package com.linkedin.restli.client.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.linkedin.restli.client.InboundRequestFinder;
import com.linkedin.restli.client.Request;

class ResourceConfigProviderImpl implements ResourceConfigProvider {

  static final Map<String, Object> DEFAULT_CONFIG = createDefaultConfigMap();
  private final InboundRequestFinder _inboundRequestFinder;

  public ResourceConfigProviderImpl(InboundRequestFinder inboundRequestFinder) {
    _inboundRequestFinder = inboundRequestFinder;
  }

  @Override
  public ResourceConfig apply(Request<?> request) {
    // TODO Auto-generated method stub
    return null;
  }

  private static Map<String, Object> createDefaultConfigMap() {
    Map<String, Object> config = new HashMap<>();
    config.put("*.*/*.*/timeoutNs", TimeUnit.SECONDS.toNanos(10));
    config.put("*.*/*.*/batchingEnabled", Boolean.FALSE);
    config.put("*.*/*.*/batchingDryRun", Boolean.FALSE);
    config.put("*.*/*.*/maxBatchSize", 1024);
    return config;
  }

}
