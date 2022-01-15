package com.linkedin.parseq.guava;

import com.linkedin.parseq.promise.Promises;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.SettablePromise;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;


/**
 * Utility methods to convert between Parseq {@link Task} and Guava's {@link ListenableFuture}.
 */
public class ListenableFutureUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(ListenableFutureUtil.class);

  private ListenableFutureUtil() {
    // Prevent instantiation.
  }

  public static <T> Task<T> fromListenableFuture(ListenableFuture<T> future) {
    // Setup cancellation propagation from Task -> ListenableFuture.
    final SettableTask<T> task =
        new SettableTask<T>("fromListenableFuture: " + Task._taskDescriptor.getDescription(future.getClass().getName())) {
          @Override
          public boolean cancel(Exception rootReason) {
            if (future.isCancelled()) {
              return super.cancel(rootReason);
            }

            return super.cancel(rootReason) && future.cancel(true);
          }
        };

    final SettablePromise<T> promise = task.getSettablePromise();

    // Setup forward event propagation ListenableFuture -> Task.
    Runnable callbackRunnable = () -> {
      if (promise.isDone()) {
        boolean isPromiseFailed = promise.isFailed();
        LOGGER.warn("ListenableFuture callback triggered but ParSeq already done. "
                + "Future is done: {}, "
                + "Future is cancelled: {}"
                + "Promise is failed:{}"
            + (isPromiseFailed? " Promise hold error: {}" : "Promise hold data:{}"),
            future.isDone(),
            future.isCancelled(),
            isPromiseFailed,
            isPromiseFailed ? promise.getError(): promise.get()
            );
        return;
      }
      try {
        final T value = future.get();
        promise.done(value);
      } catch (CancellationException ex) {
        task.cancel(ex);
      } catch (ExecutionException ex) {
        promise.fail(ex.getCause());
      } catch (Exception | Error ex) {
        promise.fail(ex);
      }
    };
    future.addListener(callbackRunnable, MoreExecutors.directExecutor());

    return task;
  }

  public static <T> ListenableFuture<T> toListenableFuture(Task<T> task) {
    // Setup cancellation propagation from ListenableFuture -> Task.
    SettableFuture<T> listenableFuture = new SettableFuture<T>() {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        return super.cancel(mayInterruptIfRunning) && task.cancel(new CancellationException());
      }

      @Override
      public boolean setException(Throwable ex) {
        if (!task.isDone() && ex instanceof CancellationException) {
          task.cancel((CancellationException) ex);
        }
        return super.setException(ex);
      }
    };

    // Setup forward event propagation Task -> ListenableFuture.
    task.addListener(promise -> {
      if (!promise.isFailed()) {
        listenableFuture.set(promise.get());
      }
      else {
        if (promise.getError() instanceof com.linkedin.parseq.CancellationException) {
          listenableFuture.cancel(true);
        } else {
          listenableFuture.setException(promise.getError());
        }
      }
    });

    return listenableFuture;
  }

  /**
   * A private helper class to assist toListenableFuture(), by overriding some methods to make them public.
   *
   * @param <T> The Settable future's type.
   */
  @VisibleForTesting
  static class SettableFuture<T> extends AbstractFuture<T> {
    @Override
    public boolean set(T value) {
      return super.set(value);
    }

    @Override
    public boolean setException(Throwable throwable) {
      return super.setException(throwable);
    }
  }

  /**
   * A private helper class to assist fromListenableFuture(), by overriding some methods to make them public.
   *
   * @param <T> The Settable task's type.
   */
  @VisibleForTesting
  static class SettableTask<T> extends BaseTask<T> {
    private final SettablePromise _promise = Promises.settable();

    public SettableTask(String name) {
      super(name);
    }

    @Override
    protected Promise<? extends T> run(Context context) throws Throwable {
      return _promise;
    }

    public SettablePromise<T> getSettablePromise() {
      return _promise;
    }
  }
}
