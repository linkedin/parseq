package com.linkedin.restli.client.config;

import static org.testng.Assert.assertEquals;

import java.util.Optional;

import org.testng.annotations.Test;

import com.linkedin.restli.common.ResourceMethod;

public class TestRequestConfigElement {

  @Test
  public void testParsingFallback() throws RequestConfigKeyParsingException {
    RequestConfigElement el = RequestConfigElement.parse("timeoutMs", "*.*/*.*", 100L);
    assertEquals(el.getInboundName(), Optional.empty());
    assertEquals(el.getInboundOp(), Optional.empty());
    assertEquals(el.getInboundOpName(), Optional.empty());
    assertEquals(el.getOutboundName(), Optional.empty());
    assertEquals(el.getOutboundOp(), Optional.empty());
    assertEquals(el.getOutboundOpName(), Optional.empty());
    assertEquals(el.getProperty(), "timeoutMs");
    assertEquals(el.getValue(), 100L);
  }

  @Test
  public void testParsingFullSimpleSpec() throws RequestConfigKeyParsingException {
    RequestConfigElement el = RequestConfigElement.parse("batchingEnabled", "profileView.GET/profile.BATCH_GET", true);
    assertEquals(el.getInboundName().get(), "profileView");
    assertEquals(el.getInboundOp().get(), ResourceMethod.GET.toString().toUpperCase());
    assertEquals(el.getInboundOpName(), Optional.empty());
    assertEquals(el.getOutboundName().get(), "profile");
    assertEquals(el.getOutboundOp().get(), ResourceMethod.BATCH_GET);
    assertEquals(el.getOutboundOpName(), Optional.empty());
    assertEquals(el.getProperty(), "batchingEnabled");
    assertEquals(el.getValue(), true);
  }

  @Test
  public void testParsingFullComplexSpec() throws RequestConfigKeyParsingException {
    RequestConfigElement el = RequestConfigElement.parse("batchingEnabled", "profileView.ACTION-doIt/profile.FINDER-all", true);
    assertEquals(el.getInboundName().get(), "profileView");
    assertEquals(el.getInboundOp().get(), ResourceMethod.ACTION.toString().toUpperCase());
    assertEquals(el.getInboundOpName().get(), "doIt");
    assertEquals(el.getOutboundName().get(), "profile");
    assertEquals(el.getOutboundOp().get(), ResourceMethod.FINDER);
    assertEquals(el.getOutboundOpName().get(), "all");
    assertEquals(el.getProperty(), "batchingEnabled");
    assertEquals(el.getValue(), true);
  }

  @Test(expectedExceptions = {RequestConfigKeyParsingException.class})
  public void testParsingMissingComplexOpName() throws RequestConfigKeyParsingException {
    RequestConfigElement.parse("timeoutMs", "*.*/*.FINDER", 100L);
  }

  @Test(expectedExceptions = {RequestConfigKeyParsingException.class})
  public void testParsingInvalidProperty() throws RequestConfigKeyParsingException {
    RequestConfigElement.parse("blah", "*.*/*.*", 100L);
  }

  @Test(expectedExceptions = {RequestConfigKeyParsingException.class})
  public void testParsingInvalidValue() throws RequestConfigKeyParsingException {
    RequestConfigElement.parse("timeoutMs", "*.*/*.*", true);
  }

  @Test(expectedExceptions = {RequestConfigKeyParsingException.class})
  public void testParsingInvalidKey() throws RequestConfigKeyParsingException {
    RequestConfigElement.parse("timeoutMs", "greetings.POST/greetings.DELETE/timeoutMs", 100L);
  }
}
