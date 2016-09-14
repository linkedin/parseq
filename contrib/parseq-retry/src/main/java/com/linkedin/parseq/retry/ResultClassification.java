package com.linkedin.parseq.retry;


/**
 * Definitions of the supported result classifications and the default classification function.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public enum ResultClassification {
  /** The classification of results that will not interrupt the retry operation. */
  ACCEPTABLE,

  /** The classification of results that will interrupt the retry operation. */
  UNACCEPTABLE;

  /** Error classification associated with this result. */
  protected ErrorClassification _status = ErrorClassification.RECOVERABLE;

  /**
   * @return Error classification associated with this result.
   */
  public ErrorClassification getStatus() {
    return _status;
  }

  /**
   * Associate the error classification with this result. It controls the behavior of retry operation.
   *
   * @param status Error classification.
   */
  public ResultClassification setStatus(ErrorClassification status) {
    _status = status;
    return this;
  }
}
