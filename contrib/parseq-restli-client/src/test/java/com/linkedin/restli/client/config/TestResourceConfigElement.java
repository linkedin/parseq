package com.linkedin.restli.client.config;

import static org.testng.Assert.assertEquals;

import java.util.Optional;

import org.testng.annotations.Test;

import com.linkedin.restli.common.ResourceMethod;

public class TestResourceConfigElement {

  @Test
  public void testParsingFallback() throws ResourceConfigKeyParsingException {
    ResourceConfigElement el = ResourceConfigElement.parse("timeoutMs", "*.*/*.*", 100L);
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
  public void testParsingFullSimpleSpec() throws ResourceConfigKeyParsingException {
    ResourceConfigElement el = ResourceConfigElement.parse("batchingEnabled", "profileView.GET/profile.BATCH_GET", true);
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
  public void testParsingFullComplexSpec() throws ResourceConfigKeyParsingException {
    ResourceConfigElement el = ResourceConfigElement.parse("batchingEnabled", "profileView.ACTION-doIt/profile.FINDER-all", true);
    assertEquals(el.getInboundName().get(), "profileView");
    assertEquals(el.getInboundOp().get(), ResourceMethod.ACTION.toString().toUpperCase());
    assertEquals(el.getInboundOpName().get(), "doIt");
    assertEquals(el.getOutboundName().get(), "profile");
    assertEquals(el.getOutboundOp().get(), ResourceMethod.FINDER);
    assertEquals(el.getOutboundOpName().get(), "all");
    assertEquals(el.getProperty(), "batchingEnabled");
    assertEquals(el.getValue(), true);
  }

  @Test(expectedExceptions = {ResourceConfigKeyParsingException.class})
  public void testParsingMissingComplexOpName() throws ResourceConfigKeyParsingException {
    ResourceConfigElement.parse("timeoutMs", "*.*/*.FINDER", 100L);
  }

  @Test(expectedExceptions = {ResourceConfigKeyParsingException.class})
  public void testParsingInvalidProperty() throws ResourceConfigKeyParsingException {
    ResourceConfigElement.parse("blah", "*.*/*.*", 100L);
  }

  @Test(expectedExceptions = {ResourceConfigKeyParsingException.class})
  public void testParsingInvalidValue() throws ResourceConfigKeyParsingException {
    ResourceConfigElement.parse("timeoutMs", "*.*/*.*", true);
  }

  @Test(expectedExceptions = {ResourceConfigKeyParsingException.class})
  public void testParsingInvalidKey() throws ResourceConfigKeyParsingException {
    ResourceConfigElement.parse("timeoutMs", "greetings.POST/greetings.DELETE/timeoutMs", 100L);
  }
}
