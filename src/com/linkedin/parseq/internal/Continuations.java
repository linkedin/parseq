package com.linkedin.parseq.internal;


import java.util.ArrayDeque;
import java.util.Deque;

import com.linkedin.parseq.ParSeqGlobalConfiguration;

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
 * The guarantee is that actions are called in the same order that recursive approach
 * would have called them. To put it in another way, this class guarantees to traversal the
 * execution tree in pre-order.
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
      return new Continuation();
    }
  };

  public void submit(final Runnable action) {
    if (ParSeqGlobalConfiguration.isTrampolineEnabled()) {
      CONTINUATION.get().submit(action);
    } else {
      action.run();
    }
  }

  private static final class Continuation {
    // contains sibling actions in reverse order of submission
    // sibling actions are actions submitted by the same parent action
    private final Deque<Runnable> _siblingActions = new ArrayDeque<>();

    // contains actions for execution in pre-order
    // actions with larger depth at the top
    // for sibling actions with the same depth, the first submitted is at the top
    private final Deque<Runnable> _preOrderExecutionStack = new ArrayDeque<>();

    private boolean _inLoop = false;

    private void submit(final Runnable action) {
      if (!_inLoop) {
        // we are at the root level of a call tree
        // this branch contains main loop responsible for
        // executing all actions
        _preOrderExecutionStack.push(action);
        loop();
      } else {
        // another child action added by the current action
        _siblingActions.push(action);
      }
    }

    private void loop() {
      //  Entering state:
      //  - _siblingActions is empty
      //  - _preOrderExecutionStack has one element
      //  - _inLoop is false

      _inLoop = true;
      try {
        do {
          _preOrderExecutionStack.pop().run();

          // currentAction could have submitted a few children actions, so we pop them out from
          // _siblingActions & push them into _preOrderExecutionStack, resulting in the desired
          // pre-order execution
          while (_siblingActions.size() > 0) {
            _preOrderExecutionStack.push(_siblingActions.pop());
          }
        } while (_preOrderExecutionStack.size() > 0);
      } finally {
        // maintain invariants
        _preOrderExecutionStack.clear();
        _siblingActions.clear();
        _inLoop = false;
      }
      //  Exiting state (even when exception is thrown):
      //  - _siblingActions is empty
      //  - _preOrderExecutionStack is empty
      //  - _inLoop is false
    }
  }
}