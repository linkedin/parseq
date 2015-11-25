package com.linkedin.parseq.batching;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.testng.annotations.Test;

import com.linkedin.parseq.batching.BatchImpl.BatchBuilder;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.ShallowTraceBuilder;

public class TestBatch {

  @Test
  public void testEmptyBatch() {
    BatchBuilder<Integer, String> builder = Batch.builder();
    Batch<Integer, String> empty = builder.build();

    assertEquals(empty.size(), 0);
    assertEquals(empty.values().size(), 0);
    assertEquals(empty.entries().size(), 0);
  }

  @Test
  public void testBatch() {

    final AtomicInteger counter = new AtomicInteger(0);

    final Function<String, SettablePromise<String>> createPromise = expected -> {
      SettablePromise<String> promise = Promises.settable();
      promise.addListener(p -> {
        if (p.get().equals(expected)) {
          counter.incrementAndGet();
        }
      });
      return promise;
    };

    BatchBuilder<Integer, String> builder = Batch.builder();
    builder.add(0, new ShallowTraceBuilder(0L), createPromise.apply("0"));
    builder.add(1, new ShallowTraceBuilder(1L), createPromise.apply("1"));
    final SettablePromise<String> p2 = createPromise.apply("2");
    builder.add(2, new ShallowTraceBuilder(2L), p2);
    final SettablePromise<String> p3 = Promises.settable();
    builder.add(3, new ShallowTraceBuilder(2L), p3);
    builder.add(0, new ShallowTraceBuilder(3L), createPromise.apply("0"));  //duplicate
    Batch<Integer, String> batch = builder.build();

    assertEquals(batch.size(), 4);
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
