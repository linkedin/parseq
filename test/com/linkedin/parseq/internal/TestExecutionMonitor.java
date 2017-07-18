package com.linkedin.parseq.internal;

import org.testng.annotations.Test;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.Task;


public class TestExecutionMonitor extends BaseEngineTest {

  @Test
  public void test() {

    runAndWait(Task.value("Hello world"));

    ThreadDumper td = new ThreadDumper();
    StringBuilder sb = new StringBuilder();
    td.threadDump(sb);
    System.out.println(sb);
  }

}
