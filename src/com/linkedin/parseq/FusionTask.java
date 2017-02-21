package com.linkedin.parseq;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.function.Consumer3;
import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.PromisePropagator;
import com.linkedin.parseq.promise.PromiseResolvedException;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.Settable;
import com.linkedin.parseq.promise.SettablePromise;
import com.linkedin.parseq.trace.Relationship;
import com.linkedin.parseq.trace.ResultType;
import com.linkedin.parseq.trace.ShallowTraceBuilder;
import com.linkedin.parseq.trace.TraceBuilder;


/**
 * FusionTask implements optimization in case when multiple synchronous
 * transformations are applied in sequence. Instead of rescheduling every transformation
 * as a separate task it is faster to apply transformations in a chain.
 *
 * This is internal class and it should not be extended or used outside ParSeq.
 * It may change or be removed at any time in a backwards incompatible way.
 *
 * @author Jaroslaw Odzga (jodzga@linkedin.com)
 *
 * @param <S> type of source
 * @param <T> type of target
 */
class FusionTask<S, T> extends BaseTask<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FusionTask.class);

  /* Encapsulates transformation from source to target, includes all predecessors' transformations, can't be null */
  private final Consumer3<FusionTraceContext, Promise<S>, Settable<T>> _propagator;

  /* Asynchronous task that will complete source promise, can be null in which case propagator must not depend on source */
  private final Task<S> _asyncTask;

  /* Trace builder for the predecessor, used for building trace relationships, can be null */
  private final ShallowTraceBuilder _predecessorShallowTraceBuilder;

  private FusionTask(final String desc, final Task<S> task, final PromisePropagator<S, T> propagator) {
    super(desc, TaskType.FUSION.getName());
    _propagator = completing(adaptToAcceptTraceContext(propagator));
    _asyncTask = task;
    _predecessorShallowTraceBuilder = null;
  }

  private <R> FusionTask(final String desc, final FusionTask<S, R> predecessor,
      final PromisePropagator<R, T> propagator) {
    super(desc, TaskType.FUSION.getName());
    _asyncTask = predecessor._asyncTask;
    _predecessorShallowTraceBuilder = predecessor.getShallowTraceBuilder();
    _propagator = completing(compose(predecessor._propagator, adaptToAcceptTraceContext(propagator)));
  }

  /**
   * Drops FusionTraceContext on the floor. This method adapts pure transformation to be used within FusionTask
   * that needs additional information (FusionTraceContext) to build trace.
   */
  private static <A, B> Consumer3<FusionTraceContext, Promise<A>, Settable<B>> adaptToAcceptTraceContext(
      final PromisePropagator<A, B> propagator) {
    return (traceContext, src, dest) -> propagator.accept(src, dest);
  }

  private void transitionDone(final FusionTraceContext traceContext) {
    addRelationships(traceContext);
    transitionPending();
    transitionDone();
  }

  /**
   * Has current task initiated propagation?
   */
  private boolean isPropagationInitiator(final FusionTraceContext traceContext) {
    return traceContext.getPropagationInitiator().getId().equals(getId());
  }

  /**
   * Depending on the context, returns trace builder that will contain information about the
   * transformation. This is used to avoid wrapping single transformation with "fused" parent.
   */
  private ShallowTraceBuilder getEffectiveShallowTraceBuilder(final FusionTraceContext traceContext) {
    if (isPropagationInitiator(traceContext)) {
      return traceContext.getSurrogate();
    } else {
      return _shallowTraceBuilder;
    }
  }

  private void addRelationships(final FusionTraceContext traceContext) {
    final ShallowTraceBuilder effectoveShallowTraceBuilder = getEffectiveShallowTraceBuilder(traceContext);
    TraceBuilder builder = getTraceBuilder();
    builder.addRelationship(Relationship.PARENT_OF, traceContext.getParent().getShallowTraceBuilder(), effectoveShallowTraceBuilder);
    if (_predecessorShallowTraceBuilder != null) {
      builder.addRelationship(Relationship.SUCCESSOR_OF, effectoveShallowTraceBuilder, _predecessorShallowTraceBuilder);
    }
  }

  private void addPotentialRelationships(final FusionTraceContext traceContext, final TraceBuilder builder) {
    final ShallowTraceBuilder effectoveShallowTraceBuilder = getEffectiveShallowTraceBuilder(traceContext);
    builder.addRelationship(Relationship.POTENTIAL_CHILD_OF, effectoveShallowTraceBuilder, traceContext.getParent().getShallowTraceBuilder());
    if (_predecessorShallowTraceBuilder != null) {
      builder.addRelationship(Relationship.POSSIBLE_SUCCESSOR_OF, effectoveShallowTraceBuilder, _predecessorShallowTraceBuilder);
    }
  }

  /**
   * Composes transformation with the transformation of the predecessor.
   */
  private <R> Consumer3<FusionTraceContext, Promise<S>, Settable<T>> compose(
      final Consumer3<FusionTraceContext, Promise<S>, Settable<R>> predecessor,
      final Consumer3<FusionTraceContext, Promise<R>, Settable<T>> propagator) {

    return (traceContext, src, dst) -> {

      /*
       * At this point we know that transformation chain's length > 1.
       * This code is executed during task execution, not when task is constructed.
       */
      traceContext.createSurrogate();

      predecessor.accept(traceContext, src, new Settable<R>() {

        @Override
        public void done(R value) throws PromiseResolvedException {
          try {
            /* Track start time of the transformation. End time is tracked by closure created by completing() */
            getEffectiveShallowTraceBuilder(traceContext).setStartNanos(System.nanoTime());
            propagator.accept(traceContext, Promises.value(value), dst);
          } catch (Exception e) {
            /* This can only happen if there is an internal problem. Propagators should not throw any exceptions. */
            LOGGER.error("ParSeq ingternal error. An exception was thrown by propagator", e);
          }
        }

        @Override
        public void fail(Throwable error) throws PromiseResolvedException {
          try {
            /* Track start time of the transformation. End time is tracked by closure created by completing() */
            getEffectiveShallowTraceBuilder(traceContext).setStartNanos(System.nanoTime());
            propagator.accept(traceContext, Promises.error(error), dst);
          } catch (Exception e) {
            /* This can only happen if there is an internal problem. Propagators should not throw any exceptions. */
            LOGGER.error("ParSeq ingternal error. An exception was thrown by propagator.", e);
          }
        }
      });
    };
  }

  /**
   * Adds closure to the propagator that maintains correct state of the task e.g. transitions between states,
   * adds trace etc.
   */
  private Consumer3<FusionTraceContext, Promise<S>, Settable<T>> completing(
      final Consumer3<FusionTraceContext, Promise<S>, Settable<T>> propagator) {
    return (traceContext, src, dest) -> {
      final SettablePromise<T> settable = FusionTask.this.getSettableDelegate();

      if (isPropagationInitiator(traceContext)) {
        /* BaseTask's code handles completing the parent task
         * we need to handle tracing of a surrogate task here
         */
        propagator.accept(traceContext, src, new Settable<T>() {
          @Override
          public void done(T value) throws PromiseResolvedException {
            final ShallowTraceBuilder shallowTraceBuilder = traceContext.getSurrogate();
            if (shallowTraceBuilder != null) {
              addRelationships(traceContext);
              final long endNanos = System.nanoTime();
              shallowTraceBuilder.setPendingNanos(endNanos);
              shallowTraceBuilder.setEndNanos(endNanos);
              final Function<T, String> traceValueProvider = _traceValueProvider;
              shallowTraceBuilder.setResultType(ResultType.SUCCESS);
              if (traceValueProvider != null) {
                try {
                  shallowTraceBuilder.setValue(traceValueProvider.apply(value));
                } catch (Exception e) {
                  shallowTraceBuilder.setValue(Exceptions.failureToString(e));
                }
              }
            }
            dest.done(value);
          }

          @Override
          public void fail(Throwable error) throws PromiseResolvedException {
            final ShallowTraceBuilder shallowTraceBuilder = traceContext.getSurrogate();
            if (shallowTraceBuilder != null) {
              addRelationships(traceContext);
              final long endNanos = System.nanoTime();
              shallowTraceBuilder.setPendingNanos(endNanos);
              shallowTraceBuilder.setEndNanos(endNanos);
              if (Exceptions.isEarlyFinish(error)) {
                shallowTraceBuilder.setResultType(ResultType.EARLY_FINISH);
              } else {
                shallowTraceBuilder.setResultType(ResultType.ERROR);
                shallowTraceBuilder.setValue(Exceptions.failureToString(error));
              }
            }
            dest.fail(error);
          }
        });
      } else if (transitionRun(traceContext.getParent().getTraceBuilder())) {
        markTaskStarted();
        //non-parent task executed for the first time
        traceContext.getParent().getTaskLogger().logTaskStart(this);
          try {
            propagator.accept(traceContext, src, new Settable<T>() {
              @Override
              public void done(final T value) throws PromiseResolvedException {
                try {
                  transitionDone(traceContext);
                  final Function<T, String> traceValueProvider = _traceValueProvider;
                  _shallowTraceBuilder.setResultType(ResultType.SUCCESS);
                  if (traceValueProvider != null) {
                    try {
                      _shallowTraceBuilder.setValue(traceValueProvider.apply(value));
                    } catch (Exception e) {
                      _shallowTraceBuilder.setValue(Exceptions.failureToString(e));
                    }
                  }
                  settable.done(value);
                  //traceContext.getParent().getTaskLogger().logTaskEnd(FusionTask.this, _traceValueProvider);
                  dest.done(value);
                } catch (Exception e) {
                  dest.fail(e);
                }
              }

              @Override
              public void fail(final Throwable error) throws PromiseResolvedException {
                try {
                  transitionDone(traceContext);
                  traceFailure(error);
                  settable.fail(error);
                  //traceContext.getParent().getTaskLogger().logTaskEnd(FusionTask.this, _traceValueProvider);
                  dest.fail(error);
                } catch (Exception e) {
                  dest.fail(e);
                }
              }
            });
          } catch (Exception e) {
            /* This can only happen if there is an internal problem. Propagators should not throw any exceptions. */
            LOGGER.error("ParSeq ingternal error. An exception was thrown by propagator.", e);
          }
      } else {
        //non-parent tasks subsequent executions
        addPotentialRelationships(traceContext, traceContext.getParent().getTraceBuilder());
        Promises.propagateResult(settable, dest);
      }
    };
  }

  /**
   * Create new FusionTask without any predecessors.
   */
  public static <S, T> FusionTask<?, T> create(final String name, final PromisePropagator<S, T> propagator) {
    return new FusionTask<S, T>(name, (Task<S>)null, propagator);
  }

  /**
   * Create new FusionTask with an async predecessor.
   */
  public static <S, T> FusionTask<?, T> create(final String name, final Task<S> task, final PromisePropagator<S, T> propagator) {
    return new FusionTask<S, T>(name, task, propagator);
  }

  @Override
  public <R> Task<R> apply(String desc, PromisePropagator<T, R> propagator) {
    return new FusionTask<>(desc, this, propagator);
  }

  @Override
  public Task<T> recoverWith(final String desc, final Function1<Throwable, Task<T>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    final Task<T> that = this;
    return Task.async(desc, context -> {
      final SettablePromise<T> result = Promises.settable();
      context.after(that).run(() -> {
        if (that.isFailed()) {
          if (!(Exceptions.isCancellation(that.getError()))) {
            try {
              Task<T> r = func.apply(that.getError());
              Promises.propagateResult(r, result);
              return r;
            } catch (Throwable t) {
              result.fail(t);
              return null;
            }
          } else {
            result.fail(that.getError());
            return null;
          }
        } else {
          result.done(that.get());
          return null;
        }
      } );
      context.run(that);
      return result;
    });
  }

  private void propagate(final FusionTraceContext traceContext, final SettablePromise<T> result) {
    try {
      _propagator.accept(traceContext, _asyncTask, result);
    } catch (Throwable t) {
      result.fail(t);
    }
  }

  @Override
  protected Promise<? extends T> run(final Context context) throws Throwable {
    final SettablePromise<T> result = Promises.settable();
    String baseName = getName();
    if (_asyncTask == null) {
      /* There is no async predecessor, run propagation immediately */
      FusionTraceContext traceContext = new FusionTraceContext(context,
          FusionTask.this.getShallowTraceBuilder(), baseName);
      propagate(traceContext, result);
    } else {
      /* There is async predecessor, need to run it first
         PropagationTask will actually run propagation */
      final Task<T> propagationTask = Task.async(baseName, ctx -> {
        final SettablePromise<T> fusionResult = Promises.settable();
        FusionTraceContext traceContext = new FusionTraceContext(ctx, FusionTask.this.getShallowTraceBuilder(), baseName);
        propagate(traceContext, fusionResult);
        return fusionResult;
      });
      propagationTask.getShallowTraceBuilder()
        .setHidden(_shallowTraceBuilder.getHidden())
        .setSystemHidden(_shallowTraceBuilder.getSystemHidden());
      _shallowTraceBuilder.setName("async fused");
      _shallowTraceBuilder.setSystemHidden(true);
      context.after(_asyncTask).run(propagationTask);
      context.run(_asyncTask);
      Promises.propagateResult(propagationTask, result);
    }
    return result;
  }
}
