package com.linkedin.parseq;

import org.testng.annotations.Test;

import static org.testng.Assert.assertThrows;

public class ParSeqUnitTestHelperTest {

    @Test public void assertEquals() {
        ParSeqUnitTestHelper.assertEquals(null, null);
        ParSeqUnitTestHelper.assertEquals("x", "x");

        assertThrows(AssertionError.class, () -> ParSeqUnitTestHelper.assertEquals(null, "x"));
        assertThrows(AssertionError.class, () -> ParSeqUnitTestHelper.assertEquals("x", null));
        assertThrows(AssertionError.class, () -> ParSeqUnitTestHelper.assertEquals("x", "y"));
    }
}
