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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static com.linkedin.parseq.Tasks.par;
import static com.linkedin.parseq.Tasks.seq;
import static com.linkedin.parseq.TestUtil.value;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

/**
 * @author Chris Pettitt
 * @author Chi Chan
 */
public class TestTaskToTrace extends BaseEngineTest
{
  @Test
  public void testUnstartedTrace()
  {
    final Task<?> task = value("taskName", "value");

    // We don't run the task

    final Trace trace = task.getTrace();
    assertShallowTraceMatches(task, trace);
  }

  @Test
  public void testSuccessfulTrace() throws InterruptedException
  {
    final Task<String> task = value("taskName", "value");

    getEngine().run(task);
    task.await();

    final Trace trace = task.getTrace();
    assertShallowTraceMatches(task, trace);
  }

  @Test
  public void testSuccessfulTraceWithNullValue() throws InterruptedException
  {
    final Task<String> task = value("taskName", null);

    getEngine().run(task);
    task.await();

    final Trace trace = task.getTrace();
    assertShallowTraceMatches(task, trace);
  }

  @Test
  public void testErrorTrace() throws InterruptedException
  {
    final Task<?> task = TestUtil.errorTask("taskName", new Exception("error message"));

    getEngine().run(task);
    task.await();

    final Trace trace = task.getTrace();
    assertShallowTraceMatches(task, trace);
  }

  @Test
  public void testSeqSystemHiddenTrace() throws InterruptedException
  {
    final Task<String> task1 = value("taskName1", "value");
    final Task<String> task2 = value("taskName2", "value2");


    final Task<?> seq1 = seq(task1, task2);
    final Task<?> seq2 = seq(seq1);
    getEngine().run(seq1);
    seq1.await();

    assertTrue(seq1.getTrace().getSystemHidden());
    assertTrue(seq2.getTrace().getSystemHidden());
  }

