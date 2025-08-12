package com.linkedin.parseq.batching;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.Trace;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;


public class TestTaskSimpleBatchingStrategyBlocking extends BaseEngineTest {

  private final ScheduledExecutorService _executorService = Executors.newScheduledThreadPool(10);
  private final BatchingSupport _batchingSupport = new BatchingSupport();
  private final Strategy _strategy = new Strategy(1000);

  private class Strategy extends SimpleTaskBatchingStrategy<Integer, String> {

    private final long _sleepMs;

    private Strategy(long sleepMs) {
      _sleepMs = sleepMs;
    }

    @Override
    public Task<Map<Integer, Try<String>>> taskForBatch(Set<Integer> keys) {
      return Task.callableInExecutor(() -> {
        try {
          // make this batching task long-running
          Thread.sleep(_sleepMs);
        } catch (InterruptedException ignored) {
        }
        return keys.stream().collect(Collectors.toMap(k -> k, k -> Success.of(Integer.toString(k))));
      }, _executorService);
    }
  }

  @Override
  protected void customizeEngine(EngineBuilder engineBuilder) {
    engineBuilder.setTaskExecutor(_executorService).setTimerScheduler(_executorService);
    engineBuilder.setPlanDeactivationListener(_batchingSupport);
    _batchingSupport.registerStrategy(_strategy);
  }


  @Test
  public void testLongRunningBatchTaskFailure() {
    Task<String> batchTask = _strategy.batchable(1);
    Task<String> failingTask = delayedFailure(new UnsupportedOperationException("not supported!"), 5, TimeUnit.MILLISECONDS);

    Task<String> finalTask = Task.par(batchTask, failingTask).map("concat", (s, t) -> s + t).recover("recover", throwable -> "hello");
    runAndWaitForPlanToComplete("TestTaskSimpleBatchingStrategyBlocking.testLongRunningBatchTaskFailure", finalTask, 5, TimeUnit.SECONDS);
    verifyBatchFinished(finalTask);
  }

  @Test
  public void testLongRunningBatchTaskSuccess() {
    Task<String> batchTask = _strategy.batchable(1);
    Task<String> successTask = delayedValue("hello", 1, TimeUnit.MILLISECONDS);

    Task<String> finalTask = Task.par(batchTask, successTask).map("concat", (s, t) -> s + t);
    runAndWaitForPlanToComplete("TestTaskSimpleBatchingStrategyBlocking.testLongRunningBatchTaskSuccess", finalTask, 5, TimeUnit.SECONDS);
    verifyBatchFinished(finalTask);
  }

  private void verifyBatchFinished(final Task<?> task) {
    final ShallowTrace trace = findBatchTrace(task);
    assertTrue(trace != null);
    assertTrue(trace.getResultType() != ResultType.UNFINISHED);
  }

  private ShallowTrace findBatchTrace(final Task<?> task) {
    final ShallowTrace main = task.getShallowTrace();
    if (main.getName().startsWith("batch(")) {
      return main;
    } else {
      final Trace trace = task.getTrace();
      Optional<ShallowTrace> batchTrace = trace.getTraceMap().entrySet().stream()
          .filter(entry -> entry.getValue().getName().startsWith("batch("))
          .findFirst()
          .map(Map.Entry::getValue);
      return batchTrace.orElse(null);
    }
  }
}