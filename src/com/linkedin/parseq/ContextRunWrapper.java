package com.linkedin.parseq;

import com.linkedin.parseq.promise.Promise;

public interface ContextRunWrapper<T> {

  void before(Context context);

  Promise<T> after(Context context, Promise<T> promise);

  default ContextRunWrapper<T> compose(final ContextRunWrapper<T> wrapper) {
    ContextRunWrapper<T> that = this;
    return new ContextRunWrapper<T>() {

      @Override
      public void before(Context context) {
        wrapper.before(context);
        that.before(context);
      }

      @Override
      public Promise<T> after(Context context, Promise<T> promise) {
        return wrapper.after(context, that.after(context, promise));
      }
    };
  }
}