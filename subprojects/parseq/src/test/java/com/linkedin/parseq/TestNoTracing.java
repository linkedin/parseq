package com.linkedin.parseq;

import static org.testng.Assert.*;

import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

import com.linkedin.parseq.trace.ResultType;

public class TestNoTracing extends BaseEngineTest {

  @Override
  protected void customizeEngine(EngineBuilder builder) {
    builder.setEngineProperty(Engine.MAX_RELATIONSHIPS_PER_TRACE, 0);
  }

  private RuntimeException countingError(AtomicInteger stackPrints) {
    return new RuntimeException("boom") {
      @Override
      public void printStackTrace(PrintWriter writer) {
        stackPrints.incrementAndGet();
        super.printStackTrace(writer);
      }
    };
  }

  @Test
  public void testFailureSkipsStackTraceSerializationWhenTracingDisabled() throws InterruptedException {
    AtomicInteger stackPrints = new AtomicInteger();
    RuntimeException error = countingError(stackPrints);
    Task<Integer> task = Task.callable("failing", () -> { throw error; });

    getEngine().run(task);
    assertTrue(task.await(5, TimeUnit.SECONDS));

    assertSame(task.getError(), error);
    assertEquals(task.getShallowTrace().getResultType(), ResultType.ERROR);
    assertNull(task.getShallowTrace().getValue());
    assertEquals(stackPrints.get(), 0);
  }

  @Test
  public void testFusedFailureSkipsStackTraceSerializationWhenTracingDisabled() throws InterruptedException {
    AtomicInteger stackPrints = new AtomicInteger();
    RuntimeException error = countingError(stackPrints);
    Task<Integer> source = Task.callable("source", () -> { throw error; });
    Task<Integer> mapped = source.map("mapped", n -> n + 1);
    Task<Integer> root = Task.par(mapped, mapped.map("branch", n -> n * 2)).map("sum", (a, b) -> a + b);

    getEngine().run(root);
    assertTrue(root.await(5, TimeUnit.SECONDS));

    assertTrue(root.isFailed());
    assertEquals(source.getShallowTrace().getResultType(), ResultType.ERROR);
    assertNull(source.getShallowTrace().getValue());
    assertNull(mapped.getShallowTrace().getValue());
    assertEquals(stackPrints.get(), 0);
  }

  @Test
  public void testSuccessSkipsValueSerializationWhenTracingDisabled() throws InterruptedException {
    AtomicInteger serialized = new AtomicInteger();
    Task<Integer> source = Task.value("source", 1);
    Task<Integer> mapped = source.map("mapped", n -> n + 1);
    source.setTraceValueSerializer(n -> { serialized.incrementAndGet(); return n.toString(); });
    mapped.setTraceValueSerializer(n -> { serialized.incrementAndGet(); return n.toString(); });

    getEngine().run(mapped);
    assertTrue(mapped.await(5, TimeUnit.SECONDS));

    assertEquals(mapped.get(), Integer.valueOf(2));
    assertEquals(mapped.getShallowTrace().getResultType(), ResultType.SUCCESS);
    assertNull(source.getShallowTrace().getValue());
    assertNull(mapped.getShallowTrace().getValue());
    assertEquals(serialized.get(), 0);
  }

  @Test
  public void testNonZeroLimitsStillSerializeFailuresAndValues() throws InterruptedException {
    for (int limit : new int[] { 100, -1 }) {
      Engine engine = new EngineBuilder()
          .setTaskExecutor(Runnable::run)
          .setTimerScheduler(getScheduler())
          .setEngineProperty(Engine.MAX_RELATIONSHIPS_PER_TRACE, limit)
          .build();
      try {
        AtomicInteger stackPrints = new AtomicInteger();
        Task<Integer> failing = Task.callable("failing", () -> { throw countingError(stackPrints); });
        engine.run(failing);
        assertTrue(failing.await(5, TimeUnit.SECONDS));
        assertEquals(stackPrints.get(), 1);
        assertNotNull(failing.getShallowTrace().getValue());

        AtomicInteger serialized = new AtomicInteger();
        Task<Integer> ok = Task.value("ok", 42);
        ok.setTraceValueSerializer(n -> { serialized.incrementAndGet(); return "v=" + n; });
        engine.run(ok);
        assertTrue(ok.await(5, TimeUnit.SECONDS));
        assertEquals(serialized.get(), 1);
        assertEquals(ok.getShallowTrace().getValue(), "v=42");
      } finally {
        engine.shutdown();
      }
    }
  }
}
