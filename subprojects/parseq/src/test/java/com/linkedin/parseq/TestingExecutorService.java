package com.linkedin.parseq;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;


class TestingExecutorService implements ExecutorService {

  private final ExecutorService _delegate;
  private AtomicLong _counter = new AtomicLong();

  public TestingExecutorService(ExecutorService delegate) {
    _delegate = delegate;
  }

  public long getCount() {
    return _counter.get();
  }

  public void execute(Runnable command) {
    _counter.incrementAndGet();
    _delegate.execute(command);
  }

  public void shutdown() {
    _delegate.shutdown();
  }

  public List<Runnable> shutdownNow() {
    return _delegate.shutdownNow();
  }

  public boolean isShutdown() {
    return _delegate.isShutdown();
  }

  public boolean isTerminated() {
    return _delegate.isTerminated();
  }

  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return _delegate.awaitTermination(timeout, unit);
  }

  public <T> Future<T> submit(Callable<T> task) {
    _counter.incrementAndGet();
    return _delegate.submit(task);
  }

  public <T> Future<T> submit(Runnable task, T result) {
    _counter.incrementAndGet();
    return _delegate.submit(task, result);
  }

  public Future<?> submit(Runnable task) {
    _counter.incrementAndGet();
    return _delegate.submit(task);
  }

  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    _counter.getAndAdd(tasks.size());
    return _delegate.invokeAll(tasks);
  }

  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException {
    _counter.getAndAdd(tasks.size());
    return _delegate.invokeAll(tasks, timeout, unit);
  }

  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    _counter.getAndAdd(tasks.size());
    return _delegate.invokeAny(tasks);
  }

  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    _counter.getAndAdd(tasks.size());
    return _delegate.invokeAny(tasks, timeout, unit);
  }

}
