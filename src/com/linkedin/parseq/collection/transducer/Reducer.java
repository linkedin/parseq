package com.linkedin.parseq.collection.transducer;

import java.util.function.BiFunction;

import com.linkedin.parseq.TaskOrValue;

@FunctionalInterface
public interface Reducer<Z, T> extends BiFunction<Ref<Z>, TaskOrValue<T>, TaskOrValue<Reducer.Step<Z>>>{

  static final class Step<S> {

    private final S _value;
    private final FlowControl _flow;

    private Step(FlowControl flow, S value) {
      _flow = flow;
      _value = value;
    }

    public static <S> Step<S> cont(S value) {
      return new Step<S>(FlowControl.cont, value);
    }

    public static <S> Step<S> done(S value) {
      return new Step<S>(FlowControl.done, value);
    }

    public S getValue() {
      return _value;
    }

    public FlowControl getType() {
      return _flow;
    }

    @Override
    public String toString() {
      return "Step [flow=" + _flow + ", value=" + _value + "]";
    }

  }

}
