package com.linkedin.restli.client;

import java.util.Optional;

@FunctionalInterface
public interface InboundRequestFinder {
  Optional<Request<?>> find();
}
