/*
 * Copyright 2012 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.parseq;

import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestTaskReuse extends BaseEngineTest
{

  @Test
  public void testExecuteMultipleTimes() {
    final AtomicInteger counter = new AtomicInteger();

    Task<String> task = Task.callable(() -> {
      counter.incrementAndGet();
      return "hello";
    });
    runAndWait("TestTaskReuse.testExecuteMultipleTimes-1", task);
    runAndWait("TestTaskReuse.testExecuteMultipleTimes-2", task);
    runAndWait("TestTaskReuse.testExecuteMultipleTimes-3", task);

    assertEquals(counter.get(), 1);
  }
  
  /**
   * In this case the "increaser" task is not being executed because
   * the "bob" task has already been resolved and test1 task is
   * resolved immediately.
   */
  @Test
  public void testLastTaskAlreadyResolved() {
    final AtomicInteger counter = new AtomicInteger();

    final Task<String> bob = Task.value("bob", "bob");
    runAndWait("TestTaskReuse.testLastTaskResolved-bob", bob);
    
    Task<String> task = Task.callable("increaser", () -> {
      counter.incrementAndGet();
      return "hello";
    });
    
    Task<String> test1 = task.andThen(bob);
    
    runAndWait("TestTaskReuse.testLastTaskResolved", test1);

    assertEquals(counter.get(), 0);
  }
  
  @Test
  public void testLastTaskAlreadyResolvedShareable() {
    final AtomicInteger counter = new AtomicInteger();

    final Task<String> bob = Task.value("bob", "bob");
    runAndWait("TestTaskReuse.testLastTaskAlreadyResolvedShareable-bob", bob);
    
    Task<String> task = Task.callable("increaser", () -> {
      counter.incrementAndGet();
      return "hello";
    });
    
    Task<String> test1 = task.andThen(bob.shareable());
    
    runAndWait("TestTaskReuse.testLastTaskAlreadyResolvedShareable", test1);

    assertEquals(counter.get(), 1);
  }
  
  @Test
  public void testTaskReusedByTwoPlans() {
    final AtomicInteger counter = new AtomicInteger();

    Task<String> task = Task.callable(() -> {
      counter.incrementAndGet();
      return "hello";
    });

    Task<String> plan1 = task.map(s -> s + " on earth!");
    Task<String> plan2 = task.map(s -> s + " on moon!");

    runAndWait("TestTaskReuse.testTaskReusedByTwoPlans-plan1", plan1);
    runAndWait("TestTaskReuse.testTaskReusedByTwoPlans-plan2", plan2);

    assertEquals(counter.get(), 1);
    assertEquals(countTasks(plan1.getTrace()), 2);
    assertEquals(countTasks(plan2.getTrace()), 2);
  }

  @Test
  public void testTaskReusedByTwoPlansAndMerged() {
    final AtomicInteger counter = new AtomicInteger();

    Task<String> task = Task.callable(() -> {
      counter.incrementAndGet();
      return "hello";
    });

    Task<String> plan1 = task.map(s -> s + " on earth!");
    Task<String> plan2 = task.map(s -> s + " on moon!");

    runAndWait("TestTaskReuse.testTaskReusedByTwoPlansAndMerged-plan1", plan1);
    runAndWait("TestTaskReuse.testTaskReusedByTwoPlansAndMerged-plan2", plan2);
    Task<Integer> length1 = plan1.map("length", s -> s.length());
    Task<Integer> length2 = plan2.map("length", s -> s.length());

    Task<Integer> merged = Task.par(length1, length2).map((a, b) -> a + b);
    runAndWait("TestTaskReuse.testTaskReusedByTwoPlansAndMerged-merged", merged);

    assertEquals(counter.get(), 1);
    assertEquals(countTasks(merged.getTrace()), 8);

    assertEquals(countTasks(plan1.getTrace()), 2);
    assertEquals(countTasks(plan2.getTrace()), 2);
  }

  @Test
  public void testTaskReusedByTwoPlansAndMergedWithFlatMap() {
    final AtomicInteger counter = new AtomicInteger();

    Task<String> task = Task.callable(() -> {
      counter.incrementAndGet();
      return "hello";
    });

    Task<String> plan1 = task.flatMap("+eatch", s -> Task.callable(() -> s + " on earth!"));
    Task<String> plan2 = task.flatMap("+moon", s -> Task.callable(() -> s + " on moon!"));

    runAndWait("TestTaskReuse.testTaskReusedByTwoPlansAndMergedWithFlatMap-plan1", plan1);
    runAndWait("TestTaskReuse.testTaskReusedByTwoPlansAndMergedWithFlatMap-plan2", plan2);
    Task<Integer> length1 = plan1.map("length", s -> s.length());
    Task<Integer> length2 = plan2.map("length", s -> s.length());

    Task<Integer> merged = Task.par(length1, length2).map((a, b) -> a + b);
    runAndWait("TestTaskReuse.testTaskReusedByTwoPlansAndMergedWithFlatMap-merged", merged);

    assertEquals(counter.get(), 1);
    assertEquals(countTasks(merged.getTrace()), 9);

    assertEquals(countTasks(plan1.getTrace()), 4);
    assertEquals(countTasks(plan2.getTrace()), 4);
  }

}
