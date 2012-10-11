package com.linkedin.parseq;

import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static com.linkedin.parseq.Tasks.par;
import static org.testng.AssertJUnit.assertEquals;

/**
 * @author wfender
 * @version $Revision:$
 */
public class TestCallableTask extends BaseEngineTest
{
  protected static final String SUCCESS = "success";
  protected static final String FAIL_TIME_OUT = "Failure - Timed out";

  @Test
  public void testConcurrentCallableTasks() throws InterruptedException
  {
    final int size = 2; // Should be <= CallableWrapperTask size

    final List<AsyncCallableTask<String>> tasks = new ArrayList<AsyncCallableTask<String>>(size);
    for (int counter = 0; counter < size; counter++)
    {
      tasks.add(counter, new AsyncCallableTask<String>(new ConcurrentCallable(size)));
    }

    final ParTask<?> par = par(tasks);
    getEngine().run(par);

    par.await();

    assertEquals(2, par.getSuccessful().size());
    assertEquals(2, par.getTasks().size());
    assertEquals(2, par.get().size());
    for (int counter = 0; counter < size; counter++)
    {
      assertEquals(SUCCESS, tasks.get(counter).get());
    }
  }

}

class ConcurrentCallable implements Callable<String>
{
  private static final long TEN_SECONDS = 10 * 1000;
  private static AtomicInteger _counter = new AtomicInteger(0);

  private Integer _gate;

  protected ConcurrentCallable(int gate)
  {
    _gate = gate;
  }
  @Override
  public String call() throws Exception
  {
    _counter.incrementAndGet();
    long end = System.currentTimeMillis() + TEN_SECONDS; // Prevent the test for running more than 10 seconds.
    while (_counter.get() < _gate)
    {
      if (end < System.currentTimeMillis())
      {
        return TestCallableTask.FAIL_TIME_OUT;
      }
      Thread.sleep(100);
    }
    return TestCallableTask.SUCCESS;
  }
}