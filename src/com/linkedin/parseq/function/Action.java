package com.linkedin.parseq.function;

@FunctionalInterface
public interface Action {
  public void run() throws Exception;
}
