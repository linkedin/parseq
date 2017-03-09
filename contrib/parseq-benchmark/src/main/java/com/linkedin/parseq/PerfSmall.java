/* $Id$ */
package com.linkedin.parseq;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class PerfSmall extends AbstractBenchmark {

  public static void main(String[] args) throws Exception {
    FullLoadBenchmarkConfig cfg = new FullLoadBenchmarkConfig();
    cfg.N = 10000000;
    cfg.WARMUP_ROUNDS = 100000;
    new PerfSmall().runExample(cfg);
  }

  @Override
  Task<?> createPlan() {
    return Task.value("kldfjlajflskjflsjfslkajflkasj").map("length", s -> s.length()).map("+1", s -> s + 1)
        .map("+2", s -> s + 2).map("+3", s -> s + 3);
  }

}
