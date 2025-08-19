package com.linkedin.parseq;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.testng.annotations.Test;

/**
 * test {@link Exceptions}
 *
 * @author wiilei
 */
public class TestExceptions {
    @Test
    public void testIsMultiException() {
        assertTrue(Exceptions.isMultiple(new MultiException(Collections.emptyList())));
        assertFalse(Exceptions.isMultiple(new Exception()));
        assertFalse(Exceptions.isMultiple(null));
    }

    @Test
    public void testAllMultiCauses_withNull() {
        Collection<? extends Throwable> realCauses = Exceptions.allMultiCauses(null);

        assertNotNull(realCauses, "Real causes is null");
        assertEquals(realCauses.size(), 0, "The size of real causes is not correct");
    }

    /**
     * test single layer multi exception, ME1[E1, E2]
     */
    @Test
    public void testAllMultiCauses_singleLayerMultiExceptionNonEmpty() {
        Exception e1 = new Exception("E1");
        Exception e2 = new Exception("E2");
        MultiException me1 = new MultiException(Arrays.asList(e1, e2));

        Collection<? extends Throwable> realCauses = Exceptions.allMultiCauses(me1);

        assertEquals(realCauses.size(), 2, "The size of real causes is not correct");
        assertTrue(realCauses.contains(e1), "Real causes does not contain Exception 1");
        assertTrue(realCauses.contains(e2), "Real causes does not contain Exception 2");
    }

    /**
     * test single layer multi exception, ME1[]
     */
    @Test
    public void testAllMultiCauses_singleLayerMultiExceptionEmpty() {
        MultiException me1 = new MultiException(Collections.emptyList());
        Collection<? extends Throwable> realCauses = Exceptions.allMultiCauses(me1);
        assertEquals(realCauses.size(), 0, "The size of real causes is not correct");
    }

    /**
     * test nested head multi exception, ME1[ ME2[E1, E2], E3 ]
     */
    @Test
    public void testAllMultiCauses_nestedHeadMultiException() {
        Exception e1 = new Exception("Exception 1");
        Exception e2 = new Exception("Exception 2");
        Exception e3 = new Exception("Exception 3");
        MultiException me2 = new MultiException(Arrays.asList(e1, e2));
        MultiException me1 = new MultiException(Arrays.asList(me2, e3));

        Collection<? extends Throwable> realCauses = Exceptions.allMultiCauses(me1);

        assertEquals(realCauses.size(), 3, "The size of real causes is not correct");
        assertTrue(realCauses.contains(e1), "Real causes does not contain Exception 1");
        assertTrue(realCauses.contains(e2), "Real causes does not contain Exception 2");
        assertTrue(realCauses.contains(e3), "Real causes does not contain Exception 3");
    }

    /**
     * test nested single multi exception with empty, ME1[ ME2[] ]
     */
    @Test
    public void testAllMultiCauses_nestedSingleMultiExceptionWithEmpty() {
        MultiException me2 = new MultiException(Collections.emptyList());
        MultiException me1 = new MultiException(Collections.singletonList(me2));

        Collection<? extends Throwable> realCauses = Exceptions.allMultiCauses(me1);

        assertEquals(realCauses.size(), 0, "The size of real causes is not correct");
    }

    /**
     * test nested single multi exception with tail non-empty, ME1[ ME2[E1] ]
     */
    @Test
    public void testAllMultiCauses_nestedSingleMultiExceptionWithTailNonEmpty() {
        Exception e1 = new Exception("Exception 1");
        MultiException me2 = new MultiException(Collections.singletonList(e1));
        MultiException me1 = new MultiException(Collections.singletonList(me2));

        Collection<? extends Throwable> realCauses = Exceptions.allMultiCauses(me1);

        assertEquals(realCauses.size(), 1, "The size of real causes is not correct");
        assertTrue(realCauses.contains(e1), "Real causes does not contain Exception 1");
    }

    /**
     * test nested multi exception with tail non-empty, ME1[ ME2[E1], E1, ME3[E2] ]
     */
    @Test
    public void testAllMultiCauses_nestedMultiExceptionWithTailNonEmpty() {
        Exception e1 = new Exception("Exception 1");
        Exception e2 = new Exception("Exception 2");

        MultiException me2 = new MultiException(Collections.emptyList());
        MultiException me3 = new MultiException(Collections.singletonList(e2));
        MultiException me1 = new MultiException(Arrays.asList(me2, e1, me3));

        Collection<? extends Throwable> realCauses = Exceptions.allMultiCauses(me1);

        assertEquals(realCauses.size(), 2, "The size of real causes is not correct");
        assertTrue(realCauses.contains(e1), "Real causes does not contain Exception 1");
        assertTrue(realCauses.contains(e2), "Real causes does not contain Exception 2");
    }

    /**
     * test takes the first non-MultiException cause of the given exception, ME1[ ME2[E1], E1, ME3[E2] ]
     */
    @Test
    public void testAnyMultiCause_nestedMultiExceptionWithTailNonEmpty() {
        Exception e1 = new Exception("Exception 1");
        Exception e2 = new Exception("Exception 2");

        MultiException me2 = new MultiException(Collections.emptyList());
        MultiException me3 = new MultiException(Collections.singletonList(e2));
        MultiException me1 = new MultiException(Arrays.asList(me2, e1, me3));

        Throwable anyCause = Exceptions.anyMultiCause(me1);
        assertEquals(anyCause, e1, "The first non-MultiException cause is not correct");
    }

    /**
     * test takes the first non-MultiException cause of the given exception, ME1[ ME2[], E1, ME3[E2] ]
     */
    @Test
    public void testAnyMultiCause_nestedSingleMultiExceptionWithTailNonEmpty() {
        Exception e1 = new Exception("Exception 1");
        Exception e2 = new Exception("Exception 1");
        MultiException me2 = new MultiException(Collections.emptyList());
        MultiException me3 = new MultiException(Collections.singletonList(e2));
        MultiException me1 = new MultiException(Arrays.asList(me2, e1, me3));

        Throwable anyCause = Exceptions.anyMultiCause(me1);
        assertEquals(anyCause, e1, "The first non-MultiException cause is not correct");
    }

    @Test
    public void testAnyMultiCause_singleLayerMultiExceptionNonEmpty() {
        Throwable anyCause = Exceptions.anyMultiCause(null);
        assertNull(anyCause, "The first non-MultiException cause is not correct");

        Exception e1 = new Exception("Exception 1");
        anyCause = Exceptions.anyMultiCause(e1);
        assertEquals(anyCause, e1, "The first non-MultiException cause is not correct");
    }
}
