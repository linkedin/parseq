package com.linkedin.parseq.lambda;

import java.util.Optional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


public class TestStaticMethodRef extends BaseTest {

  private static final String CLASSNAME = TestStaticMethodRef.class.getSimpleName();

  @Test
  public void testStaticFunction() {
    Optional<LambdaClassDescription> description = getDescriptionForFunction(BaseTest::staticFunction);
    assertTrue(description.isPresent());
    assertNameMatch("BaseTest::staticFunction", "testStaticFunction", CLASSNAME, description.get().toString());
  }

  @Test
  public void testStaticCallable() {
    Optional<LambdaClassDescription> description = getDescriptionForCallable(BaseTest::staticCallable);
    assertTrue(description.isPresent());
    assertNameMatch("BaseTest::staticCallable", "testStaticCallable", CLASSNAME, description.get().toString());
  }

  @Test
  public void testStaticConsumer() {
    Optional<LambdaClassDescription> description = getDescriptionForConsumer(BaseTest::staticConsumer);
    assertTrue(description.isPresent());
    assertNameMatch("BaseTest::staticConsumer", "testStaticConsumer", CLASSNAME, description.get().toString());
  }
}