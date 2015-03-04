package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

public class TimeoutContextRunWrapper<T> implements ContextRunWrapper<T> {

  protected final SettablePromise<T> _result = Promises.settable();
  protected final AtomicBoolean _committed = new AtomicBoolean();
  private final long _time;
  private final TimeUnit _unit;
  private final Exception _exception;

  public TimeoutContextRunWrapper(long time, TimeUnit unit, final Exception exception) {
    _time = time;
    _unit = unit;
    _exception = exception;
  }

  @Override
  public void before(Context context) {
    final Task<?> timeoutTask = Task.action("timeoutTimer", () -> {
      if (_committed.compareAndSet(false, true)) {
        _result.fail(_exception);
      }
    });
    //timeout tasks should run as early as possible
    timeoutTask.setPriority(Priority.MAX_PRIORITY);
    context.createTimer(_time, _unit, timeoutTask);
  }

  @Override
  public Promise<T> after(Context context, Promise<T> promise) {
    promise.addListener(p -> {
      if (_committed.compareAndSet(false, true)) {
        Promises.propagateResult(promise, _result);
      }
    });
    return _result;
  }
}