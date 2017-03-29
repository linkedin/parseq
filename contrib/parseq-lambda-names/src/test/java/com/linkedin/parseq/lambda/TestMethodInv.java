package com.linkedin.parseq.lambda;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


public class TestMethodInv extends BaseTest {

  private static final String CLASSNAME = TestMethodInv.class.getSimpleName();

  @Test
  public void testFunctionInvocation() {
    Optional<String> description = getDescriptionForFunction(s -> function(s));
    assertTrue(description.isPresent());
    assertNameMatch("function(_)", "testFunctionInvocation", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableInvocation() {
    Optional<String> description = getDescriptionForCallable(() -> callable());
    assertTrue(description.isPresent());
    assertNameMatch("callable()", "testCallableInvocation", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerInvocation() {
    Optional<String> description = getDescriptionForConsumer(s -> consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("consumer(_)", "testConsumerInvocation", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionInvocationOnNew() {
    Optional<String> description = getDescriptionForFunction(s -> new TestMethodInv().function(s));
    assertTrue(description.isPresent());
    assertNameMatch("function(_)", "testFunctionInvocationOnNew", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableInvocationOnNew() {
    Optional<String> description = getDescriptionForCallable(() -> new TestMethodInv().callable());
    assertTrue(description.isPresent());
    assertNameMatch("callable()", "testCallableInvocationOnNew", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerInvocationOnNew() {
    Optional<String> description = getDescriptionForConsumer(s -> new TestMethodInv().consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("consumer(_)", "testConsumerInvocationOnNew", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionInvocationOnField() {
    Optional<String> description = getDescriptionForFunction(s -> staticField.function(s));
    assertTrue(description.isPresent());
    assertNameMatch("function(_)", "testFunctionInvocationOnField", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableInvocationOnField() {
    Optional<String> description = getDescriptionForCallable(() -> staticField.callable());
    assertTrue(description.isPresent());
    assertNameMatch("callable()", "testCallableInvocationOnField", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerInvocationOnField() {
    Optional<String> description = getDescriptionForConsumer(s -> staticField.consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("consumer(_)", "testConsumerInvocationOnField", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionInvocationWithTwoParams() {
    Optional<String> description = getDescriptionForBiFunction((s1, s2) -> functionTwo(s1, s2));
    assertTrue(description.isPresent());
    assertNameMatch("functionTwo(_,_)", "testFunctionInvocationWithTwoParams", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerInvocationWithTwoParams() {
    Optional<String> description = getDescriptionForBiConsumer((s1, s2) -> consumerTwo(s1, s2));
    assertTrue(description.isPresent());
    assertNameMatch("consumerTwo(_,_)", "testConsumerInvocationWithTwoParams", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<String> description = getDescriptionForFunction(s -> localVar.function(s));
    assertTrue(description.isPresent());
    assertNameMatch("function(_)", "testFunctionOnVar", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<String> description = getDescriptionForCallable(() -> localVar.callable());
    assertTrue(description.isPresent());
    assertNameMatch("callable()", "testCallableOnVar", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerOnVar() {
    BaseTest localVar = noParamMethod();
    Optional<String> description = getDescriptionForConsumer(s -> localVar.consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("consumer(_)", "testConsumerOnVar", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionOnNoParamMethod() {
    Optional<String> description = getDescriptionForFunction(s -> noParamMethod().function(s));
    assertTrue(description.isPresent());
    assertNameMatch("function(_)", "testFunctionOnNoParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableOnNoParamMethod() {
    Optional<String> description = getDescriptionForCallable(() -> noParamMethod().callable());
    assertTrue(description.isPresent());
    assertNameMatch("callable()", "testCallableOnNoParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerOnNoParamMethod() {
    Optional<String> description = getDescriptionForConsumer(s -> noParamMethod().consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("consumer(_)", "testConsumerOnNoParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionOnNoParamStaticMethod() {
    Optional<String> description = getDescriptionForFunction(s -> noParamStaticMethod().function(s));
    assertTrue(description.isPresent());
    assertNameMatch("function(_)", "testFunctionOnNoParamStaticMethod", CLASSNAME,
        description.get().toString());
  }

  @Test
  public void testCallableOnNoParamStaticMethod() {
    Optional<String> description = getDescriptionForCallable(() -> noParamStaticMethod().callable());
    assertTrue(description.isPresent());
    assertNameMatch("callable()", "testCallableOnNoParamStaticMethod", CLASSNAME,
        description.get().toString());
  }

  @Test
  public void testConsumerOnNoParamStaticMethod() {
    Optional<String> description = getDescriptionForConsumer(s -> noParamStaticMethod().consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("consumer(_)", "testConsumerOnNoParamStaticMethod", CLASSNAME,
        description.get().toString());
  }

  @Test
  public void testFunctionOnParamStaticMethod() {
    Optional<String> description = getDescriptionForFunction(s -> paramStaticMethod(0, "").function(s));
    assertTrue(description.isPresent());
    assertNameMatch("function(_)", "testFunctionOnParamStaticMethod", CLASSNAME,
        description.get().toString());
  }

  @Test
  public void testCallableOnParamStaticMethod() {
    Optional<String> description = getDescriptionForCallable(() ->
        paramStaticMethod(Long.MAX_VALUE, "").callable());
    assertTrue(description.isPresent());
    assertNameMatch("callable()", "testCallableOnParamStaticMethod", CLASSNAME,
        description.get().toString());
  }

  @Test
  public void testConsumerOnParamStaticMethod() {
    Optional<String> description = getDescriptionForConsumer(s -> paramStaticMethod(Long.MAX_VALUE, "").consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("consumer(_)", "testConsumerOnParamStaticMethod", CLASSNAME,
        description.get().toString());
  }

  @Test
  public void testFunctionOnParamMethod() {
    Optional<String> description = getDescriptionForFunction(s -> paramMethod(0, "").function(s));
    assertTrue(description.isPresent());
    assertNameMatch("function(_)", "testFunctionOnParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testConsumerOnParamMethod() {
    Optional<String> description = getDescriptionForConsumer(s -> paramMethod(Long.MAX_VALUE, "").consumer(s));
    assertTrue(description.isPresent());
    assertNameMatch("consumer(_)", "testConsumerOnParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testCallableOnParamMethod() {
    Optional<String> description = getDescriptionForCallable(() -> paramMethod(Long.MAX_VALUE, "").callable());
    assertTrue(description.isPresent());
    assertNameMatch("callable()", "testCallableOnParamMethod", CLASSNAME, description.get().toString());
  }

  @Test
  public void testNewInstance() {
    Optional<String> description = getDescriptionForCallable(() ->
        new String("abc")
    );
    assertTrue(description.isPresent());
    assertNameMatch("new String(_)", "testNewInstance", CLASSNAME, description.get().toString());
  }

  @Test
  public void testStringValueOf() {
    Optional<String> description = getDescriptionForCallable(() -> {
      return String.valueOf("10");
    });
    assertTrue(description.isPresent());
    assertNameMatch("String.valueOf(_)", "testStringValueOf", CLASSNAME, description.get().toString());
  }

  //TODO currently returns "lambda$28" as a method name e.g. lambda$28(TestMethodInv:228)
  //we should detect string "lambda$" in method name on a stack while we figure out method name
  @Test
  public void testNestedCallbackLambdas() throws Exception {
    Callable<Optional<String>> descriptionProvider = () -> getDescriptionForCallable(() -> "hello");
    Optional<String> description = descriptionProvider.call();
    assertTrue(description.isPresent());
    assertNameMatch("", "testNestedCallbackLambdas", CLASSNAME, description.get().toString());
  }

}
