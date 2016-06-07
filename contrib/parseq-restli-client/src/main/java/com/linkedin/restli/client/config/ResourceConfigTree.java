package com.linkedin.restli.client.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.linkedin.restli.common.ResourceMethod;

class ResourceConfigTree<T> {

  /**
   * Priorities:
   * 1. outbound name
   * 2. inbound name
   * 3. outbound operation
   * 4. outbound operation name
   * 5. inbound operation
   * 6. inbound operation name
   */
  private final Map<Optional<String>, Map<Optional<String>, Map<Optional<ResourceMethod>, Map<Optional<String>, Map<Optional<String>, Map<Optional<String>, ConfigValue<T>>>>>>> _tree =
      new HashMap<>();

  @SuppressWarnings("unchecked")
  void add(ResourceConfigElement element) {
    _tree.computeIfAbsent(element.getOutboundName(), k -> new HashMap<>())
         .computeIfAbsent(element.getInboundName(), k -> new HashMap<>())
         .computeIfAbsent(element.getOutboundOp(), k -> new HashMap<>())
         .computeIfAbsent(element.getOutboundOpName(), k -> new HashMap<>())
         .computeIfAbsent(element.getInboundOp(), k -> new HashMap<>())
         .putIfAbsent(element.getInboundOpName(), new ConfigValue<T>((T)element.getValue(), element.getKey()));
  }

  Optional<ConfigValue<T>> resolveInboundOpName(ResourceConfigCacheKey cacheKeyd,
     Map<Optional<String>, ConfigValue<T>> map) {
    if (map != null) {
      Optional<String> inboundOpName = cacheKeyd.getInboundOpName();
      if (inboundOpName.isPresent()) {
        ConfigValue<T> value = map.get(inboundOpName);
        if (value != null) {
          return Optional.of(value);
        }
      }
      return Optional.ofNullable(map.get(Optional.empty()));
    } else {
      return Optional.empty();
    }
  }

  Optional<ConfigValue<T>> resolveInboundOp(ResourceConfigCacheKey cacheKeyd,
      Map<Optional<String>, Map<Optional<String>, ConfigValue<T>>> map) {
    if (map != null) {
      Optional<String> inboundOp = cacheKeyd.getInboundOp();
      if (inboundOp.isPresent()) {
        Optional<ConfigValue<T>> value = resolveInboundOpName(cacheKeyd, map.get(inboundOp));
        if (value.isPresent()) {
          return value;
        }
      }
      return resolveInboundOpName(cacheKeyd, map.get(Optional.empty()));
    } else {
      return Optional.empty();
    }
  }

  Optional<ConfigValue<T>> resolveOutboundOpName(ResourceConfigCacheKey cacheKeyd,
      Map<Optional<String>, Map<Optional<String>, Map<Optional<String>, ConfigValue<T>>>> map) {
    if (map != null) {
      Optional<String> outboundOpName = cacheKeyd.getOutboundOpName();
      if (outboundOpName.isPresent()) {
        Optional<ConfigValue<T>> value = resolveInboundOp(cacheKeyd, map.get(outboundOpName));
        if (value.isPresent()) {
          return value;
        }
      }
      return resolveInboundOp(cacheKeyd, map.get(Optional.empty()));
    } else {
      return Optional.empty();
    }
  }

  Optional<ConfigValue<T>> resolveOutboundOp(ResourceConfigCacheKey cacheKeyd,
      Map<Optional<ResourceMethod>, Map<Optional<String>, Map<Optional<String>, Map<Optional<String>, ConfigValue<T>>>>> map) {
    if (map != null) {
      Optional<ResourceMethod> outboundOp = Optional.of(cacheKeyd.getOutboundOp());
      if (outboundOp.isPresent()) {
        Optional<ConfigValue<T>> value = resolveOutboundOpName(cacheKeyd, map.get(outboundOp));
        if (value.isPresent()) {
          return value;
        }
      }
      return resolveOutboundOpName(cacheKeyd, map.get(Optional.empty()));
    } else {
      return Optional.empty();
    }
  }

  Optional<ConfigValue<T>> resolveInboundName(ResourceConfigCacheKey cacheKeyd,
      Map<Optional<String>, Map<Optional<ResourceMethod>, Map<Optional<String>, Map<Optional<String>, Map<Optional<String>, ConfigValue<T>>>>>> map) {
    if (map != null) {
      Optional<String> inboundName = cacheKeyd.getInboundName();
      if (inboundName.isPresent()) {
        Optional<ConfigValue<T>> value = resolveOutboundOp(cacheKeyd, map.get(inboundName));
        if (value.isPresent()) {
          return value;
        }
      }
      return resolveOutboundOp(cacheKeyd, map.get(Optional.empty()));
    } else {
      return Optional.empty();
    }
  }

  Optional<ConfigValue<T>> resolveOutboundName(ResourceConfigCacheKey cacheKeyd) {
    Optional<String> outboundName = Optional.of(cacheKeyd.getOutboundName());
    if (outboundName.isPresent()) {
      Optional<ConfigValue<T>> value = resolveInboundName(cacheKeyd, _tree.get(outboundName));
      if (value.isPresent()) {
        return value;
      }
    }
    return resolveInboundName(cacheKeyd, _tree.get(Optional.empty()));
  }

  ConfigValue<T> resolve(ResourceConfigCacheKey cacheKey) {
    return resolveOutboundName(cacheKey).get();
  }
}
