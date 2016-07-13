package com.linkedin.restli.client;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 *
 *
 * @param <T> enum that represents available configurations
 */
@FunctionalInterface
public interface ParSeqRestClientConfigChooser<T extends Enum<T>> extends BiFunction<Optional<InboundRequestContext>, Request<?>, T> {
}
