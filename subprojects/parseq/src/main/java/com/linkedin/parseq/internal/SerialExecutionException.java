package com.linkedin.parseq.internal;

public class SerialExecutionException extends Exception {
  private static final long serialVersionUID = 0L;

  public SerialExecutionException(String msg, Throwable error) {
    super(msg, error);
  }
}
