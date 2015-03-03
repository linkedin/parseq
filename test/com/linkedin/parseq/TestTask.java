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
    testAndThenTask(3);
  }

  @Test
  public void testRecover() {
    testRecover(3);
  }

  @Test
  public void testNoRecover() {
    testNoRecover(3);
  }

  @Test
  public void testTry() {
    testTry(3);
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
  public void testWithTimeout() {
    testWithTimeout(3);
  }

  @Test
  public void testWithSideEffectPartial() {
    testWithSideEffectPartial(4);
  }

  @Test
  public void testWithSideEffectFullCompletion() throws Exception {
    testWithSideEffectFullCompletion(4);
  }

  @Test
  public void testWithSideEffectFailure() throws Exception {
    testWithSideEffectFailure(2);
  }

  @Test
  public void testOnFailure() {
    testOnFailure(3);
  }

  @Override
  Task<String> getSuccessTask() {
    return Task.async("success", () -> Promises.value(TASK_VALUE), true);
  }

  @Override
  Task<String> getFailureTask() {
    return Task.async("failure", () -> {
      throw new RuntimeException(TASK_ERROR_MESSAGE);
    }, true);
  }
}
