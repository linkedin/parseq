package com.linkedin.restli.client.config;

import org.testng.annotations.Test;

public class TestResourceConfigElement {

  @Test
  public void testParsingKey() {
    ResourceConfigElement.parse("*.*/*.*/timeoutNsads", 100L);
  }
}
