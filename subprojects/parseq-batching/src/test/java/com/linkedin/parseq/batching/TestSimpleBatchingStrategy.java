package com.linkedin.parseq.batching;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;

public class TestSimpleBatchingStrategy extends BaseEngineTest {

  private final BatchingSupport _batchingSupport = new BatchingSupport();
  private final Strategy _strategy = new Strategy();

  private class Strategy extends SimpleBatchingStrategy<Integer, String> {
    @Override
    public void executeBatch(Batch<Integer, String> batch) {
      batch.foreach((k, p) -> p.done(String.valueOf(k)));
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

    String result = runAndWait("TestSimpleBatchingStrategy.testNone", task);

    assertEquals(result, "01");
  }

  @Test
  public void testSingle() {

    Task<String> task = Task.par(Task.value("0"), _strategy.batchable(1))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestSimpleBatchingStrategy.testSingle", task);

    assertEquals(result, "01");
  }

  @Test
  public void testTwo() {

    Task<String> task = Task.par(_strategy.batchable(0), _strategy.batchable(1))
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestSimpleBatchingStrategy.testTwo", task);

    assertEquals(result, "01");
  }


  @Test
 public void testShareable() {

    Task<String> task = Task.par(_strategy.batchable(0).shareable(), _strategy.batchable(1).shareable())
        .map("concat", (s0, s1) -> s0 + s1);

    String result = runAndWait("TestSimpleBatchingStrategy.testShareable", task);
    assertEquals(result, "01");
  }

}
