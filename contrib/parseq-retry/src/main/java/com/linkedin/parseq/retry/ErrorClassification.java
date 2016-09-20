package com.linkedin.parseq.retry;

import java.util.concurrent.CancellationException;
import java.util.function.Function;


/**
 * Definitions of the supported error classifications and the default classification function.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public enum ErrorClassification {
  /** The classification of errors that will interrupt the retry operation. */
  UNRECOVERABLE,

  /** The classification of errors that will not interrupt the retry operation. */
  RECOVERABLE;

  /** Default error classification: non-fatal errors are recoverable, and fatal errors are not recoverable. */
  public static final Function<Throwable, ErrorClassification> DEFAULT = throwable ->
      nonFatal(throwable) ? ErrorClassification.RECOVERABLE : ErrorClassification.UNRECOVERABLE;

  protected static boolean nonFatal(Throwable throwable) {
    return throwable instanceof StackOverflowError || !(throwable instanceof VirtualMachineError || throwable instanceof ThreadDeath
        || throwable instanceof InterruptedException || throwable instanceof LinkageError || throwable instanceof CancellationException);
  }
}
