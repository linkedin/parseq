package com.linkedin.parseq;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestFusionTask extends AbstractTaskTest {

  @Test
  public void testMap() {
    testMap(2);
  }

  @Test
  public void testFlatMap() {
    testFlatMap(4);
  }

  @Test
  public void testAndThenConsumer() {
    testAndThenConsumer(2);
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
    testNoRecover(2);
  }

  @Test
  public void testTry() {
    testToTry(3);
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
  public void testWithSideEffectFullCompletion() throws Exception {
    testWithSideEffectFullCompletion(4);
  }

  @Test
  public void testWithSideEffectFailure() throws Exception {
    testWithSideEffectFailure(3);
  }

  @Test
  public void testOnFailure() {
    testOnFailure(2);
  }

  @Test
  public void testUsedTwice() {
    //TODO
    Task<String> source = getSuccessTask().andThen(System.out::println);
    Task<?> task = Task.par(source.map("map1", s -> s + "-1"), source.map("map2", s -> s + "-2"));

    runAndWait("TestFusionTask.testUsedTwice", task);

  }

  @Test
  public void testWithTimeoutAsLastOperation() {
    Task<String> task = delayedValue("value", 110, TimeUnit.MILLISECONDS).map(x -> x + 1).map(x -> TASK_VALUE)
        .withTimeout(5, TimeUnit.MILLISECONDS);

    try {
      runAndWait("TestFusionTask.testWithTimeoutAsLastOperation", task);
      fail("should have failed!");
    } catch (Exception ex) {
      assertSame(ex.getCause(), Exceptions.TIMEOUT_EXCEPTION);
    }
    assertEquals(countTasks(task.getTrace()), 5);
  }

  @Test
  public void testWithTimeoutAsMiddleOperation() {
    Task<String> task = delayedValue("value", 110, TimeUnit.MILLISECONDS).map("first", x -> x + 3)
        .withTimeout(5, TimeUnit.MILLISECONDS).map("second", x -> TASK_VALUE);

    try {
      runAndWait("TestFusionTask.testWithTimeoutAsMiddleOperation", task);
      fail("should have failed!");
    } catch (Exception ex) {
      assertSame(ex.getCause(), Exceptions.TIMEOUT_EXCEPTION);
    }
    assertEquals(countTasks(task.getTrace()), 7);
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
