package com.linkedin.parseq.lambda;

import java.util.Optional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


public class TestStaticMethodRef extends BaseTest {

  private static final String CLASSNAME = TestStaticMethodRef.class.getSimpleName();

  @Test
  public void testStaticFunction() {
    Optional<String> description = getDescriptionForFunction(BaseTest::staticFunction);
    assertTrue(description.isPresent());
    assertNameMatch("BaseTest::staticFunction", "testStaticFunction", CLASSNAME, description.get().toString());
  }

  @Test
  public void testStaticCallable() {
    Optional<String> description = getDescriptionForCallable(BaseTest::staticCallable);
    assertTrue(description.isPresent());
    assertNameMatch("BaseTest::staticCallable", "testStaticCallable", CLASSNAME, description.get().toString());
  }

  @Test
  public void testStaticConsumer() {
    Optional<String> description = getDescriptionForConsumer(BaseTest::staticConsumer);
    assertTrue(description.isPresent());
    assertNameMatch("BaseTest::staticConsumer", "testStaticConsumer", CLASSNAME, description.get().toString());
  }
}