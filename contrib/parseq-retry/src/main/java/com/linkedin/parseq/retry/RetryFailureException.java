package com.linkedin.parseq.retry;


public class RetryFailureException extends Exception {
  private static final long serialVersionUID = 0L;

  public RetryFailureException() {
    super();
  }

  public RetryFailureException(final String message) {
    super(message);
  }
}
