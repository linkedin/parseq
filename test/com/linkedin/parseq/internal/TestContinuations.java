package com.linkedin.parseq.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;


public class TestContinuations {

  @Test
  public void testActionCalled() {
    final AtomicBoolean action = new AtomicBoolean(false);
    Continuations CONT = new Continuations();
    CONT.doSubmit(() -> {
      action.set(true);
    });
    assertTrue(action.get());
  }

  @Test
  public void testOnSuccessCalled() {
    final AtomicInteger sequence = new AtomicInteger(0);
    final AtomicInteger action = new AtomicInteger();
    final AtomicInteger onSuccess = new AtomicInteger();
    Continuations CONT = new Continuations();
    CONT.doSubmit(() -> {
      action.set(sequence.incrementAndGet());
    });
    CONT.doSubmit(() -> {
      onSuccess.set(sequence.incrementAndGet());
    });
    assertEquals(action.get(), 1);
    assertEquals(onSuccess.get(), 2);
  }

  @Test
  public void testOnSuccessNotCalledWhenException() {
    final AtomicInteger sequence = new AtomicInteger(0);
    final AtomicInteger onSuccess = new AtomicInteger();
    Continuations CONT = new Continuations();
    try {
      CONT.doSubmit(() -> {
        throw new RuntimeException("test");
      });
      CONT.doSubmit(() -> {
        onSuccess.set(sequence.incrementAndGet());
      });
      fail("should have thrown exception");
    } catch (Exception e) {
      assertEquals(e.getMessage(), "test");
    }
    assertEquals(onSuccess.get(), 0);
  }

  @Test
  public void testRecursiveOrder() {
    final Continuations CONT = new Continuations();
    final StringBuilder result = new StringBuilder();
    CONT.doSubmit(() -> {
      result.append("BEGIN{");
      recursivePostOrder(CONT, result, "root", 0);
    });
    CONT.doSubmit(() -> {
      result.append("}END");
    });
    assertEquals(result.toString(),
        "BEGIN{[done(rootLL:2)][done(rootLR:2)][done(rootL:1)][done(rootRL:2)][done(rootRR:2)][done(rootR:1)][done(root:0)]}END");
  }

  private void recursivePostOrder(final Continuations CONT, final StringBuilder result, final String branch,
      final int depth) {
    CONT.doSubmit(() -> {
      if (depth < 2) {
        recursivePostOrder(CONT, result, branch + "L", depth + 1);
        recursivePostOrder(CONT, result, branch + "R", depth + 1);
      }
    });
    CONT.doSubmit(() -> {
      result.append("[done(" + branch + ":" + depth + ")]");
    });
  }

  @Test
  public void testRecursiveOrderWithException() {
    final Continuations CONT = new Continuations();
    final StringBuilder result = new StringBuilder();
    try {
      CONT.doSubmit(() -> {
        result.append("BEGIN{");
        recursivePostOrderWithException(CONT, result, "root", 0);
      });
      CONT.doSubmit(() -> {
        result.append("}END");
      });
      fail("should have thrown exception");
    } catch (Exception e) {
      assertEquals(e.getMessage(), "nested");
    }
    assertEquals(result.toString(), "BEGIN{[done(rootLL:2)][done(rootLR:2)][done(rootL:1)][done(rootRL:2)]");
  }

  private void recursivePostOrderWithException(final Continuations CONT, final StringBuilder result,
      final String branch, final int depth) {
    CONT.doSubmit(() -> {
      if (depth < 2) {
        recursivePostOrderWithException(CONT, result, branch + "L", depth + 1);
        recursivePostOrderWithException(CONT, result, branch + "R", depth + 1);
      } else {
        if (branch.equals("rootRR")) {
          throw new RuntimeException("nested");
        }
      }
    });
    CONT.doSubmit(() -> {
      result.append("[done(" + branch + ":" + depth + ")]");
    });
  }

  @Test
  public void testDeepRecursion() {
    final Continuations CONT = new Continuations();
    final AtomicInteger result = new AtomicInteger();
    CONT.doSubmit(() -> {
      deepRecursion(CONT, result, 0);
    });
    assertEquals(result.get(), 1000001);
  }

  private void deepRecursion(final Continuations CONT, final AtomicInteger result, final int depth) {
    CONT.doSubmit(() -> {
      if (depth < 1000000) {
        deepRecursion(CONT, result, depth + 1);
      }
    });
    CONT.doSubmit(() -> {
      result.incrementAndGet();
    });
  }

}
