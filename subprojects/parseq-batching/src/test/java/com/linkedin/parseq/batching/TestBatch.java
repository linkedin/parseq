package com.linkedin.parseq.batching;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.testng.annotations.Test;

import com.linkedin.parseq.batching.BatchImpl.BatchBuilder;
import com.linkedin.parseq.batching.BatchImpl.BatchPromise;
import com.linkedin.parseq.trace.ShallowTraceBuilder;

public class TestBatch {

  @Test
  public void testEmptyBatch() {
    BatchBuilder<Integer, String> builder = new BatchBuilder<>(10, new BatchAggregationTimeMetric());
    Batch<Integer, String> empty = builder.build();

    assertEquals(empty.keySize(), 0);
    assertEquals(empty.batchSize(), 0);
    assertEquals(empty.values().size(), 0);
    assertEquals(empty.entries().size(), 0);
  }

  @Test
  public void testOverflow() {
    BatchBuilder<Integer, String> builder = new BatchBuilder<>(10, new BatchAggregationTimeMetric());
    assertTrue(builder.add(0, new ShallowTraceBuilder(0L), new BatchPromise<>(), 3));
    assertTrue(builder.add(1, new ShallowTraceBuilder(1L), new BatchPromise<>(), 3));
    assertTrue(builder.add(2, new ShallowTraceBuilder(2L), new BatchPromise<>(), 3));
    assertFalse(builder.add(3, new ShallowTraceBuilder(3L), new BatchPromise<>(), 3));
  }

  @Test
  public void testNoOverflowOnEmptyBuilder() {
    BatchBuilder<Integer, String> builder = new BatchBuilder<>(10, new BatchAggregationTimeMetric());
    assertTrue(builder.add(0, new ShallowTraceBuilder(0L), new BatchPromise<>(), 100));
    assertFalse(builder.add(1, new ShallowTraceBuilder(0L), new BatchPromise<>(), 1));
  }

  @Test
  public void testSizeOverflow() {
    BatchBuilder<Integer, String> builder = new BatchBuilder<>(Integer.MAX_VALUE, new BatchAggregationTimeMetric());
    assertTrue(builder.add(0, new ShallowTraceBuilder(0L), new BatchPromise<>(), Integer.MAX_VALUE - 3));
    assertTrue(builder.add(1, new ShallowTraceBuilder(0L), new BatchPromise<>(), 1));
    assertTrue(builder.add(2, new ShallowTraceBuilder(0L), new BatchPromise<>(), 1));
    assertTrue(builder.add(3, new ShallowTraceBuilder(0L), new BatchPromise<>(), 1));
    assertFalse(builder.add(4, new ShallowTraceBuilder(0L), new BatchPromise<>(), 1));
  }

  @Test
  public void testOverflowAfterFull() {
    BatchBuilder<Integer, String> builder = new BatchBuilder<>(10, new BatchAggregationTimeMetric());
    assertTrue(builder.add(0, new ShallowTraceBuilder(0L), new BatchPromise<>(), 3));
    assertTrue(builder.add(1, new ShallowTraceBuilder(1L), new BatchPromise<>(), 3));
    assertTrue(builder.add(2, new ShallowTraceBuilder(2L), new BatchPromise<>(), 4));
    assertFalse(builder.add(3, new ShallowTraceBuilder(3L), new BatchPromise<>(), 3));
  }

  @Test
  public void testBatch() {

    final AtomicInteger counter = new AtomicInteger(0);
    final Set<String> keys = new HashSet<>();

    final Function<String, BatchPromise<String>> createPromise = expected -> {
      BatchPromise<String> promise = new BatchPromise<>();
      promise.addListener(p -> {
        if (p.get().equals(expected)) {
          counter.incrementAndGet();
        }
      });
      if (!keys.contains(expected)) {
        promise.trigger();
        keys.add(expected);
      }
      return promise;
    };

    BatchBuilder<Integer, String> builder = new BatchBuilder<>(10, new BatchAggregationTimeMetric());
    assertTrue(builder.add(0, new ShallowTraceBuilder(0L), createPromise.apply("0"), 1));
    assertTrue(builder.add(1, new ShallowTraceBuilder(1L), createPromise.apply("1"), 1));
    final BatchPromise<String> p2 = createPromise.apply("2");
    assertTrue(builder.add(2, new ShallowTraceBuilder(2L), p2, 1));
    final BatchPromise<String> p3 = new BatchPromise<>();
    assertTrue(builder.add(3, new ShallowTraceBuilder(2L), p3, 1));
    assertTrue(builder.add(0, new ShallowTraceBuilder(3L), createPromise.apply("0"), 1));  //duplicate
    Batch<Integer, String> batch = builder.build();

    assertEquals(batch.keySize(), 4);
    assertEquals(batch.batchSize(), 5);
    assertEquals(batch.values().size(), 4);
    assertEquals(batch.entries().size(), 4);
    assertEquals(batch.keys().size(), 4);

    assertTrue(batch.keys().contains(0));
    assertTrue(batch.keys().contains(1));
    assertTrue(batch.keys().contains(2));
    assertTrue(batch.keys().contains(3));

    batch.done(0, "0");
    batch.done(1, "1");
    batch.fail(3, new Exception());

    assertEquals(counter.get(), 3);  // 0 with duplicate + 1
    assertFalse(p2.isDone());
    assertTrue(p3.isDone());
    assertTrue(p3.isFailed());
  }
}
