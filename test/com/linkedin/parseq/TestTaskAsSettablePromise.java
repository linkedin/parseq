package com.linkedin.parseq;

import com.linkedin.parseq.promise.AbstractSettablePromiseTest;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestTaskAsSettablePromise extends AbstractSettablePromiseTest
{
  ScheduledExecutorService _scheduler;
  Engine _engine;

  @BeforeMethod
  @Override
  public void setUp() throws Exception
  {
    super.setUp();

    _scheduler = Executors.newScheduledThreadPool(5);
    EngineBuilder engineBuilder = new EngineBuilder()
        .setTaskExecutor(_scheduler)
        .setTimerScheduler(_scheduler);
    _engine = engineBuilder.build();
  }

  @AfterMethod
  @Override
  public void tearDown() throws Exception
  {
    _engine.shutdown();
    _engine.awaitTermination(200, TimeUnit.MILLISECONDS);
    _engine = null;
    _scheduler.shutdownNow();
    _scheduler = null;

    super.tearDown();
  }

  @Override
  protected <T> Promise<T> createPromise()
  {
    return new TaskAndPromise<T>();
  }

  @Override
  protected <T> void setPromiseValue(Promise<T> promise, T value)
  {
    final TaskAndPromise<T> task = (TaskAndPromise<T>)promise;
    _engine.run(task);
    task._promise.done(value);

    try
    {
      task.await(5, TimeUnit.SECONDS);
    }
    catch (InterruptedException e)
    {
      // Do nothing
    }
  }

  @Override
  protected <T> void setPromiseError(Promise<T> promise, Throwable error)
  {
    final TaskAndPromise<T> task = (TaskAndPromise<T>)promise;
    _engine.run(task);
    task._promise.fail(error);

    try
    {
      task.await(5, TimeUnit.SECONDS);
    }
    catch (InterruptedException e)
    {
      // Do nothing
    }
  }

  private static class TaskAndPromise<T> extends BaseTask<T>
  {
    private final SettablePromise<T> _promise = Promises.settable();

    @Override
    protected Promise<? extends T> run(Context context) throws Throwable
    {
      return _promise;
    }
  }
}
