package com.linkedin.parseq;


import org.testng.Assert;
import org.testng.annotations.Test;

import static com.linkedin.parseq.AsyncCallableTaskWithSLA.TaskState.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.AssertJUnit.assertEquals;


/**
 * @author emelz
 */
public class TestAsyncCallableTaskWithSLA extends BaseEngineTest
{
  private static final int SHORT_TIMEOUT_IN_MS = 200;
  private static final int LONG_TIMEOUT_IN_MS = 1000;
  private static final String DID_SOME_WORK = "Did some work: ";
  public static final String CUSTOM_TIMEOUT = "Custom timeout!";
  public static final String THROWING_AN_ERROR = "Throwing an error!";
  public static final String CUSTOM_ERROR = "Custom error!";
  public static final String A_SPECIAL_MESSAGE = "A special message";
  public static final String THROWING_ERROR_ON_TIMEOUT = "Throwing Error on timeout!";
  public static final String THROWING_ERROR_ON_ERROR = "Throwing Error on error!";

  // Do about 500ms worth of work
  private String doMyWork(String specialMessage, boolean throwError) {
    try
    {
      Thread.sleep(500);
    }
    catch (InterruptedException e)
    {
//      e.printStackTrace();
    }
    if (throwError)
    {
      throw new RuntimeException(THROWING_AN_ERROR);
    }
    return DID_SOME_WORK + specialMessage;
  }

  /**
   * Create a task that just overrides the work method.  Timeout and error throwing are configurable.
   * @param timeoutInMs
   * @param throwAnError
   * @return
   */
  private AsyncCallableTaskWithSLA<String> makeBasicTask(int timeoutInMs, final boolean throwAnError)
  {

    return new AsyncCallableTaskWithSLA<String>(timeoutInMs)
    {
      private String _specialMessage = A_SPECIAL_MESSAGE;

      @Override
      protected String doTask()
      {
        return doMyWork(_specialMessage, throwAnError);
      }
    };
  }

  /**
   * Create a task that overrides onTimeout() in addition to the work method.  Timeout and error throwing are configurable.
   * @param timeoutInMs
   * @param throwAnError
   * @return
   */
  private AsyncCallableTaskWithSLA<String> makeTaskWithCustomTimeout(int timeoutInMs, final boolean throwAnError)
  {

    return new AsyncCallableTaskWithSLA<String>(timeoutInMs)
    {
      private String _specialMessage = A_SPECIAL_MESSAGE;

      @Override
      protected String doTask()
      {
        return doMyWork(_specialMessage, throwAnError);
      }

      @Override
      protected String onTimeout()
      {
        return CUSTOM_TIMEOUT;
      }
    };
  }

  /**
   * Create a task that overrides onError() in addition to the work method.  Timeout and error throwing are configurable.
   * @param timeoutInMs
   * @param throwAnError
   * @return
   */
  private AsyncCallableTaskWithSLA<String> makeTaskWithCustomError(int timeoutInMs, final boolean throwAnError)
  {

    return new AsyncCallableTaskWithSLA<String>(timeoutInMs)
    {
      private String _specialMessage = A_SPECIAL_MESSAGE;

      @Override
      protected String doTask()
      {
        return doMyWork(_specialMessage, throwAnError);
      }

      @Override
      protected String onError()
      {
        return CUSTOM_ERROR;
      }
    };
  }

  private AsyncCallableTaskWithSLA<String> makeTaskWithErrorOnTimeout(int timeoutInMs, final boolean throwAnError)
  {

    return new AsyncCallableTaskWithSLA<String>(timeoutInMs)
    {
      private String _specialMessage = A_SPECIAL_MESSAGE;

      @Override
      protected String doTask()
      {
        return doMyWork(_specialMessage, throwAnError);
      }

      @Override
      protected String onTimeout()
      {
        throw new RuntimeException(THROWING_ERROR_ON_TIMEOUT);
      }
    };
  }

