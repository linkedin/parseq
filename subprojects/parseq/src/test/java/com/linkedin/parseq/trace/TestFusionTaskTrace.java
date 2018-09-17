package com.linkedin.parseq.trace;

import org.testng.annotations.Test;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestFusionTaskTrace extends BaseEngineTest {

  @Test
  public void testMap0() {
    Task<Integer> task = Task.value("value", 1);
    runAndWait("FusionTaskTraceTest.testMap0", task);
  }

  @Test
  public void testMap1() {
    Task<Integer> task = Task.value("value", 1).map("m1", x -> x);
    runAndWait("FusionTaskTraceTest.testMap1", task);
  }

  @Test
  public void testMap2() {
    Task<Integer> task = Task.value("value", 1).map("m1", x -> x).map("m2", x -> x);
    runAndWait("FusionTaskTraceTest.testMap2", task);
  }

  @Test
  public void testMap3() {
    Task<Integer> task = Task.value("value", 1).map("m1", x -> x).map("m2", x -> x).map("m3", x -> x);
    runAndWait("FusionTaskTraceTest.testMap3", task);
  }

  @Test
  public void testMap4() {
    Task<Integer> task = Task.value("value", 1).map("m1", x -> x).map("m2", x -> x).map("m3", x -> x).map("m4", x -> x);
    runAndWait("FusionTaskTraceTest.testMap4", task);
  }

  @Test
  public void testMapFork() {
    Task<Integer> base = Task.value("value", 1);
    Task<Integer> task = Task.par(base.map("m1", x -> x), base.map("m2", x -> x))
    .map("sum", (x, y) -> x + y);
    runAndWait("FusionTaskTraceTest.testMapFork", task);
  }

  @Test
  public void testAsyncMap0() {
    Task<Integer> task = Task.async("value", () -> {
      SettablePromise<Integer> p = Promises.settable();
      p.done(1);
      return p;
    });
    runAndWait("FusionTaskTraceTest.testAsyncMap0", task);
  }

  @Test
  public void testAsyncMap1() {
    Task<Integer> task = Task.async("value", () -> {
      SettablePromise<Integer> p = Promises.settable();
      p.done(1);
      return p;
    }).map("m1", x -> x);
    runAndWait("FusionTaskTraceTest.testAsyncMa1p", task);
  }

  @Test
  public void testAsync2() {
    Task<Integer> task = Task.async("value", () -> {
      SettablePromise<Integer> p = Promises.settable();
      p.done(1);
      return p;
    }).map("m1", x -> x).map("m2", x -> x);
    runAndWait("FusionTaskTraceTest.testAsyncMap2", task);
  }

  @Test
  public void testAsync3() {
    Task<Integer> task = Task.async("value", () -> {
      SettablePromise<Integer> p = Promises.settable();
      p.done(1);
      return p;
    }).map("m1", x -> x).map("m2", x -> x).map("m3", x -> x);
    runAndWait("FusionTaskTraceTest.testAsyncMap3", task);
  }

  @Test
  public void testAsync4() {
    Task<Integer> task = Task.async("value", () -> {
      SettablePromise<Integer> p = Promises.settable();
      p.done(1);
      return p;
    }).map("m1", x -> x).map("m2", x -> x).map("m3", x -> x).map("m4", x -> x);
    runAndWait("FusionTaskTraceTest.testAsyncMap4", task);
  }

  @Test
  public void testAsyncFork() {
    Task<Integer> base = Task.async("value", () -> {
      SettablePromise<Integer> p = Promises.settable();
      p.done(1);
      return p;
    });
    Task<Integer> task = Task.par(base.map("m1", x -> x), base.map("m2", x -> x))
    .map("sum", (x, y) -> x + y);
    runAndWait("FusionTaskTraceTest.testAsyncFork", task);
  }



}