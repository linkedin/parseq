package com.linkedin.parseq;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


/**
 * @author Chris Pettitt
 */
public class TestIndirectDelayedExecutor {
  private ScheduledExecutorService _executorService;
  private IndirectDelayedExecutor _executor;

  @BeforeMethod
  public void setUp() {
    _executorService = Executors.newScheduledThreadPool(4);
    _executor = new IndirectDelayedExecutor(new DelayedExecutorAdapter(_executorService));
  }

  @AfterMethod
  public void tearDown() {
    _executorService.shutdownNow();
  }

  @Test
  public void testSchedule() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final Cancellable cancellable = _executor.schedule(50, TimeUnit.MILLISECONDS, new Runnable() {
      @Override
      public void run() {
        latch.countDown();
      }
    });

    assertTrue(latch.await(100, TimeUnit.MILLISECONDS));

    // Should not be able to cancel now
    assertFalse(cancellable.cancel(new Exception()));
  }

  @Test
  public void testCancel() throws InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    final Cancellable cancellable = _executor.schedule(100, TimeUnit.MILLISECONDS, new Runnable() {
      @Override
      public void run() {
        latch.countDown();
      }
    });

    assertTrue(cancellable.cancel(new Exception()));
    assertFalse(cancellable.cancel(new Exception()));
    assertFalse(latch.await(150, TimeUnit.MILLISECONDS));
  }
}
