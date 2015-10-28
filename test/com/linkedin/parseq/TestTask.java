package com.linkedin.parseq;

import org.testng.annotations.Test;

import com.linkedin.parseq.promise.Promises;


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
    testAndThenTask(5);
  }

  @Test
  public void testAndThenTaskWithFailure() {
    testAndThenTaskWithFailure(4);
  }

  @Test
  public void testRecover() {
    testRecover(4);
  }

  @Test
  public void testNoRecover() {
    testNoRecover(3);
  }

  @Test
  public void testTry() {
    testToTry(4);
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
  public void testWithTimeoutFailure() {
    testWithTimeoutFailure(7);
  }

  @Test
  public void testWithTimeoutSuccess() {
    testWithTimeoutSuccess(7);
  }

  @Test
  public void testWithTimeoutTwiceFailure() {
    testWithTimeoutTwiceFailure(9);
  }

  @Test
  public void testWithTimeoutTwiceSuccess() {
    testWithTimeoutTwiceSuccess(9);
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
}
