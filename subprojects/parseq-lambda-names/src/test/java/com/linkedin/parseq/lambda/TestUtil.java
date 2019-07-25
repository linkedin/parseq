package com.linkedin.parseq.lambda;

import org.testng.annotations.Test;

import static com.linkedin.parseq.lambda.Util.*;
import static org.testng.Assert.*;


public class TestUtil {
  @Test
  public void testRegexForLambdaClassName() {
    String lambdaIdentifierInHotSpot = "HelloWorld$$Lambda$1";
    String lambdaIdentifierInZing = "HelloWorld$$Lambda$lambda$main$0$1310938867";

    assertTrue(isALambdaClassByName(lambdaIdentifierInHotSpot));
    assertTrue(isALambdaClassByName(lambdaIdentifierInZing));
  }
}
