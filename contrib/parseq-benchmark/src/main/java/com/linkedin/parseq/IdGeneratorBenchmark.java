package com.linkedin.parseq;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.linkedin.parseq.internal.IdGenerator;


@State(Scope.Benchmark)
public class IdGeneratorBenchmark {


  @Benchmark
  public Object getNextId() {
      return IdGenerator.getNextId();
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder().include(".*" + IdGeneratorBenchmark.class.getSimpleName() + ".*").warmupIterations(3)
        .measurementIterations(5).threads(4).forks(1).build();

    new Runner(opt).run();
  }

}
