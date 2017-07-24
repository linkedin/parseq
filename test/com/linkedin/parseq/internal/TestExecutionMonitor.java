package com.linkedin.parseq.internal;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.Engine;
import com.linkedin.parseq.EngineBuilder;
import com.linkedin.parseq.Task;


public class TestExecutionMonitor extends BaseEngineTest {

  @Override
  protected void customizeEngine(EngineBuilder engineBuilder) {
    engineBuilder.setEngineProperty(Engine.MONITOR_EXECUTION, true);
  };

  @Test
  public void testThreadDump() {
    final TestLogAppender logAppender = new TestLogAppender();
    Logger.getLogger(ExecutionMonitor.class).addAppender(logAppender);

    runAndWait(Task.action(() -> Thread.sleep(2000)));

    assertEquals(logAppender.getNumberOfLogEvents(), 1);
    assertTrue(logAppender.logEventAtIndexMatchesCriteria(0, "com.linkedin.parseq.internal.ExecutionMonitor", Level.WARN,
        "Found ParSeq threads running longer than", null));
    assertTrue(logAppender.logEventAtIndexMatchesCriteria(0, "com.linkedin.parseq.internal.ExecutionMonitor", Level.WARN,
        "Thread.sleep", null));
  }

}
