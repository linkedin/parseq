package com.linkedin.parseq.retry;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.function.Try;
import com.linkedin.parseq.retry.backoff.BackoffPolicy;
import com.linkedin.parseq.retry.monitor.EventMonitor;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.testng.annotations.Test;

import static com.linkedin.parseq.retry.RetriableTask.withRetryPolicy;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class TestRetryPolicy extends BaseEngineTest {
  @Test
  public void testSuccessfulTask()
  {
    Task<String> task = withRetryPolicy(RetryPolicy.attempts(3), attempt -> Task.value("successful attempt " + attempt));
    runAndWait(task);
    assertTrue(task.isDone());
    assertEquals(task.get(), "successful attempt 0");
  }

  @Test
  public void testSimpleRetryPolicy()
  {
    Task<Void> task = withRetryPolicy("testSimpleRetryPolicy", RetryPolicy.attempts(3),
        attempt -> Task.failure(new RuntimeException("current attempt: " + attempt)));
    runAndWaitException(task, RuntimeException.class);
    assertTrue(task.isDone());
    assertEquals(task.getError().getMessage(), "current attempt: 2");
  }

  @Test
  public void testErrorClassification()
  {
    Function<Throwable, ErrorClassification> errorClassifier = error -> error instanceof TimeoutException ? ErrorClassification.RECOVERABLE : ErrorClassification.FATAL;

    Task<Void> task1 = withRetryPolicy("testErrorClassification", RetryPolicy.<Void>attempts(3).setErrorClassifier(errorClassifier),
        attempt -> Task.failure(new TimeoutException("current attempt: " + attempt)));
    runAndWaitException(task1, TimeoutException.class);
    assertTrue(task1.isDone());
    assertEquals(task1.getError().getMessage(), "current attempt: 2");

    Task<Void> task2 = withRetryPolicy("testErrorClassification", RetryPolicy.<Void>attempts(3).setErrorClassifier(errorClassifier),
        attempt -> Task.failure(new IllegalArgumentException("current attempt: " + attempt)));
    runAndWaitException(task2, IllegalArgumentException.class);
    assertTrue(task2.isDone());
    assertEquals(task2.getError().getMessage(), "current attempt: 0");
  }

  @Test
  public void testResultClassification()
  {
    Function<String, ResultClassification> resultClassifier = result -> result.startsWith("x") ? ResultClassification.UNACCEPTABLE : ResultClassification.ACCEPTABLE;

    Task<String> task1 = withRetryPolicy("testResultClassification", RetryPolicy.<String>attempts(3).setResultClassifier(resultClassifier),
        attempt -> Task.value(attempt.toString()));
    runAndWait(task1);
    assertTrue(task1.isDone());
    assertEquals(task1.get(), "0");

    Task<String> task2 = withRetryPolicy("testResultClassification", RetryPolicy.<String>attempts(3).setResultClassifier(resultClassifier),
        attempt -> Task.value("x" + attempt));
    runAndWaitException(task2, RetryFailureException.class);
    assertTrue(task2.isDone());
    assertTrue(task2.getError().getMessage().endsWith("x2"));
  }

  @Test
  public void testEventMonitor()
  {
    AtomicInteger retryCount = new AtomicInteger(0);
    AtomicInteger interruptedCount = new AtomicInteger(0);
    AtomicInteger abortedCount = new AtomicInteger(0);

    EventMonitor<Void> monitor = new EventMonitor<Void>() {
      @Override
      public void retrying(String name, Try<Void> outcome, int attempts, long backoffTime, boolean isSilent) {
        assertEquals(name, "testEventMonitor");
        assertEquals(outcome.getError().getMessage(), "current attempt: " + (attempts-1));
        assertEquals(backoffTime, 10 * attempts);
        retryCount.incrementAndGet();
      }

      @Override
      public void interrupted(String name, Try<Void> outcome, int attempts) {
        assertEquals(name, "testEventMonitor");
        interruptedCount.incrementAndGet();
      }

      @Override
      public void aborted(String name, Try<Void> outcome, int attempts) {
        assertEquals(name, "testEventMonitor");
        abortedCount.incrementAndGet();
      }
    };

    Task<Void> task = withRetryPolicy("testEventMonitor", RetryPolicy.<Void>attempts(3).setBackoffPolicy(BackoffPolicy.linear(10)).setEventMonitor(monitor),
        attempt -> Task.failure(new RuntimeException("current attempt: " + attempt)));
    runAndWaitException(task, RuntimeException.class);
    assertTrue(task.isDone());
    assertEquals(retryCount.get(), 2);
    assertEquals(interruptedCount.get(), 0);
    assertEquals(abortedCount.get(), 1);
  }
}
