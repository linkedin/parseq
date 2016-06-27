/*
 * Copyright 2013 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.linkedin.parseq;

import java.util.concurrent.Callable;


/**
 * An object similar to {@link java.util.concurrent.Callable}, but the
 * {@link #call()} method allows a {@link Throwable} to be thrown.
 *
 * @deprecated  As of 2.0.0, replaced by {@link Callable}.
 * @see Callable
 * @author Chris Pettitt
 */
@FunctionalInterface
@Deprecated
public interface ThrowableCallable<T> {
  /**
   * Computes a value of type {@code T} or throws a {@link Throwable} if an
   * error occurred during the computation.
   *
   * @return the computed result for this callable
   * @throws Throwable if an error occurred during the computation.
   */
  T call() throws Throwable;
}
