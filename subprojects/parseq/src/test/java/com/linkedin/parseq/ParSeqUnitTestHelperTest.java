package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

public class ParSeqUnitTestHelperTest {

    @Test public void assertEquals() {
        ParSeqUnitTestHelper.assertEquals(null, null);
        ParSeqUnitTestHelper.assertEquals("x", "x");

        assertThrows(AssertionError.class, () -> ParSeqUnitTestHelper.assertEquals(null, "x"));
        assertThrows(AssertionError.class, () -> ParSeqUnitTestHelper.assertEquals("x", null));
        assertThrows(AssertionError.class, () -> ParSeqUnitTestHelper.assertEquals("x", "y"));
    }

  @Test(description = "Proves that runAndWaitForPlanToCompleteException verifies the expected exception is thrown "
    + "while also waiting for entire plan to complete")
  public void testRunAndWaitForPlanToCompleteException() throws Exception {
    ParSeqUnitTestHelper parSeqUnitTestHelper = new ParSeqUnitTestHelper();
    parSeqUnitTestHelper.setUp();

    Task<String> delayedSideEffect = parSeqUnitTestHelper.delayedValue("delayed", 1, TimeUnit.SECONDS);
    parSeqUnitTestHelper.runAndWaitForPlanToCompleteException(
      Task.par(
        Task.value("a").withSideEffect(a -> delayedSideEffect),
        parSeqUnitTestHelper.delayedFailure(new Exception(), 100, TimeUnit.MILLISECONDS)
      ),
      Exception.class
    );
    // This assertion proves that entire plan is completing
    assertTrue(delayedSideEffect.isDone());
    parSeqUnitTestHelper.tearDown();
  }
}
