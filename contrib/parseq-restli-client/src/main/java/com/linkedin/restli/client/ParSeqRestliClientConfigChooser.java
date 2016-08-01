package com.linkedin.restli.client;

import java.util.Optional;
import java.util.function.BiFunction;

@FunctionalInterface
public interface ParSeqRestliClientConfigChooser extends BiFunction<Optional<InboundRequestContext>, Request<?>, String> {
}
