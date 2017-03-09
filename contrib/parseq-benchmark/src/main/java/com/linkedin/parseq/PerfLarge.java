/* $Id$ */
package com.linkedin.parseq;

import java.util.ArrayList;
import java.util.List;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.Tasks;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class PerfLarge extends AbstractBenchmark {

  public static void main(String[] args) throws Exception {
//    FullLoadBenchmarkConfig cfg = new FullLoadBenchmarkConfig();
    ConstantThroughputBenchmarkConfig cfg = new ConstantThroughputBenchmarkConfig();
    cfg.CONCURRENCY_LEVEL = Integer.MAX_VALUE;
    cfg.events = 1000;
    new PerfLarge().runExample(cfg);
  }

  @Override
  Task<?> createPlan() {
    List<Task<?>> l = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      l.add(task());
    }
    return Tasks.par(l);
  }

  private Task<?> task() {
    return Task.value("kldfjlajflskjflsjfslkajflkasj").map("length", s -> s.length()).map("+1", s -> s + 1)
        .map("+2", s -> s + 2).map("+3", s -> s + 3).shareable().recoverWith(t -> Task.value(0))
        .flatMap(x -> Task.value(x * 40)).map(x -> x -10);
  }

}
