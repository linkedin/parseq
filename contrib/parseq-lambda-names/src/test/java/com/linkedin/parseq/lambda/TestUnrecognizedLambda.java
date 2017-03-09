package com.linkedin.parseq.lambda;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


public class TestUnrecognizedLambda extends BaseTest {

  private static final String CLASSNAME = TestUnrecognizedLambda.class.getSimpleName();

  Optional<String> getDescriptionForVoidCallable(Callable<Void> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  Optional<String> getDescriptionForIntCallable(Callable<Integer> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  Optional<String> getDescriptionForStringPredicate(Predicate<String> c) {
    return _asmBasedTaskDescriptor.getLambdaClassDescription(c.getClass().getName());
  }

  @Test
  public void testReturnExpression() {
    Optional<String> description = getDescriptionForCallable(() -> {return "";});
    assertTrue(description.isPresent());
    assertNameMatch("", "testReturnExpression", CLASSNAME, description.get().toString());
  }

  @Test
  public void testExpressions() {
    Optional<String> description = getDescriptionForVoidCallable(() -> {
      int a = 5;
      int b = 10;
      int c = a + b;
      return null;
    });
    assertTrue(description.isPresent());
    assertNameMatch("", "testExpressions", CLASSNAME, description.get().toString());
  }

  @Test
  public void testOperations() {
    MathOperation multiplication = (int a, int b) -> { return a * b; };
    Optional<String> description = getDescriptionForIntCallable(() -> {return this.operate(5, 3, multiplication);});;
    assertTrue(description.isPresent());
    //if operate function's return type is changed to Integer, we aren't able to infer operation
    assertNameMatch("operate(_,_,_).Integer.valueOf(_)", "testOperations", CLASSNAME, description.get().toString());
  }

  @Test
  public void testStream() {
//    example of how streams are used with lambda
//    List<String> list = Arrays.asList("a1", "a2", "b1", "c2", "c1");
//    list.stream()
//        .filter(p -> p.startsWith("c"))
//        .map(s -> s.toUpperCase())
//        .sorted()
//        .forEach(System.out::println);

    Optional<String> predicateDescription = getDescriptionForStringPredicate(p -> p.startsWith("c"));
    assertTrue(predicateDescription.isPresent());
    assertNameMatch("startsWith(_)", "testStream", CLASSNAME, predicateDescription.get().toString());

    Optional<String> mapDescription = getDescriptionForFunction(s -> s.toUpperCase());
    assertTrue(mapDescription.isPresent());
    assertNameMatch("toUpperCase()", "testStream", CLASSNAME, mapDescription.get().toString());

    Optional<String> foreachDescription = getDescriptionForConsumer(System.out::println);
    assertTrue(foreachDescription.isPresent());
    assertNameMatch("out::println", "testStream", CLASSNAME, foreachDescription.get().toString());
  }

  interface MathOperation {
    int operation(int a, int b);
  }

  private int operate(int a, int b, MathOperation mathOperation){
    return mathOperation.operation(a, b);
  }
}
