package com.linkedin.restli.client;

import java.util.Optional;

public class InboundRequestContextBuilder {

  private String _name;
  private String _method;
  private String _finderName;
  private String _actionName;

  public InboundRequestContextBuilder setName(String name) {
    _name = name;
    return this;
  }

  public InboundRequestContextBuilder setMethod(String method) {
    _method = method;
    return this;
  }

  public InboundRequestContextBuilder setFinderName(String finderName) {
    _finderName = finderName;
    return this;
  }

  public InboundRequestContextBuilder setActionName(String actionName) {
    _actionName = actionName;
    return this;
  }

  public InboundRequestContext build() {
    return new InboundRequestContextImpl(_name, _method, Optional.ofNullable(_finderName), Optional.ofNullable(_actionName));
  }
}
