<#include "../../../macros/macros.ftl">
<@pp.dropOutputFile />
<#list 3..max as i>
<@pp.changeOutputFile name="Tuple" + i + "TaskDelegate.java" />
package com.linkedin.parseq;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.linkedin.parseq.function.Tuple${i};
import com.linkedin.parseq.internal.TaskLogger;
import com.linkedin.parseq.promise.PromiseException;
import com.linkedin.parseq.promise.PromiseListener;
import com.linkedin.parseq.promise.PromiseUnresolvedException;
import com.linkedin.parseq.trace.Related;
import com.linkedin.parseq.trace.ShallowTrace;
import com.linkedin.parseq.trace.Trace;

public class Tuple${i}TaskDelegate<<@typeParameters i/>> implements Tuple${i}Task<<@typeParameters i/>> {

  private final Task<Tuple${i}<<@typeParameters i/>>> _task;

  public Tuple${i}TaskDelegate(Task<Tuple${i}<<@typeParameters i/>>> task) {
    _task = task;
  }

  public boolean cancel(Exception reason) {
    return _task.cancel(reason);
  }

  public Tuple${i}<<@typeParameters i/>> get() throws PromiseException {
    return _task.get();
  }

  public Throwable getError() throws PromiseUnresolvedException {
    return _task.getError();
  }

  public Tuple${i}<<@typeParameters i/>> getOrDefault(Tuple${i}<<@typeParameters i/>> defaultValue) throws PromiseUnresolvedException {
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

  public void wrapContextRun(ContextRunWrapper<Tuple${i}<<@typeParameters i/>>> wrapper) {
    _task.wrapContextRun(wrapper);
  }

  public ShallowTrace getShallowTrace() {
    return _task.getShallowTrace();
  }

  public void addListener(PromiseListener<Tuple${i}<<@typeParameters i/>>> listener) {
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
</#list>
