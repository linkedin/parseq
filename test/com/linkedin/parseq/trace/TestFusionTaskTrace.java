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
  public void testMap() {
    Task<Integer> task = Task.value("value", 1).map("m1", x -> x);
    runAndWait("FusionTaskTraceTest.testMap", task);
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
  public void testAsyncMap() {
    Task<Integer> task = Task.async("value", () -> {
      SettablePromise<Integer> p = Promises.settable();
      p.done(1);
      return p;
    }).map("m1", x -> x);
    runAndWait("FusionTaskTraceTest.testAsyncMap", task);
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

}