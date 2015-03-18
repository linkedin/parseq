package com.linkedin.parseq.internal;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * This class allows running the following code structure:
 * <pre><code>
 * method() {
 *   action()
 *   onSuccess()
 * }
 * </code></pre>
 * such that {@code action} can throw exception and can recursively call {@code method},
 * without worry about stack overflow.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 */
public class Continuations {

  private final ThreadLocal<Continuations> CONTINUATIONS = new ThreadLocal<Continuations>() {
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

  public final Continuations get() {
    return CONTINUATIONS.get();
  }

  private static class Continuation {
    private final Runnable _action;
    private final Runnable _onSuccess;

    public Continuation(Runnable action, Runnable onSuccess) {
      _action = action;
      _onSuccess = onSuccess;
    }
  }

  public void submit(final Runnable action, Runnable onSuccess) {
    submit(new Continuation(action, onSuccess));
  }

  private void submit(final Continuation contiuation) {
    final Continuations conts = get();
    if (conts._active == null) {
      //invariants: _scheduled and _inactive are empty
      conts._active = conts._inactive;
      conts._scheduled.add(contiuation._action);
      try {
        while (!conts._scheduled.isEmpty()) {
          final Runnable next = conts._scheduled.pollFirst();
          next.run();
          while(!conts._active.isEmpty()) {
            conts._scheduled.addFirst(conts._active.pollLast());
          }
        }
      } finally {
        //maintain invariants
        conts._scheduled.clear();
        conts._active.clear();
        conts._inactive = conts._active;
        conts._active = null;
      }
      if (contiuation._onSuccess != null) {
        contiuation._onSuccess.run();
      }
    } else {
      conts._active.add(contiuation._action);
      if (contiuation._onSuccess != null) {
        conts._active.add(contiuation._onSuccess);
      }
    }
  }

}