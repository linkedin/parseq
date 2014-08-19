package com.linkedin.parseq;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.linkedin.parseq.Tasks.seqActions;
import static com.linkedin.parseq.TestUtil.noop;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

/**
 * @author Chris Pettitt
 */
public class TestSeqActionsTask extends BaseEngineTest
{
  @Test
  public void testWithEmptyList()
  {
    try
    {
      seqActions();
      fail("Should have thrown IllegalArgumentException");
    }
    catch (IllegalArgumentException e)
    {
      // Expected case
    }
  }

  @Test
  public void testWithSequenceOfActions() throws InterruptedException
  {
    final int numTasks = 10;
    final AtomicInteger counter = new AtomicInteger();
    final List<Task<?>> tasks = new ArrayList<Task<?>>();
    for (int i = 0; i < numTasks; ++i)
    {
      final int finalI = i;
      tasks.add(Tasks.action("action-" + i, new Runnable()
      {
        @Override
        public void run()
        {
          final int count = counter.getAndIncrement();
          assertEquals("Tasks executed out of order!", count, finalI);
        }
      }));
    }

    final Task<Void> seq = Tasks.seqActions(tasks);
    getEngine().run(seq);

    seq.await();

    assertNull(seq.get());
    assertEquals("Task count does not match counter", counter.get(), numTasks);
  }

  @Test
  public void testWithFailureMidSequence() throws InterruptedException
  {
    final RuntimeException exception = new RuntimeException();
    final Task<Void> throwingTask = Tasks.action("throwing task", new Runnable()
    {
      @Override
      public void run()
      {
        throw exception;
      }
    });

    final Task<?> lastTask = noop();
    final Task<Void> seq = Tasks.seqActions(noop(), throwingTask, lastTask);
    getEngine().run(seq);

    seq.await();

    assertTrue(seq.isFailed());
    assertEquals(exception, seq.getError());
    assertTrue(lastTask.isFailed());
    assertTrue(lastTask.getError() instanceof EarlyFinishException);
  }
}
