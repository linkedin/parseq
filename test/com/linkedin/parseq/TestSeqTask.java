package com.linkedin.parseq;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import static com.linkedin.parseq.Tasks.callable;
import static com.linkedin.parseq.Tasks.seq;
import static com.linkedin.parseq.TestUtil.value;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

/**
 * @author Chris Pettitt
 */
public class TestSeqTask extends BaseEngineTest
{
  @Test
  public void testIterableSeqWithEmptyList()
  {
    try
    {
      seq(Collections.<Task<?>>emptyList());
      fail("Should have thrown IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }

  @Test
  public void testIterableSeqWithSingletonList() throws InterruptedException
  {
    final String valueStr = "value";
    final Task<String> task = value("value", valueStr);
    final Task<List<String>> seq = seq(Collections.singleton(task));

    getEngine().run(seq);

    seq.await();

    assertEquals(valueStr, seq.get());
  }

  @Test
  public void testIterableSeqWithMultipleElements() throws InterruptedException
  {
    final int iters = 500;

    final Task<?>[] tasks = new Task<?>[iters];
    for (int i = 0; i < iters; i++)
    {
      final int finalI = i;
      tasks[i] = callable("task-" + i, new Callable<Integer>()
      {
        @Override
        public Integer call() throws Exception
        {
          if (finalI == 0)
          {
            return 1;
          }
          else
          {
            final int prevValue = (Integer) tasks[finalI - 1].get();
            if (prevValue != finalI)
            {
              throw new IllegalStateException("Expected: " + finalI + ". Got: " + prevValue);
            }
            return prevValue + 1;
          }
        }
      });
    }

    final Task<Integer> seq = seq(Arrays.asList(tasks));
    getEngine().run(seq);

    seq.await();

    assertEquals(iters, (int)seq.get());
  }

  // For following testSeqX() tests, we verify that we can use different types
  // for the last element and the rest of the list.

  @Test
  public void testSeq2() throws InterruptedException
  {
    final Task<String> seq = seq(value(1),
        value("result"));
    getEngine().run(seq);
    seq.await();
    assertEquals("result", seq.get());
  }

  @Test
  public void testSeq3() throws InterruptedException
  {
    final Task<String> seq = seq(value(1),
        value(2),
        value("result"));
    getEngine().run(seq);
    seq.await();
    assertEquals("result", seq.get());
  }

  @Test
  public void testSeq4() throws InterruptedException
  {
    final Task<String> seq = seq(value(1),
        value(2),
        value(3),
        value("result"));
    getEngine().run(seq);
    seq.await();
    assertEquals("result", seq.get());
  }

  @Test
  public void testSeq5() throws InterruptedException
  {
    final Task<String> seq = seq(value(1),
        value(2),
        value(3),
        value(4),
        value("result"));
    getEngine().run(seq);
    seq.await();
    assertEquals("result", seq.get());
  }

  @Test
  public void testSeq6() throws InterruptedException
  {
    final Task<String> seq = seq(value(1),
        value(2),
        value(3),
        value(4),
        value(5),
        value("result"));
    getEngine().run(seq);
    seq.await();
    assertEquals("result", seq.get());
  }

  @Test
  public void testSeq7() throws InterruptedException
  {
    final Task<String> seq = seq(value(1),
        value(2),
        value(3),
        value(4),
        value(5),
        value(6),
        value("result"));
    getEngine().run(seq);
    seq.await();
    assertEquals("result", seq.get());
  }

  @Test
  public void testSeq8() throws InterruptedException
  {
    final Task<String> seq = seq(value(1),
        value(2),
        value(3),
        value(4),
        value(5),
        value(6),
        value(7),
        value("result"));
    getEngine().run(seq);
    seq.await();
    assertEquals("result", seq.get());
  }

  @Test
  public void testSeq9() throws InterruptedException
  {
    final Task<String> seq = seq(value(1),
        value(2),
        value(3),
        value(4),
        value(5),
        value(6),
        value(7),
        value(8),
        value("result"));
    getEngine().run(seq);
    seq.await();
    assertEquals("result", seq.get());
  }

  @Test
  public void testSeq10() throws InterruptedException
  {
    final Task<String> seq = seq(value(1),
        value(2),
        value(3),
        value(4),
        value(5),
        value(6),
        value(7),
        value(9),
        value("result"));
    getEngine().run(seq);
    seq.await();
    assertEquals("result", seq.get());
  }
}
