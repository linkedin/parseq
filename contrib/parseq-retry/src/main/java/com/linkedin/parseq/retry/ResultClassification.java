package com.linkedin.parseq.retry;


/**
 * Definitions of the supported result classifications and the default classification function.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public final class ResultClassification {
  /** The classification of results that will not interrupt the retry operation. */
  public static final ResultClassification ACCEPTABLE = new ResultClassification();

  /** The classification of results that will interrupt the retry operation. */
  public static final ResultClassification UNACCEPTABLE = new ResultClassification();

  /** Error classification associated with this result. */
  private final ErrorClassification _status;

  private ResultClassification() {
    _status = ErrorClassification.RECOVERABLE;
  }

  private ResultClassification(ErrorClassification status) {
    _status = status;
  }

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
    return new ResultClassification(status);
  }
}
