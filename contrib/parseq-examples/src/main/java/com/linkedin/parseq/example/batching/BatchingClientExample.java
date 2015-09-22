/* $Id$ */
package com.linkedin.parseq.example.batching;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.linkedin.parseq.Engine;
import com.linkedin.parseq.PlanActivityListener;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.common.AbstractExample;
import com.linkedin.parseq.example.common.ExampleUtil;
import com.linkedin.parseq.function.Tuple2;
import com.linkedin.parseq.function.Tuples;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class BatchingClientExample extends AbstractExample {

  ConcurrentHashMap<Long, List<Tuple2<Long, SettablePromise<String>>>> batches = new ConcurrentHashMap<>();

  // simple implementation of a 'batchable' task
  Task<String> batchableTask(final Long id) {
    return Task.async("fetch id: " + id, ctx -> {

      // get plan id of the context in which task is being executed
      Long planId = ctx.getPlanId();

      // aggregate all ids and promises and return immediately
      batches.putIfAbsent(planId, new ArrayList<>());
      SettablePromise<String> p = Promises.settable();
      batches.get(planId).add(Tuples.tuple(id, p));

      return p;
    });
  }

  // plan activity listener will run computation for batch of ids
  // when plan gets deactivated and (in real world asynchronously,
  // but here for simplicity synchronously) complete adequate promises
  PlanActivityListener planListener = new PlanActivityListener() {

    @Override
    public void onPlanDeactivated(Long planId) {
      List<Tuple2<Long, SettablePromise<String>>> batch = batches.remove(planId);
      if (batch != null) {
        //execute batch
        System.out.println("batch: " + batch);

        // asynchronously complete associated promises
        // here, do it synchronously for simplicity
        batch.forEach(t -> t._2().done("value for id + " + t._1()));
      }
    }

    @Override
    public void onPlanActivated(Long planId) {
    }
  };

  @Override
  protected void customizeEngine(com.linkedin.parseq.EngineBuilder engineBuilder) {
    engineBuilder.setPlanActivityListener(planListener);
  };


  public static void main(String[] args) throws Exception {
    new BatchingClientExample().runExample();
  }

  @Override
  protected void doRunExample(final Engine engine) throws Exception {
    final Task<String> a = batchableTask(1L).flatMap(x -> batchableTask(2L)).flatMap(x -> batchableTask(3L));
    final Task<String> b = batchableTask(4L).flatMap(x -> batchableTask(5L)).flatMap(x -> batchableTask(6L));
    final Task<String> c = batchableTask(7L).flatMap(x -> batchableTask(8L)).flatMap(x -> batchableTask(9L));

    final Task<?> parFetch = Task.par(a, b, c);
    engine.run(parFetch);

    parFetch.await();

    ExampleUtil.printTracingResults(parFetch);
  }
}
