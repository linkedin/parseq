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

import static org.testng.AssertJUnit.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.linkedin.parseq.trace.Trace;
import com.linkedin.parseq.trace.codec.json.JsonTraceCodec;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A base class that builds an Engine with default configuration.
 *
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class BaseEngineTest
{
  private static final Logger LOG = LoggerFactory.getLogger(BaseEngineTest.class.getName());

  private ScheduledExecutorService _scheduler;
  private ExecutorService _asyncExecutor;
  private Engine _engine;
  private ListLoggerFactory _loggerFactory;

  @BeforeMethod
  public void setUp() throws Exception
  {
    final int numCores = Runtime.getRuntime().availableProcessors();
    _scheduler = Executors.newScheduledThreadPool(numCores + 1);
    _asyncExecutor = Executors.newFixedThreadPool(2);
    _loggerFactory = new ListLoggerFactory();
    EngineBuilder engineBuilder = new EngineBuilder()
        .setTaskExecutor(_scheduler)
        .setTimerScheduler(_scheduler)
        .setLoggerFactory(_loggerFactory);
    AsyncCallableTask.register(engineBuilder, _asyncExecutor);
    _engine = engineBuilder.build();
  }

  @AfterMethod
  public void tearDown() throws Exception
  {
    _engine.shutdown();
    _engine.awaitTermination(200, TimeUnit.MILLISECONDS);
    _engine = null;
    _scheduler.shutdownNow();
    _scheduler = null;
    _asyncExecutor.shutdownNow();
    _asyncExecutor = null;
    _loggerFactory.reset();
    _loggerFactory = null;
  }

  protected Engine getEngine()
  {
    return _engine;
  }

  /**
   * Runs task, verifies that task finishes within 5 sec and logs trace from the task execution.
   *
   * @param desc description of a test
   * @param task task to run
   * @return value task was completed with
   */
  protected void runWait5sAndLogTrace(final String desc, Task<?> task)
  {
    try
    {
      _engine.run(task);
      assertTrue(task.await(5, TimeUnit.SECONDS));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    finally
    {
      logTracingResults(desc, task);
    }
  }

  protected void logTracingResults(final String test, final Task<?> task)
  {
    final Trace trace = task.getTrace();

    try
    {
      LOG.info("Trace [" + test + "]: " + new JsonTraceCodec().encode(trace));
    }
    catch (IOException e)
    {
      LOG.error("Failed to encode JSON");
      e.printStackTrace();
    }
    System.out.println();
  }


  protected void setLogLevel(final String loggerName, final int level)
  {
    _loggerFactory.getLogger(loggerName).setLogLevel(level);
  }

  protected List<ListLogger.Entry> getLogEntries(final String loggerName)
  {
    return _loggerFactory.getLogger(loggerName).getEntries();
  }

  protected void resetLoggers()
  {
    _loggerFactory.reset();
  }
}
