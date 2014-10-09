package com.linkedin.parseq;

import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;

/**
 * A {@link Task} that returns a value known before the task
 * itself is instantiated.
 * <p/>
 * Use {@link Tasks#value} to create instances of this class.
 */
/* package private */ class ValueTask<T> extends BaseTask<T> {

  private final T _value;

  ValueTask(String name, T value) {
    super(name);
    _value = value;
  }

  @Override
  protected Promise<? extends T> run(Context context) throws Throwable {
    return Promises.value(_value);
  }
}
