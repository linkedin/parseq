package com.linkedin.restli.client.config;

public class BatchingConfig {

  public static final int DEFAULT_MAX_BATCH_SIZE = 1024;
  public static final BatchingConfig DEFAULT_BATCHING_CONFIG = new BatchingConfig(false, DEFAULT_MAX_BATCH_SIZE, true);

  private final boolean _batchingEnabled;
  private final int _maxBatchSize;
  private final boolean _dryRun;

  public BatchingConfig(boolean batchingEnabled, int maxBatchSize, boolean dryRun) {
    _batchingEnabled = batchingEnabled;
    _maxBatchSize = maxBatchSize;
    _dryRun = dryRun;
  }

  public boolean isBatchingEnabled() {
    return _batchingEnabled;
  }

  public int getMaxBatchSize() {
    return _maxBatchSize;
  }

  public boolean isDryRun() {
    return _dryRun;
  }
}
