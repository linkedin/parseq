package com.linkedin.parseq;

import com.linkedin.parseq.promise.Promises;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


/**
 * @author Zhenkai Zhu
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestTask extends AbstractTaskTest {

  private boolean _crossThreadStackTracesEnabled;

  @BeforeClass
  public void start() {
    _crossThreadStackTracesEnabled = ParSeqGlobalConfiguration.getInstance().isCrossThreadStackTracesEnabled();
    ParSeqGlobalConfiguration.getInstance().setCrossThreadStackTracesEnabled(true);
  }

  @AfterClass
  public void stop() {
    ParSeqGlobalConfiguration.getInstance().setCrossThreadStackTracesEnabled(_crossThreadStackTracesEnabled);
  }

  @Test
  public void testMap() {
    testMap(3);
  }

  @Test
  public void testFlatMap() {
    testFlatMap(5);
  }

  @Test
  public void testAndThenConsumer() {
    testAndThenConsumer(3);
  }

  @Test
  public void testAndThenTask() {
    testAndThenTask(3);
  }

  @Test
  public void testRecover() {
    testRecover(5);
  }

  @Test
  public void testCancelledRecover() {
    testCancelledRecover(5);
  }

  @Test
  public void testNoRecover() {
    testNoRecover(3);
  }

  @Test
  public void testTry() {
    testToTry(5);
  }

  @Test
  public void testRecoverWithSuccess() {
    testRecoverWithSuccess(3);
  }

  @Test
  public void testRecoverWithFailure() {
    testRecoverWithFailure(4);
  }

  @Test
  public void testRecoverWithCancelled() {
    testRecoverWithCancelled(3);
  }

  @Test
  public void testRecoverWithRecoverd() {
    testRecoverWithRecoverd(4);
  }

  @Test
  public void testWithSideEffectFullCompletion() throws Exception {
    testWithSideEffectFullCompletion(4);
  }

  @Test
  public void testWithSideEffectFailure() throws Exception {
    testWithSideEffectFailure(3);
  }

  @Test
  public void testOnFailure() {
    testOnFailure(3);
  }

  @Test
  public void testOnFailureWhenCancelled() {
    testOnFailureWhenCancelled(3);
  }

  @Test
  public void testStackFrames() {
    Task<String> successTask = getSuccessTask();
    try {
      runAndWait("TestTask.testStackFrames", successTask.flatMap("nested", x -> getFailureTask()));
      fail("should have failed");
    } catch (Exception ex) {
      String stackTrace = Arrays.toString(ex.getCause().getStackTrace());
      assertFalse(stackTrace.contains("BaseTask"));
      assertTrue(stackTrace.contains("\"failure\""));
      assertTrue(stackTrace.contains("\"nested\""));
    }
  }

  @Override
  Task<String> getSuccessTask() {
    return Task.async("success", () -> Promises.value(TASK_VALUE));
  }

  @Override
  Task<String> getFailureTask() {
    return Task.async("failure", () -> {
      throw new RuntimeException(TASK_ERROR_MESSAGE);
    });
  }

  @Override
  Task<String> getCancelledTask() {
    return Task.async("cancelled", () -> {
      throw new CancellationException(new TimeoutException());
    });
  }
}
