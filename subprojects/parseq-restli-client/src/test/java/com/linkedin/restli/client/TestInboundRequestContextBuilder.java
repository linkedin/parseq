package com.linkedin.restli.client;

import static org.testng.Assert.assertEquals;

import java.util.Optional;

import org.testng.annotations.Test;

public class TestInboundRequestContextBuilder {

  @Test
  public void testGet() {
    InboundRequestContext ctx = new InboundRequestContextBuilder()
        .setName("name")
        .setMethod("method")
        .build();
    assertEquals(ctx.getName(), "name");
    assertEquals(ctx.getMethod(), "method");
    assertEquals(ctx.getActionName(), Optional.empty());
    assertEquals(ctx.getFinderName(), Optional.empty());
  }

  @Test
  public void testFullAction() {
    InboundRequestContext ctx = new InboundRequestContextBuilder()
        .setName("name")
        .setMethod("ACTION")
        .setActionName("doIt")
        .build();
    assertEquals(ctx.getName(), "name");
    assertEquals(ctx.getMethod(), "ACTION");
    assertEquals(ctx.getActionName(), Optional.of("doIt"));
    assertEquals(ctx.getFinderName(), Optional.empty());
  }

  @Test
  public void testFullFinder() {
    InboundRequestContext ctx = new InboundRequestContextBuilder()
        .setName("name")
        .setMethod("FINDER")
        .setFinderName("findIt")
        .build();
    assertEquals(ctx.getName(), "name");
    assertEquals(ctx.getMethod(), "FINDER");
    assertEquals(ctx.getActionName(), Optional.empty());
    assertEquals(ctx.getFinderName(), Optional.of("findIt"));
  }

  @Test
  public void testPartialAction() {
    InboundRequestContext ctx = new InboundRequestContextBuilder()
        .setName("name")
        .setMethod("ACTION")
        .build();
    assertEquals(ctx.getName(), "name");
    assertEquals(ctx.getMethod(), "ACTION");
    assertEquals(ctx.getActionName(), Optional.empty());
    assertEquals(ctx.getFinderName(), Optional.empty());
  }

  @Test
  public void testPartialFinder() {
    InboundRequestContext ctx = new InboundRequestContextBuilder()
        .setName("name")
        .setMethod("FINDER")
        .build();
    assertEquals(ctx.getName(), "name");
    assertEquals(ctx.getMethod(), "FINDER");
    assertEquals(ctx.getActionName(), Optional.empty());
    assertEquals(ctx.getFinderName(), Optional.empty());
  }

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testJustFinderName() {
    new InboundRequestContextBuilder()
        .setName("name")
        .setMethod("GET")
        .setFinderName("findIt")
        .build();
  }

  @Test(expectedExceptions={IllegalArgumentException.class})
  public void testJustActionName() {
    new InboundRequestContextBuilder()
        .setName("name")
        .setMethod("GET")
        .setActionName("doIt")
        .build();
  }

}
