package com.linkedin.parseq.internal;

import junit.framework.Assert;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author  Zhenkai Zhu
 */
public class TestCachedLoggerFactory {

  @Test
  public void testCaching() throws Exception {
    CountingLoggerFactory loggerFactory = new CountingLoggerFactory();
    final ILoggerFactory cachedFactory = new CachedLoggerFactory(loggerFactory);

    ExecutorService executorService = Executors.newFixedThreadPool(5);
    CountDownLatch startRace = new CountDownLatch(1);
    CountDownLatch stopRace = new CountDownLatch(5);
    for (int i = 0; i < 5; i++) {
      executorService.submit(() -> {
        try {
          startRace.await();
          cachedFactory.getLogger("com.linkedin.parseq.Task");
          stopRace.countDown();
        } catch (Exception e) {
          Assert.fail();
        }
      } );
    }
    // start race
    startRace.countDown();
    stopRace.await(5000, TimeUnit.MILLISECONDS);
    Assert.assertEquals(loggerFactory.getCount(), 1);
  }

  private static class CountingLoggerFactory implements ILoggerFactory {
    private final AtomicInteger _count = new AtomicInteger(0);
    private final ILoggerFactory _loggerFactory = LoggerFactory.getILoggerFactory();

    @Override
    public Logger getLogger(String s) {
      _count.incrementAndGet();
      return _loggerFactory.getLogger(s);
    }

    public int getCount() {
      return _count.get();
    }
  }
}
