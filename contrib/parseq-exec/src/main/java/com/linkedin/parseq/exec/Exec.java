package com.linkedin.parseq.exec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.Exceptions;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

public class Exec {

  private static final Logger LOGGER = LoggerFactory.getLogger(Exec.class);

  private final ConcurrentMap<Process, ProcessEntry> _runningProcesses = new ConcurrentHashMap<>();
  private final ConcurrentMap<Long, Process> _runningProcessesByTaskId = new ConcurrentHashMap<>();
  private final ScheduledExecutorService _reaperExecutor = Executors.newSingleThreadScheduledExecutor();
  private final AtomicLong _seqGenerator = new AtomicLong(0);
  private final ConcurrentSkipListSet<ProcessRequest> _processRequestQueue =
      new ConcurrentSkipListSet<>(Comparator.comparingLong(request -> request.getSeq()));

  private final int _parallelizationLevel;
  private final long _reaperDelayMs;
  private final int _maxProcessQueueSize;
  private final AtomicInteger _processQueueSize = new AtomicInteger(0);

  private volatile boolean _shutdownInitiated = false;

  public Exec(int parallelizationLevel, long reaperDelayMs, int maxProcessQueueSize) {
    _parallelizationLevel = parallelizationLevel;
    _reaperDelayMs = reaperDelayMs;
    _maxProcessQueueSize = maxProcessQueueSize;
  }

  public static class Result {
    private final Path _stdout;
    private final Path _stderr;
    private final int status;

    public Result(int status, Path stdout, Path stderr) {
      this.status = status;
      _stdout = stdout;
      _stderr = stderr;
    }

    public Path getStdout() {
      return _stdout;
    }

    public Path getStderr() {
      return _stderr;
    }

    public int getStatus() {
      return status;
    }
  }

  private static class ProcessEntry {
    private final SettablePromise<Result> _resultPromise;
    private final Path _stdout;
    private final Path _stderr;
    private final Long _taskId;

    public ProcessEntry(SettablePromise<Result> resultPromise, Path stdout, Path stderr, Long taskId) {
      _resultPromise = resultPromise;
      _stderr = stderr;
      _stdout = stdout;
      _taskId = taskId;
    }

    public SettablePromise<Result> getResultPromise() {
      return _resultPromise;
    }

    public Path getStdout() {
      return _stdout;
    }

    public Path getStderr() {
      return _stderr;
    }

    public Long getTaskId() {
      return _taskId;
    }
  }

  private static class ProcessRequest {
    private final long _seq;
    private final ProcessBuilder _builder;
    private final ProcessEntry _entry;
    private final long _timeout;
    private final TimeUnit _timeUnit;
    private final Long _taskId;

    public ProcessRequest(long seq, ProcessBuilder builder, ProcessEntry entry, final long timeout, final TimeUnit timeUnit, Long taskId) {
      _seq = seq;
      _builder = builder;
      _entry = entry;
      _timeout = timeout;
      _timeUnit = timeUnit;
      _taskId = taskId;
    }

    public long getSeq() {
      return _seq;
    }

    public ProcessBuilder getBuilder() {
      return _builder;
    }

    public ProcessEntry getEntry() {
      return _entry;
    }

    public long getTimeout() {
      return _timeout;
    }

    public TimeUnit getTimeUnit() {
      return _timeUnit;
    }

    public Long getTaskId() {
      return _taskId;
    }
  }

  public Task<Result> command(final String desc, final long timeout, final TimeUnit timeUnit, final String... command) {
    final Task<Result> task = Task.async(desc, ctx -> {
      int queueSize = _processQueueSize.get();
      if (_shutdownInitiated) {
        throw new IllegalStateException("can't start new process because Exec has been shut down");
      } else if (queueSize  >= _maxProcessQueueSize) {
        throw new RuntimeException("queue for processes to run is full, size: " + queueSize);
      } else {
        final SettablePromise<Result> result = Promises.settable();
        final ProcessBuilder builder = new ProcessBuilder(command);
        final Path stderr = Files.createTempFile("parseq-Exec", ".stderr");
        final Path stdout = Files.createTempFile("parseq-Exec", ".stdout");
        builder.redirectError(stderr.toFile());
        builder.redirectOutput(stdout.toFile());
        final ProcessRequest request = new ProcessRequest(_seqGenerator.getAndIncrement(), builder,
            new ProcessEntry(result, stdout, stderr, ctx.getTaskId()), timeout, timeUnit, ctx.getTaskId());
        _processRequestQueue.add(request);
        _processQueueSize.incrementAndGet();
        return result;
      }
    }, false);
    task.addListener(p -> {
      if (p.isFailed() && Exceptions.isCancellation(p.getError())) {
        //best effort to try to kill process in case task was cancelled
        Process process = _runningProcessesByTaskId.get(task.getId());
        if (process != null) {
          process.destroyForcibly();
        }
      }
    });
    return task;
  }

  public void start() {
    _reaperExecutor.scheduleWithFixedDelay(() -> {
      try {
        for (Entry<Process, ProcessEntry> en: _runningProcesses.entrySet()) {
          final Process process = en.getKey();
          final ProcessEntry entry = en.getValue();
          if (!process.isAlive()) {
            _runningProcesses.remove(process);
            _runningProcessesByTaskId.remove(entry.getTaskId());
            final Result result = new Result(process.exitValue(), entry.getStdout(), entry.getStderr());
            entry.getResultPromise().done(result);
          }
        }
        while (_runningProcesses.size() < _parallelizationLevel && !_processRequestQueue.isEmpty()) {
          ProcessRequest request = _processRequestQueue.pollFirst();
          if (request != null) {
            _processQueueSize.decrementAndGet();
            final Process process = request.getBuilder().start();
            _runningProcesses.put(process, request.getEntry());
            _runningProcessesByTaskId.put(request.getTaskId(), process);
            _reaperExecutor.schedule(() -> {
              if (process.isAlive()) {
                process.destroyForcibly();
              }
            }, request.getTimeout(), request.getTimeUnit());
          }
        }
      } catch (Exception e) {
        LOGGER.error("error while checking process status", e);
      }
    }, _reaperDelayMs, _reaperDelayMs, TimeUnit.MILLISECONDS);
  }

  public void stop() {
    _shutdownInitiated = true;
    _reaperExecutor.shutdown();
  }


}
