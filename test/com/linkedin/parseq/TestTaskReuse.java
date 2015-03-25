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
    runAndWait("TestTaskExecution.testExecuteMultipleTimes-1", task);
    runAndWait("TestTaskExecution.testExecuteMultipleTimes-2", task);
    runAndWait("TestTaskExecution.testExecuteMultipleTimes-3", task);

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

    runAndWait("TestTaskExecution.testTaskReusedByTwoPlans-plan1", plan1);
    runAndWait("TestTaskExecution.testTaskReusedByTwoPlans-plan2", plan2);

    assertEquals(counter.get(), 1);
    System.out.println(plan1.getTrace().getTraceMap());
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

    runAndWait("TestTaskExecution.testTaskReusedByTwoPlansAndMerged-plan1", plan1);
    runAndWait("TestTaskExecution.testTaskReusedByTwoPlansAndMerged-plan2", plan2);
    Task<Integer> length1 = plan1.map(s -> s.length());
    Task<Integer> length2 = plan2.map(s -> s.length());

    Task<Integer> merged = Task.par(length1, length2).map((a, b) -> a + b);
    runAndWait("TestTaskExecution.testTaskReusedByTwoPlansAndMerged-merged", merged);

    assertEquals(counter.get(), 1);
    assertEquals(countTasks(merged.getTrace()), 8);

    logTracingResults("plan1", plan1);
    logTracingResults("plan2", plan2);

    assertEquals(countTasks(plan1.getTrace()), 2);
    assertEquals(countTasks(plan2.getTrace()), 2);
  }


}
