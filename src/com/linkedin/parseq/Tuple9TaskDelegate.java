package com.linkedin.parseq;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.linkedin.parseq.function.Tuple9;
import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.promise.PromiseException;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.PromiseUnresolvedException;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.Trace;

public class Tuple9TaskDelegate<T1, T2, T3, T4, T5, T6, T7, T8, T9> implements Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> {

  private final Task<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> _task;

  public Tuple9TaskDelegate(Task<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> task) {
    _task = task;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean cancel(Exception reason) {
    return _task.cancel(reason);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> get() throws PromiseException {
    return _task.get();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Throwable getError() throws PromiseUnresolvedException {
    return _task.getError();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> getOrDefault(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> defaultValue) throws PromiseUnresolvedException {
    return _task.getOrDefault(defaultValue);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return _task.getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPriority() {
    return _task.getPriority();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean setPriority(int priority) {
    return _task.setPriority(priority);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void await() throws InterruptedException {
    _task.await();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void contextRun(Context context, TaskLogger taskLogger, Task<?> parent, Collection<Task<?>> predecessors) {
    _task.contextRun(context, taskLogger, parent, predecessors);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean await(long time, TimeUnit unit) throws InterruptedException {
    return _task.await(time, unit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void wrapContextRun(ContextRunWrapper<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> wrapper) {
    _task.wrapContextRun(wrapper);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ShallowTrace getShallowTrace() {
    return _task.getShallowTrace();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addListener(PromiseListener<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> listener) {
    _task.addListener(listener);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Trace getTrace() {
    return _task.getTrace();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDone() {
    return _task.isDone();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isFailed() {
    return _task.isFailed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getId() {
    return _task.getId();
  }

  @Override
  public void traceValue(Function<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>, String> serializer) {
    _task.traceValue(serializer);
  }

  @Override
  public ShallowTraceBuilder getShallowTraceBuilder() {
    return _task.getShallowTraceBuilder();
  }

}
