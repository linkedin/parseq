package com.linkedin.parseq.lambda;

import java.util.Optional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


public class TestMethodInv extends BaseTest {

  private static final String CLASSNAME = TestMethodInv.class.getSimpleName();

  @Test
  public void testFunctionInvocation() {
    Optional<LambdaClassDescription> description = getDescriptionForFunction(s -> function(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> function(_)", "testFunctionInvocation", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableInvocation() {
    Optional<LambdaClassDescription> description = getDescriptionForCallable(() -> callable());
    assertTrue(description.isPresent());
    assertNameMatch("() -> callable()", "testCallableInvocation", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerInvocation() {
    Optional<LambdaClassDescription> description = getDescriptionForConsumer(s -> consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> consumer(_)", "testConsumerInvocation", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionInvocationOnNew() {
    Optional<LambdaClassDescription> description = getDescriptionForFunction(s -> new TestMethodInv().function(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> new TestMethodInv().function(_)", "testFunctionInvocationOnNew", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableInvocationOnNew() {
    Optional<LambdaClassDescription> description = getDescriptionForCallable(() -> new TestMethodInv().callable());
    assertTrue(description.isPresent());
    assertNameMatch("() -> new TestMethodInv().callable()", "testCallableInvocationOnNew", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerInvocationOnNew() {
    Optional<LambdaClassDescription> description = getDescriptionForConsumer(s -> new TestMethodInv().consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> new TestMethodInv().consumer(_)", "testConsumerInvocationOnNew", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionInvocationOnField() {
    Optional<LambdaClassDescription> description = getDescriptionForFunction(s -> staticField.function(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> staticField.function(_)", "testFunctionInvocationOnField", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableInvocationOnField() {
    Optional<LambdaClassDescription> description = getDescriptionForCallable(() -> staticField.callable());
    assertTrue(description.isPresent());
    assertNameMatch("() -> staticField.callable()", "testCallableInvocationOnField", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerInvocationOnField() {
    Optional<LambdaClassDescription> description = getDescriptionForConsumer(s -> staticField.consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> staticField.consumer(_)", "testConsumerInvocationOnField", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionInvocationWithTwoParams() {
    Optional<LambdaClassDescription> description = getDescriptionForBiFunction((s1,s2) -> functionTwo(s1,s2));
    assertTrue(description.isPresent());
    assertNameMatch("(s1,s2) -> functionTwo(_,_)", "testFunctionInvocationWithTwoParams", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerInvocationWithTwoParams() {
    Optional<LambdaClassDescription> description = getDescriptionForBiConsumer((s1,s2) -> consumerTwo(s1,s2));
    assertTrue(description.isPresent());
    assertNameMatch("(s1,s2) -> consumerTwo(_,_)", "testConsumerInvocationWithTwoParams", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<LambdaClassDescription> description  = getDescriptionForFunction(s -> localVar.function(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> function(_)", "testFunctionOnVar", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<LambdaClassDescription> description  = getDescriptionForCallable(() -> localVar.callable());
    assertTrue(description.isPresent());
    assertNameMatch("() -> callable()", "testCallableOnVar", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<LambdaClassDescription> description  = getDescriptionForConsumer(s -> localVar.consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> consumer(_)", "testConsumerOnVar", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionOnNoParamMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForFunction(s -> noParamMethod().function(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> noParamMethod().function(_)", "testFunctionOnNoParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableOnNoParamMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForCallable(() -> noParamMethod().callable());
    assertTrue(description.isPresent());
    assertNameMatch("() -> noParamMethod().callable()", "testCallableOnNoParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerOnNoParamMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForConsumer(s -> noParamMethod().consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> noParamMethod().consumer(_)", "testConsumerOnNoParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionOnNoParamStaticMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForFunction(s -> noParamStaticMethod().function(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> noParamStaticMethod().function(_)", "testFunctionOnNoParamStaticMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableOnNoParamStaticMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForCallable(() -> noParamStaticMethod().callable());
    assertTrue(description.isPresent());
    assertNameMatch("() -> noParamStaticMethod().callable()", "testCallableOnNoParamStaticMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerOnNoParamStaticMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForConsumer(s -> noParamStaticMethod().consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> noParamStaticMethod().consumer(_)", "testConsumerOnNoParamStaticMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionOnParamStaticMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForFunction(s -> paramStaticMethod(0, "").function(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> paramStaticMethod(_,_).function(_)", "testFunctionOnParamStaticMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableOnParamStaticMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForCallable(() -> paramStaticMethod(Long.MAX_VALUE, "").callable());
    assertTrue(description.isPresent());
    assertNameMatch("() -> paramStaticMethod(_,_).callable()", "testCallableOnParamStaticMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerOnParamStaticMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForConsumer(s -> paramStaticMethod(Long.MAX_VALUE, "").consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> paramStaticMethod(_,_).consumer(_)", "testConsumerOnParamStaticMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionOnParamMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForFunction(s -> paramMethod(0, "").function(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> paramMethod(_,_).function(_)", "testFunctionOnParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerOnParamMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForConsumer(s -> paramMethod(Long.MAX_VALUE, "").consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("s -> paramMethod(_,_).consumer(_)", "testConsumerOnParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableOnParamMethod() {
    Optional<LambdaClassDescription> description  = getDescriptionForCallable(() -> paramMethod(Long.MAX_VALUE, "").callable());
    assertTrue(description.isPresent());
    assertNameMatch("() -> paramMethod(_,_).callable()", "testCallableOnParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testNewInstance() {
    Optional<LambdaClassDescription> description = getDescriptionForCallable(() -> new String("abc").toString());
    assertTrue(description.isPresent());
    assertNameMatch("() -> new String().toString()", "testNewInstance", CLASSNAME, description.get().toString());
  }
}
