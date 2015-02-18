package com.linkedin.parseq;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import com.linkedin.parseq.internal.SystemHiddenTask;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromisePropagator;
import com.linkedin.parseq.promise.PromiseResolvedException;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.Settable;
import com.linkedin.parseq.promise.SettablePromise;

/**
 * TODO define how cancellation is supposed to work
 *
 * @author jodzga
 *
 * @param <S>
 * @param <T>
 */
//TODO zmienic w jaki sposob task jest hidded - nie przez inheritance
public class FusionTask<S, T>  extends SystemHiddenTask<T> {

  private static final String FUSION_TRACE_SYMBOL = " => ";

  private final PromisePropagator<S, T> _propagator;
  private final Task<S> _task;

  private FusionTask(final String name, final Task<S> task, final PromisePropagator<S, T> propagator) {
    super(name);
    _propagator = propagator;
    _task = task;
  }

  private PromisePropagator<S, T> fulfilling(final PromisePropagator<S, T> propagator) {
    return (src, dest) -> {
      propagator.accept(src, new Settable<T>() {

        @Override
        public void done(final T value) throws PromiseResolvedException {
          try {
            final SettablePromise<T> settable = FusionTask.this.getSettableDelegate();
            if (!settable.isDone()) {
              settable.done(value);
            }
          } finally {
            dest.done(value);
          }
        }

        @Override
        public void fail(final Throwable error) throws PromiseResolvedException {
          try {
            final SettablePromise<T> settable = FusionTask.this.getSettableDelegate();
            if (!settable.isDone()) {
              settable.fail(error);
            }
          } finally {
            dest.fail(error);
          }
        }
      });
    };
  }

  @SuppressWarnings("unchecked")
  public static <S, T> FusionTask<?, T> fuse(final String name, final Task<S> task, final PromisePropagator<S, T> propagator) {
    if (task instanceof FusionTask) {
      return ((FusionTask<?, S>)task).apply(name, propagator);
    } else {
      return new FusionTask<S, T>(name, task, propagator);
    }
  }

  @Override
  public <R> FusionTask<?, R> apply(String desc, PromisePropagator<T,R> propagator) {
    return fuse(desc, _task, fulfilling(_propagator).compose(propagator));
  };

  @Override
  public <R> Task<R> map(final String desc, final Function<T,R> f) {
    return fuse(getName() + FUSION_TRACE_SYMBOL + desc, _task, fulfilling(_propagator).map(f));
  }

  @Override
  public Task<T> andThen(final String desc, final Consumer<T> consumer) {
    return fuse(getName() + FUSION_TRACE_SYMBOL + desc, _task,
        fulfilling(_propagator).andThen(consumer));
  }

  @Override
  public Task<T> recover(final String desc, final Function<Throwable, T> f) {
    return fuse(getName() +FUSION_TRACE_SYMBOL + desc, _task, (src, dst) -> {
      fulfilling(_propagator).accept(src, new Settable<T>() {
        @Override
        public void done(T value) throws PromiseResolvedException {
          dst.done(value);
        }
        @Override
        public void fail(Throwable error) throws PromiseResolvedException {
          try {
            dst.done(f.apply(error));
          } catch (Throwable t) {
            dst.fail(t);
          }
        }
      });
    });
  }

  //TODO implement other functions

  protected SettablePromise<T> propagate(Promise<S> promise, SettablePromise<T> result) {
    try {
      _propagator.accept(_task, result);
      return result;
    } catch (Throwable t) {
      result.fail(t);
      return result;
    }
  }

  @Override
  protected Promise<? extends T> run(Context context) throws Throwable {
    final SettablePromise<T> result = Promises.settable();
    context.after(_task).run(new SystemHiddenTask<T>(FusionTask.this.getName()) {
      @Override
      protected Promise<? extends T> run(Context context) throws Throwable {
        return propagate(_task, result);
      }
    });
    context.run(_task);
    return result;
  }

  @Override
  public Task<T> within(final long time, final TimeUnit unit) {
    _task.within(time, unit);
    return this;
  }

  @Override
  public Task<T> withTimeout(long time, TimeUnit unit) {
    _task.withTimeout(time, unit);
    return this;
  }

}
