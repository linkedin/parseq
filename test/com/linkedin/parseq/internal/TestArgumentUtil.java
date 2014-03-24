package com.linkedin.parseq.internal;

import org.testng.annotations.Test;

public class TestArgumentUtil
{
  @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = ".*foo.*")
  public void testNotNullWithNull()
  {
    ArgumentUtil.notNull(null, "foo");
  }

  @Test
  public void testNotNullWithNotNull()
  {
    // This should not throw
    ArgumentUtil.notNull(new Object(), "foo");
  }

  @Test(expectedExceptions = NullPointerException.class,
        expectedExceptionsMessageRegExp = ".*foo.*")
  public void testNotEmptyWithNull()
  {
    ArgumentUtil.notEmpty(null, "foo");
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
        expectedExceptionsMessageRegExp = ".*foo.*")
  public void testNotEmptyWithEmptyString()
  {
    ArgumentUtil.notEmpty("", "foo");
  }

  @Test
  public void testNotEmptyWithNotEmptyString()
  {
    // This should not throw
    ArgumentUtil.notEmpty("not empty string", "foo");
  }
}
