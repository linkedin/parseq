package com.linkedin.restli.client;

import java.util.Map;

public interface ParSeqRestliClientConfig {

  public Map<String, Boolean> isD2RequestTimeoutEnabledConfig();

  public Map<String, Long> getTimeoutMsConfig();

  public Map<String, Boolean> isBatchingEnabledConfig();

  public Map<String, Integer> getMaxBatchSizeConfig();
}
