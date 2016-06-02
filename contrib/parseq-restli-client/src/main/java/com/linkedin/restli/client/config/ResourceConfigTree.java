package com.linkedin.restli.client.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.linkedin.restli.client.InboundRequestContext;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.RestConstants;
import com.linkedin.restli.internal.common.URIParamUtils;

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

  private Optional<String> getOpInName(Optional<InboundRequestContext> inboundRequestContext, String method) {
    if (method.equals(ResourceMethod.ACTION.toString().toUpperCase())) {
      return inboundRequestContext.flatMap(InboundRequestContext::getActionName);
    } else if (method.equals(ResourceMethod.FINDER.toString().toUpperCase())) {
      return inboundRequestContext.flatMap(InboundRequestContext::getFinderName);
    } else {
      return Optional.empty();
    }
  }

  private Optional<String> getOpOutName(Request<?> request) {
    if (request.getMethod() == ResourceMethod.ACTION) {
      return Optional.of((String)request.getQueryParamsObjects().get(RestConstants.ACTION_PARAM));
    } else if (request.getMethod() == ResourceMethod.FINDER) {
      return Optional.of((String)request.getQueryParamsObjects().get(RestConstants.QUERY_TYPE_PARAM));
    } else {
      return Optional.empty();
    }
  }

  Optional<ConfigValue<T>> resolveInboundOpName(Optional<InboundRequestContext> inbound, Request<?> outbound,
     Map<Optional<String>, ConfigValue<T>> map) {
    if (map != null) {
      Optional<String> inboundOp = inbound.map(r -> r.getMethod());
      Optional<String> inboundOpName = inboundOp.flatMap(method -> getOpInName(inbound, method));
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

  Optional<ConfigValue<T>> resolveInboundOp(Optional<InboundRequestContext> inbound, Request<?> outbound,
      Map<Optional<String>, Map<Optional<String>, ConfigValue<T>>> map) {
    if (map != null) {
      Optional<String> inboundOp = inbound.map(r -> r.getMethod());
      if (inboundOp.isPresent()) {
        Optional<ConfigValue<T>> value = resolveInboundOpName(inbound, outbound, map.get(inboundOp));
        if (value.isPresent()) {
          return value;
        }
      }
      return resolveInboundOpName(inbound, outbound, map.get(Optional.empty()));
    } else {
      return Optional.empty();
    }
  }

  Optional<ConfigValue<T>> resolveOutboundOpName(Optional<InboundRequestContext> inbound, Request<?> outbound,
      Map<Optional<String>, Map<Optional<String>, Map<Optional<String>, ConfigValue<T>>>> map) {
    if (map != null) {
      Optional<String> outboundOpName = getOpOutName(outbound);
      if (outboundOpName.isPresent()) {
        Optional<ConfigValue<T>> value = resolveInboundOp(inbound, outbound, map.get(outboundOpName));
        if (value.isPresent()) {
          return value;
        }
      }
      return resolveInboundOp(inbound, outbound, map.get(Optional.empty()));
    } else {
      return Optional.empty();
    }
  }

  Optional<ConfigValue<T>> resolveOutboundOp(Optional<InboundRequestContext> inbound, Request<?> outbound,
      Map<Optional<ResourceMethod>, Map<Optional<String>, Map<Optional<String>, Map<Optional<String>, ConfigValue<T>>>>> map) {
    if (map != null) {
      Optional<ResourceMethod> outboundOp = Optional.of(outbound.getMethod());
      if (outboundOp.isPresent()) {
        Optional<ConfigValue<T>> value = resolveOutboundOpName(inbound, outbound, map.get(outboundOp));
        if (value.isPresent()) {
          return value;
        }
      }
      return resolveOutboundOpName(inbound, outbound, map.get(Optional.empty()));
    } else {
      return Optional.empty();
    }
  }

  Optional<ConfigValue<T>> resolveInboundName(Optional<InboundRequestContext> inbound, Request<?> outbound,
      Map<Optional<String>, Map<Optional<ResourceMethod>, Map<Optional<String>, Map<Optional<String>, Map<Optional<String>, ConfigValue<T>>>>>> map) {
    if (map != null) {
      Optional<String> inboundName = inbound.map(r -> r.getName());
      if (inboundName.isPresent()) {
        Optional<ConfigValue<T>> value = resolveOutboundOp(inbound, outbound, map.get(inboundName));
        if (value.isPresent()) {
          return value;
        }
      }
      return resolveOutboundOp(inbound, outbound, map.get(Optional.empty()));
    } else {
      return Optional.empty();
    }
  }

  Optional<ConfigValue<T>> resolveOutboundName(Optional<InboundRequestContext> inbound, Request<?> outbound) {
    Optional<String> outboundName = Optional.of(URIParamUtils.extractPathComponentsFromUriTemplate(outbound.getBaseUriTemplate())[0]);
    if (outboundName.isPresent()) {
      Optional<ConfigValue<T>> value = resolveInboundName(inbound, outbound, _tree.get(outboundName));
      if (value.isPresent()) {
        return value;
      }
    }
    return resolveInboundName(inbound, outbound, _tree.get(Optional.empty()));
  }

  ConfigValue<T> resolve(Optional<InboundRequestContext> inbound, Request<?> outbound) {
    return resolveOutboundName(inbound, outbound).get();
  }
}
