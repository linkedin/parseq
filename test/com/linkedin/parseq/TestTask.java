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

  @Test
  public void testFusionTaskType() {
    Task<Integer> task = getSuccessTask().map(String::length);
    runAndWait("fusionTaskType", task);
    assertEquals(task.getShallowTrace().getTaskType(), TaskType.FUSION.getName());
  }

  @Test
  public void testBlockingTaskType() {
    TestingExecutorService es = new TestingExecutorService(Executors.newSingleThreadExecutor());
    try {
      Task<String> task = Task.blocking(() -> "blocking task", es);
      runAndWait("blockingTaskType", task);
      assertEquals(task.getShallowTrace().getTaskType(), TaskType.BLOCKING.getName());
    } finally {
      es.shutdown();
    }
  }

  @Test
  public void testShareableTaskType() {
    Task<Integer> value = Task.value(10);
    Task<Integer> shareableTask = value.shareable();
    assertEquals(shareableTask.getShallowTrace().getTaskType(), TaskType.SHAREABLE.getName());
  }

  @Test
  public void testFlatMapTaskType() {
    Task<String> task = Task.value("Welcome");
    Task<String> flatMap = task.flatMap("+earth", s -> Task.callable(() -> s + " on earth!"));
    runAndWait("flatMapTaskType", flatMap);
    assertEquals(flatMap.getShallowTrace().getTaskType(), TaskType.FLATTEN.getName());
  }

  @Test
  public void testWithTimeoutTaskType() {
    Task<?> taskWithTimeout = Task.value("test").withTimeout(50, TimeUnit.MILLISECONDS);

    runAndWait("taskWithTimeoutTaskType", taskWithTimeout);
    Assert.assertEquals(taskWithTimeout.getShallowTrace().getTaskType(), TaskType.WITH_TIMEOUT.getName());
  }

  @Test
  public void testWithSideEffectTaskType() {
    Task<?> taskWithSideEffect = Task.value("value1Task", "value1").withSideEffect("delayed sideEffect",
        v -> delayedValue("value2", 100, TimeUnit.MILLISECONDS));

    runAndWait("taskWithSideEffectTaskType", taskWithSideEffect);
    Assert.assertEquals(taskWithSideEffect.getShallowTrace().getTaskType(), TaskType.WITH_SIDE_EFFECT.getName());
  }

  @Test
  public void testWithRetryTaskType() {
    Task<String> task = Task.withRetryPolicy(RetryPolicy.attempts(1, 0), attempt -> Task.value("successful attempt " + attempt));
    runAndWait(task);
    assertEquals(task.getShallowTrace().getTaskType(), TaskType.WITH_RETRY.getName());
  }

  @Test
  public void testWithRecoverTaskType() {
    Task<String> task = getFailureTask().recoverWith(e -> Task.value("recoveryTask"));
    runAndWait(task);
    assertEquals(task.getShallowTrace().getTaskType(), TaskType.WITH_RECOVER.getName());
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
