package com.linkedin.restli.client;

import java.util.Optional;

public interface InboundRequestContext {

  public String getName();

  public String getMethod();

  public Optional<String> getFinderName();

  public Optional<String> getActionName();
}
