package com.linkedin.parseq;

import com.linkedin.parseq.DelayedExecutor;
import com.linkedin.parseq.internal.CancellableScheduledFuture;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Adapts a {@link ScheduledExecutorService} to the simpler
 * {@link DelayedExecutor} interface.
 *
 * @author Chris Pettitt
 */
public class DelayedExecutorAdapter implements DelayedExecutor
{
  private final ScheduledExecutorService _scheduler;

  public DelayedExecutorAdapter(final ScheduledExecutorService scheduler)
  {
    _scheduler = scheduler;
  }

  @Override
  public Cancellable schedule(final long delay, final TimeUnit unit,
                              final Runnable command)
  {
    return new CancellableScheduledFuture(_scheduler.schedule(command, delay, unit));
  }
}
