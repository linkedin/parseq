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

package com.linkedin.restli.client;

import java.util.function.Function;

import com.linkedin.data.template.RecordTemplate;
import com.linkedin.parseq.batching.Batch;
import com.linkedin.r2.message.RequestContext;
import com.linkedin.restli.client.config.RequestConfig;
import com.linkedin.restli.common.ResourceMethod;

interface RequestGroup {

  public static RequestGroup fromRequest(final Request<?> request, int maxBatchSize) {
    switch (request.getMethod()) {
      case GET:
        return new GetRequestGroup(request, maxBatchSize);
      case BATCH_GET:
        return new GetRequestGroup(request, maxBatchSize);
      default:
        throw new IllegalArgumentException("Can't create RequestGroup for request method: " + request.getMethod()
            + ", batching for this method must be disabled");
    }
  }

  public static boolean isBatchable(final Request<?> request, RequestConfig config) {
    return (request.getMethod().equals(ResourceMethod.GET) || request.getMethod().equals(ResourceMethod.BATCH_GET))
        && config.isBatchingEnabled().getValue();
  }

  <RT extends RecordTemplate> void executeBatch(Client client,
      Batch<RestRequestBatchKey, Response<Object>> batch, Function<Request<?>, RequestContext> requestContextProvider);

  <K, V> String getBatchName(Batch<K, V> batch);

  String getBaseUriTemplate();

  int getMaxBatchSize();

  int keySize(RestRequestBatchKey key);

}
