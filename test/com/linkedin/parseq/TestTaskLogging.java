/*
 * Copyright 2012 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.parseq;

import com.linkedin.parseq.trace.ResultType;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class TestTaskLogging extends BaseEngineTest
{
  private static final String ALL_LOGGER = Engine.LOGGER_BASE + ":all";
  private static final String ROOT_LOGGER = Engine.LOGGER_BASE + ":root";
  private static final String PLAN_CLASS_LOGGER = Engine.LOGGER_BASE + ":planClass=";

  @Test
  public void testSingleTaskWithDefaultLogging() throws InterruptedException
  {
    final Task<?> task = TestUtil.noop();
    runAndWait("TestTaskLogging.testSingleTaskWithDefaultLogging", task);

    assertEquals(0, getLogEntries(ALL_LOGGER).size());
    assertEquals(0, getLogEntries(ROOT_LOGGER).size());
    assertEquals(0, getLogEntries(planClassLogger(task)).size());
  }

  @Test
  public void testSingleTaskCombinations() throws InterruptedException
  {
    final String taskValue = "value";

    final String[] loggers = new String[] {
        ALL_LOGGER, ROOT_LOGGER, planClassLogger(Task.value("dummy", "dummy"))
    };
    final int[] levels = new int[] { ListLogger.LEVEL_DEBUG, ListLogger.LEVEL_TRACE };

    for(String logger : loggers)
    {
      for (int level : levels)
      {
        resetLoggers();

        final Task<?> task = Task.value("t1", taskValue);
        task.setTraceValueSerializer(Object::toString);
        setLogLevel(logger, level);
        runAndWait("TestTaskLogging.testSingleTaskCombinations", task);

        for (String checkLogger : loggers)
        {
          if (logger.equals(checkLogger))
          {
            assertTaskLogged(task, taskValue, checkLogger, level);
          }
          else
          {
            assertEquals(Collections.emptyList(), getLogEntries(checkLogger));
          }
        }
      }
    }
  }

  @Test
  public void testSingleTaskWithErrorCombinations() throws InterruptedException
  {
    final String errorMsg = "this task has failed";
    final Exception exception = new Exception(errorMsg);

    final String[] loggers = new String[] {
        ALL_LOGGER, ROOT_LOGGER, planClassLogger(Task.failure("dummy", exception))
    };
    final int[] levels = new int[] { ListLogger.LEVEL_DEBUG, ListLogger.LEVEL_TRACE };

    for(String logger : loggers)
    {
      for (int level : levels)
      {
        resetLoggers();

        final Task<?> task = Task.failure("t1", exception);
        setLogLevel(logger, level);

        try {
          runAndWait("TestTaskLogging.testSingleTaskWithErrorCombinations", task);
          fail("task should finish with Error");
        } catch (Throwable t) {
          assertEquals(exception, task.getError());
        }

        for (String checkLogger : loggers)
        {
          if (logger.equals(checkLogger))
          {
            assertTaskLogged(task, errorMsg, checkLogger, level);
          }
          else
          {
            assertEquals(Collections.emptyList(), getLogEntries(checkLogger));
          }
        }
      }
    }
  }

  @Test
  public void testCompositeTaskWithAllLoggerTrace() throws InterruptedException
  {
    final Task<?> child1 = Task.value("t1", "value");
    child1.setTraceValueSerializer(Object::toString);
    final Task<?> child2 = TestUtil.noop();
    final Task<?> parent = Tasks.seq(child1, child2);

    setLogLevel(ALL_LOGGER, ListLogger.LEVEL_TRACE);
    runAndWait("TestTaskLogging.testCompositeTaskWithAllLoggerTrace", parent);

    assertTaskLogged(parent, "null", ALL_LOGGER, ListLogger.LEVEL_TRACE);
    assertTaskLogged(child1, "value", ALL_LOGGER, ListLogger.LEVEL_TRACE);
    assertTaskLogged(child2, "null", ALL_LOGGER, ListLogger.LEVEL_TRACE);
  }

  @Test
  public void testCompositeTaskWithRootLoggerTrace() throws InterruptedException
  {
    final Task<?> child1 = Task.value("t1", "value");
    final Task<?> child2 = TestUtil.noop();
    final Task<?> parent = Tasks.seq(child1, child2);

    setLogLevel(ROOT_LOGGER, ListLogger.LEVEL_TRACE);
    runAndWait("TestTaskLogging.testCompositeTaskWithRootLoggerTrace", parent);

    assertTaskLogged(parent, "null", ROOT_LOGGER, ListLogger.LEVEL_TRACE);

    final List<ListLogger.Entry> entries = getLogEntries(ROOT_LOGGER);
    assertEquals("Should only log start and stop for the root trace: " + entries, 2, entries.size());
  }

  @Test
  public void testCompositeTaskWithPlanClassLoggerTrace() throws InterruptedException
  {
    final Task<?> child1 = Task.value("t1", "value");
    child1.setTraceValueSerializer(Object::toString);
    final Task<?> child2 = TestUtil.noop();
    final Task<?> parent = Tasks.seq(child1, child2);

    final String planClassLogger = planClassLogger(parent);

    setLogLevel(planClassLogger, ListLogger.LEVEL_TRACE);
    runAndWait("TestTaskLogging.testCompositeTaskWithPlanClassLoggerTrace", parent);

    assertTaskLogged(parent, "null", planClassLogger, ListLogger.LEVEL_TRACE);
    assertTaskLogged(child1, "value", planClassLogger, ListLogger.LEVEL_TRACE);
    assertTaskLogged(child2, "null", planClassLogger, ListLogger.LEVEL_TRACE);
  }

  private void assertTaskLogged(final Task<?> task,
                                final String taskValue,
                                final String loggerName,
                                final int level) throws InterruptedException
  {
    if (level != ListLogger.LEVEL_TRACE && level != ListLogger.LEVEL_DEBUG)
    {
      throw new IllegalArgumentException("Log level is not trace or debug");
    }

    retryAssertions(5, 20, new Runnable()
    {
      @Override
      public void run()
      {
        final List<ListLogger.Entry> entries = getLogEntries(loggerName);
        final int startIdx = assertHasStartLog(loggerName, task, entries);
        final int endIdx = level == ListLogger.LEVEL_TRACE
            ? assertHasTraceEndLog(loggerName, task, taskValue, entries)
            : assertHasDebugEndLog(loggerName, task, entries);

        assertTrue("Start should happen before end. Start: " + startIdx + " End: " + endIdx,
                   startIdx < endIdx);
      }
    });
  }

  private int assertHasStartLog(String loggerName, Task<?> task, List<ListLogger.Entry> entries)
  {
    for (int i = 0; i < entries.size(); i++)
    {
      final String message = entries.get(i).getMessage();
      if (message.contains("Starting task") && message.contains(task.getName()))
      {
        return i;
      }
    }

    fail(loggerName + ": " + entries + " does not contain a start log entry for " + task.getName());
    throw new RuntimeException("Execution never gets here - see preceding fail");
  }

  private int assertHasDebugEndLog(String loggerName, Task<?> task, List<ListLogger.Entry> entries)
  {
    for (int i = 0; i < entries.size(); i++)
    {
      final String message = entries.get(i).getMessage();
      if (message.contains("Ending task") && message.contains(task.getName()) &&
          message.contains(ResultType.fromTask(task).toString()))
      {
        return i;
      }
    }

    fail(loggerName + ": " + entries + " does not contain an end log entry for " + task.getName());
    throw new RuntimeException("Execution never gets here - see preceding fail");
  }

  private int assertHasTraceEndLog(String loggerName, Task<?> task, String taskValue, List<ListLogger.Entry> entries)
  {
    final int i = assertHasDebugEndLog(loggerName, task, entries);
    final String message = entries.get(i).getMessage();
    assertContains(taskValue, message);
    return i;
  }

  private static void assertContains(String substring, String actual)
  {
    assertTrue("Actual string '" + actual + "' does not contain substring '" + substring + "'",
               actual.contains(substring));
  }

  // Retries a runnable with asserts until all the asserts pass or
  // retries * delayMillis has passed.
  private static void retryAssertions(int retries, int delayMillis,
                                      Runnable runnable) throws InterruptedException
  {
    for (int i = 0; i < retries; i++)
    {
      try
      {
        runnable.run();
        return;
      }
      catch (AssertionError t)
      {
        // Swallow and retry after sleep
        Thread.sleep(delayMillis);
      }
    }

    runnable.run();
  }

  private static String planClassLogger(Task<?> plan)
  {
    return PLAN_CLASS_LOGGER + plan.getClass().getName();
  }
}
