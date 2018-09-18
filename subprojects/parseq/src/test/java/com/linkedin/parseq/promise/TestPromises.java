/*
 * Copyright 2012 LinkedIn, Inc
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

package com.linkedin.parseq.promise;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;


/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TestPromises {
  @Test
  public void testValue() {
    final String value = "value";
    final Promise<String> promise = Promises.value(value);

    assertTrue(promise.isDone());
    assertFalse(promise.isFailed());
    assertEquals(value, promise.get());
  }

  @Test
  public void testError() {
    final Exception error = new Exception();
    final Promise<?> promise = Promises.error(error);

    assertTrue(promise.isDone());
    assertTrue(promise.isFailed());
    assertEquals(error, promise.getError());
  }

  @Test
  public void testPropagateValue() {
    final String value = "value";
    final SettablePromise<String> source = Promises.settable();
    final SettablePromise<String> dest = Promises.settable();

    Promises.propagateResult(source, dest);

    assertFalse(dest.isDone());

    source.done(value);

    assertTrue(dest.isDone());
    assertFalse(dest.isFailed());
    assertEquals(value, dest.get());
  }

  @Test
  public void testPropagateError() {
    final Exception error = new Exception();
    final SettablePromise<String> source = Promises.settable();
    final SettablePromise<String> dest = Promises.settable();

    Promises.propagateResult(source, dest);

    assertFalse(dest.isDone());

    source.fail(error);

    assertTrue(dest.isDone());
    assertTrue(dest.isFailed());
    assertEquals(error, dest.getError());
  }
}
