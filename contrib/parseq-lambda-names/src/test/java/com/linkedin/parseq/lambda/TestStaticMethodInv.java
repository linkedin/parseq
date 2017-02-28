package com.linkedin.parseq.lambda;

import java.util.Optional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


public class TestStaticMethodInv extends BaseTest {

  private static final String CLASSNAME = TestStaticMethodInv.class.getSimpleName();

  @Test
  public void testStaticFunction() {
    Optional<LambdaClassDescription> description = getDescriptionForFunction(s -> BaseTest.staticFunction(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> BaseTest.staticFunction(_)", "testStaticFunction", CLASSNAME, description.get().toString());
  }

  @Test
  public void testStaticCallable() {
    Optional<LambdaClassDescription> description = getDescriptionForCallable(() -> BaseTest.staticCallable());
    assertTrue(description.isPresent());
    assertNameMatch("() -> BaseTest.staticCallable()", "testStaticCallable", CLASSNAME, description.get().toString());
  }

  @Test
  public void testStaticConsumer() {
    Optional<LambdaClassDescription> description = getDescriptionForConsumer(s -> BaseTest.staticConsumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> BaseTest.staticConsumer(_)", "testStaticConsumer", CLASSNAME, description.get().toString());
  }
}
