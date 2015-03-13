package com.linkedin.parseq.trace;

import java.util.Map;

/**
 * A shallow trace is a trace without any relationship information. Use
 * {@link ShallowTraceBuilder} to construct new instances of this class.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
public interface ShallowTrace {

  /**
   * Returns the task name for this trace
   *
   * @return the task name for this trace
   */
  String getName();

  /**
   * Returns if the trace will be hidden from the visualizations.
   *
   * @return if the trace is hidden
   */
  boolean getHidden();

  /**
   * Returns if the trace will be system hidden from the visualizations.
   *
   * @return if the trace is system hidden.
   */
  boolean getSystemHidden();

  /**
   * Returns a String representation of the value or error produced by the
   * traced task. If the task was unfinished then the string representation
   * will be null.
   *
   * @return String representation of the task's value
   */
  String getValue();

  /**
   * Returns the result type of the task.
   *
   * @return the result type of the task
   */
  ResultType getResultType();

  /**
   * Returns the time at which the task was started in nanoseconds. If
   * the task had not yet started, this method will return {@code null}.
   *
   * @return the time at which this task was started or {@code null}
   */
  Long getStartNanos();

  /**
   * Returns the time at which the task finished its run method in nanoseconds.
   * If the task has not finished its run method yet this method will return
   * {@code null}.
   *
   * @return the time at which this task finished its run method or {@code null}.
   */
  Long getPendingNanos();

  /**
   * Returns the time at which the task was finished in nanoseconds. If the
   * task had not yet finished, this method will return {@code null}.
   *
   * @return the time at which this task was finished or {@code null}
   */
  Long getEndNanos();

  /**
   * Get the set of attributes related to this trace.
   * @return the map of attributes related to this trace.
   */
  Map<String, String> getAttributes();

  /**
   * Id of this trace.
   * @return Id of this trace.
   */
  long getId();

}
