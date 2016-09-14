package com.linkedin.parseq.retry.monitor;


/**
 * Definition of the printing-related actions that can be performed when a retry event is received.
 *
 * @author Oleg Anashkin (oleg.anashkin@gmail.com)
 */
public enum PrintAction {
  /** A print action that will not print anything. */
  NOTHING,

  /** A print action that will only print the formatted event message. */
  MESSAGE,

  /** A print action that will print the formatted event message and the most recent exception's stack trace. */
  MESSAGE_AND_STACK_TRACE
}
