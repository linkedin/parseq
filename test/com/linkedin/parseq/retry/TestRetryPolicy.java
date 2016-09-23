package com.linkedin.parseq.retry;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.retry.termination.TerminationPolicy;

import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.testng.annotations.Test;

import static com.linkedin.parseq.Task.withRetryPolicy;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class TestRetryPolicy extends BaseEngineTest {
  @Test
  public void testSuccessfulTask()
  {
    Task<String> task = withRetryPolicy(RetryPolicy.attempts(3, 0), attempt -> Task.value("successful attempt " + attempt));
    runAndWait(task);
    assertTrue(task.isDone());
    assertEquals(task.get(), "successful attempt 0");
  }

  @Test
  public void testSimpleRetryPolicy()
  {
    Task<Void> task = withRetryPolicy("testSimpleRetryPolicy", RetryPolicy.attempts(3, 0),
        attempt -> Task.failure(new RuntimeException("current attempt: " + attempt)));
    runAndWaitException(task, RuntimeException.class);
    assertTrue(task.isDone());
    assertEquals(task.getError().getMessage(), "current attempt: 2");
  }

  @Test
  public void testErrorClassification()
  {
    Function<Throwable, ErrorClassification> errorClassifier = error -> error instanceof TimeoutException ? ErrorClassification.RECOVERABLE : ErrorClassification.UNRECOVERABLE;

    RetryPolicy retryPolicy = new RetryPolicyBuilder().
        setTerminationPolicy(TerminationPolicy.limitAttempts(3)).
        setErrorClassifier(errorClassifier).
        build();
    assertEquals(retryPolicy.getName(), "RetryPolicy.LimitAttempts");

    Task<Void> task1 = withRetryPolicy("testErrorClassification", retryPolicy, attempt -> Task.failure(new TimeoutException("current attempt: " + attempt)));
    runAndWaitException(task1, TimeoutException.class);
    assertTrue(task1.isDone());
    assertEquals(task1.getError().getMessage(), "current attempt: 2");

    Task<Void> task2 = withRetryPolicy("testErrorClassification", retryPolicy, attempt -> Task.failure(new IllegalArgumentException("current attempt: " + attempt)));
    runAndWaitException(task2, IllegalArgumentException.class);
    assertTrue(task2.isDone());
    assertEquals(task2.getError().getMessage(), "current attempt: 0");
  }
}
