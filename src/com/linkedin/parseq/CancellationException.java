package com.linkedin.parseq;

class CancellationException extends Exception {

  private static final long serialVersionUID = 1L;

  public CancellationException(String message, Throwable cause) {
    super(message, cause);
  }

  public CancellationException(String message) {
    super(message);
  }

  public CancellationException(Throwable cause) {
    super(cause);
  }

  public CancellationException() {
    super();
  }
}
