package com.linkedin.parseq.promise;

import java.util.ArrayDeque;
import java.util.Deque;

class Continuations {

  private static final ThreadLocal<Continuations> CONTINUATIONS = new ThreadLocal<Continuations>() {
    @Override
    protected Continuations initialValue() {
      Continuations conts = new Continuations();
      conts._inactive = new ArrayDeque<>();
      conts._scheduled = new ArrayDeque<>();
      return conts;
    }
  };

  Deque<Runnable> _active;
  Deque<Runnable> _inactive;
  Deque<Runnable> _scheduled;

  private Continuations() { }

  public static final Continuations get() {
    return CONTINUATIONS.get();
  }

}