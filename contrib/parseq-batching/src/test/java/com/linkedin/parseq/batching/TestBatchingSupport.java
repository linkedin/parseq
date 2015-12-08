package com.linkedin.parseq.batching;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.batching.BatchImpl.BatchEntry;

public class TestBatchingSupport extends BaseEngineTest {

  private final BatchingSupport _batchingSupport = new BatchingSupport();

  @Override
  protected void customizeEngine(EngineBuilder engineBuilder) {
    engineBuilder.setPlanDeactivationListener(_batchingSupport);
  }

  @Test
  public void testBatchInvoked() {
    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<>((key, promise) -> promise.done(String.valueOf(key)), key -> 0);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestBatchingSupport.testBatchInvoked", task);

    assertEquals(result, "01");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 0);
  }

  @Test
  public void testSingletonsInvoked() {
    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<>((key, promise) -> promise.done(String.valueOf(key)), key -> key);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestBatchingSupport.testSingletonsInvoked", task);

    assertEquals(result, "01");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertEquals(strategy.getExecutedBatches().size(), 0);
    assertEquals(strategy.getExecutedSingletons().size(), 2);
  }

  @Test
  public void testBatchAndSingleton() {
    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<>((key, promise) -> promise.done(String.valueOf(key)), key -> key % 2);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1), strategy.batchable(2))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestBatchingSupport.testBatchAndSingleton", task);

    assertEquals(result, "012");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testBatchAndFailedSingleton() {
    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<>((key, promise) -> {
          if (key % 2 == 0) {
            promise.done(String.valueOf(key));
          } else {
            promise.fail(new Exception());
          }
        }, key -> key % 2);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1).recover(e -> "failed"), strategy.batchable(2))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestBatchingSupport.testBatchAndFailedSingleton", task);

    assertEquals(result, "0failed2");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testFailedBatchAndSingleton() {
    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<>((key, promise) -> {
          if (key % 2 == 1) {
            promise.done(String.valueOf(key));
          } else {
            promise.fail(new Exception());
          }
        }, key -> key % 2);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0).recover(e -> "failed"), strategy.batchable(1), strategy.batchable(2).recover(e -> "failed"))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestBatchingSupport.testFailedBatchAndSingleton", task);

    assertEquals(result, "failed1failed");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testClassifyFailure() {
    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<>((key, promise) -> promise.done(String.valueOf(key)), key -> key / key);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0).recover(e -> "failed"), strategy.batchable(1).recover(e -> "failed"))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestBatchingSupport.testClassifyFailure", task);

    assertEquals(result, "failedfailed");
    assertEquals(strategy.getExecutedBatches().size(), 0);
    assertEquals(strategy.getExecutedSingletons().size(), 0);
  }

  @Test
  public void testExecuteBatchFailure() {
    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<Integer, Integer, String>((key, promise) -> promise.done(String.valueOf(key)), key -> key % 2) {

      @Override
      public void executeBatch(Integer group, Batch<Integer, String> batch) {
        throw new RuntimeException();
      }

    };

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0).recover(e -> "failed"), strategy.batchable(1), strategy.batchable(2).recover(e -> "failed"))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestBatchingSupport.testExecuteBatchFailure", task);

    assertEquals(result, "failed1failed");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 0);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testExecuteSingletonFailure() {
    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<Integer, Integer, String>((key, promise) -> promise.done(String.valueOf(key)), key -> key % 2) {

      @Override
      public void executeSingleton(Integer group, Integer key, BatchEntry<String> entry) {
        throw new RuntimeException();
      }

    };

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1).recover(e -> "failed"), strategy.batchable(2))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestBatchingSupport.testExecuteSingletonFailure", task);

    assertEquals(result, "0failed2");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 0);
  }

  @Test
  public void testNothingToDoForStrategy() {
    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<>((key, promise) -> promise.done(String.valueOf(key)), key -> 0);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(Task.value("0"), Task.value("1"))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestBatchingSupport.testNothingToDoForStrategy", task);

    assertEquals(result, "01");
    assertEquals(strategy.getClassifiedKeys().size(), 0);
    assertEquals(strategy.getExecutedBatches().size(), 0);
    assertEquals(strategy.getExecutedSingletons().size(), 0);
  }

  @Test
  public void testDeduplication() {
    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<>((key, promise) -> promise.done(String.valueOf(key)), key -> key % 2);

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0), strategy.batchable(1), strategy.batchable(2),
        strategy.batchable(0), strategy.batchable(1), strategy.batchable(2))
        .map("concat", (s0, s1, s2, s3, s4, s5) -> s0 + s1 + s2 + s3 + s4 + s5);

    String result = runAndWait("TestBatchingSupport.testDeduplication", task);

    assertEquals(result, "012012");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testBatchWithTimeoutAndSingleton() {

    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<Integer, Integer, String>((key, promise) -> promise.done(String.valueOf(key)), key -> key % 2) {
      @Override
      public void executeBatch(final Integer group, final Batch<Integer, String> batch) {
        getScheduler().schedule(() -> {
          super.executeBatch(group, batch);
        }, 250, TimeUnit.MILLISECONDS);
      }
    };

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0).withTimeout(10, TimeUnit.MILLISECONDS).recover("toExceptionName", e -> e.getClass().getName()),
        strategy.batchable(1), strategy.batchable(2))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestBatchingSupport.testBatchWithTimeoutAndSingleton", task);

    assertEquals(result, "java.util.concurrent.TimeoutException12");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

  @Test
  public void testBatchAndSingletonWithTimeout() {

    RecordingStrategy<Integer, Integer, String> strategy =
        new RecordingStrategy<Integer, Integer, String>((key, promise) -> promise.done(String.valueOf(key)), key -> key % 2) {
      @Override
      public void executeBatch(final Integer group, final Batch<Integer, String> batch) {
        getScheduler().schedule(() -> {
          super.executeBatch(group, batch);
        }, 250, TimeUnit.MILLISECONDS);
      }

      @Override
          public void executeSingleton(Integer group, Integer key, BatchEntry<String> entry) {
            getScheduler().schedule(() -> {
              super.executeSingleton(group, key, entry);
            }, 250, TimeUnit.MILLISECONDS);
          }
    };

    _batchingSupport.registerStrategy(strategy);

    Task<String> task = Task.par(strategy.batchable(0),
        strategy.batchable(1).withTimeout(10, TimeUnit.MILLISECONDS).recover("toExceptionName", e -> e.getClass().getName()),
        strategy.batchable(2))
        .map("concat", (s0, s1, s2) -> s0 + s1 + s2);

    String result = runAndWait("TestBatchingSupport.testBatchAndSingletonWithTimeout", task);

    assertEquals(result, "0java.util.concurrent.TimeoutException2");
    assertTrue(strategy.getClassifiedKeys().contains(0));
    assertTrue(strategy.getClassifiedKeys().contains(1));
    assertTrue(strategy.getClassifiedKeys().contains(2));
    assertEquals(strategy.getExecutedBatches().size(), 1);
    assertEquals(strategy.getExecutedSingletons().size(), 1);
  }

}
