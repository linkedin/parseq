package com.linkedin.parseq.retry;

import java.util.function.Function;


/**
 * Definitions of the supported error classifications and the default classification function.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public enum ErrorClassification {
  /** The classification of errors that will interrupt the retry operation. */
  FATAL(true, false),

  /** The classification of errors that will not interrupt the retry operation. */
  RECOVERABLE(false, false),

  /** The classification of errors that will not interrupt the retry operation or record information about the failure. */
  SILENTLY_RECOVERABLE(false, true);

  /** True if this classification will interrupt the retry operation. */
  protected final boolean _isFatal;

  /** True if this classification will not record information about a failure. */
  protected final boolean _isSilent;

  /**
   * Definitions of the supported error classifications and the default classification function.
   *
   * @param isFatal True if this classification will interrupt the retry operation.
   * @param isSilent True if this classification will not record information about a failure.
   */
  ErrorClassification(boolean isFatal, boolean isSilent) {
    _isFatal = isFatal;
    _isSilent = isSilent;
  }

  /** Default error classification: non-fatal errors are recoverable, and fatal errors are not recoverable. */
  public static final Function<Throwable, ErrorClassification> DEFAULT = throwable ->
      nonFatal(throwable) ? ErrorClassification.RECOVERABLE : ErrorClassification.FATAL;

  /**
   * @return True if this classification will interrupt the retry operation.
   */
  public boolean isFatal() {
    return _isFatal;
  }

  /**
   * @return True if this classification will not record information about a failure.
   */
  public boolean isSilent() {
    return _isSilent;
  }

  protected static boolean nonFatal(Throwable throwable) {
    return throwable instanceof StackOverflowError || !(throwable instanceof Error || throwable instanceof InterruptedException);
  }
}
