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

package com.linkedin.parseq.zk.client;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promises;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * A {@link Map} that is shared across {@link Task}s belong to the same plan.
 *
 * @author Ang Xu
 */
public final class PlanLocal {

  private static final ConcurrentMap<Long, Map<String, Object>> _planLocalMap = new ConcurrentHashMap<>();

  public static <V> Task<Void> put(String key, V value) {
    return Task.async("Put PlanLocal", context -> {
      long id = context.getPlanId();
      final Map<String, Object> map = _planLocalMap.computeIfAbsent(id, k -> new HashMap());
      map.put(key, value);
      return Promises.value(null);
    });
  }

  public static <V> Task<V> get(String key, Class<V> valueType) {
    return Task.async("Get PlanLocal", context -> {
      long id = context.getPlanId();
      final Map<String, Object> map = _planLocalMap.get(id);
      return Promises.value(map != null ? (V) map.get(key) : null);
    });
  }

  public static Task<Void> remove(String key) {
    return Task.async("Remove PlanLocal", context -> {
      long id = context.getPlanId();
      _planLocalMap.computeIfPresent(id, (k, v) -> {
        v.remove(key);
        return v.isEmpty() ? null : v;
      });
      return Promises.value(null);
    });
  }
}