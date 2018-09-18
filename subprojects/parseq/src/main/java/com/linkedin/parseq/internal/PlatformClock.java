package com.linkedin.parseq.internal;

import java.util.concurrent.TimeUnit;

/**
 * Default implementation of Clock interface that delegates to default Java platform's nano timer.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
public class PlatformClock implements Clock {

  @Override
  public long nanoTime() {
    return System.nanoTime();
  }

  @Override
  public void sleepNano(long nao) throws InterruptedException {
    TimeUnit.NANOSECONDS.sleep(nao);
  }

}
