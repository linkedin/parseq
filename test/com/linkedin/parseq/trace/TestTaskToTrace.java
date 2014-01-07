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

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.TestUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.linkedin.parseq.Tasks.par;
import static com.linkedin.parseq.Tasks.seq;
import static com.linkedin.parseq.TestUtil.value;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Chris Pettitt
 * @author Chi Chan
 */
public class TestTaskToTrace extends BaseEngineTest
{
  @Test
  public void testSnapshotNanosForUnstartedTrace()
  {
    final Task<?> task = value("foo");
    final Trace trace = task.getTrace();
    assertFalse(trace.getSnapshotNanos() == 0);
    assertLTE(trace.getSnapshotNanos(), System.nanoTime());
  }

  @Test
  public void testSnapshotNanosForStartedTrace() throws InterruptedException
  {
    final Task<?> task = value("foo");
    getEngine().run(task, true);

    task.await(5, TimeUnit.SECONDS);

    final Trace trace = task.getTrace();
    assertFalse(trace.getSnapshotNanos() == 0);
    assertLTE(trace.getSnapshotNanos(), System.nanoTime());
  }


  @Test
  public void testUnstartedTrace()
  {
    final Task<?> task = value("taskName", "value");

    // We don't run the task

    assertShallowTraceMatches(task, getTrace(task));
  }

  @Test
  public void testSuccessfulTrace() throws InterruptedException
  {
    final Task<String> task = value("taskName", "value");

    getEngine().run(task);
    task.await();

    assertShallowTraceMatches(task, getTrace(task));
    assertLTE(task.getTrace().getSnapshotNanos(), System.nanoTime());
  }

  @Test
  public void testSuccessfulTraceWithNullValue() throws InterruptedException
  {
    final Task<String> task = value("taskName", null);

    getEngine().run(task);
    task.await();

    assertShallowTraceMatches(task, getTrace(task));
  }

  @Test
  public void testErrorTrace() throws InterruptedException
  {
    final Task<?> task = TestUtil.errorTask("taskName", new Exception("error message"));

    getEngine().run(task);
    task.await();

    assertShallowTraceMatches(task, getTrace(task));
  }

  @Test
  public void testSeqSystemHiddenTrace() throws InterruptedException
  {
    final Task<String> task1 = value("taskName1", "value");
    final Task<String> task2 = value("taskName2", "value2");

    final Task<?> seq1 = seq(task1, task2);
    getEngine().run(seq1);
    seq1.await();

    assertTrue(getTrace(seq1).getSystemHidden());

    final Task<String> task3 = value("taskName3", "value3");
    final Task<String> task4 = value("taskName4", "value4");
    final Task<?> seq2 = seq(task3, task4);

    assertTrue(getTrace(seq2).getSystemHidden());
  }
  @Test
  public void testParSystemHiddenTrace() throws InterruptedException
  {
    final Task<String> task1 = value("taskName1", "value");
    final Task<String> task2 = value("taskName2", "value2");


    final Task<?> par1 = par(task1, task2);

    getEngine().run(par1);
    par1.await();

    assertTrue(getTrace(par1).getSystemHidden());

    final Task<String> task3 = value("taskName3", "value3");
    final Task<String> task4 = value("taskName4", "value4");
    final Task<?> par2 = par(task3, task4);

    assertTrue(getTrace(par2).getSystemHidden());
  }

  @Test
  public void testNotHiddenTrace() throws InterruptedException
  {
    final Task<String> task1 = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception
      {
        return  Promises.value("task1");
      }
    };

