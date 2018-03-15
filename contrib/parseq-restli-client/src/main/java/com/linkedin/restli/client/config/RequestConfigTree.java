package com.linkedin.restli.client.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.linkedin.restli.common.ResourceMethod;

class RequestConfigTree<T> {

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
  void add(RequestConfigElement element) {
    _tree.computeIfAbsent(element.getOutboundName(), k -> new HashMap<>())
         .computeIfAbsent(element.getInboundName(), k -> new HashMap<>())
         .computeIfAbsent(element.getOutboundOp(), k -> new HashMap<>())
         .computeIfAbsent(element.getOutboundOpName(), k -> new HashMap<>())
         .computeIfAbsent(element.getInboundOp(), k -> new HashMap<>())
         .putIfAbsent(element.getInboundOpName(), new ConfigValue<>((T)element.getValue(), element.getKey()));
  }

  Optional<ConfigValue<T>> resolveInboundOpName(RequestConfigCacheKey cacheKeyd,
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

  Optional<ConfigValue<T>> resolveInboundOp(RequestConfigCacheKey cacheKeyd,
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

  Optional<ConfigValue<T>> resolveOutboundOpName(RequestConfigCacheKey cacheKeyd,
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

  Optional<ConfigValue<T>> resolveOutboundOp(RequestConfigCacheKey cacheKeyd,
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

  /**
   * This method recursively uses given resolver to resolve a config by given name taking into account
   * syntax of sub-resource names. For example, for given name: Optional.of("foo:bar:baz") it will make
   * the following resolver calls:
   * - resolver(Optional.of("foo:bar:baz"))
   * - resolver(Optional.of("foo:bar"))
   * - resolver(Optional.of("foo"))
   * - resolver(Optional.empty())
   */
  Optional<ConfigValue<T>> resolveNameRecursively(Optional<String> name, Function<Optional<String>, Optional<ConfigValue<T>>> resolver) {
    Optional<ConfigValue<T>> value = resolver.apply(name);
    if (value.isPresent()) {
      return value;
    } else {
      if (name.isPresent()) {
        return resolveNameRecursively(name.filter(s -> s.lastIndexOf(':') > 0).map(s -> s.substring(0, s.lastIndexOf(':'))), resolver);
      } else {
        return Optional.empty();
      }
    }
  }

  Optional<ConfigValue<T>> resolveInboundName(RequestConfigCacheKey cacheKeyd,
      Map<Optional<String>, Map<Optional<ResourceMethod>, Map<Optional<String>, Map<Optional<String>, Map<Optional<String>, ConfigValue<T>>>>>> map) {
    if (map != null) {
      return resolveNameRecursively(cacheKeyd.getInboundName(), x -> resolveOutboundOp(cacheKeyd, map.get(x)));
    } else {
      return Optional.empty();
    }
  }

  Optional<ConfigValue<T>> resolveOutboundName(RequestConfigCacheKey cacheKeyd) {
    return resolveNameRecursively(Optional.of(cacheKeyd.getOutboundName()), x -> resolveInboundName(cacheKeyd, _tree.get(x)));
  }

  ConfigValue<T> resolve(RequestConfigCacheKey cacheKey) {
    return resolveOutboundName(cacheKey).get();
  }
}
