package com.linkedin.parseq.lambda;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertTrue;

class BaseTest {

  static String staticFunction(String s) {
    return s;
  }

  static String staticCallable() {
    return "";
  }

  static void staticConsumer(String s) {
  }

  String function(String s) {
    return s;
  }

  String callable() {
    return "";
  }

  void consumer(String s) {
  }

  static BaseTest staticField = new TestMethodInv();

  BaseTest noParamMethod() {
    return new BaseTest();
  }

  static BaseTest noParamStaticMethod() {
    return staticField;
  }

  BaseTest paramMethod(long x, String y) {
    return this;
  }

  static BaseTest paramStaticMethod(long x, String y) {
    return staticField;
  }

  String functionTwo(String s1, String s2) {
    return s1 + s2;
  }

  void consumerTwo(String s1, String s2) {
  }

  ASMBasedTaskDescriptor _asmBasedTaskDescriptor = new ASMBasedTaskDescriptor();

  Optional<String> getDescriptionForFunction(Function<String, String> f) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(f.getClass().getName());
  }

  Optional<String> getDescriptionForBiFunction(BiFunction<String, String, String> f) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(f.getClass().getName());
  }

  Optional<String> getDescriptionForCallable(Callable<String> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  Optional<String> getDescriptionForCallableInteger(Callable<Integer> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  Optional<String> getDescriptionForConsumer(Consumer<String> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  Optional<String> getDescriptionForBiConsumer(BiConsumer<String, String> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  void assertNameMatch(String inferredFunction, String callerMethodName, String callerClassName,
                                  String lambdaClassDescription) {
    if (inferredFunction.isEmpty()) {
      Pattern p = Pattern.compile(callerMethodName + "\\(" + callerClassName + ":\\d+\\)");
      Matcher m = p.matcher(lambdaClassDescription);
      assertTrue(m.matches());
    } else {
      Pattern p = Pattern.compile(Pattern.quote(inferredFunction) + " "
          + callerMethodName + "\\(" + callerClassName+ ":\\d+\\)");
      Matcher m = p.matcher(lambdaClassDescription);
      assertTrue(m.matches());
    }
  }
}
