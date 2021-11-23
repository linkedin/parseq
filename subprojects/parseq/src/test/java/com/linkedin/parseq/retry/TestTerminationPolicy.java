package com.linkedin.parseq.retry;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.retry.termination.GuavaRateLimiter;
import com.linkedin.parseq.retry.termination.RateLimiter;
import com.linkedin.parseq.retry.termination.TerminationPolicy;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;


public class TestTerminationPolicy extends BaseEngineTest {
  @Test
  public void testLimitAttempts()
  {
    TerminationPolicy policy = TerminationPolicy.limitAttempts(10);
    assertFalse(policy.shouldTerminate(0, 0));
    assertFalse(policy.shouldTerminate(9, 0));
    assertTrue(policy.shouldTerminate(10, 0));
    assertTrue(policy.shouldTerminate(100, 0));
  }

  @Test
  public void testLimitDuration()
  {
    TerminationPolicy policy = TerminationPolicy.limitDuration(100);
    assertFalse(policy.shouldTerminate(10, 0));
    assertFalse(policy.shouldTerminate(0, 99));
    assertTrue(policy.shouldTerminate(10, 100));
    assertTrue(policy.shouldTerminate(0, 200));
  }

  @Test
  public void testLimitRate() throws InterruptedException
  {
    RateLimiter rateLimiter = new GuavaRateLimiter(1);
    TerminationPolicy policy = TerminationPolicy.limitRate(rateLimiter);
    assertFalse(policy.shouldTerminate(1, 10));
    assertTrue(policy.shouldTerminate(0, 10));
    Thread.sleep(1000);
    assertFalse(policy.shouldTerminate(0, 10));
    assertTrue(policy.shouldTerminate(0, 10));
  }

  @Test
  public void testRequireBoth()
  {
    TerminationPolicy policy = TerminationPolicy.requireBoth(TerminationPolicy.limitAttempts(10), TerminationPolicy.limitDuration(100));
    assertFalse(policy.shouldTerminate(20, 0));
    assertFalse(policy.shouldTerminate(0, 200));
    assertFalse(policy.shouldTerminate(9, 99));
    assertFalse(policy.shouldTerminate(9, 100));
    assertFalse(policy.shouldTerminate(10, 99));
    assertTrue(policy.shouldTerminate(10, 100));
    assertTrue(policy.shouldTerminate(20, 100));
    assertTrue(policy.shouldTerminate(10, 200));
  }

  @Test
  public void testRequireEither()
  {
    TerminationPolicy policy = TerminationPolicy.requireEither(TerminationPolicy.limitAttempts(10), TerminationPolicy.limitDuration(100));
    assertFalse(policy.shouldTerminate(0, 0));
    assertFalse(policy.shouldTerminate(0, 99));
    assertFalse(policy.shouldTerminate(9, 0));
    assertFalse(policy.shouldTerminate(9, 99));
    assertTrue(policy.shouldTerminate(10, 99));
    assertTrue(policy.shouldTerminate(9, 100));
    assertTrue(policy.shouldTerminate(20, 100));
    assertTrue(policy.shouldTerminate(10, 200));
  }

  @Test
  public void testRequireAny()
  {
    TerminationPolicy policy = TerminationPolicy.requireAny(TerminationPolicy.limitAttempts(10), TerminationPolicy.limitDuration(100));
    assertFalse(policy.shouldTerminate(0, 0));
    assertFalse(policy.shouldTerminate(0, 99));
    assertFalse(policy.shouldTerminate(9, 0));
    assertFalse(policy.shouldTerminate(9, 99));
    assertTrue(policy.shouldTerminate(10, 99));
    assertTrue(policy.shouldTerminate(9, 100));
    assertTrue(policy.shouldTerminate(20, 100));
    assertTrue(policy.shouldTerminate(10, 200));
  }

  @Test
  public void testAlwaysTerminate()
  {
    TerminationPolicy policy = TerminationPolicy.alwaysTerminate();
    assertTrue(policy.shouldTerminate(0, 0));
    assertTrue(policy.shouldTerminate(10, 0));
    assertTrue(policy.shouldTerminate(0, 100));
  }

  @Test
  public void testNeverTerminate()
  {
    TerminationPolicy policy = TerminationPolicy.neverTerminate();
    assertFalse(policy.shouldTerminate(0, 0));
    assertFalse(policy.shouldTerminate(10, 0));
    assertFalse(policy.shouldTerminate(0, 100));
  }
}
