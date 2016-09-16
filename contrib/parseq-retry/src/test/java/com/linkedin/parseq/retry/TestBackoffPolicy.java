package com.linkedin.parseq.retry;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.function.Failure;
import com.linkedin.parseq.retry.backoff.BackoffPolicy;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


public class TestBackoffPolicy extends BaseEngineTest {
  @Test
  public void testConstantBackoff()
  {
    BackoffPolicy policy = BackoffPolicy.constant(100);
    assertEquals(policy.nextBackoff(1, null), 100);
    assertEquals(policy.nextBackoff(2, null), 100);
    assertEquals(policy.nextBackoff(3, null), 100);
    assertEquals(policy.nextBackoff(4, null), 100);
    assertEquals(policy.nextBackoff(5, null), 100);
  }

  @Test
  public void testLinearBackoff()
  {
    BackoffPolicy policy = BackoffPolicy.linear(100);
    assertEquals(policy.nextBackoff(1, null), 100);
    assertEquals(policy.nextBackoff(2, null), 200);
    assertEquals(policy.nextBackoff(3, null), 300);
    assertEquals(policy.nextBackoff(4, null), 400);
    assertEquals(policy.nextBackoff(5, null), 500);
  }

  @Test
  public void testExponentialBackoff()
  {
    BackoffPolicy policy = BackoffPolicy.exponential(100);
    assertEquals(policy.nextBackoff(1, null), 100);
    assertEquals(policy.nextBackoff(2, null), 200);
    assertEquals(policy.nextBackoff(3, null), 400);
    assertEquals(policy.nextBackoff(4, null), 800);
    assertEquals(policy.nextBackoff(5, null), 1600);
  }

  @Test
  public void testFibonacciBackoff()
  {
    BackoffPolicy policy = BackoffPolicy.fibonacci(100);
    assertEquals(policy.nextBackoff(1, null), 100);
    assertEquals(policy.nextBackoff(2, null), 160);
    assertEquals(policy.nextBackoff(3, null), 256);
    assertEquals(policy.nextBackoff(4, null), 410);
    assertEquals(policy.nextBackoff(5, null), 655);
  }

  @Test
  public void testRandomizedBackoff()
  {
    BackoffPolicy policy = BackoffPolicy.randomized(BackoffPolicy.exponential(100), -100, 100);
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
    BackoffPolicy policy = BackoffPolicy.selected(error -> error.getMessage().isEmpty() ? BackoffPolicy.constant(100) : BackoffPolicy.constant(200));
    assertEquals(policy.nextBackoff(1, new RuntimeException("")), 100);
    assertEquals(policy.nextBackoff(2, new RuntimeException("x")), 200);
    assertEquals(policy.nextBackoff(3, new RuntimeException("")), 100);
    assertEquals(policy.nextBackoff(4, new RuntimeException("x")), 200);
  }
}
