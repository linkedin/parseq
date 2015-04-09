package com.linkedin.parseq;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkedin.parseq.function.Consumer3;
import com.linkedin.parseq.function.Function1;
import com.linkedin.parseq.internal.ArgumentUtil;
import com.linkedin.parseq.internal.Continuations;
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
 *
 * @author jodzga
 *
 * @param <S>
 * @param <T>
 */
public class FusionTask<S, T> extends BaseTask<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FusionTask.class);

  private static final Continuations CONTINUATIONS = new Continuations();

  private final Consumer3<FusionTraceContext, Promise<S>, Settable<T>> _propagator;
  private final Task<S> _task;
  private final Promise<S> _source;

  private final ShallowTraceBuilder _predecessor;

  private FusionTask(final String desc, final Task<S> task, final Promise<S> source,
      final Consumer3<FusionTraceContext, Promise<S>, Settable<T>> propagator, final boolean systemHidden,
      final ShallowTraceBuilder predecessor) {
    super(desc);
    _shallowTraceBuilder.setSystemHidden(systemHidden);
    _propagator = completing(propagator);
    _task = task;
    _source = source;
    _predecessor = predecessor;
  }

  private void trnasitionToDone(final FusionTraceContext traceContext) {
    addRelationships(traceContext);
    transitionPending();
    transitionDone();
  }

  private void addRelationships(final FusionTraceContext traceContext) {
    TraceBuilder builder = getTraceBuilder();
    final ShallowTraceBuilder parent = traceContext.getContext().getShallowTraceBuilder();
    final boolean isTrigger = traceContext.getTrigger().getId().equals(getId());

    if (!isTrigger) {
      builder.addRelationship(Relationship.PARENT_OF, parent, _shallowTraceBuilder);
    }

    if (_predecessor != null && (!isTrigger)) {
      builder.addRelationship(Relationship.SUCCESSOR_OF, _shallowTraceBuilder, _predecessor);
    }
  }

  private void addPotentialRelationships(final FusionTraceContext traceContext, final TraceBuilder builder) {
    final ShallowTraceBuilder parent = traceContext.getContext().getShallowTraceBuilder();
    final boolean isTrigger = traceContext.getTrigger().getId().equals(getId());

    if (!isTrigger) {
      builder.addRelationship(Relationship.POTENTIAL_CHILD_OF, _shallowTraceBuilder, parent);
    }

    if (_predecessor != null && (!isTrigger)) {
      builder.addRelationship(Relationship.POSSIBLE_SUCCESSOR_OF, _shallowTraceBuilder, _predecessor);
    }
  }

  private Consumer3<FusionTraceContext, Promise<S>, Settable<T>> completing(
      final Consumer3<FusionTraceContext, Promise<S>, Settable<T>> propagator) {
    return (traceContext, src, dest) -> {
      final SettablePromise<T> settable = FusionTask.this.getSettableDelegate();

      if (traceContext.getTrigger() != null && traceContext.getTrigger().getId().equals(getId())) {
        //simply propagate result because the BaseTask's code
        //handles completing the task
        propagator.accept(traceContext, src, new Settable<T>() {
          @Override
          public void done(T value) throws PromiseResolvedException {
            dest.done(value);
          }

          @Override
          public void fail(Throwable error) throws PromiseResolvedException {
            dest.fail(error);
          }
        });
      } else if (transitionRun(traceContext.getContext().getTraceBuilder())) {
        //non-parent task executed for the first time
        traceContext.getContext().getTaskLogger().logTaskStart(this);
        Runnable complete = () -> {
          try {
            propagator.accept(traceContext, src, new Settable<T>() {
              @Override
              public void done(final T value) throws PromiseResolvedException {
                try {
                  trnasitionToDone(traceContext);
                  final Function<T, String> traceValueProvider = _traceValueProvider;
                  _shallowTraceBuilder.setResultType(ResultType.SUCCESS);
                  if (traceValueProvider != null) {
                    try {
                      _shallowTraceBuilder.setValue(traceValueProvider.apply(value));
                    } catch (Exception e) {
                      _shallowTraceBuilder.setValue(e.toString());
                    }
                  }
                  settable.done(value);
                  traceContext.getContext().getTaskLogger().logTaskEnd(FusionTask.this, _traceValueProvider);
                  CONTINUATIONS.submit(() -> dest.done(value), null);
                } catch (Exception e) {
                  CONTINUATIONS.submit(() -> dest.fail(e), null);
                }
              }

              @Override
              public void fail(final Throwable error) throws PromiseResolvedException {
                try {
                  trnasitionToDone(traceContext);
                  traceFailure(error);
                  settable.fail(error);
                  traceContext.getContext().getTaskLogger().logTaskEnd(FusionTask.this, _traceValueProvider);
                  CONTINUATIONS.submit(() -> dest.fail(error), null);
                } catch (Exception e) {
                  CONTINUATIONS.submit(() -> dest.fail(e), null);
                }
              }
            });
          } catch (Exception e) {
            LOGGER.error("An exception was thrown by propagator", e);
          }
        };
        CONTINUATIONS.submit(complete, null);
      } else {
        //non-parent tasks subsequent executions
        addPotentialRelationships(traceContext, traceContext.getContext().getTraceBuilder());
        Promises.propagateResult(settable, dest);
      }
    };
  }

  private static <A, B> Consumer3<FusionTraceContext, Promise<A>, Settable<B>> withFusionTraceContext(
      final PromisePropagator<A, B> propagator) {
    return (traceContext, src, dest) -> propagator.accept(src, dest);
  }

  @SuppressWarnings("unchecked")
  public static <S, T> FusionTask<?, T> fuse(final String name, final Task<S> task,
      final Consumer3<FusionTraceContext, Promise<S>, Settable<T>> propagator,
      final ShallowTraceBuilder predecessor) {
    if (task instanceof FusionTask) {
      return ((FusionTask<?, S>) task).apply(name, propagator);
    } else {
      return new FusionTask<S, T>(name, task, task, propagator, false, predecessor);
    }
  }

  public static <S, T> FusionTask<?, T> create(final String name, final Promise<S> source,
      final PromisePropagator<S, T> propagator) {
    return new FusionTask<S, T>(name, null, source, withFusionTraceContext(propagator), false, null);
  }

  private <R> Consumer3<FusionTraceContext, Promise<S>, Settable<R>> compose(
      final Consumer3<FusionTraceContext, Promise<T>, Settable<R>> propagator) {
    return (traceContext, src, dst) -> {
      _propagator.accept(traceContext, src, new Settable<T>() {
        @Override
        public void done(T value) throws PromiseResolvedException {
          try {
            propagator.accept(traceContext, Promises.value(value), dst);
          } catch (Exception e) {
            LOGGER.error("An exception was thrown by propagator", e);
          }
        }

        @Override
        public void fail(Throwable error) throws PromiseResolvedException {
          try {
            propagator.accept(traceContext, Promises.error(error), dst);
          } catch (Exception e) {
            LOGGER.error("An exception was thrown by propagator", e);
          }
        }
      });
    };
  }

  @Override
  public <R> Task<R> apply(String desc, PromisePropagator<T, R> propagator) {
    return apply(desc, withFusionTraceContext(propagator));
  }

  public <R> FusionTask<?, R> apply(String desc, Consumer3<FusionTraceContext, Promise<T>, Settable<R>> propagator) {
    return fuse(desc, _task, compose(propagator), _shallowTraceBuilder);
  };

  @Override
  public Task<T> recoverWith(final String desc, final Function1<Throwable, Task<T>> func) {
    ArgumentUtil.requireNotNull(func, "function");
    final Task<T> that = this;
    return Task.async(desc, context -> {
      final SettablePromise<T> result = Promises.settable();
      context.after(that).run(() -> {
        if (that.isFailed() && !(Exceptions.isCancellation(that.getError()))) {
          try {
            Task<T> r = func.apply(that.getError());
            Promises.propagateResult(r, result);
            return r;
          } catch (Throwable t) {
            result.fail(t);
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

  protected void propagate(final FusionTraceContext traceContext, final SettablePromise<T> result) {
    try {
      _propagator.accept(traceContext, _source, result);
    } catch (Throwable t) {
      result.fail(t);
    }
  }

  @Override
  protected Promise<? extends T> run(final Context context) throws Throwable {
    final SettablePromise<T> result = Promises.settable();
    if (_task == null) {
      FusionTraceContext traceContext = new FusionTraceContext(context, null,
          FusionTask.this.getShallowTraceBuilder());
      propagate(traceContext, result);
    } else {
      final Task<T> propagationTask = Task.async("fusion", ctx -> {
        FusionTraceContext traceContext = new FusionTraceContext(ctx, _task.getShallowTraceBuilder(),
            FusionTask.this.getShallowTraceBuilder());
        propagate(traceContext, result);
        return result;
      });
      propagationTask.getShallowTraceBuilder().setSystemHidden(true);
      context.after(_task).run(propagationTask);
      context.run(_task);
    }
    return result;
  }

  public static <S, T> FusionTask<?, T> fuse(final String name, final Task<S> task,
      final PromisePropagator<S, T> propagator, final ShallowTraceBuilder predecessor) {
    return fuse(name, task, withFusionTraceContext(propagator), predecessor);
  }
}
