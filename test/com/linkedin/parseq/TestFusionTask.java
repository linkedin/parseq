package com.linkedin.parseq;

import org.testng.annotations.Test;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestFusionTask extends AbstractTaskTest {

  @Test
  public void testMap() {
    testMap(1);
  }

  @Test
  public void testFlatMap() {
    testFlatMap(3);
  }

  @Test
  public void testAndThenConsumer() {
    testAndThenConsumer(1);
  }

  @Test
  public void testAndThenTask() {
    testAndThenTask(3);
  }

  @Test
  public void testRecover() {
    testRecover(1);
  }

  @Test
  public void testNoRecover() {
    testNoRecover(1);
  }

  @Test
  public void testTry() {
    testTry(1);
  }

  @Test
  public void testRecoverWithSuccess() {
    testRecoverWithSuccess(2);
  }

  @Test
  public void testRecoverWithFailure() {
    testRecoverWithFailure(3);
  }

  @Test
  public void testRecoverWithRecoverd() {
    testRecoverWithRecoverd(3);
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
    testOnFailure(1);
  }

  @Override
  Task<String> getSuccessTask() {
    return Task.value("success", TASK_VALUE);
  }

  @Override
  Task<String> getFailureTask() {
    return Task.failure("failure", new RuntimeException(TASK_ERROR_MESSAGE));
  }

}
