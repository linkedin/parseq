package com.linkedin.parseq;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Tuple6;
import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.promise.PromiseException;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.PromiseUnresolvedException;
import com.linkedin.parseq.trace.Related;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.Trace;

public class Tuple6TaskDelegate<T1, T2, T3, T4, T5, T6> implements Tuple6Task<T1, T2, T3, T4, T5, T6> {

  private final Task<Tuple6<T1, T2, T3, T4, T5, T6>> _task;

  public Tuple6TaskDelegate(Task<Tuple6<T1, T2, T3, T4, T5, T6>> task) {
    _task = task;
  }

  public boolean cancel(Exception reason) {
    return _task.cancel(reason);
  }

  public Tuple6<T1, T2, T3, T4, T5, T6> get() throws PromiseException {
    return _task.get();
  }

  public Throwable getError() throws PromiseUnresolvedException {
    return _task.getError();
  }

  public Tuple6<T1, T2, T3, T4, T5, T6> getOrDefault(Tuple6<T1, T2, T3, T4, T5, T6> defaultValue) throws PromiseUnresolvedException {
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

  public void wrapContextRun(ContextRunWrapper<Tuple6<T1, T2, T3, T4, T5, T6>> wrapper) {
    _task.wrapContextRun(wrapper);
  }

  public ShallowTrace getShallowTrace() {
    return _task.getShallowTrace();
  }

  public void addListener(PromiseListener<Tuple6<T1, T2, T3, T4, T5, T6>> listener) {
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
