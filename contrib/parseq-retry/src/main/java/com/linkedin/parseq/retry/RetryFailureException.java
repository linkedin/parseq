package com.linkedin.parseq.retry;

/**
 * Exception that is thrown when the process of retries is interrupted by any reason.
 * Usually it's either the exhausted number of attempts, or the unacceptable task result.
 */
public class RetryFailureException extends Exception {
  private static final long serialVersionUID = 0L;

  public RetryFailureException() {
    super();
  }

  public RetryFailureException(final String message) {
    super(message);
  }
}