    final Task<String> task2 = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception
      {
        return  Promises.value("task2");
      }
    };

    final Task<?> par1 = par(task1, task2);
    getEngine().run(par1);
    par1.await();

    assertFalse(getTrace(par1).getHidden());
    assertFalse(getTrace(task1).getHidden());
    assertFalse(getTrace(task2).getHidden());
  }

  @Test
  public void testUserHiddenTrace() throws InterruptedException
  {
    final Task<String> task1 = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception
      {
        return  Promises.value("task1");
      }

      @Override
      public ShallowTrace getShallowTrace()
      {
        return new ShallowTraceBuilder(super.getShallowTrace())
            .setHidden(true)
            .build();
      }
    };

    final Task<String> task2 = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception
      {
        return  Promises.value("task2");
      }

      @Override
      public ShallowTrace getShallowTrace()
      {
        return new ShallowTraceBuilder(super.getShallowTrace())
            .setHidden(true)
            .build();
      }
    };

    final Task<?> par1 = par(task1, task2);
    getEngine().run(par1);
    par1.await();

    assertFalse(getTrace(par1).getHidden());
    assertTrue(getTrace(task1).getHidden());
    assertTrue(getTrace(task2).getHidden());
  }

  @Test
  public void testUnfinishedTrace() throws InterruptedException
  {
    // Used to ensure that the task has started running
    final CountDownLatch cdl = new CountDownLatch(1);

    final SettablePromise<Void> promise = Promises.settable();

    final Task<Void> task = new BaseTask<Void>()
    {
      @Override
      public Promise<Void> run(final Context context) throws Exception
      {
        cdl.countDown();

        // Return a promise that won't be satisfied until after out test
        return promise;
      }
    };

    getEngine().run(task);

    cdl.await();

    assertShallowTraceMatches(task, getTrace(task));

    // Finish task
    promise.done(null);
  }

  @Test
  public void testTraceWithPredecessorTrace() throws InterruptedException
  {
    final Task<String> predecessor = value("predecessor", "predecessorValue");
    final Task<String> successor = value("successor", "successorValue");

    final Task<?> seq = seq(predecessor, successor);
    getEngine().run(seq, true);
    seq.await();

    assertShallowTraceMatches(successor, getTrace(successor));
    assertShallowTraceMatches(predecessor, getTrace(predecessor));

    assertEquals(Collections.singleton(Relationship.SUCCESSOR_OF),
        getRelationships(seq, successor, predecessor));
  }


  @Test
  public void testTraceWithSuccessChild() throws InterruptedException
  {
    final Task<String> task = value("taskName", "value");

    @SuppressWarnings("unchecked")
    final Task<?> seq = seq(Arrays.asList(task));
    getEngine().run(seq, true);
    seq.await();

    assertShallowTraceMatches(task, getTrace(task));
    assertShallowTraceMatches(seq, getTrace(seq));

    assertEquals(Collections.singleton(Relationship.PARENT_OF),
        getRelationships(seq, seq, task));
  }

  @Test
  public void testTraceWithEarlyFinish() throws InterruptedException
  {
    final Task<String> innerTask = value("xyz");
    final Task<String> task = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception
      {
        // We kick off a task that won't finish before the containing task
        // (this task) is finished.
        context.run(innerTask);

        return Promises.value("value");
      }
    };

    getEngine().run(task, true);
    task.await();

    assertShallowTraceMatches(task, getTrace(task));
    assertShallowTraceMatches(innerTask, getTrace(innerTask));
    assertEquals(ResultType.EARLY_FINISH, getTrace(innerTask).getResultType());
    assertEquals(Collections.singleton(Relationship.PARENT_OF),
        getRelationships(task, task, innerTask));
  }

  @Test
  public void testTraceWithMultiplePotentialParent() throws InterruptedException
  {
    final Task<String> innerTask = value("xyz");
    final Task<String> task1 = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception
      {
        // Ensure that inner task runs before this task finishes
        final Task<String> valueTask = TestUtil.value("valueTask", "value1");
        context.run(innerTask);
        context.after(innerTask).run(valueTask);
        return valueTask;
      }
    };

    final Task<String> task2 = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception
      {
        context.run(innerTask);
        return Promises.value("value2");
      }
    };

    Task<?> seq = seq(task1, task2);
    getEngine().run(seq, true);
    seq.await();

    assertEquals(Collections.singleton(Relationship.PARENT_OF), getRelationships(seq, seq, task1));
    assertEquals(Collections.singleton(Relationship.PARENT_OF), getRelationships(seq, task1, innerTask));
    assertEquals(Collections.singleton(Relationship.PARENT_OF), getRelationships(seq, seq, task2));
    assertEquals(Collections.singleton(Relationship.POTENTIAL_PARENT_OF), getRelationships(seq, task2, innerTask));
    assertShallowTraceMatches(seq, getTrace(seq));
    assertShallowTraceMatches(task1, getTrace(task1));
    assertShallowTraceMatches(task2, getTrace(task2));
  }

  @Test
  public void testTraceWithDiamond() throws InterruptedException
  {
    final Task<String> a = value("a", "valueA");
    final Task<String> b = value("b", "valueB");
    final Task<String> c = value("c", "valueC");
    final Task<String> d = value("d", "valueD");

    final Task<String> parent = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception
      {
        context.after(a).run(b);
        context.after(a).run(c);
        context.after(b, c).run(d);
        context.run(a);
        return d;
      }
    };

    getEngine().run(parent, true);
    parent.await();

    assertShallowTraceMatches(parent, getTrace(parent));
    assertShallowTraceMatches(a, getTrace(a));
    assertShallowTraceMatches(b, getTrace(b));
    assertShallowTraceMatches(c, getTrace(c));
    assertShallowTraceMatches(d, getTrace(d));

    assertEquals(Collections.singleton(Relationship.PARENT_OF), getRelationships(parent, parent, a));
    assertEquals(Collections.singleton(Relationship.PARENT_OF), getRelationships(parent, parent, b));
    assertEquals(Collections.singleton(Relationship.PARENT_OF), getRelationships(parent, parent, c));
    assertEquals(Collections.singleton(Relationship.PARENT_OF), getRelationships(parent, parent, d));

    assertEquals(Collections.singleton(Relationship.SUCCESSOR_OF), getRelationships(parent, d, b));
    assertEquals(Collections.singleton(Relationship.SUCCESSOR_OF), getRelationships(parent, b, a));
    assertEquals(Collections.singleton(Relationship.SUCCESSOR_OF), getRelationships(parent, d, c));
    assertEquals(Collections.singleton(Relationship.SUCCESSOR_OF), getRelationships(parent, c, a));
  }

  private void assertShallowTraceMatches(final Task<?> task, final ShallowTrace shallowTrace)
  {
    assertEquals(task.getName(), shallowTrace.getName());
    assertEquals(task.getShallowTrace().getResultType(), shallowTrace.getResultType());
    assertEquals(task.getShallowTrace().getStartNanos(), shallowTrace.getStartNanos());
    assertEquals(task.getShallowTrace().getEndNanos(), shallowTrace.getEndNanos());

    // Consistency checks w.r.t. timing
    if (shallowTrace.getEndNanos() != null)
    {
      assertLTE(shallowTrace.getEndNanos(), System.nanoTime());
      assertNotNull(shallowTrace.getStartNanos());
    }

    if (shallowTrace.getStartNanos() != null)
    {
      assertLTE(shallowTrace.getStartNanos(), System.nanoTime());
    }

    switch (task.getShallowTrace().getResultType())
    {
      case SUCCESS:
        Object value = task.get();
        assertEquals(value == null ? null : value.toString(), shallowTrace.getValue());
        break;
      case ERROR:
        assertTrue(shallowTrace.getValue().contains(task.getError().toString()));
        break;
      case UNFINISHED:
        assertNull(shallowTrace.getValue());
        break;
      case EARLY_FINISH:
        assertNull(shallowTrace.getValue());
        break;
    }
  }

  private ShallowTrace getTrace(Task<?> task)
  {
    final Trace trace = task.getTrace();
    return trace.getShallowTrace(findTraceIdByName(task.getName(), trace));
  }

  private Collection<Relationship> getRelationships(Task<?> planTask, Task<?> fromTask, Task<?> toTask)
  {
    final Trace trace = planTask.getTrace();
    return trace.getRelationships(
        findTraceIdByName(fromTask.getName(), trace),
        findTraceIdByName(toTask.getName(), trace));
  }

  private int findTraceIdByName(String name, Trace trace)
  {
    final Set<Integer> ids = new HashSet<Integer>();
    for (ShallowTraceEntry traceEntry : trace.traces())
    {
      if (traceEntry.getShallowTrace().getName().equals(name))
      {
        ids.add(traceEntry.getId());
      }
    }
    assertEquals(1, ids.size());
    return ids.iterator().next();
  }

  private <T extends Comparable<T>> void assertLTE(T lhs, T rhs)
  {
    assertTrue("Expected " + lhs + " <= " + rhs, lhs.compareTo(rhs) <= 0);
  }
}


