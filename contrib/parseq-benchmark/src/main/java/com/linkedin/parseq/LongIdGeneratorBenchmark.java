package com.linkedin.parseq;

import java.util.concurrent.atomic.AtomicLong;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


@State(Scope.Benchmark)
public class LongIdGeneratorBenchmark {


  private final AtomicLong _idGenerator = new AtomicLong(0);

  @Benchmark
  public Object getNextId() {
      return Long.valueOf(_idGenerator.getAndIncrement());
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder().include(".*" + LongIdGeneratorBenchmark.class.getSimpleName() + ".*").warmupIterations(3)
        .measurementIterations(5).threads(4).forks(1).build();

    new Runner(opt).run();
  }

}
