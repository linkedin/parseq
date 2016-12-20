package com.linkedin.parseq;

import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.retry.RetryPolicy;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


/**
 * @author Zhenkai Zhu
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestTask extends AbstractTaskTest {

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
    });  }
}
