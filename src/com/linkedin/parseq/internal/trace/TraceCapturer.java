package com.linkedin.parseq.internal.trace;

import com.linkedin.parseq.Task;
import com.linkedin.parseq.trace.Trace;

import java.util.List;

public interface TraceCapturer
{
  /**
   * Registers a task with this object. If the task has already been
   * registered, this is a no-op.
   * @return a unique identifier for the task.
   */
  int registerTask(Task<?> task);

  /**
   * Sets the parent for a task.
   */
  void setParent(int task, int parent);

  /**
   * Adds a potential parent for a task. A potential parent is a task that could
   * have triggered execution of a task. A potential parent can also be set as
   * the actual parent via {@link #setParent(com.linkedin.parseq.Task,
   * com.linkedin.parseq.Task)}. In this case the final trace graph will only
   * include the parent relationship, and not the potential parent
   * relationship.
   */
  void addPotentialParent(int task, int parent);

  /**
   * Adds a successor for the given predecessors.
   */
  void addPredecessors(int successor, List<Integer> predecessors);

  /**
   * Adds a potential successor for the given predecessors. A potential successor
   * relationship is one such that the completion of a predecessor would have
   * triggered the execution of the successor, but the successor was already
   * started.
   */
  void addPotentialPredecessors(int successor, List<Integer> predecessors);

  /**
   * Returns a {@link ImmutableTrace} representing the current state of the execution
   * managed by object. If tracing is not enabled, this returns
   * {@code null}.
   */
  Trace getTrace();
}
