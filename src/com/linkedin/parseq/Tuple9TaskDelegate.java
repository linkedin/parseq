package com.linkedin.parseq;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Tuple9;
import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.promise.PromiseException;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.PromiseUnresolvedException;
import com.linkedin.parseq.trace.Related;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.Trace;

public class Tuple9TaskDelegate<T1, T2, T3, T4, T5, T6, T7, T8, T9> implements Tuple9Task<T1, T2, T3, T4, T5, T6, T7, T8, T9> {

  private final Task<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> _task;

  public Tuple9TaskDelegate(Task<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> task) {
    _task = task;
  }

  public boolean cancel(Exception reason) {
    return _task.cancel(reason);
  }

  public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> get() throws PromiseException {
    return _task.get();
  }

  public Throwable getError() throws PromiseUnresolvedException {
    return _task.getError();
  }

  public Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> getOrDefault(Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> defaultValue) throws PromiseUnresolvedException {
    return _task.getOrDefault(defaultValue);
  }

  public String getName() {
    return _task.getName();
  }

  public int getPriority() {
    return _task.getPriority();
  }

  public boolean setPriority(int priority) {
    return _task.setPriority(priority);
  }

  public void await() throws InterruptedException {
    _task.await();
  }

  public void contextRun(Context context, TaskLogger taskLogger, Task<?> parent, Collection<Task<?>> predecessors) {
    _task.contextRun(context, taskLogger, parent, predecessors);
  }

  public boolean await(long time, TimeUnit unit) throws InterruptedException {
    return _task.await(time, unit);
  }

  public void wrapContextRun(ContextRunWrapper<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> wrapper) {
    _task.wrapContextRun(wrapper);
  }

  public ShallowTrace getShallowTrace() {
    return _task.getShallowTrace();
  }

  public void addListener(PromiseListener<Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>> listener) {
    _task.addListener(listener);
  }

  public Trace getTrace() {
    return _task.getTrace();
  }

  public Set<Related<Task<?>>> getRelationships() {
    return _task.getRelationships();
  }

  public boolean isDone() {
    return _task.isDone();
  }

  public boolean isFailed() {
    return _task.isFailed();
  }

}
