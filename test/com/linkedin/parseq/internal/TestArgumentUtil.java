package com.linkedin.parseq.internal;

import org.testng.annotations.Test;

public class TestArgumentUtil
{
  @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = ".*foo.*")
  public void testNotNullWithNull()
  {
    ArgumentUtil.requireNotNull(null, "foo");
  }

  @Test
  public void testNotNullWithNotNull()
  {
    // This should not throw
    ArgumentUtil.requireNotNull(new Object(), "foo");
  }

  @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = ".*foo.*")
  public void testNotEmptyWithNull()
  {
    ArgumentUtil.requireNotEmpty(null, "foo");
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = ".*foo.*")
  public void testNotEmptyWithEmptyString()
  {
    ArgumentUtil.requireNotEmpty("", "foo");
  }

  @Test
  public void testNotEmptyWithNotEmptyString()
  {
    // This should not throw
    ArgumentUtil.requireNotEmpty("not empty string", "foo");
  }
}
