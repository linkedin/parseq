package com.linkedin.parseq;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Stack;
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

  /**
   * Returns whether the given exception is a {@link MultiException}.
   */
  public static boolean isMultiple(final Throwable e) {
    return e instanceof MultiException;
  }

  /**
   * Returns all causes of the given exception.
   * If the exception is a {@link MultiException}, it will return a list of all causes.
   * If the exception is not a {@link MultiException} and not null, it will return a list with the exception; otherwise, it will return an empty list.
   *
   * @param e the exception to search, nullable
   * @return a list of all causes, or an empty list if none found, never null
   */
  public static List<Throwable> allMultiCauses(final Throwable e) {
    if (e == null) {
      return Collections.emptyList();
    }
    if (!isMultiple(e)) {
      return Collections.singletonList(e);
    }

    Deque<MultiException> meStack = new ArrayDeque<>();
    meStack.push((MultiException) e);
    List<Throwable> causes = new ArrayList<>();

    while (!meStack.isEmpty()) {
      MultiException me = meStack.pop();
      if (me.getCauses() == null) {
        continue;
      }

      for (Throwable cause : me.getCauses()) {
        if (cause == null) {
          continue;
        }
        if (isMultiple(cause)) {
          meStack.push((MultiException) cause);
        } else {
          causes.add(cause);
        }
      }
    }
    return causes;
  }

  /**
   * Finds the first non-MultiException cause of the given exception.
   *
   * @param e the exception to search, nullable
   * @return the first non-MultiException cause, or null if none found, nullable
   */
  public static Throwable anyMultiCause(final Throwable e) {
    if (!isMultiple(e)) {
      return e;
    }

    Deque<MultiException> meStack = new ArrayDeque<>();
    meStack.push((MultiException) e);

    while (!meStack.isEmpty()) {
      MultiException curMe = meStack.pop();
      Collection<? extends Throwable> causes = curMe.getCauses();
      if (causes == null) {
        continue;
      }

      for (Throwable subCause : causes) {
        if (subCause == null) {
          continue;
        }
        if (isMultiple(subCause)) {
          meStack.push((MultiException) subCause);
        } else {
          return subCause;
        }
      }
    }
    return null;
  }
}
