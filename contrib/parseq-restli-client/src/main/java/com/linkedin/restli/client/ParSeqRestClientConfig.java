package com.linkedin.restli.client;

import java.util.Map;

public interface ParSeqRestClientConfig {

  public Map<String, Long> getTimeoutMsConfig();

  public Map<String, Boolean> isBatchingEnabledConfig();

  public Map<String, Integer> getMaxBatchSizeConfig();

  public Map<String, Boolean> isBatchingDryRunConfig();

}
