package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.linkedin.parseq.Cancellable;

/**
 * A wrapper around a delayed executor that provides better behavior for
 * cancellation. If a task is cancelled (or run), we forget the reference to
 * that task. Without this indirection it is possible that the underlying
 * scheduled executor will hold a reference to the task - even if it has been
 * cancelled - thus keeping a path to the task from a GC root.
 *
 * TODO understand how this works and verify that it is expected
 *
 * @author Chris Pettitt
 */
/* package private */ class IndirectDelayedExecutor implements DelayedExecutor
{
  private final DelayedExecutor _executor;

  public IndirectDelayedExecutor(final DelayedExecutor executor)
  {
    _executor = executor;
  }

  @Override
  public Cancellable schedule(final long delay, final TimeUnit unit, final Runnable command)
  {
    final IndirectRunnable indirectRunnable = new IndirectRunnable(command);
    final Cancellable cancellable = _executor.schedule(delay, unit, indirectRunnable);
    return new IndirectCancellable(cancellable, indirectRunnable);
  }

  private static class IndirectRunnable implements Runnable
  {
    private AtomicReference<Runnable> _commandRef;

    public IndirectRunnable(final Runnable command)
    {
      _commandRef = new AtomicReference<Runnable>(command);
    }

    @Override
    public void run()
    {
      final Runnable runnable = _commandRef.get();
      if (runnable != null && _commandRef.compareAndSet(runnable, null))
      {
        runnable.run();
      }
    }

    public boolean cancel()
    {
      final Runnable runnable = _commandRef.get();
      return _commandRef.compareAndSet(runnable, null);
    }
  }

  private static class IndirectCancellable implements Cancellable
  {
    private final Cancellable _cancellable;
    private final IndirectRunnable _runnable;

    private IndirectCancellable(final Cancellable cancellable, final IndirectRunnable runnable)
    {
      _cancellable = cancellable;
      _runnable = runnable;
    }

    @Override
    public boolean cancel(final Exception reason)
    {
      return _runnable.cancel() && _cancellable.cancel(reason);
    }
  }
}
