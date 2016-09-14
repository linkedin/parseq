package com.linkedin.parseq.retry;


/**
 * Definitions of the supported result classifications and the default classification function.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public final class ResultClassification {
  /** The classification of results that will not interrupt the retry operation. */
  public static final ResultClassification ACCEPTABLE = new ResultClassification(true);

  /** The classification of results that will interrupt the retry operation. */
  public static final ResultClassification UNACCEPTABLE = new ResultClassification(false);

  /** Error classification associated with this result. */
  private final ErrorClassification _status;

  /** Is this result acceptable. */
  private final boolean _isAcceptable;

  private ResultClassification(boolean isAcceptable) {
    _isAcceptable = isAcceptable;
    _status = ErrorClassification.RECOVERABLE;
  }

  private ResultClassification(boolean isAcceptable, ErrorClassification status) {
    _isAcceptable = isAcceptable;
    _status = status;
  }

  /**
   * @return Is this result acceptable.
   */
  public boolean isAcceptable() {
    return _isAcceptable;
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
    return new ResultClassification(_isAcceptable, status);
  }
}
