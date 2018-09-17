package com.linkedin.parseq.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;


/**
 * This class is a Log4j Appender implementation that can be used in tests
 * to check that something was logged.
 */
public class TestLogAppender extends AppenderSkeleton {

  private List<LoggingEvent> _entries;

  public TestLogAppender() {
    super();
    _entries = new ArrayList<LoggingEvent>();
    super.setName("TestLogAppender");
  }

  /**
   * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
   */
  @Override
  protected void append(LoggingEvent event) {
    _entries.add(event);
  }

  /**
   * @see org.apache.log4j.Appender#requiresLayout()
   */
  public boolean requiresLayout() {
    return false;
  }

  /**
   * @see org.apache.log4j.Appender#close()
   */
  public void close() {
    _entries.clear();
  }

  /**
   * @return the number of log events currently in the receiver
   */
  public int getNumberOfLogEvents() {
    return _entries.size();
  }

  /**
   * Removes all saved log events from receiver.
   */
  public void clear() {
    _entries.clear();
  }

  /**
   * Test method let's you check if the log entry at specified index
   * is what you expect it to be. If any of the specified criteria are null
   * they are ignored and not matched.
   *
   * @param index index of log entry (if index is out of range returns false)
   * @param loggerName logger name that the entry should have (null ok)
   * @param messagePart a substring that should be in the text of the log entry (null ok)
   * @param exceptionClass the class of the exception associated with the log entry (null ok)
   * @return true if it matches criteria
   */
  public boolean logEventAtIndexMatchesCriteria(int index, String loggerName, Level level, Object messagePart,
      Class<? extends Throwable> exceptionClass) {
    if (index < 0 || index >= _entries.size()) {
      return false;
    }

    LoggingEvent event = (LoggingEvent) _entries.get(index);

    if (event == null) {
      return false;
    }

    if (loggerName != null && (!event.getLoggerName().equals(loggerName))) {
      return false;
    }

    if (level != null && (!event.getLevel().equals(level))) {
      return false;
    }

    if (messagePart != null) {

      if (messagePart instanceof String) {
        if (event.getRenderedMessage().indexOf((String) messagePart) == -1) {
          return false;
        }
      } else if (messagePart instanceof Pattern) {
        Matcher matcher = ((Pattern) messagePart).matcher(event.getRenderedMessage());

        if (!matcher.find()) {
          return false;
        }
      } else {
        if (!event.getMessage().equals(messagePart)) {
          return false;
        }
      }
    }

    if (exceptionClass != null) {
      ThrowableInformation tinfo = event.getThrowableInformation();

      if (tinfo == null || (!exceptionClass.isInstance(tinfo.getThrowable()))) {
        return false;
      }
    }

    return true;
  }
}
