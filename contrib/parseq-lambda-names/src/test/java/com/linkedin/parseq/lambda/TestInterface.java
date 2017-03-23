package com.linkedin.parseq.lambda;

import java.util.Optional;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


public class TestInterface extends BaseTest {

  private static final String CLASSNAME = TestInterface.class.getSimpleName();

  public interface SampleInterface {

    default String defaultFunction(String s) {
      return s;
    }

    static String staticFunction(String s) {
      return s;
    }

    String interfaceFunction(String s);
  }

  public abstract class SampleAbstract {

    abstract String abstractFunction(String s);

  }

  public class SampleImplementation extends SampleAbstract implements SampleInterface {

    @Override
    String abstractFunction(String s) {
      return s;
    }

    @Override
    public String interfaceFunction(String s) {
      return s;
    }
  }

  @Test
  public void testFunctionReferenceOnInterface() {
    SampleImplementation impl = new SampleImplementation();
    Optional<String> description = getDescriptionForFunction(impl::interfaceFunction);
    assertTrue(description.isPresent());
    assertNameMatch("interfaceFunction", "testFunctionReferenceOnInterface", CLASSNAME, description.get().toString());
  }

  @Test
  public void testStaticFunctionReferenceOnInterface() {
    Optional<String> description = getDescriptionForFunction(SampleInterface::staticFunction);
    assertTrue(description.isPresent());
    assertNameMatch("TestInterface$SampleInterface::staticFunction", "testStaticFunctionReferenceOnInterface", CLASSNAME, description.get().toString());
  }

  @Test
  public void testAbstractFunctionReferenceOnInterface() {
    SampleImplementation impl = new SampleImplementation();
    Optional<String> description = getDescriptionForFunction(impl::abstractFunction);
    assertTrue(description.isPresent());
    assertNameMatch("abstractFunction", "testAbstractFunctionReferenceOnInterface", CLASSNAME, description.get().toString());
  }

  @Test
  public void testFunctionInvocationOnInterface() {
    SampleImplementation impl = new SampleImplementation();
    Optional<String> description = getDescriptionForFunction(s -> impl.interfaceFunction(s));
    assertTrue(description.isPresent());
    assertNameMatch("interfaceFunction(_)", "testFunctionInvocationOnInterface", CLASSNAME, description.get().toString());
  }

  @Test
  public void testStaticFunctionInvocationOnInterface() {
    Optional<String> description = getDescriptionForFunction(s -> SampleInterface.staticFunction(s));
    assertTrue(description.isPresent());
    assertNameMatch("TestInterface$SampleInterface.staticFunction(_)", "testStaticFunctionInvocationOnInterface", CLASSNAME, description.get().toString());
  }

  @Test
  public void testAbstractFunctionInvocationOnInterface() {
    SampleImplementation impl = new SampleImplementation();
    Optional<String> description = getDescriptionForFunction(s -> impl.abstractFunction(s));
    assertTrue(description.isPresent());
    assertNameMatch("abstractFunction(_)", "testAbstractFunctionInvocationOnInterface", CLASSNAME,
        description.get().toString());
  }
}
