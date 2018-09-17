package com.linkedin.parseq;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;


public class Exceptions {

  public static final Exception EARLY_FINISH_EXCEPTION =
      sanitize(new EarlyFinishException("Task execution cancelled because it's promise was already completed"));
  public static final Exception TIMEOUT_EXCEPTION = sanitize(new TimeoutException());
  public static final Exception NO_SUCH_ELEMENT_EXCEPTION = sanitize(new NoSuchElementException());

  private Exceptions() {
  }

  private static Exception sanitize(Exception e) {
    // Clear out everything but the last frame
    final StackTraceElement[] stackTrace = e.getStackTrace();
    if (stackTrace.length > 0) {
      e.setStackTrace(Arrays.copyOf(e.getStackTrace(), 1));
    }
    return e;
  }

  public static Exception noSuchElement() {
    return NO_SUCH_ELEMENT_EXCEPTION;
  }

  private static Exception addCause(Exception e, Throwable cause) {
    return (Exception) e.initCause(cause);
  }

  public static Exception noSuchElement(final Throwable cause) {
    return addCause(new NoSuchElementException(), cause);
  }

  public static boolean isCancellation(final Throwable e) {
    return e instanceof CancellationException;
  }

  public static boolean isEarlyFinish(final Throwable e) {
    return isCancellation(e) && e.getCause() instanceof EarlyFinishException;
  }

  private static String toString(final Throwable t) {
    final StringWriter sw = new StringWriter();
    t.printStackTrace(new PrintWriter(sw));
    return sw.toString();
  }

  public static String failureToString(final Throwable e) {
    if (isCancellation(e)) {
      if (isEarlyFinish(e)) {
        return "";
      } else {
        return "cancelled because: " + toString(e.getCause());
      }
    } else {
      return toString(e);
    }
  }

  public static Exception timeoutException(String desc) {
    if (desc == null || desc.isEmpty()) {
      return TIMEOUT_EXCEPTION;
    } else {
      return new TimeoutException(desc);
    }
  }

}
