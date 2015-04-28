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

  private final ThreadLocal<Continuation> CONTINUATION = new ThreadLocal<Continuation>() {
    @Override
    protected Continuation initialValue() {
      Continuation cont = new Continuation();
      cont._inactive = new ArrayDeque<>();
      cont._scheduled = new ArrayDeque<>();
      return cont;
    }
  };

  public void submit(final Runnable action) {
    CONTINUATION.get().submit(action);
  }

  private static final class Continuation {
    // contains actions scheduled in the current recurrence level
    private Deque<Runnable> _active;

    // variable to cache empty deque instance
    private Deque<Runnable> _inactive;

    // contains actions scheduled for execution
    private Deque<Runnable> _scheduled;

    private void submit(final Runnable action) {
      if (_active == null) {
        // we are at the root level of a call tree
        // this branch contains main loop responsible for
        // executing all actions
        _active = _inactive;
        _scheduled.add(action);
        loop();
      } else {
        // not a root level, just schedule an action
        _active.add(action);
      }
    }

    private void loop() {
      //  Entering state:
      //  - _active is empty
      //  - _scheduled has one action
      try {
        do {
          final Runnable next = _scheduled.pollFirst();
          next.run();
          while (!_active.isEmpty()) {
            _scheduled.addFirst(_active.pollLast());
          }
        } while (!_scheduled.isEmpty());
      } finally {
        // maintain invariants
        _scheduled.clear();
        _active.clear();
        _inactive = _active;
        _active = null;
      }
      //  Exiting state (even when exception is thrown):
      //  - _active is null
      //  - _scheduled and _inactive is empty
    }
  }
}
