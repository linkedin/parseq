package com.linkedin.parseq.retry;

import com.linkedin.parseq.BaseEngineTest;
import com.linkedin.parseq.retry.termination.AlwaysTerminate;
import com.linkedin.parseq.retry.termination.LimitAttempts;
import com.linkedin.parseq.retry.termination.LimitDuration;
import com.linkedin.parseq.retry.termination.NeverTerminate;
import com.linkedin.parseq.retry.termination.RequireBoth;
import com.linkedin.parseq.retry.termination.RequireEither;
import com.linkedin.parseq.retry.termination.TerminationPolicy;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;


public class TestTerminationPolicy extends BaseEngineTest {
  @Test
  public void testLimitAttempts()
  {
    TerminationPolicy policy = new LimitAttempts(10);
    assertFalse(policy.shouldTerminate(0, 0));
    assertFalse(policy.shouldTerminate(9, 0));
    assertTrue(policy.shouldTerminate(10, 0));
    assertTrue(policy.shouldTerminate(100, 0));
  }

  @Test
  public void testLimitDuration()
  {
    TerminationPolicy policy = new LimitDuration(100);
    assertFalse(policy.shouldTerminate(10, 0));
    assertFalse(policy.shouldTerminate(0, 99));
    assertTrue(policy.shouldTerminate(10, 100));
    assertTrue(policy.shouldTerminate(0, 200));
  }

  @Test
  public void testRequireBoth()
  {
    TerminationPolicy policy = new RequireBoth(new LimitAttempts(10), new LimitDuration(100));
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
    TerminationPolicy policy = new RequireEither(new LimitAttempts(10), new LimitDuration(100));
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
    TerminationPolicy policy = new AlwaysTerminate();
    assertTrue(policy.shouldTerminate(0, 0));
    assertTrue(policy.shouldTerminate(10, 0));
    assertTrue(policy.shouldTerminate(0, 100));
  }

  @Test
  public void testNeverTerminate()
  {
    TerminationPolicy policy = new NeverTerminate();
    assertFalse(policy.shouldTerminate(0, 0));
    assertFalse(policy.shouldTerminate(10, 0));
    assertFalse(policy.shouldTerminate(0, 100));
  }
}
