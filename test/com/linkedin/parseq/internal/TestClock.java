package com.linkedin.parseq.internal;

import java.util.concurrent.Callable;

import com.linkedin.parseq.function.Consumer1;

public class TestClock implements Clock {

  private final Callable<Long> _timer;

  private final Consumer1<Long> _sleeper;

  public TestClock(Callable<Long> timer, Consumer1<Long> sleeper) {
    _timer = timer;
    _sleeper = sleeper;
  }

  @Override
  public long nanoTime() {
    try {
      return _timer.call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void sleepNano(long nano) throws InterruptedException {
    try {
      _sleeper.accept(nano);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


}
