package com.linkedin.parseq;

/**
 * Different types of parseq tasks. The idea is to attach type to a task so that it can be displayed in trace
 * visualization tool to help in debugging.
 */
public enum TaskType {
  FUSION ("fusion"),
  BLOCKING ("blocking"),
  SHAREABLE ("shareable"),
  FLATTEN ("flatten"),
  WITH_SIDE_EFFECT ("withSideEffect"),
  RETRY ("retry"),
  WITH_RETRY ("withRetry"),
  TIMEOUT ("timeout"),
  WITH_TIMEOUT ("withTimeout"),
  RECOVER ("recover"),
  WITH_RECOVER ("withRecover"),
  WITH_DELAY ("withDelay");

  private final String _name;

  TaskType(String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }
}
