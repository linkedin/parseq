package com.linkedin.restli.client;

import java.util.Optional;

class InboundRequestContextImpl implements InboundRequestContext {

  private final String _name;
  private final String _method;
  private final Optional<String> _finderName;
  private final Optional<String> _actionName;

  public InboundRequestContextImpl(String name, String method, Optional<String> finderName,
      Optional<String> actionName) {
    if (finderName.isPresent()) {
      if (!method.equalsIgnoreCase("FINDER")) {
        throw new IllegalArgumentException("Finder name declared but menthod is not FINDER, it is: " + method);
      }
      if (actionName.isPresent()) {
        throw new IllegalArgumentException("Action name declared but method if FINDER");
      }
    }
    if (actionName.isPresent()) {
      if (!method.equalsIgnoreCase("ACTION")) {
        throw new IllegalArgumentException("Action name declared but menthod is not ACTION, it is: " + method);
      }
    }
    _name = name;
    _method = method;
    _finderName = finderName;
    _actionName = actionName;
  }

  @Override
  public String getName() {
    return _name;
  }

  @Override
  public String getMethod() {
    return _method;
  }

  @Override
  public Optional<String> getFinderName() {
    return _finderName;
  }

  @Override
  public Optional<String> getActionName() {
    return _actionName;
  }
}
