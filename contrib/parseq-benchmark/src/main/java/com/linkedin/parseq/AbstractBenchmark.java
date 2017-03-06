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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.HdrHistogram.Base64CompressedHistogramSerializer;
import org.HdrHistogram.Histogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.batching.BatchingSupport;
import com.linkedin.parseq.trace.ShallowTrace;


/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public abstract class AbstractBenchmark {

  public static final String BENCHMARK_TEST_RESULTS_LOG_PREFIX = "Benchmark test results -> ";

  private static final Logger LOG = LoggerFactory.getLogger(AbstractBenchmark.class);

  private final BatchingSupport _batchingSupport = new BatchingSupport();
  private static final HistogramSerializer _histogramSerializer = new Base64CompressedHistogramSerializer();

  private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
  private final ConcurrentLinkedQueue<Thread> _parseqThreads = new ConcurrentLinkedQueue<>();
  private final Map<Long, Long> threadCPU = new HashMap<>();
  private final Map<Long, Long> threadUserCPU = new HashMap<>();

  public void runExample(BenchmarkConfig config) throws Exception {
    final int numCores = Runtime.getRuntime().availableProcessors();
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(numCores - 1,
        new ThreadFactory() {

          @Override
          public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            _parseqThreads.add(t);
            return t;
          }
        });
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

  private int warmUpN(BenchmarkConfig config) {
    if (config instanceof FullLoadBenchmarkConfig) {
      FullLoadBenchmarkConfig cfg = (FullLoadBenchmarkConfig)config;
      return cfg.WARMUP_ROUNDS;
    } else if (config instanceof ConstantThroughputBenchmarkConfig) {
      ConstantThroughputBenchmarkConfig cfg = (ConstantThroughputBenchmarkConfig)config;
      return (int) (cfg.warmupRime * cfg.events);
    } else {
      throw new IllegalArgumentException();
    }
  }

  protected void doRunBenchmark(final Engine engine, BenchmarkConfig config) throws Exception {

    final int N = N(config);
    final int warmUpN = warmUpN(config);

    final Histogram planHistogram = createHistogram();
    final Histogram taskHistogram = createHistogram();

    LOG.info("Number of cores: " + Runtime.getRuntime().availableProcessors());
    LOG.info("Configuration: " + config);

    Task<?> probe = createPlan();
    engine.run(probe);
    probe.await();

    final int numberOfTasks = probe.getTrace().getTraceMap().size();

    LOG.info("Number of tasks per plan: " + numberOfTasks);

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
    LOG.info("Warming up using " + warmUpN + " plan execution");
    System.out.print("Progress[");
    Stepper warmUpPercentage = new Stepper(0.1, warmUpN);
    for (int i = 0; i < warmUpN; i++) {
      t = createPlan();
      config.runTask(engine, t);
      warmUpPercentage.isNewStep(i).ifPresent(pct -> {
        System.out.print(".");
      });
    }
    System.out.println(".]");

    grabCPUTimesBeforeTest();

    LOG.info("Starting test of " + N + " plan executions");
    System.out.print("Progress[");
    Stepper percentage = new Stepper(0.1, N);
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
        System.out.print(".");
      });
    }
    long end = System.nanoTime();
    System.out.println(".]");

    grabCPUTimesAfterTest();

    exchanger.exchange(Optional.empty());
    histogramCollector.join();

    config.wrapUp();

    LOG.info("----------------------------------------------------------------");
    LOG.info("Histogram of task execution times on parseq threads in \u00B5s:");
    taskHistogram.outputPercentileDistribution(System.out, 1000.0);
    LOG.info(BENCHMARK_TEST_RESULTS_LOG_PREFIX + "Histogram of task execution times on parseq threads in \u00B5s: " +
        _histogramSerializer.serlialize(taskHistogram));


    LOG.info("----------------------------------------------------------------");
    LOG.info("Histogram of plan completion times in \u00B5s:");
    planHistogram.outputPercentileDistribution(System.out, 1000.0);
    LOG.info(BENCHMARK_TEST_RESULTS_LOG_PREFIX + "Histogram of plan completion times in \u00B5s: " +
        _histogramSerializer.serlialize(planHistogram));

    LOG.info("----------------------------------------------------------------");
    LOG.info("Throughput: " + String.format("%.3f", (N / ((double)(end - start) / 1000000000))) + " plans/s, " +
        String.format("%.3f", ((N * numberOfTasks) / ((double)(end - start) / 1000000000))) + " tasks/s");

  }

  private void grabCPUTimesBeforeTest() {
    final boolean threadCPUTimeSupported = threadBean.isThreadCpuTimeSupported();
    LOG.info("Thread CPU time measurment supported: " + threadCPUTimeSupported);
    if (threadCPUTimeSupported) {
      threadBean.setThreadCpuTimeEnabled(true);
    }

    //grab CPU times before test
    for (Thread thread: _parseqThreads) {
      long threadId = thread.getId();
      long cpuTime = threadBean.getThreadCpuTime(threadId);
      if (cpuTime > -1) {
        threadCPU.put(threadId, cpuTime);
      }
      long cpuUserTime = threadBean.getThreadUserTime(threadId);
      if (cpuUserTime > -1) {
        threadUserCPU.put(threadId, cpuUserTime);
      }
    }
  }

  private long addTime(Map<Long, Long> before, long time, long total, long threadId, String name) {
    long beforeTime = before.get(threadId);
    if (beforeTime == -1) {
      if (time > -1) {
        LOG.warn(name + " time could not be captured before test but was captured after the test - bailing out...");
      } //else CPU time measuring not supported
    } else {
      if (time > -1) {
        if (time < beforeTime) {
          LOG.warn(name + " Time captured before test is greater than the one captured after the test - bailing out...");
        } else {
          //happy path
          total += time - beforeTime;
        }
      } else {
        LOG.warn(name + " Time could be captured before test but was not captured after the test - bailing out...");
      }
    }
    return total;
  }

  private void grabCPUTimesAfterTest() {
    long totalCPUTime = 0;
    long totalUserTime = 0;
    for (Thread thread: _parseqThreads) {
      long threadId = thread.getId();
      long cpuTime = threadBean.getThreadCpuTime(threadId);
      long cpuUserTime = threadBean.getThreadUserTime(threadId);
      if (!threadCPU.containsKey(threadId)) {
        LOG.warn("New ParSeq thread was added during test");
      } else {
        totalCPUTime = addTime(threadCPU, cpuTime, totalCPUTime, threadId, "CPU");
        totalUserTime = addTime(threadUserCPU, cpuUserTime, totalUserTime, threadId, "User");
      }
    }
    if (totalCPUTime > 0) {
      LOG.info(BENCHMARK_TEST_RESULTS_LOG_PREFIX + "Total CPU time in ms: " + totalCPUTime / 1000000);
      LOG.info(BENCHMARK_TEST_RESULTS_LOG_PREFIX + "Total CPU User time in ms: " + totalUserTime / 1000000);
    }
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
    int WARMUP_ROUNDS = 100000;

    int N = 1000000;

    @Override
    public void runTask(Engine engine, Task<?> t) {
      engine.blockingRun(t);
    }

    @Override
    public String toString() {
      return "FullLoadBenchmarkConfig [WARMUP_ROUNDS=" + WARMUP_ROUNDS + ", ROUNDS=" + N +"]";
    }

    @Override
    public void wrapUp() {
    }
  }

  static class ConstantThroughputBenchmarkConfig extends BenchmarkConfig {
    long warmupRime = 2*60;

    double events = 1000;
    TimeUnit perUnit = TimeUnit.SECONDS;
    long runtime = 6*60;
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
      return "ConstantThroughputBenchmarkConfig [throughput=" + events + "/" + perUnit + ", warmup=" + warmupRime + " "
          + perUnit + ", runtime=" + runtime + " " + perUnit + ", arrivalProcess=" + arrivalProcess + "], "
          + super.toString();
    }

    @Override
    public void wrapUp() {
      LOG.info("----------------------------------------------------------------");
      LOG.info("Histogram of benchmark execution plan accuracy in \u00B5s:");
      planExecutionAccuracy.outputPercentileDistribution(System.out, 1000.0);
      LOG.info(BENCHMARK_TEST_RESULTS_LOG_PREFIX + "Histogram of benchmark execution plan accuracy in \u00B5s: " +
          _histogramSerializer.serlialize(planExecutionAccuracy));
    }
  }

  abstract static class BenchmarkConfig {
    int CONCURRENCY_LEVEL = Runtime.getRuntime().availableProcessors() / 2 + 1;
    double sampleRate = 0.001;

    abstract public void runTask(Engine engine, Task<?> t) throws InterruptedException;

    abstract public void wrapUp();

    @Override
    public String toString() {
      return "BenchmarkConfig [CONCURRENCY_LEVEL=" + CONCURRENCY_LEVEL
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
