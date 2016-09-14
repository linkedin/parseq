package com.linkedin.parseq.retry;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.function.Failure;
import com.linkedin.parseq.retry.backoff.BackoffPolicy;
import com.linkedin.parseq.retry.backoff.ConstantBackoff;
import com.linkedin.parseq.retry.backoff.ExponentialBackoff;
import com.linkedin.parseq.retry.backoff.FibonacciBackoff;
import com.linkedin.parseq.retry.backoff.LinearBackoff;
import com.linkedin.parseq.retry.backoff.RandomizedBackoff;
import com.linkedin.parseq.retry.backoff.SelectedBackoff;
import com.linkedin.parseq.retry.termination.AlwaysTerminate;
import com.linkedin.parseq.retry.termination.LimitAttempts;
import com.linkedin.parseq.retry.termination.LimitDuration;
import com.linkedin.parseq.retry.termination.NeverTerminate;
import com.linkedin.parseq.retry.termination.RequireBoth;
import com.linkedin.parseq.retry.termination.RequireEither;
import com.linkedin.parseq.retry.termination.TerminationPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import org.testng.annotations.Test;

import static com.linkedin.parseq.retry.RetriableTask.withRetryPolicy;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class TestBackoffPolicy extends BaseEngineTest {
  @Test
  public void testConstantBackoff()
  {
    BackoffPolicy<Void> policy = BackoffPolicy.constant(100);
    assertEquals(policy.nextBackoff(1, null), 100);
    assertEquals(policy.nextBackoff(2, null), 100);
    assertEquals(policy.nextBackoff(3, null), 100);
    assertEquals(policy.nextBackoff(4, null), 100);
    assertEquals(policy.nextBackoff(5, null), 100);
  }

  @Test
  public void testLinearBackoff()
  {
    BackoffPolicy<Void> policy = BackoffPolicy.linear(100);
    assertEquals(policy.nextBackoff(1, null), 100);
    assertEquals(policy.nextBackoff(2, null), 200);
    assertEquals(policy.nextBackoff(3, null), 300);
    assertEquals(policy.nextBackoff(4, null), 400);
    assertEquals(policy.nextBackoff(5, null), 500);
  }

  @Test
  public void testExponentialBackoff()
  {
    BackoffPolicy<Void> policy = BackoffPolicy.exponential(100);
    assertEquals(policy.nextBackoff(1, null), 100);
    assertEquals(policy.nextBackoff(2, null), 200);
    assertEquals(policy.nextBackoff(3, null), 400);
    assertEquals(policy.nextBackoff(4, null), 800);
    assertEquals(policy.nextBackoff(5, null), 1600);
  }

  @Test
  public void testFibonacciBackoff()
  {
    BackoffPolicy<Void> policy = BackoffPolicy.fibonacci(100);
    assertEquals(policy.nextBackoff(1, null), 100);
    assertEquals(policy.nextBackoff(2, null), 160);
    assertEquals(policy.nextBackoff(3, null), 256);
    assertEquals(policy.nextBackoff(4, null), 410);
    assertEquals(policy.nextBackoff(5, null), 655);
  }

  @Test
  public void testRandomizedBackoff()
  {
    BackoffPolicy<Void> policy = BackoffPolicy.randomized(BackoffPolicy.exponential(100), -100, 100);
    assertTrue(policy.nextBackoff(1, null) >= 0);
    assertTrue(policy.nextBackoff(1, null) <= 200);
    assertTrue(policy.nextBackoff(2, null) >= 100);
    assertTrue(policy.nextBackoff(2, null) <= 300);
    assertTrue(policy.nextBackoff(3, null) >= 300);
    assertTrue(policy.nextBackoff(3, null) <= 500);
    assertTrue(policy.nextBackoff(4, null) >= 700);
    assertTrue(policy.nextBackoff(4, null) <= 900);
    assertTrue(policy.nextBackoff(5, null) >= 1500);
    assertTrue(policy.nextBackoff(5, null) <= 1700);
  }

  @Test
  public void testSelectedBackoff()
  {
    BackoffPolicy<Void> policy = BackoffPolicy.selected(error -> error.getError().getMessage().isEmpty() ? BackoffPolicy.constant(100) : BackoffPolicy.constant(200));
    assertEquals(policy.nextBackoff(1, Failure.of(new RuntimeException(""))), 100);
    assertEquals(policy.nextBackoff(2, Failure.of(new RuntimeException("x"))), 200);
    assertEquals(policy.nextBackoff(3, Failure.of(new RuntimeException(""))), 100);
    assertEquals(policy.nextBackoff(4, Failure.of(new RuntimeException("x"))), 200);
  }
}
