/*
 * Copyright 2017 LinkedIn, Inc
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

import java.util.Optional;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.HdrHistogram.Histogram;

import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.trace.ShallowTrace;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public abstract class AbstractBenchmark {

  private final BatchingSupport _batchingSupport = new BatchingSupport();

  public void runExample(BenchmarkConfig config) throws Exception {
    final int numCores = Runtime.getRuntime().availableProcessors();
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(numCores + 1);
    final EngineBuilder builder = new EngineBuilder().setTaskExecutor(scheduler).setTimerScheduler(scheduler);
    builder.setPlanDeactivationListener(_batchingSupport);
    builder.setEngineProperty(Engine.MAX_CONCURRENT_PLANS, config.CONCURRENCY_LEVEL);
    final Engine engine = builder.build();
    try {
      doRunBenchmark(engine, config);
    } finally {
      engine.shutdown();
      scheduler.shutdownNow();
    }
  }

  abstract Task<?> createPlan();

  private int N(BenchmarkConfig config) {
    if (config instanceof FullLoadBenchmarkConfig) {
      FullLoadBenchmarkConfig cfg = (FullLoadBenchmarkConfig)config;
      return cfg.N;
    } else if (config instanceof ConstantThroughputBenchmarkConfig) {
      ConstantThroughputBenchmarkConfig cfg = (ConstantThroughputBenchmarkConfig)config;
      return (int) (cfg.runtime * cfg.events);
    } else {
      throw new IllegalArgumentException();
    }
  }

  protected void doRunBenchmark(final Engine engine, BenchmarkConfig config) throws Exception {

    final int N = N(config);

    final Histogram planHistogram = createHistogram();
    final Histogram taskHistogram = createHistogram();

    System.out.println("Number of cores: " + Runtime.getRuntime().availableProcessors());
    System.out.println("Configuration: " + config);

    Task<?> probe = createPlan();
    engine.run(probe);
    probe.await();

    final int numberOfTasks = probe.getTrace().getTraceMap().size();

    System.out.println("Number of tasks per plan: " + numberOfTasks);

    final Exchanger<Optional<Task<?>>> exchanger = new Exchanger<>();
    Thread histogramCollector = new Thread(() -> {
      try {
        Optional<Task<?>> t = exchanger.exchange(Optional.empty());
        while (t.isPresent()) {
          Task<?> task = t.get();
          task.await();
          recordCompletionTimes(planHistogram, taskHistogram, task);
          t = exchanger.exchange(Optional.empty());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    histogramCollector.start();

    Task<?> t = null;
    final Semaphore concurrentPlans = new Semaphore(Runtime.getRuntime().availableProcessors());
    System.out.println("Warming up using " + config.WARMUP_ROUNDS + " plan executions");
    for (int i = 0; i < config.WARMUP_ROUNDS; i++) {
      t = createPlan();
      t.addListener(p -> {
        concurrentPlans.release();
      });
      concurrentPlans.acquire();
      engine.blockingRun(t);
    }

    System.out.println("Starting test of " + N + " plan executions");
    Stepper percentage = new Stepper(0.01, N);
    Stepper sampler = new Stepper(1 / (N * config.sampleRate), N);

    long start = System.nanoTime();
    for (int i = 0; i < N; i++) {
      t = createPlan();

      config.runTask(engine, t);

      final Task<?> task = t;
      sampler.isNewStep(i).ifPresent(s -> {
        try {
          exchanger.exchange(Optional.of(task));
        } catch (Exception e) {
          e.printStackTrace();
        }
      });

      percentage.isNewStep(i).ifPresent(pct -> {
        System.out.println("progress: " + pct + "%");
      });
    }
    long end = System.nanoTime();

    exchanger.exchange(Optional.empty());
    histogramCollector.join();

    config.wrapUp();

    System.out.println("----------------------------------------------------------------");
    System.out.println("Histogram of task execution times on parseq threads in \u00B5s:");
    taskHistogram.outputPercentileDistribution(System.out, 1000.0);

    System.out.println("----------------------------------------------------------------");
    System.out.println("Histogram of plan completion times in \u00B5s:");
    planHistogram.outputPercentileDistribution(System.out, 1000.0);

    System.out.println("----------------------------------------------------------------");
    System.out.println("Throughput: " + String.format("%.3f", (N / ((double)(end - start) / 1000000000))) + " plans/s, " +
        String.format("%.3f", ((N * numberOfTasks) / ((double)(end - start) / 1000000000))) + " tasks/s");

  }

  private static Histogram createHistogram() {
    return new Histogram(1, 10000000000L, 3);
  }

  private void recordCompletionTimes(final Histogram planHistogram, Histogram taskHistogram, Task<?> task) {
    ShallowTrace st = task.getShallowTrace();
    planHistogram.recordValue(st.getEndNanos() - st.getStartNanos());
    task.getTrace().getTraceMap().values().forEach(shallowTrace -> {
      taskHistogram.recordValue(shallowTrace.getPendingNanos() - shallowTrace.getStartNanos());
    });
  }

  static class FullLoadBenchmarkConfig extends BenchmarkConfig {
    int N = 1000000;

    @Override
    public void runTask(Engine engine, Task<?> t) {
      engine.blockingRun(t);
    }

    @Override
    public String toString() {
      return "FullLoadBenchmarkConfig []";
    }

    @Override
    public void wrapUp() {
    }
  }

  static class ConstantThroughputBenchmarkConfig extends BenchmarkConfig {
    double events = 1000;
    TimeUnit perUnit = TimeUnit.SECONDS;
    long runtime = 3*60;
    final Histogram planExecutionAccuracy = createHistogram();

    EventsArrival arrivalProcess;

    private long lastNano = 0;
    @Override
    public void runTask(Engine engine, Task<?> t) throws InterruptedException {
      initArrivalProcess();
      if (lastNano == 0) {
        lastNano = System.nanoTime();
      }
      long nextNano = lastNano + arrivalProcess.nanosToNextEvent();
      long actualNano = waitUntil(nextNano);
      planExecutionAccuracy.recordValue(Math.abs(actualNano - nextNano));
      engine.run(t);
      lastNano = nextNano;
    }

    private void initArrivalProcess() {
      if (arrivalProcess == null) {
        arrivalProcess = new PoissonEventsArrival(events, perUnit);
      }
    }

    @Override
    public String toString() {
      initArrivalProcess();
      return "ConstantThroughputBenchmarkConfig [throughput=" + events + "/" + perUnit + ", runtime=" + runtime
          + " " + perUnit + ", arrivalProcess=" + arrivalProcess + "], " + super.toString();
    }

    @Override
    public void wrapUp() {
      System.out.println("----------------------------------------------------------------");
      System.out.println("Histogram of benchmark execution plan accuracy in \u00B5s:");
      planExecutionAccuracy.outputPercentileDistribution(System.out, 1000.0);
    }
  }

  abstract static class BenchmarkConfig {
    int WARMUP_ROUNDS = 10000;
    int CONCURRENCY_LEVEL = Runtime.getRuntime().availableProcessors() / 2 + 1;
    double sampleRate = 0.001;

    abstract public void runTask(Engine engine, Task<?> t) throws InterruptedException;

    abstract public void wrapUp();

    @Override
    public String toString() {
      return "BenchmarkConfig [WARMUP_ROUNDS=" + WARMUP_ROUNDS + ", CONCURRENCY_LEVEL=" + CONCURRENCY_LEVEL
          + ", sampleRate=" + sampleRate + "]";
    }


  }

  private static long waitUntil(long nextNano) throws InterruptedException {
    long current = System.nanoTime();
    if ((nextNano - current) > 0) {
      return waitNano(nextNano, current);
    } else {
      return current;
    }
  }

  private static long waitNano(long nextNano, long current) throws InterruptedException {
    long waitTime = nextNano - current;
    long millis = (waitTime >> 20) - 1;  //2^20ns = 1048576ns ~ 1ms
    if (millis < 0) {
      millis = 0;
    }
    if (millis > 0) {
      Thread.sleep(millis);
      return waitUntil(nextNano);
    } else {
      return busyWaitUntil(nextNano);
    }
  }

  private static long busyWaitUntil(long nextNano) {
    long counter = 0L;
    while (true) {
      counter += 1;
      if (counter % 1000 == 0) {
        long current = System.nanoTime();
        if (current - nextNano >= 0) {
          return current;
        }
      }
    }
  }

  private static class Stepper {
    private final double countPerStep;
    private int currentStep  = 0;

    public Stepper(double fractionPerStep, int N) {
      countPerStep = ((double)N) * fractionPerStep;
    }

    Optional<Integer> isNewStep(int i) {
      int step = (int) (i / countPerStep);
      if (currentStep != step) {
        currentStep = step;
        return Optional.of(step);
      } else {
        return Optional.empty();
      }
    }
  }

}
