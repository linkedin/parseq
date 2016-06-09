package com.linkedin.restli.client.config;

import java.util.Optional;

import com.linkedin.restli.client.InboundRequestContext;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.common.ResourceMethod;
import com.linkedin.restli.common.RestConstants;
import com.linkedin.restli.internal.common.URIParamUtils;

class RequestConfigCacheKey {

  private final Optional<String> _inboundName;
  private final Optional<String> _inboundOp;
  private final Optional<String> _inboundOpName;
  private final String _outboundName;
  private final ResourceMethod _outboundOp;
  private final Optional<String> _outboundOpName;

  RequestConfigCacheKey(Optional<InboundRequestContext> inbound, Request<?> outbound) {
    _outboundName = URIParamUtils.extractPathComponentsFromUriTemplate(outbound.getBaseUriTemplate())[0];
    _inboundName = inbound.map(r -> r.getName());
    _outboundOp = outbound.getMethod();
    _outboundOpName = getOpOutName(outbound);
    _inboundOp = inbound.map(r -> r.getMethod());
    _inboundOpName = _inboundOp.flatMap(method -> getOpInName(inbound, method));
  }

  private static Optional<String> getOpOutName(Request<?> request) {
    if (request.getMethod() == ResourceMethod.ACTION) {
      return Optional.of((String)request.getQueryParamsObjects().get(RestConstants.ACTION_PARAM));
    } else if (request.getMethod() == ResourceMethod.FINDER) {
      return Optional.of((String)request.getQueryParamsObjects().get(RestConstants.QUERY_TYPE_PARAM));
    } else {
      return Optional.empty();
    }
  }

  private static Optional<String> getOpInName(Optional<InboundRequestContext> inboundRequestContext, String method) {
    if (method.equals(ResourceMethod.ACTION.toString().toUpperCase())) {
      return inboundRequestContext.flatMap(InboundRequestContext::getActionName);
    } else if (method.equals(ResourceMethod.FINDER.toString().toUpperCase())) {
      return inboundRequestContext.flatMap(InboundRequestContext::getFinderName);
    } else {
      return Optional.empty();
    }
  }

  public Optional<String> getInboundName() {
    return _inboundName;
  }

  public Optional<String> getInboundOp() {
    return _inboundOp;
  }

  public Optional<String> getInboundOpName() {
    return _inboundOpName;
  }

  public String getOutboundName() {
    return _outboundName;
  }

  public ResourceMethod getOutboundOp() {
    return _outboundOp;
  }

  public Optional<String> getOutboundOpName() {
    return _outboundOpName;
  }

  @Override
  public String toString() {
    return "RequestConfigCacheKey [inboundName=" + _inboundName + ", inboundOp=" + _inboundOp + ", inboundOpName="
        + _inboundOpName + ", outboundName=" + _outboundName + ", outboundOp=" + _outboundOp + ", outboundOpName="
        + _outboundOpName + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((_inboundName == null) ? 0 : _inboundName.hashCode());
    result = prime * result + ((_inboundOp == null) ? 0 : _inboundOp.hashCode());
    result = prime * result + ((_inboundOpName == null) ? 0 : _inboundOpName.hashCode());
    result = prime * result + ((_outboundName == null) ? 0 : _outboundName.hashCode());
    result = prime * result + ((_outboundOp == null) ? 0 : _outboundOp.hashCode());
    result = prime * result + ((_outboundOpName == null) ? 0 : _outboundOpName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    RequestConfigCacheKey other = (RequestConfigCacheKey) obj;
    if (_inboundName == null) {
      if (other._inboundName != null)
        return false;
    } else if (!_inboundName.equals(other._inboundName))
      return false;
    if (_inboundOp == null) {
      if (other._inboundOp != null)
        return false;
    } else if (!_inboundOp.equals(other._inboundOp))
      return false;
    if (_inboundOpName == null) {
      if (other._inboundOpName != null)
        return false;
    } else if (!_inboundOpName.equals(other._inboundOpName))
      return false;
    if (_outboundName == null) {
      if (other._outboundName != null)
        return false;
    } else if (!_outboundName.equals(other._outboundName))
      return false;
    if (_outboundOp != other._outboundOp)
      return false;
    if (_outboundOpName == null) {
      if (other._outboundOpName != null)
        return false;
    } else if (!_outboundOpName.equals(other._outboundOpName))
      return false;
    return true;
  }
}
