package com.linkedin.restli.client;

import java.util.Optional;
import java.util.function.BiFunction;

@FunctionalInterface
public interface ParSeqRestClientConfigChooser extends BiFunction<Optional<InboundRequestContext>, Request<?>, String> {
}
