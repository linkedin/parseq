package com.linkedin.parseq;

import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionStage;

public class ParSeqHelper {
  /**
   * Helper method to create a Task instance from a ()->CompletionStage
   */
  public static <T> Task<T> toTask(final Callable<CompletionStage<? extends T>> callable)
  {
    return Task.async(() -> {
      final SettablePromise<T> promise = Promises.settable();
      CompletionStage<? extends T> future = callable.call();
      future.whenComplete((value, exception) -> {
        if (exception != null) {
          promise.fail(exception);
        }
        else {
          promise.done(value);
        }
      });
      return promise;
    });
  }
}
