package com.linkedin.parseq.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


/**
 * Default plan based throttling by doing exact match with plan class name.
 *
 * @author Min Chen (mnchen@linkedin.com)
 */
public class DefaultPlanBasedRateLimiter implements PlanBasedRateLimiter {
  private final Map<String, Semaphore> _concurrentPlansPerClass;

  public DefaultPlanBasedRateLimiter(Map<String, Integer> planConcurrencyConfig) {
    _concurrentPlansPerClass = new ConcurrentHashMap<>(planConcurrencyConfig.size());
    planConcurrencyConfig.forEach((planClass, concurrency) -> {
      _concurrentPlansPerClass.put(planClass, new Semaphore(concurrency));
    });
  }

  @Override
  public boolean tryAcquire(String planClass) {
    return _concurrentPlansPerClass.containsKey(planClass) ?
        _concurrentPlansPerClass.get(planClass).tryAcquire() : true;
  }

  @Override
  public boolean tryAcquire(String planClass, long timeout, TimeUnit unit) throws InterruptedException {
    return _concurrentPlansPerClass.containsKey(planClass) ?
        _concurrentPlansPerClass.get(planClass).tryAcquire(timeout, unit) : true;
  }

  @Override
  public void acquire(String planClass) throws InterruptedException {
    if (_concurrentPlansPerClass.containsKey(planClass)) {
      _concurrentPlansPerClass.get(planClass).acquire();
    }
  }

  @Override
  public void release(String planClass) {
    if (_concurrentPlansPerClass.containsKey(planClass)) {
      _concurrentPlansPerClass.get(planClass).release();
    }
  }
}