  @Test
  public void testParSystemHiddenTrace() throws InterruptedException
  {
    final Task<String> task1 = value("taskName1", "value");
    final Task<String> task2 = value("taskName2", "value2");


    final Task<?> par1 = par(task1, task2);
    final Task<?> par2 = par(par1);
    getEngine().run(par1);
    par1.await();

    assertTrue(par1.getTrace().getSystemHidden());
    assertTrue(par2.getTrace().getSystemHidden());
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

    assertFalse(par1.getTrace().getHidden());
    assertFalse(task1.getTrace().getHidden());
    assertFalse(task1.getTrace().getHidden());
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
        ShallowTrace shallowTrace = super.getShallowTrace();
        ShallowTraceBuilder builder = new ShallowTraceBuilder(shallowTrace);
        builder.setHidden(true);
        return builder.build();
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
        ShallowTrace shallowTrace = super.getShallowTrace();
        ShallowTraceBuilder builder = new ShallowTraceBuilder(shallowTrace);
        builder.setHidden(true);
        return builder.build();
      }
    };

    final Task<?> par1 = par(task1, task2);
    getEngine().run(par1);
    par1.await();

    assertFalse(par1.getTrace().getHidden());
    assertTrue(task1.getTrace().getHidden());
    assertTrue(task1.getTrace().getHidden());
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

    final Trace trace = task.getTrace();
    assertShallowTraceMatches(task, trace);

    // Finish task
    promise.done(null);
  }

  @Test
  public void testTraceWithPredecessorTrace() throws InterruptedException
  {
    final Task<String> predecessor = value("predecessor", "predecessorValue");
    final Task<String> successor = value("successor", "successorValue");

    final Task<?> seq = seq(predecessor, successor);
    getEngine().run(seq);
    seq.await();

    final Trace sucTrace = successor.getTrace();
    assertShallowTraceMatches(successor, sucTrace);

    final Trace predTrace = predecessor.getTrace();
    assertShallowTraceMatches(predecessor, predTrace);

    final Set<Related<Trace>> related = sucTrace.getRelated();
    assertEquals(1,related.size());
    assertEquals(new Related<Trace>(Relationship.SUCCESSOR_OF, predTrace),
            sucTrace.getRelated().iterator().next());
  }

  @Test
  public void testTraceWithSuccessChild() throws InterruptedException
  {
    final Task<String> task = value("taskName", "value");

    final Task<?> seq = seq(task);
    getEngine().run(seq);
    seq.await();

    final Trace taskTrace = task.getTrace();
    assertShallowTraceMatches(task, taskTrace);

    final Trace seqTrace = seq.getTrace();
    assertShallowTraceMatches(seq, seqTrace);

    final Set<Related<Trace>> related = seqTrace.getRelated();
    assertEquals(1, related.size());
    assertEquals(new Related<Trace>(Relationship.PARENT_OF, taskTrace),
                 related.iterator().next());
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

    getEngine().run(task);
    task.await();

    assertTrue(task.getTrace().getRelated().iterator().hasNext());
    Related<Trace> traceRelated = task.getTrace().getRelated().iterator().next();
    assertShallowTraceMatches(task, task.getTrace());
    assertShallowTraceMatches(innerTask, innerTask.getTrace());
    assertEquals(innerTask.getTrace(), traceRelated.getRelated());
    assertEquals(Relationship.PARENT_OF.name(), traceRelated.getRelationship());
    assertEquals(ResultType.EARLY_FINISH, innerTask.getTrace().getResultType());
  }

  @Test
  public void testTraceIsAddedBeforeAwaitCompletes() throws InterruptedException
  {
    for (int i = 0 ;i < 100; i++)
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

      getEngine().run(task);
      task.await();

      assertTrue(task.getTrace().getRelated().iterator().hasNext());
    }
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
        context.run(innerTask);
        return Promises.value("value1");
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

    Task<?> par = par(task1, task2);
    getEngine().run(par);
    par.await();

    Set<Trace> tracesWithParent = new HashSet<Trace>();
    Map<Trace, Integer> traceWithPotentialParent = new HashMap<Trace, Integer>();
    assertAndFindParent(par.getTrace(), tracesWithParent, traceWithPotentialParent);
    assertEquals(3, tracesWithParent.size());
    assertEquals((Integer)1, traceWithPotentialParent.get(innerTask.getTrace()));
    assertEquals(1, traceWithPotentialParent.size());
    assertTrue(tracesWithParent.contains(task1.getTrace()));
    assertTrue(tracesWithParent.contains(task2.getTrace()));
    assertTrue(tracesWithParent.contains(innerTask.getTrace()));
    assertShallowTraceMatches(task1, task1.getTrace());
    assertShallowTraceMatches(task2, task2.getTrace());
    assertShallowTraceMatches(innerTask, innerTask.getTrace());
    assertEquals(ResultType.EARLY_FINISH, innerTask.getTrace().getResultType());
  }

  @Test
  public void testTraceWithMultiplePotentialParentAndParent() throws InterruptedException
  {
    final SettablePromise<String> promise1 = Promises.settable();

    final Task<String> innerTask = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception
      {
        promise1.done("inner");
        return promise1;
      }
    };

    final Task<String> task1 = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(final Context context) throws Exception
      {
        context.run(innerTask);
        return Promises.value("value1");
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

    final Task<String> task3 = new BaseTask<String>()
    {
      @Override
      protected Promise<? extends String> run(Context context) throws Exception
      {
        context.run(innerTask);

        return Promises.value("value3");
      }
    };

    Task<?> seq = seq(task1, task2, task3);
    getEngine().run(seq);
    seq.await();

    Set<Trace> tracesWithParent = new HashSet<Trace>();
    Map<Trace, Integer> traceWithPotentialParent = new HashMap<Trace, Integer>();
    assertAndFindParent(seq.getTrace(), tracesWithParent, traceWithPotentialParent);
    assertEquals(4, tracesWithParent.size());
    assertEquals(1, traceWithPotentialParent.size());
    assertEquals((Integer) 2, traceWithPotentialParent.get(innerTask.getTrace()));
    assertTrue(tracesWithParent.contains(task1.getTrace()));
    assertTrue(tracesWithParent.contains(task2.getTrace()));
    assertTrue(tracesWithParent.contains(task3.getTrace()));
    assertTrue(tracesWithParent.contains(innerTask.getTrace()));
    assertShallowTraceMatches(task1, task1.getTrace());
    assertShallowTraceMatches(task2, task2.getTrace());
    assertShallowTraceMatches(task3, task3.getTrace());
    assertShallowTraceMatches(innerTask, innerTask.getTrace());
  }

  @Test
  public void testTraceWithDiamond() throws InterruptedException
  {
    final Task<String> a = value("valueA");
    final Task<String> b = value("valueB");
    final Task<String> c = value("valueC");
    final Task<String> d = value("valueD");

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

    getEngine().run(parent);
    parent.await();

    assertShallowTraceMatches(parent, parent.getTrace());
    assertShallowTraceMatches(a, a.getTrace());
    assertShallowTraceMatches(b, b.getTrace());
    assertShallowTraceMatches(c, c.getTrace());
    assertShallowTraceMatches(d, d.getTrace());

    assertTrue(parent.getTrace().getRelated().contains(new Related<Trace>(Relationship.PARENT_OF, a.getTrace())));
    assertTrue(parent.getTrace().getRelated().contains(new Related<Trace>(Relationship.PARENT_OF, b.getTrace())));
    assertTrue(parent.getTrace().getRelated().contains(new Related<Trace>(Relationship.PARENT_OF, c.getTrace())));
    assertTrue(parent.getTrace().getRelated().contains(new Related<Trace>(Relationship.PARENT_OF, d.getTrace())));

    assertTrue(d.getTrace().getRelated().contains(new Related<Trace>(Relationship.SUCCESSOR_OF, b.getTrace())));
    assertTrue(d.getTrace().getRelated().contains(new Related<Trace>(Relationship.SUCCESSOR_OF, c.getTrace())));
    assertTrue(b.getTrace().getRelated().contains(new Related<Trace>(Relationship.SUCCESSOR_OF, a.getTrace())));
    assertTrue(c.getTrace().getRelated().contains(new Related<Trace>(Relationship.SUCCESSOR_OF, a.getTrace())));
  }

  private void assertAndFindParent(Trace trace, Set<Trace> tracesWithParent, Map<Trace, Integer> traceWithPotentialParent)
  {
    for(Related<Trace> relatedTrace : trace.getRelated())
    {
      if (relatedTrace.getRelationship().equals(Relationship.PARENT_OF.name()))
      {
        assertFalse(tracesWithParent.contains(relatedTrace.getRelated()));
        tracesWithParent.add(relatedTrace.getRelated());
        assertAndFindParent(relatedTrace.getRelated(), tracesWithParent, traceWithPotentialParent);
      }
      else if (relatedTrace.getRelationship().equals(Relationship.POTENTIAL_PARENT_OF.name()))
      {
        if (!traceWithPotentialParent.containsKey(relatedTrace.getRelated()))
        {
          traceWithPotentialParent.put(relatedTrace.getRelated(), 0);
        }
        traceWithPotentialParent.put(relatedTrace.getRelated(), traceWithPotentialParent.get(relatedTrace.getRelated()) + 1);
        assertAndFindParent(relatedTrace.getRelated(), tracesWithParent, traceWithPotentialParent);
      }
    }
  }

  private void assertShallowTraceMatches(final Task<?> task, final Trace trace)
  {
    assertEquals(task.getName(), trace.getName());
    assertEquals(ResultType.fromTask(task), trace.getResultType());
    assertEquals(task.getShallowTrace().getStartNanos(), trace.getStartNanos());

    // If the task has not been started then we expect the endNanos to be null.
    // If the task has started but has not been finished then endNanos is set
    // to the time that the trace was taken. If the task was finished then the
    // task end time and trace end time should match.
    if (trace.getResultType().equals(ResultType.UNFINISHED))
    {
      if (trace.getStartNanos() == null)
      {
        assertNull(trace.getEndNanos());
      }
      else
      {
        // Trace will have the end time set to the time the trace was taken
        assertTrue(trace.getEndNanos() <= System.nanoTime());

        // We assume that the end time will always be at least one nanosecond
        // greater than the start time.
        assertTrue(trace.getEndNanos() > trace.getStartNanos());
      }
    }
    else
    {
      assertEquals(task.getShallowTrace().getEndNanos(), trace.getEndNanos());
    }

    switch (ResultType.fromTask(task))
    {
      case SUCCESS:
        Object value = task.get();
        assertEquals(value == null ? null : value.toString(), trace.getValue());
        break;
      case ERROR:
        assertTrue(trace.getValue().contains(task.getError().toString()));
        break;
      case UNFINISHED:
        assertNull(trace.getValue());
        break;
      case EARLY_FINISH:
        assertNull(trace.getValue());
        break;
    }
  }
}


