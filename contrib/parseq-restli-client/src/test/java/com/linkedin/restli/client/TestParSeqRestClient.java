/*
 * Copyright 2016 LinkedIn, Inc
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

package com.linkedin.restli.client;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.linkedin.parseq.Task;
import com.linkedin.restli.client.config.ResourceConfigOverridesBuilder;

public class TestParSeqRestClient extends ParSeqRestClientIntegrationTest {

  @Override
  public ParSeqRestClientConfig getParSeqRestClientConfig() {
    return new ParSeqRestClientConfigBuilder()
        .addTimeoutMs("*.*/greetings.GET", 9999L)
        .addTimeoutMs("*.*/greetings.*", 10001L)
        .addTimeoutMs("*.*/*.GET", 10002L)
        .addTimeoutMs("foo.*/greetings.GET", 10003L)
        .addTimeoutMs("foo.GET/greetings.GET", 10004L)
        .addTimeoutMs("foo.ACTION-*/greetings.GET", 10005L)
        .addTimeoutMs("foo.ACTION-bar/greetings.GET", 10006L)
        .build();
  }

  @Test
  public void testConfiguredTimeoutOutbound() {
      Task<?> task = greetingGet(1L);
      runAndWait(getTestClassName() + ".testConfiguredTimeoutOutbound", task);
      assertTrue(hasTask("withTimeout 9999ms src: *.*/greetings.GET", task.getTrace()));
  }

  @Test
  public void testConfiguredTimeoutOutboundOverride() {
      Task<?> task = greetingGet(1L, new ResourceConfigOverridesBuilder()
          .setTimeoutMs(5555L, "overriden")
          .build());
      runAndWait(getTestClassName() + ".testConfiguredTimeoutOutbound", task);
      assertTrue(hasTask("withTimeout 5555ms src: overriden", task.getTrace()));
  }

  @Test
  public void testConfiguredTimeoutOutboundOverrideNoSrc() {
      Task<?> task = greetingGet(1L, new ResourceConfigOverridesBuilder()
          .setTimeoutMs(5555L)
          .build());
      runAndWait(getTestClassName() + ".testConfiguredTimeoutOutbound", task);
      assertTrue(hasTask("withTimeout 5555ms", task.getTrace()));
  }

  @Test
  public void testConfiguredTimeoutInboundAndOutbound() {
    try {
      setInboundRequestContext(new InboundRequestContextBuilder()
          .setName("foo")
          .setMethod("GET")
          .build());
      Task<?> task = greetingGet(1L);
      runAndWait(getTestClassName() + ".testConfiguredTimeoutInboundAndOutbound", task);
      assertTrue(hasTask("withTimeout 10004ms src: foo.GET/greetings.GET", task.getTrace()));
    } finally {
      clearInboundRequestContext();
    }
  }

  @Test
  public void testConfiguredTimeoutMismatchedInboundOutbound() {
    try {
      setInboundRequestContext(new InboundRequestContextBuilder()
          .setName("blah")
          .setMethod("GET")
          .build());
      Task<?> task = greetingGet(1L);
      runAndWait(getTestClassName() + ".testConfiguredTimeoutMismatchedInboundOutbound", task);
      assertTrue(hasTask("withTimeout 9999ms src: *.*/greetings.GET", task.getTrace()));
    } finally {
      clearInboundRequestContext();
    }
  }

  @Test
  public void testConfiguredTimeoutFullActionAndOutbound() {
    try {
      setInboundRequestContext(new InboundRequestContextBuilder()
          .setName("foo")
          .setMethod("ACTION")
          .setActionName("bar")
          .build());
      Task<?> task = greetingGet(1L);
      runAndWait(getTestClassName() + ".testConfiguredTimeoutFullActionAndOutbound", task);
      assertTrue(hasTask("withTimeout 10006ms src: foo.ACTION-bar/greetings.GET", task.getTrace()));
    } finally {
      clearInboundRequestContext();
    }
  }

  @Test
  public void testConfiguredTimeoutPartialActionAndOutbound() {
    try {
      setInboundRequestContext(new InboundRequestContextBuilder()
          .setName("foo")
          .setMethod("ACTION")
          .build());
      Task<?> task = greetingGet(1L);
      runAndWait(getTestClassName() + ".testConfiguredTimeoutPartialActionAndOutbound", task);
      assertTrue(hasTask("withTimeout 10005ms src: foo.ACTION-*/greetings.GET", task.getTrace()));
    } finally {
      clearInboundRequestContext();
    }
  }

  @Test
  public void testConfiguredTimeoutOutboundOp() {
    try {
      setInboundRequestContext(new InboundRequestContextBuilder()
          .setName("blah")
          .setMethod("GET")
          .build());
      Task<?> task = greetingDel(9999L).toTry();
      runAndWait(getTestClassName() + ".testConfiguredTimeoutOutboundOp", task);
      assertTrue(hasTask("withTimeout 10001ms src: *.*/greetings.*", task.getTrace()));
    } finally {
      clearInboundRequestContext();
    }
  }

}
