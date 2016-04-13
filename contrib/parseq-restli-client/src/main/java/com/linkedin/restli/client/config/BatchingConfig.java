/*
 * Copyright 2016 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

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
