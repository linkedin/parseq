package com.linkedin.parseq;

import com.linkedin.parseq.retry.RetryPolicy;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.Trace;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;


/**
 * @author Siddharth Sodhani
 */
public class TestTaskType extends TestTask {

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
    assertEquals(doesTaskTypeExistInTrace(taskWithTimeout.getTrace(), TaskType.TIMEOUT.getName()), true);
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

    assertEquals(doesTaskTypeExistInTrace(task.getTrace(), TaskType.RETRY.getName()), true);
    assertEquals(task.getShallowTrace().getTaskType(), TaskType.WITH_RETRY.getName());
  }

  @Test
  public void testWithRecoverTaskType() {
    Task<String> task = getFailureTask().recoverWith(e -> Task.value("recoveryTask"));
    runAndWait(task);
    assertEquals(doesTaskTypeExistInTrace(task.getTrace(), TaskType.RECOVER.getName()), true);
    assertEquals(task.getShallowTrace().getTaskType(), TaskType.WITH_RECOVER.getName());
  }

  private boolean doesTaskTypeExistInTrace(Trace trace, String taskType) {
    return trace.getTraceMap().values().stream().anyMatch(shallowTrace -> shallowTrace.getTaskType().equals(taskType));
  }
}