  private AsyncCallableTaskWithSLA<String> makeTaskWithErrorOnError(int timeoutInMs, final boolean throwAnError)
  {

    return new AsyncCallableTaskWithSLA<String>(timeoutInMs)
    {
      private String _specialMessage = A_SPECIAL_MESSAGE;

      @Override
      protected String doTask()
      {
        return doMyWork(_specialMessage, throwAnError);
      }

      @Override
      protected String onError()
      {
        throw new RuntimeException(THROWING_ERROR_ON_ERROR);
      }
    };
  }



  @Test
  public void testSuccess() throws InterruptedException
  {
    AsyncCallableTaskWithSLA<String> task = makeBasicTask(LONG_TIMEOUT_IN_MS, false);
    getEngine().run(task);
    task.await();
    String result = task.get();
    assertEquals(DID_SOME_WORK + A_SPECIAL_MESSAGE, result);
    assertEquals(SUCCESS, task.getTaskState());
  }

  @Test
  public void testDefaultTimeout() throws InterruptedException
  {
    AsyncCallableTaskWithSLA<String> task = makeBasicTask(SHORT_TIMEOUT_IN_MS, false);
    getEngine().run(task);
    task.await();
    String result = task.get();
    assertNull(result);
    assertEquals(TIMEDOUT, task.getTaskState());
  }

  @Test
  public void testCustomTimeout() throws InterruptedException
  {
    AsyncCallableTaskWithSLA<String> task = makeTaskWithCustomTimeout(SHORT_TIMEOUT_IN_MS, false);
    getEngine().run(task);
    task.await();
    String result = task.get();

    Assert.assertEquals(CUSTOM_TIMEOUT, result);
    Assert.assertEquals(TIMEDOUT, task.getTaskState());
  }

  @Test
  public void testDefaultError() throws InterruptedException
  {
    AsyncCallableTaskWithSLA<String> task = makeBasicTask(LONG_TIMEOUT_IN_MS, true);
    getEngine().run(task);
    task.await();
    String result = task.get();
    assertNull(result);
    assertEquals(ERROR, task.getTaskState());
    assertNotNull(task.getPrimaryError());
    assertEquals(THROWING_AN_ERROR, task.getPrimaryError().getMessage());
  }

  @Test
  public void testCustomError() throws InterruptedException
  {
    AsyncCallableTaskWithSLA<String> task = makeTaskWithCustomError(LONG_TIMEOUT_IN_MS, true);
    getEngine().run(task);
    task.await();
    String result = task.get();
    assertEquals(CUSTOM_ERROR, result);
    assertEquals(ERROR, task.getTaskState());
    assertNotNull(task.getPrimaryError());
    assertEquals(THROWING_AN_ERROR, task.getPrimaryError().getMessage());
  }

  @Test
  public void testErrorOnTimeout()
      throws InterruptedException
  {
    AsyncCallableTaskWithSLA<String> task = makeTaskWithErrorOnTimeout(SHORT_TIMEOUT_IN_MS, true);
    getEngine().run(task);
    task.await();
    String result = task.get();
    assertNull(result);
    assertEquals(ERROR_ON_TIMEOUT, task.getTaskState());
    assertNull(task.getPrimaryError());
    assertNotNull(task.getSecondaryError());
    assertEquals(THROWING_ERROR_ON_TIMEOUT, task.getSecondaryError().getMessage());
  }

  @Test
  public void testErrorOnError()
      throws InterruptedException
  {
    AsyncCallableTaskWithSLA<String> task = makeTaskWithErrorOnError(LONG_TIMEOUT_IN_MS, true);
    getEngine().run(task);
    task.await();
    String result = task.get();
    assertNull(result);
    assertEquals(ERROR_ON_ERROR, task.getTaskState());
    assertNotNull(task.getPrimaryError());
    assertEquals(THROWING_AN_ERROR, task.getPrimaryError().getMessage());
    assertNotNull(task.getSecondaryError());
    Assert.assertEquals(THROWING_ERROR_ON_ERROR, task.getSecondaryError().getMessage());
  }

}
