package com.linkedin.parseq;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

import com.linkedin.parseq.function.Try;

/**
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 */
public class TestWithTry extends BaseEngineTest
{

  @Test
  public void testHappyPath() throws InterruptedException
  {
    final Task<Try<String>> task = Task.callable("test", () -> "hello")
        .withTry();

    runAndWait("TestWithTry.testHappyPath", task);

    assertFalse(task.get().isFailed());
    assertEquals("hello", task.get().get());
  }

  @Test
  public void testError() throws InterruptedException
  {
    @SuppressWarnings("unused")
    final Task<Try<String>> task = Task.callable("test", () -> {
      if (true) {
        throw new RuntimeException("boom");
      }
      return "hello";
    }).withTry();

    runAndWait("TestWithTry.testError", task);

    assertTrue(task.get().isFailed());
    assertTrue(task.get().getError() instanceof RuntimeException);
    assertEquals(task.get().getError().getMessage(), "boom");
  }

}
