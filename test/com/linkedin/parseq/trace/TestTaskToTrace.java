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

package com.linkedin.parseq.trace;

import static com.linkedin.parseq.Task.value;
import static org.testng.AssertJUnit.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.Exceptions;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.Tasks;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;


/**
 * @author Chris Pettitt
 * @author Chi Chan
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestTaskToTrace extends BaseEngineTest {
  @Test
  public void testUnstartedTrace() {
    final Task<?> task = value("taskName", "value");

    // We don't run the task

    verifyShallowTrace(task);
  }

  @Test
  public void testSuccessfulTrace() throws InterruptedException {
    final Task<String> task = value("taskName", "value");

    runAndWait("TestTaskToTrace.testSuccessfulTrace", task);

    verifyShallowTrace(task);
  }

  @Test
  public void testSuccessfulTraceWithNullValue() throws InterruptedException {
    final Task<String> task = value("taskName", null);

    runAndWait("TestTaskToTrace.testSuccessfulTraceWithNullValue", task);

    verifyShallowTrace(task);
  }

  @Test
  public void testErrorTrace() throws InterruptedException {
    final Exception exception = new Exception("error message");
    final Task<?> task = Task.failure("taskName", exception);

    try {
      runAndWait("TestTaskToTrace.testErrorTrace", task);
      fail("task should finish with Exception");
    } catch (Throwable t) {
      assertEquals(exception, task.getError());
    }

    verifyShallowTrace(task);
  }

  @Test
  public void testNotHiddenTrace() throws InterruptedException {
    final Task<String> task1 = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        return Promises.value("task1");
      }
    };

    final Task<String> task2 = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        return Promises.value("task2");
      }
    };

    final Task<?> par1 = Task.par(task1, task2);
    runAndWait("TestTaskToTrace.testNotHiddenTrace", par1);

    assertFalse(par1.getShallowTrace().getHidden());
    assertFalse(task1.getShallowTrace().getHidden());
    assertFalse(task1.getShallowTrace().getHidden());
  }

  @Test
  public void testUserHiddenTrace() throws InterruptedException {
    final Task<String> task1 = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        return Promises.value("task1");
      }

      @Override
      public ShallowTrace getShallowTrace() {
        _shallowTraceBuilder.setHidden(true);
        return super.getShallowTrace();
      }
    };

    final Task<String> task2 = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        return Promises.value("task2");
      }

      @Override
      public ShallowTrace getShallowTrace() {
        _shallowTraceBuilder.setHidden(true);
        return super.getShallowTrace();
      }
    };

    final Task<?> par1 = Task.par(task1, task2);
    runAndWait("TestTaskToTrace.testUserHiddenTrace", par1);

    assertFalse(par1.getShallowTrace().getHidden());
    assertTrue(task1.getShallowTrace().getHidden());
    assertTrue(task1.getShallowTrace().getHidden());
  }

  @Test
  public void testUnfinishedTrace() throws InterruptedException {
    // Used to ensure that the task has started running
    final CountDownLatch cdl = new CountDownLatch(1);

    final SettablePromise<Void> promise = Promises.settable();

    final Task<Void> task = new BaseTask<Void>() {
      @Override
      public Promise<Void> run(final Context context) throws Exception {
        cdl.countDown();

        // Return a promise that won't be satisfied until after out test
        return promise;
      }
    };

    getEngine().run(task);

    assertTrue(cdl.await(5, TimeUnit.SECONDS));
    logTracingResults("TestTaskToTrace.testUnfinishedTrace", task);

    verifyShallowTrace(task);

    // Finish task
    promise.done(null);
  }

  private Set<TraceRelationship> getRelationships(Trace trace, long id) {
    Set<TraceRelationship> result = new HashSet<>();
    for (TraceRelationship rel : trace.getRelationships()) {
      if (rel.getFrom() == id || rel.getTo() == id) {
        result.add(rel);
      }
    }
    return result;
  }

  @Test
  public void testTraceWithPredecessorTrace() throws InterruptedException {
    final Task<String> predecessor = value("predecessor", "predecessorValue");
    final Task<String> successor = value("successor", "successorValue");

    final Task<?> seq = predecessor.andThen(successor);
    runAndWait("TestTaskToTrace.testTraceWithPredecessorTrace", seq);

    verifyShallowTrace(successor);
    verifyShallowTrace(predecessor);

    assertEquals(predecessor.getTrace(), successor.getTrace());

    //expected relationship: PARENT_OF and SUCCESSOR_OF
    assertEquals(2, getRelationships(successor.getTrace(), successor.getId()).size());
    assertTrue(successor.getTrace().getRelationships()
        .contains(new TraceRelationship(successor.getShallowTraceBuilder(),
            predecessor.getShallowTraceBuilder(), Relationship.SUCCESSOR_OF)));
  }

  @Test
  public void testSideEffectsPredecessorTrace() throws InterruptedException, IOException {
    final Task<String> baseTask = value("base", "baseValue");
    final Task<String> sideEffect = value("sideEffect", "sideEffectValue");

    final Task<String> withSideEffect = baseTask.withSideEffect(x -> sideEffect);
    runAndWait("TestTaskToTrace.testSideEffectsPredecessorTrace", withSideEffect);
    assertTrue(sideEffect.await(5, TimeUnit.SECONDS));

    assertEquals(2, getRelationships(withSideEffect.getTrace(), withSideEffect.getId()).size());

    verifyShallowTrace(sideEffect);
    verifyShallowTrace(baseTask);

    assertTrue(withSideEffect.getTrace().getRelationships().toString(), withSideEffect.getTrace().getRelationships()
        .contains(new TraceRelationship(withSideEffect.getShallowTraceBuilder(),
            baseTask.getShallowTraceBuilder(), Relationship.PARENT_OF)));
  }

  @SuppressWarnings("deprecation")
  @Test
  public void testTraceWithSuccessChild() throws InterruptedException {
    final Task<String> task = value("taskName", "value");

    final Task<?> seq = Tasks.seq(Arrays.asList(task));
    runAndWait("TestTaskToTrace.testTraceWithSuccessChild", seq);

    verifyShallowTrace(task);
    verifyShallowTrace(seq);

    assertEquals(1, getRelationships(seq.getTrace(), seq.getId()).size());
    assertTrue(seq.getTrace().getRelationships()
        .contains(new TraceRelationship(seq.getShallowTraceBuilder(),
            task.getShallowTraceBuilder(), Relationship.PARENT_OF)));
  }

  @Test
  public void testTraceWithEarlyFinish() throws InterruptedException {
    final Task<String> innerTask = value("xyz");
    final Task<String> task = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception {
        // We kick off a task that won't finish before the containing task
        // (this task) is finished.
        context.run(innerTask);

        return Promises.value("value");
      }
    };

    runAndWait("TestTaskToTrace.testTraceWithEarlyFinish", task);

    assertEquals(1, getRelationships(task.getTrace(), task.getId()).size());
    assertTrue(task.getTrace().getRelationships()
        .contains(new TraceRelationship(task.getShallowTraceBuilder(),
            innerTask.getShallowTraceBuilder(), Relationship.POTENTIAL_PARENT_OF)));
    assertEquals(ResultType.EARLY_FINISH, task.getTrace().getTraceMap().get(innerTask.getId()).getResultType());
  }

  @Test
  public void testTraceIsAddedBeforeAwaitCompletes() throws InterruptedException {
    for (int i = 0; i < 100; i++) {
      final Task<String> innerTask = value("xyz");
      final Task<String> task = new BaseTask<String>() {
        @Override
        protected Promise<? extends String> run(final Context context) throws Exception {
          // We kick off a task that won't finish before the containing task
          // (this task) is finished.
          context.run(innerTask);

          return Promises.value("value");
        }
      };

      runAndWait("TestTaskToTrace.testTraceIsAddedBeforeAwaitCompletes", task);

      assertTrue(task.getTrace().getRelationships().size() > 0);
    }
  }

  @Test
  public void testTraceWithMultiplePotentialParentsPar() throws InterruptedException {
    final Task<String> innerTask = value("xyz");
    final Task<String> task1 = new BaseTask<String>("task1") {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception {
        context.run(innerTask);
        return Promises.value("value1");
      }
    };

    final Task<String> task2 = new BaseTask<String>("task2") {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception {
        context.run(innerTask);
        return Promises.value("value2");
      }
    };

    Task<?> par = Task.par(task1, task2);
    runAndWait("TestTaskToTrace.testTraceWithMultiplePotentialParentsPar", par);

    Set<Long> tasksWithParent = new HashSet<>();
    Map<Long, Integer> tasksWithPotentialParent = new HashMap<>();
    assertAndFindParent(par.getTrace(), tasksWithParent, tasksWithPotentialParent);
    assertEquals(2, tasksWithParent.size());
    assertEquals((Integer) 2, tasksWithPotentialParent.get(innerTask.getId()));
    assertEquals(1, tasksWithPotentialParent.size());
    assertTrue(tasksWithParent.contains(task1.getId()));
    assertTrue(tasksWithParent.contains(task2.getId()));
    verifyShallowTrace(task1);
    verifyShallowTrace(task2);
    verifyShallowTrace(innerTask);
    assertEquals(ResultType.EARLY_FINISH, innerTask.getTrace().getTraceMap().get(innerTask.getId()).getResultType());
  }

  @Test
  public void testTraceWithMultiplePotentialParentsSeq() throws InterruptedException {
    final SettablePromise<String> promise1 = Promises.settable();

    final Task<String> innerTask = new BaseTask<String>("innerTask") {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        promise1.done("inner");
        return promise1;
      }
    };

    final Task<String> task1 = new BaseTask<String>("task1") {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception {
        context.run(innerTask);
        return Promises.value("value1");
      }
    };

    final Task<String> task2 = new BaseTask<String>("task2") {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception {
        context.run(innerTask);
        return Promises.value("value2");
      }
    };

    final Task<String> task3 = new BaseTask<String>("task2") {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception {
        context.run(innerTask);
        return Promises.value("value3");
      }
    };

    Task<?> seq = task1.andThen(task2).andThen(task3);
    runAndWait("TestTaskToTrace.testTraceWithMultiplePotentialParentsSeq", seq);

    Set<Long> tasksWithParent = new HashSet<>();
    Map<Long, Integer> tasksWithPotentialParent = new HashMap<>();
    assertAndFindParent(seq.getTrace(), tasksWithParent, tasksWithPotentialParent);

    assertEquals(4, tasksWithParent.size());
    assertEquals((Integer) 3, tasksWithPotentialParent.get(innerTask.getId()));
    assertEquals(1, tasksWithPotentialParent.size());
    assertTrue(tasksWithParent.contains(task1.getId()));
    assertTrue(tasksWithParent.contains(task2.getId()));
    assertTrue(tasksWithParent.contains(task3.getId()));
    verifyShallowTrace(task1);
    verifyShallowTrace(task2);
    verifyShallowTrace(task3);
    verifyShallowTrace(innerTask);
    assertEquals(ResultType.EARLY_FINISH, innerTask.getTrace().getTraceMap().get(innerTask.getId()).getResultType());
  }

  @Test
  public void testTraceWithDiamond() throws InterruptedException {
    final Task<String> a = value("valueA");
    final Task<String> b = value("valueB");
    final Task<String> c = value("valueC");
    final Task<String> d = value("valueD");

    final Task<String> parent = new BaseTask<String>() {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception {
        context.after(a).run(b);
        context.after(a).run(c);
        context.after(b, c).run(d);
        context.run(a);
        return d;
      }
    };

    runAndWait("TestTaskToTrace.testTraceWithDiamond", parent);

    verifyShallowTrace(parent);
    verifyShallowTrace(a);
    verifyShallowTrace(b);
    verifyShallowTrace(c);
    verifyShallowTrace(d);

    assertTrue(parent.getTrace().getRelationships().contains(
        new TraceRelationship(parent.getShallowTraceBuilder(), a.getShallowTraceBuilder(), Relationship.PARENT_OF)));
    assertTrue(parent.getTrace().getRelationships().contains(
        new TraceRelationship(parent.getShallowTraceBuilder(), b.getShallowTraceBuilder(), Relationship.PARENT_OF)));
    assertTrue(parent.getTrace().getRelationships().contains(
        new TraceRelationship(parent.getShallowTraceBuilder(), c.getShallowTraceBuilder(), Relationship.PARENT_OF)));
    assertTrue(parent.getTrace().getRelationships().contains(
        new TraceRelationship(parent.getShallowTraceBuilder(), d.getShallowTraceBuilder(), Relationship.PARENT_OF)));

    assertTrue(parent.getTrace().getRelationships().contains(
        new TraceRelationship(d.getShallowTraceBuilder(), b.getShallowTraceBuilder(), Relationship.SUCCESSOR_OF)));
    assertTrue(parent.getTrace().getRelationships().contains(
        new TraceRelationship(d.getShallowTraceBuilder(), c.getShallowTraceBuilder(), Relationship.SUCCESSOR_OF)));
    assertTrue(parent.getTrace().getRelationships().contains(
        new TraceRelationship(b.getShallowTraceBuilder(), a.getShallowTraceBuilder(), Relationship.SUCCESSOR_OF)));
    assertTrue(parent.getTrace().getRelationships().contains(
        new TraceRelationship(c.getShallowTraceBuilder(), a.getShallowTraceBuilder(), Relationship.SUCCESSOR_OF)));
  }

  /**
   * Populates sets with ids of tasks that have parent and ids of tasks that have potential
   * parents with number of potential parents.
   * Validates that task can have only one parent but many potential parents.
   */
  private void assertAndFindParent(Trace trace, Set<Long> tasksWithParent,
      Map<Long, Integer> tasksWithPotentialParent) {
    for (TraceRelationship rel : trace.getRelationships()) {
      if (rel.getRelationhsip() == Relationship.PARENT_OF) {
        assertFalse(tasksWithParent.contains(rel.getTo()));
        tasksWithParent.add(rel.getTo());
      } else if (rel.getRelationhsip() == Relationship.POTENTIAL_PARENT_OF) {
        if (!tasksWithPotentialParent.containsKey(rel.getTo())) {
          tasksWithPotentialParent.put(rel.getTo(), 0);
        }
        tasksWithPotentialParent.put(rel.getTo(), tasksWithPotentialParent.get(rel.getTo()) + 1);
      }
    }
  }

  private Long getChild(TraceRelationship rel, Long parent) {
    if ((rel.getRelationhsip() == Relationship.PARENT_OF || rel.getRelationhsip() == Relationship.POTENTIAL_PARENT_OF) &&
        rel.getFrom().equals(parent)) {
      return rel.getTo();
    }
    if (rel.getRelationhsip() == Relationship.POTENTIAL_CHILD_OF &&  rel.getTo().equals(parent)) {
      return rel.getFrom();
    }
    return null;
  }

  private ShallowTrace findPossiblyFusedTrace(final Task<?> task) {
    final ShallowTrace main = task.getShallowTrace();
    if (main.getName().equals("fused")) {
      final Trace trace = task.getTrace();
      Optional<Long> child = trace.getRelationships().stream()
                               .map(rel -> getChild(rel, main.getId()))
                               .filter(id-> id != null)
                               .findFirst();
      if (child.isPresent()) {
        return trace.getTraceMap().get(child.get());
      }
    }
    return main;
  }

  private void verifyShallowTrace(final Task<?> task) {
    final ShallowTrace trace = findPossiblyFusedTrace(task);
    assertEquals(task.getName(), trace.getName());
    assertEquals(ResultType.fromTask(task), trace.getResultType());

    // If the task has not been started then we expect the endNanos to be null.
    // If the task has started but has not been finished then endNanos is set
    // to the time that the trace was taken. If the task was finished then the
    // task end time and trace end time should match.
    if (trace.getResultType().equals(ResultType.UNFINISHED)) {
      if (trace.getStartNanos() == null) {
        assertNull(trace.getEndNanos());
      } else {
        // Trace will have the end time set to the time the trace was taken
        assertTrue(trace.getEndNanos() <= System.nanoTime());

        // We assume that the end time will always be at least one nanosecond
        // greater than the start time.
        assertTrue(trace.getEndNanos() > trace.getStartNanos());
      }
    }
    if (task.isDone()) {
      if (task.isFailed() && !(Exceptions.isEarlyFinish(task.getError()))) {
        assertNotNull(trace.getValue());
      } else {
        assertNull(trace.getValue());
      }
    } else {
      assertNull(trace.getValue());
    }
  }
}
