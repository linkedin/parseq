package com.linkedin.restli.client;

import java.util.Optional;

@FunctionalInterface
public interface InboundRequestContextFinder {
  Optional<InboundRequestContext> find();
}
