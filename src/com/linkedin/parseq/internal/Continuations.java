package com.linkedin.parseq.internal;

import java.util.ArrayDeque;
import java.util.Deque;


/**
 * This class allows running the following code structure:
 * <pre><code>
 * method() {
 *   action1()
 *   ...
 *   actionN()
 * }
 * </code></pre>
 * such that {@code actionX} can throw exception or recursively call {@code method}
 * multiple times, without worry about stack overflow.
 * <p>
 * The guarantee is that actions are called in the same order than recursive approch
 * would have called them.
 * <p>
 * You can imagine recursive invocations as walking an execution tree in DFS order where
 * order of visiting children of a tree matters. This class implements DFS walk
 * in such a way that used stack is not proportional to the tree height, instead used heap
 * is proportional to the tree height.
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

  public void submit(final Runnable action) {
    final Continuations conts = get();
    if (conts._active == null) {
      //invariants: _scheduled and _inactive are empty
      conts._active = conts._inactive;
      conts._scheduled.add(action);
      try {
        while (!conts._scheduled.isEmpty()) {
          final Runnable next = conts._scheduled.pollFirst();
          next.run();
          while (!conts._active.isEmpty()) {
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
    } else {
      conts._active.add(action);
    }
  }
}
