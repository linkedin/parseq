package com.linkedin.parseq.lambda;

import java.util.Optional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


public class TestMethodRef extends BaseTest {

  private static final String CLASSNAME = TestMethodRef.class.getSimpleName();

  TestMethodRef field = this;

  @Test
  public void testFunctionOnThis() {
    Optional<String> description = getDescriptionForFunction(this::function);
    assertTrue(description.isPresent());
    assertNameMatch("::function", "testFunctionOnThis", CLASSNAME, description.get());
  }

  TestMethodRef getTestMethodRef() {
    return new TestMethodRef();
  }

  @Test
  public void testFunctionOnThisChained() {
    Optional<String> description = getTestMethodRef().getDescriptionForFunction(this::function);
    assertTrue(description.isPresent());
    assertNameMatch("::function", "testFunctionOnThisChained", CLASSNAME, description.get());
  }


  @Test
  public void testCallableOnThis() {
    Optional<String> description = getDescriptionForCallable(this::callable);
    assertTrue(description.isPresent());
    assertNameMatch("::callable", "testCallableOnThis", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerOnThis() {
    Optional<String> description = getDescriptionForConsumer(this::consumer);
    assertTrue(description.isPresent());
    assertNameMatch("::consumer", "testConsumerOnThis", CLASSNAME, description.get());
  }

  @Test
  public void testFunctionOnStaticField() {
    Optional<String> description = getDescriptionForFunction(staticField::function);
    assertTrue(description.isPresent());
    assertNameMatch("::function", "testFunctionOnStaticField", CLASSNAME, description.get());
  }

  @Test
  public void testCallableOnStaticField() {
    Optional<String> description = getDescriptionForCallable(staticField::callable);
    assertTrue(description.isPresent());
    assertNameMatch("::callable", "testCallableOnStaticField", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerOnStaticField() {
    Optional<String> description = getDescriptionForConsumer(staticField::consumer);
    assertTrue(description.isPresent());
    assertNameMatch("::consumer", "testConsumerOnStaticField", CLASSNAME, description.get());
  }

  @Test
  public void testFunctionOnField() {
    Optional<String> description = getDescriptionForFunction(field::function);
    assertTrue(description.isPresent());
    assertNameMatch("::function", "testFunctionOnField", CLASSNAME, description.get());
  }

  @Test
  public void testCallableOnField() {
    Optional<String> description = getDescriptionForCallable(field::callable);
    assertTrue(description.isPresent());
    assertNameMatch("::callable", "testCallableOnField", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerOnField() {
    Optional<String> description = getDescriptionForConsumer(field::consumer);
    assertTrue(description.isPresent());
    assertNameMatch("::consumer", "testConsumerOnField", CLASSNAME, description.get());
  }

  @Test
  public void testFunctionWithTwoParams() {
    Optional<String> description = getDescriptionForBiFunction(this::functionTwo);
    assertTrue(description.isPresent());
    assertNameMatch("::functionTwo", "testFunctionWithTwoParams", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerWithTwoParams() {
    Optional<String> description = getDescriptionForBiConsumer(this::consumerTwo);
    assertTrue(description.isPresent());
    assertNameMatch("::consumerTwo", "testConsumerWithTwoParams", CLASSNAME, description.get());
  }

  @Test
  public void testFunctionOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<String> description = getDescriptionForFunction(localVar::function);
    assertTrue(description.isPresent());
    assertNameMatch("::function", "testFunctionOnVar", CLASSNAME, description.get());
  }

  @Test
  public void testCallableOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<String> description = getDescriptionForCallable(localVar::callable);
    assertTrue(description.isPresent());
    assertNameMatch("::callable", "testCallableOnVar", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<String> description = getDescriptionForConsumer(localVar::consumer);
    assertTrue(description.isPresent());
    assertNameMatch("::consumer", "testConsumerOnVar", CLASSNAME, description.get());
  }

  @Test
  public void testFunctionOnNoParamMethod() {
    Optional<String> description = getDescriptionForFunction(noParamMethod()::function);
    assertTrue(description.isPresent());
    assertNameMatch("::function", "testFunctionOnNoParamMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testCallableOnNoParamMethod() {
    Optional<String> description = getDescriptionForCallable(noParamMethod()::callable);
    assertTrue(description.isPresent());
    assertNameMatch("::callable", "testCallableOnNoParamMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testConsumerOnNoParamMethod() {
    Optional<String> description = getDescriptionForConsumer(noParamMethod()::consumer);
    assertTrue(description.isPresent());
    assertNameMatch("::consumer", "testConsumerOnNoParamMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testFunctionOnNoParamStaticMethod() {
    Optional<String> description = getDescriptionForFunction(noParamStaticMethod()::function);
    assertTrue(description.isPresent());
    assertNameMatch("::function", "testFunctionOnNoParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testCallableOnNoParamStaticMethod() {
    Optional<String> description = getDescriptionForCallable(noParamStaticMethod()::callable);
    assertTrue(description.isPresent());
    assertNameMatch("::callable", "testCallableOnNoParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testConsumerOnNoParamStaticMethod() {
    Optional<String> description = getDescriptionForConsumer(noParamStaticMethod()::consumer);
    assertTrue(description.isPresent());
    assertNameMatch("::consumer", "testConsumerOnNoParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testFunctionOnParamStaticMethod() {
    Optional<String> description = getDescriptionForFunction(paramStaticMethod(0, "")::function);
    assertTrue(description.isPresent());
    assertNameMatch("::function", "testFunctionOnParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testCallableOnParamStaticMethod() {
    Optional<String> description = getDescriptionForCallable(paramStaticMethod(Long.MAX_VALUE, "")::callable);
    assertTrue(description.isPresent());
    assertNameMatch("::callable", "testCallableOnParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testConsumerOnParamStaticMethod() {
    Optional<String> description = getDescriptionForConsumer(paramStaticMethod(Long.MAX_VALUE, "")::consumer);
    assertTrue(description.isPresent());
    assertNameMatch("::consumer", "testConsumerOnParamStaticMethod", CLASSNAME,
        description.get());
  }

  @Test
  public void testFunctionOnParamMethod() {
    Optional<String> description = getDescriptionForFunction(paramMethod(0, "")::function);
    assertTrue(description.isPresent());
    assertNameMatch("::function", "testFunctionOnParamMethod", CLASSNAME, description.get());
  }

  @Test
  public void testCallableOnParamMethod() {
    Optional<String> description = getDescriptionForCallable(paramMethod(Long.MAX_VALUE, "")::callable);
    assertTrue(description.isPresent());
    assertNameMatch("::callable", "testCallableOnParamMethod", CLASSNAME, description.get());
  }

  @Test
  public void testConsumerOnParamMethod() {
    Optional<String> description = getDescriptionForConsumer(paramMethod(Long.MAX_VALUE, "")::consumer);
    assertTrue(description.isPresent());
    assertNameMatch("::consumer", "testConsumerOnParamMethod", CLASSNAME, description.get());
  }

  @Test
  public void testNewInstance() {
    Optional<String> description = getDescriptionForCallable(new String("abc")::toString);
    assertTrue(description.isPresent());
    assertNameMatch("::toString", "testNewInstance", CLASSNAME, description.get());
  }
}
