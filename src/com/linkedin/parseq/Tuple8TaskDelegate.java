package com.linkedin.parseq;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Tuple8;
import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.promise.PromiseException;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.PromiseUnresolvedException;
import com.linkedin.parseq.trace.Related;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.Trace;

public class Tuple8TaskDelegate<T1, T2, T3, T4, T5, T6, T7, T8> implements Tuple8Task<T1, T2, T3, T4, T5, T6, T7, T8> {

  private final Task<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> _task;

  public Tuple8TaskDelegate(Task<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> task) {
    _task = task;
  }

  public boolean cancel(Exception reason) {
    return _task.cancel(reason);
  }

  public Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> get() throws PromiseException {
    return _task.get();
  }

  public Throwable getError() throws PromiseUnresolvedException {
    return _task.getError();
  }

  public Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> getOrDefault(Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> defaultValue) throws PromiseUnresolvedException {
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

  public void wrapContextRun(ContextRunWrapper<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> wrapper) {
    _task.wrapContextRun(wrapper);
  }

  public ShallowTrace getShallowTrace() {
    return _task.getShallowTrace();
  }

  public void addListener(PromiseListener<Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>> listener) {
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
