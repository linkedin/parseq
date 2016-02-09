package com.linkedin.parseq.batching;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.function.Success;
import com.linkedin.parseq.function.Try;

public class TestTaskBasedSimpleBatchingStrategy extends BaseEngineTest {

  private final BatchingSupport _batchingSupport = new BatchingSupport();
  private final Strategy _strategy = new Strategy();

  private class Strategy extends SimpleTaskBasedBatchingStrategy<Integer, String> {
    @Override
    public Task<Map<Integer, Try<String>>> taskForBatch(Set<Integer> keys) {
      return Task.callable("taskForBatch", () -> {
        return keys.stream().collect(Collectors.toMap(Function.identity(), key -> Success.of(Integer.toString(key))));
      });
    }
  }

  @Override
  protected void customizeEngine(EngineBuilder engineBuilder) {
    engineBuilder.setPlanDeactivationListener(_batchingSupport);
    _batchingSupport.registerStrategy(_strategy);
  }

  @Test
  public void testNone() {

    Task<String> task = Task.par(Task.value("0"), Task.value("1"))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestTaskBasedSimpleBatchingStrategy.testNone", task);

    assertEquals(result, "01");
  }

  @Test
  public void testSingle() {

    Task<String> task = Task.par(Task.value("0"), _strategy.batchable(1))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestTaskBasedSimpleBatchingStrategy.testSingle", task);

    assertEquals(result, "01");
  }

  @Test
  public void testTwo() {

    Task<String> task = Task.par(_strategy.batchable(0), _strategy.batchable(1))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestTaskBasedSimpleBatchingStrategy.testTwo", task);

    assertEquals(result, "01");
  }

}
