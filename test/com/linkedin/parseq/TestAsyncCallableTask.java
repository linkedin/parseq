package com.linkedin.parseq;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.linkedin.parseq.Tasks.par;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author wfender
 * @version $Revision:$
 */
public class TestAsyncCallableTask extends BaseEngineTest
{
  protected static final String SUCCESS = "success";
  protected static final String FAIL_TIME_OUT = "Failure - Timed out";

  @Test
  public void testConcurrentTasks() throws InterruptedException
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

  @Test
  public void testTaskWithoutExecutor() throws InterruptedException
  {
    final int numCores = Runtime.getRuntime().availableProcessors();
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(numCores + 1);
    final Engine engine = new EngineBuilder()
        .setTaskExecutor(scheduler)
        .setTimerScheduler(scheduler)
        .build();

    try
    {
      final Task<Integer> task = new AsyncCallableTask<Integer>(new Callable<Integer>()
      {
        @Override
        public Integer call() throws Exception
        {
          return 1;
        }
      });
      engine.run(task);

      task.await();

      assertTrue(task.isFailed());
      assertTrue(task.getError() instanceof IllegalStateException);
    }
    finally
    {
      engine.shutdown();
      engine.awaitTermination(1, TimeUnit.SECONDS);
      scheduler.shutdownNow();
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
        return TestAsyncCallableTask.FAIL_TIME_OUT;
      }
      Thread.sleep(100);
    }
    return TestAsyncCallableTask.SUCCESS;
  }
}