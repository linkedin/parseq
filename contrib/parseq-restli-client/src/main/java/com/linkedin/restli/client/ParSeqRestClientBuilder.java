package com.linkedin.restli.client;

import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.internal.ArgumentUtil;
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

import com.linkedin.restli.client.config.ParSeqRestClientConfig;

public class ParSeqRestClientBuilder {

  private RestClient _restClient;
  private ParSeqRestClientConfig _config;
  private BatchingSupport _batchingSupport;

  public ParSeqRestClient build() {
    ParSeqRestClientImpl parseqClient = new ParSeqRestClientImpl(_restClient, _config != null ? _config :
      ParSeqRestClientConfig.DEFAULT_PARSEQ_REST_CLIENT_CONFIG);
    if (_batchingSupport != null) {
      _batchingSupport.registerStrategy(parseqClient);
    }
    return parseqClient;
  }

  public RestClient getRestClient() {
    return _restClient;
  }

  public ParSeqRestClientBuilder setBatchingSupport(BatchingSupport batchingSupport) {
    _batchingSupport = batchingSupport;
    return this;
  }

  public ParSeqRestClientBuilder setRestClient(RestClient restClient) {
    ArgumentUtil.requireNotNull(restClient, "restClient");
    _restClient = restClient;
    return this;
  }

  public ParSeqRestClientConfig getConfig() {
    return _config;
  }

  public ParSeqRestClientBuilder setConfig(ParSeqRestClientConfig config) {
    ArgumentUtil.requireNotNull(config, "config");
    _config = config;
    return this;
  }

}
