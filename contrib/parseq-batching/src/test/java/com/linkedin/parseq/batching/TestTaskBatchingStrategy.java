package com.linkedin.parseq.batching;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.function.Failure;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;

public class TestTaskBatchingStrategy extends BaseEngineTest {

  private final BatchingSupport _batchingSupport = new BatchingSupport();

  @Override
  protected void customizeEngine(EngineBuilder engineBuilder) {
    engineBuilder.setPlanDeactivationListener(_batchingSupport);
  }

  @Test
  public void testBatchInvoked() {
    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> Success.of(String.valueOf(key)), key -> 0);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestTaskBatchingStrategy.testBatchInvoked", task);

    assertEquals(result, "01");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertEquals(strategy.getExecutedBatches().size(), 1);
  }

  @Test
  public void testSingletonsInvoked() {
    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> Success.of(String.valueOf(key)), key -> key);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestTaskBatchingStrategy.testSingletonsInvoked", task);

    assertEquals(result, "01");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertEquals(strategy.getExecutedBatches().size(), 0);
    assertEquals(strategy.getExecutedSingletons().size(), 2);
  }

  @Test
  public void testBatchAndSingleton() {
    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> Success.of(String.valueOf(key)), key -> key % 2);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1), strategy.batchable(2))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestTaskBatchingStrategy.testBatchAndSingleton", task);

    assertEquals(result, "012");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testBatchAndFailedSingleton() {
    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> {
          if (key % 2 == 0) {
            return Success.of(String.valueOf(key));
          } else {
            return Failure.of(new Exception());
          }
        }, key -> key % 2);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1).recover(e -> "failed"), strategy.batchable(2))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestTaskBatchingStrategy.testBatchAndFailedSingleton", task);

    assertEquals(result, "0failed2");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testFailedBatchAndSingleton() {
    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> {
          if (key % 2 == 1) {
            return Success.of(String.valueOf(key));
          } else {
            return Failure.of(new Exception());
          }
        }, key -> key % 2);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0).recover(e -> "failed"), strategy.batchable(1), strategy.batchable(2).recover(e -> "failed"))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestTaskBatchingStrategy.testFailedBatchAndSingleton", task);

    assertEquals(result, "failed1failed");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testClassifyFailure() {
    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> Success.of(String.valueOf(key)), key -> key / key);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0).recover(e -> "failed"), strategy.batchable(1).recover(e -> "failed"))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestTaskBatchingStrategy.testClassifyFailure", task);

    assertEquals(result, "failed1");
    assertEquals(strategy.getExecutedBatches().size(), 0);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testExecuteBatchFailure() {
    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> Success.of(String.valueOf(key)), key -> key % 2) {

      @Override
      public Task<Map<Integer, Try<String>>> taskForBatch(Integer group, Set<Integer> keys) {
        throw new RuntimeException();
      };
    };

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0).recover(e -> "failed"), strategy.batchable(1).recover(e -> "failed"), strategy.batchable(2).recover(e -> "failed"))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestTaskBatchingStrategy.testExecuteBatchFailure", task);

    assertEquals(result, "failedfailedfailed");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 0);
    assertEquals(strategy.getExecutedSingletons().size(), 0);
  }

  @Test
  public void testNothingToDoForStrategy() {
    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> Success.of(String.valueOf(key)), key -> 0);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(Task.value("0"), Task.value("1"))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestTaskBatchingStrategy.testNothingToDoForStrategy", task);

    assertEquals(result, "01");
    assertEquals(strategy.getClassifiedKeys().size(), 0);
    assertEquals(strategy.getExecutedBatches().size(), 0);
    assertEquals(strategy.getExecutedSingletons().size(), 0);
  }

  @Test
  public void testDeduplication() {
    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> Success.of(String.valueOf(key)), key -> key % 2);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1), strategy.batchable(2),
        strategy.batchable(0), strategy.batchable(1), strategy.batchable(2))
        .map("concat", (s0, s1, s2, s3, s4, s5) -> s0 + s1 + s2 + s3 + s4 + s5);

    String result = runAndWait("TestTaskBatchingStrategy.testDeduplication", task);

    assertEquals(result, "012012");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testBatchWithTimeoutAndSingleton() {

    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> Success.of(String.valueOf(key)), key -> key % 2) {

      @Override
          public Task<Map<Integer, Try<String>>> taskForBatch(Integer group, Set<Integer> keys) {
            return super.taskForBatch(group, keys).flatMap(map -> delayedValue(map, 250, TimeUnit.MILLISECONDS));
          }
    };

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0).withTimeout(10, TimeUnit.MILLISECONDS).recover("toExceptionName", e -> e.getClass().getName()),
        strategy.batchable(1), strategy.batchable(2))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestTaskBatchingStrategy.testBatchWithTimeoutAndSingleton", task);

    assertEquals(result, "java.util.concurrent.TimeoutException12");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testBatchAndSingletonWithTimeout() {

    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> Success.of(String.valueOf(key)), key -> key % 2) {

      @Override
          public Task<Map<Integer, Try<String>>> taskForBatch(Integer group, Set<Integer> keys) {
            return super.taskForBatch(group, keys).flatMap(map -> delayedValue(map, 250, TimeUnit.MILLISECONDS));
          }
    };

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0),
        strategy.batchable(1).withTimeout(10, TimeUnit.MILLISECONDS).recover("toExceptionName", e -> e.getClass().getName()),
        strategy.batchable(2))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestTaskBatchingStrategy.testBatchAndSingletonWithTimeout", task);

    assertEquals(result, "0java.util.concurrent.TimeoutException2");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testEntriesMissingInReturnedMap() {
    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> Success.of(String.valueOf(key)), key -> key % 2) {

      @Override
          public Task<Map<Integer, Try<String>>> taskForBatch(Integer group, Set<Integer> keys) {
            return super.taskForBatch(group, keys).andThen(map -> map.remove(1));
          }
    };

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1).recover(e -> "missing"), strategy.batchable(2))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestTaskBatchingStrategy.testEntriesMissingInReturnedMap", task);

    assertEquals(result, "0missing2");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testFailureReturned() {
    RecordingTaskStrategy<Integer, Integer, String> strategy =
        new RecordingTaskStrategy<Integer, Integer, String>(key -> {
          if (key % 2 == 1) {
            return Failure.of(new Exception("failure message"));
          } else {
            return Success.of(String.valueOf(key));
          }
        }, key -> key % 2);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1).recover(e -> e.getMessage()), strategy.batchable(2))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestTaskBatchingStrategy.testFailureReturned", task);

    assertEquals(result, "0failure message2");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

}
